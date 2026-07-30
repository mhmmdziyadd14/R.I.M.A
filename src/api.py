import sys
import os
import re
# Auto-resolve parent folder in python path to prevent import errors
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import threading
import time
import asyncio
import glob
import random
import numpy as np
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.websockets import WebSocket, WebSocketDisconnect
import uvicorn
import src.config as config

# 1. Protect Serial import
try:
    import serial
    import serial.tools.list_ports
    HAS_SERIAL = True
except ImportError:
    HAS_SERIAL = False
    print("[WARN] 'pyserial' is not installed. Arduino communication will be simulated.")

# 2. Protect Pygame import
try:
    import pygame
    HAS_PYGAME = True
    try:
        import pygame.midi
        HAS_MIDI = True
    except ImportError:
        HAS_MIDI = False
        print("[WARN] 'pygame.midi' is not available.")
except ImportError:
    HAS_PYGAME = False
    HAS_MIDI = False
    print("[WARN] 'pygame' is not installed. Local laptop synth audio is disabled.")


# 3. Protect heavy AI dependencies
try:
    import sounddevice as sd
    import torch
    import librosa
    import soundfile as sf
    from src.model import GreetingCRNN
    from src.dataset import extract_mel_spectrogram
    HAS_AI = True
except ImportError as e:
    HAS_AI = False
    print(f"[WARN] Optional AI dependencies (torch, librosa, sounddevice, soundfile) missing: {e}")
    print("[WARN] Microphone pitch tracking and AI song detection are disabled. Arduino control is fully functional.")

app = FastAPI(title="Angklung AI & Pitch Backend")

# Enable CORS for Flutter Web client access
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load CRNN Model
device = None
model = None

def init_model():
    global model, device
    if not HAS_AI:
        return
    try:
        device = torch.device("cpu")
        model_path = os.path.join(config.MODELS_DIR, "best_model_CRNN.pth")
        # if not os.path.exists(model_path):
        #     model_path = os.path.join(config.MODELS_DIR, "best_overall_model.pth")
            
        if os.path.exists(model_path):
            model = GreetingCRNN(num_classes=len(config.CLASSES)).to(device)
            model.load_state_dict(torch.load(model_path, map_location=device))
            model.eval()
            print(f"[MODEL] Model PyTorch CRNN berhasil dimuat dari '{model_path}'.")
        else:
            print(f"[WARNING] File model CRNN '{model_path}' belum ada. Silakan lakukan training.")
    except Exception as e:
        print(f"[MODEL] Gagal memuat model CRNN: {e}")

if HAS_AI:
    init_model()

def frequency_to_note(freq):
    """Maps continuous vocal pitch frequencies into exact Angklung frequency range bins.
    Guarantees that 100% of detected vocal sounds are classified into the nearest note bucket.
    """
    if freq <= 0:
        return None
    import math
    
    exact_midi = 69.0 + 12.0 * math.log2(freq / 440.0)
    raw_midi = int(round(exact_midi))
    
    transposed_midi = raw_midi
    while transposed_midi < 52:
        transposed_midi += 12
    while transposed_midi > 96:
        transposed_midi -= 12
        
    transposed_midi = max(52, min(96, transposed_midi))
        
    pitch_names = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
    pitch_name = pitch_names[transposed_midi % 12]
    octave = (transposed_midi // 12) - 1
    
    return f"{pitch_name}{octave}"

def detect_pitch_with_confidence(signal, sr):
    """Dominant Fundamental Pitch Detector with Soft Voice Sensitivity & Noise Rejection."""
    if len(signal) == 0:
        return 0.0, 0.0
    signal = signal - np.mean(signal)
    
    # Sensitivitas RMS 0.005 (Menangkap vokal manusia yang sangat lembut / "suara kecil")
    rms = np.sqrt(np.mean(signal**2))
    if rms < 0.005:
        return 0.0, 0.0
        
    max_val = np.max(np.abs(signal))
    norm_signal = signal / max_val if max_val > 0 else signal

    # Low-pass filter (Moving average 5-point untuk menapis squeak / nada tinggi kecil berisik)
    smoothed = np.convolve(norm_signal, np.ones(5)/5.0, mode='same')
        
    corr = np.correlate(smoothed, smoothed, mode='full')
    corr = corr[len(corr)//2:]
    
    # Fokus rentang frekuensi vokal utama (75 Hz hingga 850 Hz untuk nada dominan keras)
    min_lag = int(sr / 850)
    max_lag = int(sr / 75)
    
    if max_lag >= len(corr) or min_lag >= len(corr):
        return 0.0, 0.0
        
    search_segment = corr[min_lag:max_lag]
    if len(search_segment) == 0:
        return 0.0, 0.0
        
    peak = np.argmax(search_segment) + min_lag
    
    # Prioritaskan nada fundamental utama (mencegah overtone nada tinggi kecil terbaca)
    half_peak = peak // 2
    if half_peak >= min_lag and corr[half_peak] >= 0.60 * corr[peak]:
        peak = half_peak
    else:
        third_peak = peak // 3
        if third_peak >= min_lag and corr[third_peak] >= 0.60 * corr[peak]:
            peak = third_peak

    # Adaptive Voice Activity Detector (VAD):
    # Suara manusia memiliki autokorelasi tinggi (>= 0.28), sedangkan noise desisan bersifat acak (< 0.20)
    if corr[0] == 0 or (corr[peak] / corr[0]) < 0.28:
        return 0.0, 0.0

    peak_ratio = float(corr[peak] / corr[0])
    rms_weight = min(1.0, float(rms / 0.05))
    confidence = min(1.0, max(0.0, peak_ratio * 0.75 + rms_weight * 0.25))
        
    if 0 < peak < len(corr) - 1:
        alpha = corr[peak - 1]
        beta = corr[peak]
        gamma = corr[peak + 1]
        denom = (alpha - 2 * beta + gamma)
        if denom != 0:
            p = 0.5 * (alpha - gamma) / denom
            refined_peak = peak + p
        else:
            refined_peak = float(peak)
    else:
        refined_peak = float(peak)

    if refined_peak <= 0:
        return 0.0, 0.0

    freq = sr / refined_peak
    return float(freq), float(round(confidence, 3))

def detect_pitch(signal, sr):
    freq, _ = detect_pitch_with_confidence(signal, sr)
    return freq

def pitch_hz_to_scale_degree(freq_hz, root_midi=60):
    """Maps pitch frequency Hz to Note Name, Scale Degree (Do, Re, Mi, Fa, Sol, La, Si), and Angklung MIDI note.
    Guarantees 100% octave consistency in Lead Vocal Range 1 to 8/1' (C4 = 60 to C5 = 72).
    """
    if freq_hz <= 0:
        return None, None, None
    import math
    exact_midi = 69.0 + 12.0 * math.log2(freq_hz / 440.0)
    nearest_midi = int(round(exact_midi))
    
    # Octave Folding strictly into Lead Vocal Range [C4 = 60 to C5 = 72 / Range 1 to 8/1']
    transposed_midi = nearest_midi
    while transposed_midi < 60:
        transposed_midi += 12
    while transposed_midi > 72:
        transposed_midi -= 12
    transposed_midi = max(60, min(72, transposed_midi))

    pitch_names = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
    doremi_labels = ['1 (Do)', '1/ (Do#)', '2 (Re)', '2/ (Re#)', '3 (Mi)', '4 (Fa)', '4/ (Fa#)', '5 (Sol)', '5/ (Sol#)', '6 (La)', '6/ (La#)', '7 (Si)']
    
    pitch_name = pitch_names[transposed_midi % 12]
    octave = (transposed_midi // 12) - 1
    note_str = f"{pitch_name}{octave}"
    
    semitones_from_root = (transposed_midi - root_midi) % 12
    scale_deg = doremi_labels[semitones_from_root]

    return note_str, scale_deg, transposed_midi

def segment_vocal_melody_frames(frame_list, min_note_dur_ms=90):
    """Monophonic Onset-Based Vocal Note Segmentation Engine (Hop size 10ms).
    Splits continuous vocal pitch stream into distinct Note Event segments using:
    (a) Pitch Delta > 50 Cents step
    (b) Voiced / Unvoiced onset transition
    (c) Syllable Attack Energy Re-articulation (New note pulse on same pitch)
    """
    if not frame_list or len(frame_list) == 0:
        return []

    import math
    import numpy as np

    segments = []
    current_segment = []

    def cents_distance(f1, f2):
        if f1 <= 0 or f2 <= 0:
            return 9999.0
        return 1200.0 * abs(math.log2(f1 / f2))

    for frame in frame_list:
        freq = frame.get('freq', 0.0)
        voiced = frame.get('voiced', False)
        confidence = frame.get('confidence', 0.0)
        time_ms = frame.get('time_ms', 0)
        rms = frame.get('rms', 0.0)

        if not voiced or freq <= 0 or confidence < 0.22:
            # Unvoiced / Silence -> Close active note segment
            if len(current_segment) > 0:
                segments.append(current_segment)
                current_segment = []
            continue

        if len(current_segment) == 0:
            current_segment.append(frame)
        else:
            # Calculate current segment median pitch
            seg_freqs = [f['freq'] for f in current_segment if f['freq'] > 0]
            current_median = float(np.median(seg_freqs)) if len(seg_freqs) > 0 else current_segment[0]['freq']

            # Check Onset Triggers:
            # 1. Pitch Step Delta > 50 Cents from current stable note
            pitch_jump = cents_distance(freq, current_median) > 50.0

            # 2. Syllable Attack Energy Re-articulation (New note pulse on same pitch: RMS rise >= 1.6x after 4+ frames)
            prev_rms = current_segment[-1].get('rms', 0.001)
            energy_jump = (rms / max(0.001, prev_rms)) >= 1.6 and len(current_segment) >= 4

            if pitch_jump or energy_jump:
                segments.append(current_segment)
                current_segment = [frame]
            else:
                current_segment.append(frame)

    if len(current_segment) > 0:
        segments.append(current_segment)

    # Process each segment into a structured Note Event object
    note_events = []
    for idx, seg in enumerate(segments):
        if len(seg) == 0:
            continue
        
        dur_ms = seg[-1]['time_ms'] - seg[0]['time_ms'] + 10
        if dur_ms < min_note_dur_ms:
            continue # Ignore short noise transient < 100ms

        freqs = [f['freq'] for f in seg if f['freq'] > 0]
        confs = [f['confidence'] for f in seg]

        if len(freqs) == 0:
            continue

        # Representative Median Pitch Frequency for immunity against vibrato & transient spikes
        median_hz = float(np.median(freqs))
        avg_conf = float(np.mean(confs))

        note_str, scale_deg, midi_num = pitch_hz_to_scale_degree(median_hz)

        note_events.append({
            "id": idx + 1,
            "start_ms": seg[0]['time_ms'],
            "end_ms": seg[-1]['time_ms'] + 10,
            "duration_ms": dur_ms,
            "freq_hz": round(median_hz, 1),
            "note": note_str,
            "scale_degree": scale_deg,
            "midi": midi_num,
            "confidence": round(avg_conf, 2)
        })

    return note_events

def preprocess_audio_data(y):
    """Extracts Mel-Spectrogram features for CRNN model inference."""
    mel_spec = extract_mel_spectrogram(y, sr=config.SAMPLE_RATE)
    # Shape for CRNN: (1, 1, N_MELS, Time_Steps)
    return torch.tensor(mel_spec, dtype=torch.float32).unsqueeze(0).unsqueeze(0)

@app.get("/api/health")
def health_check():
    return {
        "status": "healthy",
        "model_loaded": model is not None,
        "classes": config.CLASSES
    }

# Map of the 16 Angklung note channels for the 3 distinct frames
NOTE_FREQUENCIES = {
    1: { # Angklung 1 (High/Yellow)
        1: 392.00, 2: 440.00, 3: 466.16, 4: 493.88, 5: 523.25, 6: 587.33, 7: 659.25, 8: 698.46,
        9: 739.99, 10: 783.99, 11: 880.00, 12: 932.33, 13: 987.77, 14: 1046.50, 15: 1174.66, 16: 1318.51
    },
    2: { # Angklung 2 (Medium/Green)
        1: 349.23, 2: 369.99, 3: 415.30, 4: 554.37, 5: 622.25, 6: 830.61, 7: 1109.73, 8: 1244.51,
        9: 1396.91, 10: 1479.98, 11: 1567.98, 12: 1661.22, 13: 1760.00, 14: 1864.66, 15: 1975.53, 16: 2093.00
    },
    3: { # Angklung 3 (Bass/Blue)
        1: 164.81, 2: 174.61, 3: 185.00, 4: 196.00, 5: 207.65, 6: 220.00, 7: 233.08, 8: 246.94,
        9: 261.63, 10: 277.18, 11: 293.66, 12: 311.13, 13: 329.63, 14: 349.23, 15: 369.99, 16: 392.00
    }
}

# =========================================================================
# [TODO: MANUAL CALIBRATION NEEDED]
# Tabel frekuensi hasil kalibrasi pengukuran langsung dari bilah angklung fisik nyata.
# Diisi dengan frekuensi acuan fisik per-channel untuk presisi snapping hardware.
# =========================================================================
ANGKLUNG_CALIBRATED_FREQUENCIES = {
    1: { # Angklung 1 (High/Yellow)
        1: 392.00,  2: 440.00,  3: 466.16,  4: 493.88,  5: 523.25,  6: 587.33,  7: 659.25,  8: 698.46,
        9: 739.99, 10: 783.99, 11: 880.00, 12: 932.33, 13: 987.77, 14: 1046.50, 15: 1174.66, 16: 1318.51
    },
    2: { # Angklung 2 (Medium/Green)
        1: 349.23,  2: 369.99,  3: 415.30,  4: 554.37,  5: 622.25,  6: 830.61,  7: 1109.73,  8: 1244.51,
        9: 1396.91, 10: 1479.98, 11: 1567.98, 12: 1661.22, 13: 1760.00, 14: 1864.66, 15: 1975.53, 16: 2093.00
    },
    3: { # Angklung 3 (Bass/Blue)
        1: 164.81,  2: 174.61,  3: 185.00,  4: 196.00,  5: 207.65,  6: 220.00,  7: 233.08,  8: 246.94,
        9: 261.63, 10: 277.18, 11: 293.66, 12: 311.13, 13: 329.63, 14: 349.23, 15: 369.99, 16: 392.00
    }
}

pygame_mixer_initialized = False

def init_pygame_mixer():
    global pygame_mixer_initialized
    if pygame_mixer_initialized:
        return True
    try:
        pygame.mixer.init()
        pygame.mixer.set_num_channels(64)
        pygame_mixer_initialized = True
        print("[AUDIO] Pygame mixer berhasil diaktifkan dengan 64 channels!")
        return True
    except Exception as e:
        print(f"[AUDIO] Gagal mengaktifkan pygame mixer: {e}")
        return False

def generate_angklung_sound(frequency: float, duration: float = 1.2, sr: int = 44100, volume: float = 1.0, instr_type: str = "melody"):
    t = np.linspace(0, duration, int(sr * duration), endpoint=False)
    
    if instr_type == "drum":
        note_id = int(frequency)
        if note_id == 34: # 'x' -> Hi-Hat / Cymbal (noise burst)
            signal = (np.random.rand(len(t)) - 0.5) * np.exp(-60.0 * t) * 0.6
        elif note_id == 36: # 'z' -> Kick Drum (sine sweep)
            sweep_freq = 45.0 + 115.0 * np.exp(-35.0 * t)
            signal = np.sin(2.0 * np.pi * sweep_freq * t) * np.exp(-18.0 * t)
        else: # 'y' (35) -> Snare (body + noise sweep)
            noise = (np.random.rand(len(t)) - 0.5) * np.exp(-22.0 * t) * 0.8
            body = np.sin(2.0 * np.pi * 175.0 * t) * np.exp(-45.0 * t) * 0.35
            signal = noise + body
            
    elif instr_type == "bass":
        # Bass: Warm, deep fundamental with minimal high-frequency harshness
        f1 = frequency
        f2 = frequency * 2.0
        env1 = np.exp(-2.2 * t)
        env2 = np.exp(-5.0 * t)
        
        tone1 = np.sin(2.0 * np.pi * f1 * t) * env1 * 0.85
        tone2 = np.sin(2.0 * np.pi * f2 * t) * env2 * 0.15
        signal = tone1 + tone2
        
        # Soft woody pluck for bass attack
        click_len = int(sr * 0.02)
        if len(signal) >= click_len:
            click = (np.random.rand(click_len) - 0.5) * np.exp(-np.linspace(0, 4.0, click_len)) * 0.08
            signal[:click_len] += click
            
    else: # "melody" or "chord"
        # Clean, warm resonant bamboo angklung chime:
        # - Two primary tubes tuned an octave apart (f1 and f2 = 2.0 * f1)
        # - Gentle detuning for a rich, organic chorus effect
        # - Smooth exponential decay for a clean, singing tone
        # - Very soft low-frequency frame thump for the wood hammer strike (no harsh white noise)
        f1 = frequency
        f2 = frequency * 2.0
        f3 = frequency * 3.0
        
        f1_detune = f1 * 1.0015
        f2_detune = f2 * 0.9985
        
        env1 = np.exp(-3.5 * t)
        env2 = np.exp(-2.5 * t)
        env3 = np.exp(-5.0 * t)
        
        tone1 = (np.sin(2.0 * np.pi * f1 * t) + np.sin(2.0 * np.pi * f1_detune * t)) * env1 * 0.50
        tone2 = (np.sin(2.0 * np.pi * f2 * t) + np.sin(2.0 * np.pi * f2_detune * t)) * env2 * 0.40
        tone3 = np.sin(2.0 * np.pi * f3 * t) * env3 * 0.10
        
        signal = tone1 + tone2 + tone3
        
        # Soft woody attack thump (no high-frequency noise rattle)
        click_len = int(sr * 0.02)
        if len(signal) >= click_len:
            thunk = np.sin(2.0 * np.pi * 150.0 * np.linspace(0, 0.02, click_len)) * np.exp(-150.0 * np.linspace(0, 0.02, click_len)) * 0.15
            signal[:click_len] += thunk
            
    # Normalize & Scale
    if instr_type == "chord":
        # Stereo spacing for chords
        signal_L = signal * 1.001
        signal_R = signal * 0.999
        max_L = np.max(np.abs(signal_L))
        max_R = np.max(np.abs(signal_R))
        if max_L > 0: signal_L = (signal_L / max_L) * volume
        if max_R > 0: signal_R = (signal_R / max_R) * volume
        stereo_signal = np.column_stack((signal_L, signal_R))
        return (stereo_signal * 32767).astype(np.int16)
        
    # Mono centered for melody, bass, drums
    max_val = np.max(np.abs(signal))
    if max_val > 0:
        signal = (signal / max_val) * volume
        
    stereo_signal = np.column_stack((signal, signal))
    return (stereo_signal * 32767).astype(np.int16)

global_synth_volume = 1.0

def play_synth_note_async(note_num: int, angklung_id: int, volume: float = 1.0, instr_type: str = "melody"):
    global global_synth_volume
    effective_volume = volume * global_synth_volume
    if not init_pygame_mixer():
        return
    try:
        # Route offset notes > 16 on board 1 to board 2 frequency maps
        if angklung_id == 1 and note_num > 16:
            target_id = 2
            target_note = note_num - 16
        else:
            target_id = angklung_id
            target_note = note_num

        freq_map = NOTE_FREQUENCIES.get(target_id, NOTE_FREQUENCIES[3])
        freq = freq_map.get(target_note, 261.63)
        pcm_data = generate_angklung_sound(freq, volume=effective_volume, instr_type=instr_type)
        
        sound = pygame.sndarray.make_sound(pcm_data)
        sound.play()
    except Exception as e:
        print(f"[AUDIO] Gagal memainkan suara lokal: {e}")

def play_local_sound(note_num: int, angklung_id: int = 3, volume: float = 1.0, instr_type: str = "melody"):
    t = threading.Thread(target=play_synth_note_async, args=(note_num, angklung_id, volume, instr_type))
    t.daemon = True
    t.start()

# Arduino 3-COM configurations
SERIAL_PORTS = {
    1: "COM10",
    2: "COM11",
    3: "COM12"
}
BAUD_RATE = 9600
arduino_serials = {1: None, 2: None, 3: None}
arduino_locks = {1: threading.Lock(), 3: threading.Lock()}
last_connection_attempts = {1: 0.0, 2: 0.0, 3: 0.0}
SIMULATION_MODE = True

def get_arduino_connection(angklung_id: int):
    global arduino_serials, SERIAL_PORTS, BAUD_RATE, last_connection_attempts, SIMULATION_MODE
    if SIMULATION_MODE or not HAS_SERIAL:
        return None
        
    if angklung_id not in arduino_serials:
        angklung_id = 3
        
    ser = arduino_serials[angklung_id]
    if ser is not None and ser.is_open:
        return ser
        
    # Cooldown check: if last attempt was < 10 seconds ago, skip trying to avoid block lag
    now = time.time()
    if now - last_connection_attempts.get(angklung_id, 0.0) < 10.0:
        return None
        
    last_connection_attempts[angklung_id] = now
    port = SERIAL_PORTS.get(angklung_id)
    try:
        if ser:
            try:
                ser.close()
            except:
                pass
        
        print(f"[SERIAL] Membuka port serial Angklung {angklung_id} ke {port}...")
        ser = serial.Serial(port, BAUD_RATE, timeout=0.2)
        time.sleep(1.8)
        print(f"[SERIAL] Port Angklung {angklung_id} ({port}) berhasil dibuka!")
        arduino_serials[angklung_id] = ser
        return ser
    except Exception as e:
        print(f"[SERIAL] Gagal membuka port Angklung {angklung_id} ({port}): {e}")
        arduino_serials[angklung_id] = None
        return None

def send_to_arduino(note_num, angklung_id: int = 3, play_synth: bool = True, wait_response: bool = False):
    if isinstance(note_num, int):
        notes_list = [note_num]
    elif isinstance(note_num, str):
        notes_list = [int(x) for x in note_num.split(",") if x.strip().isdigit()]
    elif isinstance(note_num, list) or isinstance(note_num, tuple):
        notes_list = [int(x) for x in note_num]
    else:
        notes_list = []

    if play_synth:
        for n in notes_list:
            if n != 0:
                play_local_sound(n, angklung_id)
    
    target_id = angklung_id
    if angklung_id == 2:
        target_id = 1
        actual_notes = [n + 16 for n in notes_list]
    else:
        actual_notes = notes_list
        
    if not actual_notes:
        return True, "No notes"

    ser = get_arduino_connection(target_id)
    if ser is None:
        notes_str = ",".join(str(x) for x in notes_list)
        return True, f"Offline - Dimainkan di Laptop (Angklung: {angklung_id}, Nada: {notes_str})"
        
    payload = ",".join(str(x) for x in actual_notes)
    
    lock = arduino_locks.get(target_id)
    if lock:
        # Acquire lock with a timeout of 200ms (0.2s) to prevent deadlocks
        acquired = lock.acquire(timeout=0.2)
        if acquired:
            try:
                ser.reset_input_buffer()
                ser.write(f"{payload}\n".encode('utf-8'))
                if wait_response:
                    response = ser.readline().decode('utf-8').strip()
                    if not response:
                        response = ser.readline().decode('utf-8').strip()
                    return True, response
                else:
                    return True, f"Sent {payload} to Arduino {target_id}"
            except Exception as e:
                print(f"[SERIAL] Gagal kirim nada {payload} ke Angklung {target_id}: {e}")
                try:
                    ser.close()
                except:
                    pass
                arduino_serials[target_id] = None
                notes_str = ",".join(str(x) for x in notes_list)
                return True, f"Error Serial ({e}) - Dimainkan di Laptop (Nada: {notes_str})"
            finally:
                lock.release()
        else:
            # Lock timeout: skip serial write to prevent server hang
            print(f"[SERIAL] Lock timeout untuk Angklung {target_id} - Melompati pengiriman untuk mencegah deadlock.")
            notes_str = ",".join(str(x) for x in notes_list)
            return True, f"Lock Timeout - Dimainkan di Laptop (Nada: {notes_str})"

def send_raw_command_to_arduino(command_str: str, board_id: int):
    ser = get_arduino_connection(board_id)
    if ser is not None:
        lock = arduino_locks.get(board_id)
        if lock:
            acquired = lock.acquire(timeout=0.2)
            if acquired:
                try:
                    ser.reset_input_buffer()
                    ser.write(f"{command_str}\n".encode('utf-8'))
                    # Read response if any
                    ser.readline()
                except Exception as e:
                    print(f"[SERIAL] Gagal mengirim perintah raw {command_str} ke Angklung {board_id}: {e}")
                    try: ser.close()
                    except: pass
                    arduino_serials[board_id] = None
                finally:
                    lock.release()
    else:
        return True, "No lock"

@app.post("/api/config-arduino")
def config_arduino(data: dict):
    global SERIAL_PORTS, arduino_serials, SIMULATION_MODE
    port1 = data.get("port1", SERIAL_PORTS[1])
    port2 = data.get("port2", SERIAL_PORTS[2])
    port3 = data.get("port3", SERIAL_PORTS[3])
    sim_mode = data.get("simulation_mode", SIMULATION_MODE)
    
    SIMULATION_MODE = sim_mode
    if SIMULATION_MODE:
        # If in simulation mode, close any open serial ports
        for i in [1, 2, 3]:
            if arduino_serials[i]:
                try: arduino_serials[i].close()
                except: pass
                arduino_serials[i] = None
                
    if port1 != SERIAL_PORTS[1]:
        if arduino_serials[1]:
            try: arduino_serials[1].close()
            except: pass
            arduino_serials[1] = None
        SERIAL_PORTS[1] = port1
        
    if port2 != SERIAL_PORTS[2]:
        if arduino_serials[2]:
            try: arduino_serials[2].close()
            except: pass
            arduino_serials[2] = None
        SERIAL_PORTS[2] = port2
        
    if port3 != SERIAL_PORTS[3]:
        if arduino_serials[3]:
            try: arduino_serials[3].close()
            except: pass
            arduino_serials[3] = None
        SERIAL_PORTS[3] = port3
        
    print(f"[API] Update ports serial -> Angklung1: {SERIAL_PORTS[1]}, Angklung2: {SERIAL_PORTS[2]}, Angklung3: {SERIAL_PORTS[3]}, Simulation Mode: {SIMULATION_MODE}")
    return {"status": "success", "ports": SERIAL_PORTS, "simulation_mode": SIMULATION_MODE}

@app.get("/api/arduino/status")
def arduino_status():
    global arduino_serials, SERIAL_PORTS, SIMULATION_MODE
    status_res = {}
    for i in [1, 2, 3]:
        if SIMULATION_MODE:
            status_res[f"angklung{i}"] = {
                "status": "simulation",
                "port": "Simulasi Laptop" if i != 2 else "Terintegrasi (Simulasi)"
            }
        else:
            if i == 2:
                ser = get_arduino_connection(1)
                is_online = ser is not None and ser.is_open
                status_res["angklung2"] = {
                    "status": "online" if is_online else "offline",
                    "port": "Terintegrasi (Angklung 1)"
                }
            else:
                ser = get_arduino_connection(i)
                is_online = ser is not None and ser.is_open
                status_res[f"angklung{i}"] = {
                    "status": "online" if is_online else "offline",
                    "port": SERIAL_PORTS[i]
                }
    return status_res

@app.get("/api/arduino/play")
def arduino_play(note: int, angklung_id: int = 3):
    if note < 1 or note > 16:
        raise HTTPException(status_code=400, detail="Nomor nada harus antara 1-16")
    success, response = send_to_arduino(note, angklung_id)
    return {"status": "success", "response": response}

@app.get("/api/arduino/play_chord")
def arduino_play_chord(notes: str, angklung_id: int = 3):
    try:
        note_list = [int(n) for n in notes.split(",") if n]
    except ValueError:
        raise HTTPException(status_code=400, detail="Format notes salah. Contoh: '1,3,5'")
    
    valid_notes = [n for n in note_list if 1 <= n <= 16]
    if not valid_notes:
        raise HTTPException(status_code=400, detail="Tidak ada nada valid (1-16)")
        
    success, response = send_to_arduino(valid_notes, angklung_id)
    return {"status": "success", "response": response}

@app.get("/api/arduino/play_multi")
def arduino_play_multi(a1: str = "", a3: str = ""):
    def run_send(notes_str, board_id):
        if not notes_str:
            return
        try:
            notes_list = [int(n) for n in notes_str.split(",") if n.strip().isdigit()]
            if notes_list:
                send_to_arduino(notes_list, board_id)
        except Exception as e:
            print(f"[API] Error in play_multi thread for board {board_id}: {e}")
            
    t1 = threading.Thread(target=run_send, args=(a1, 1))
    t3 = threading.Thread(target=run_send, args=(a3, 3))
    t1.start()
    t3.start()
    t1.join()
    t3.join()
    return {"status": "success"}

song_playback_active = False
current_playback_thread = None
current_playback_token = 0

current_playback_status = {
    "active": False,
    "song_title": "",
    "current_section": "",
    "current_bar": 0,
    "total_bars": 0,
    "elapsed_seconds": 0.0,
    "total_seconds": 0.0
}
current_playback_status_lock = threading.Lock()

seek_bar_index = -1

global_synth_volume = 0.8
global_physical_power = 100
global_v1_volume = 1.00
global_v2_volume = 0.18
global_vb_volume = 0.25
global_va_volume = 0.06
global_v1_staccato = False
global_v2_staccato = False
global_vb_staccato = False
global_va_staccato = False

def parse_subtoken(sub_tok: str, key_sig: str) -> dict:
    # 1. Determine duration
    duration = 1.0
    if '=' in sub_tok:
        duration = 0.25
    elif '-' in sub_tok:
        duration = 0.5
        
    if sub_tok.startswith('.'):
        return {
            "type": "sustain",
            "duration": duration,
            "raw": sub_tok
        }
        
    if sub_tok.startswith('0'):
        return {
            "type": "rest",
            "duration": duration,
            "raw": sub_tok
        }
        
    if sub_tok.startswith('@'):
        chord_sym = sub_tok.replace('@', '').replace('-', '').replace('=', '').replace('^', '')
        return {
            "type": "chord",
            "chord_sym": chord_sym,
            "duration": duration,
            "raw": sub_tok
        }
        
    if sub_tok and sub_tok[0].isalpha():
        return {
            "type": "drum",
            "instrument": sub_tok[0],
            "duration": duration,
            "raw": sub_tok
        }
        
    # Standard Note [1-7]
    digit = 0
    if sub_tok and sub_tok[0].isdigit():
        digit = int(sub_tok[0])
    
    # Octave modifiers
    octave_mod = 0
    octave_mod += sub_tok.count("'") * 12
    octave_mod -= sub_tok.count(",") * 12
    octave_mod -= sub_tok.count(";") * 24
    
    # Accidentals
    accidental = sub_tok.count("/") - sub_tok.count("\\")
    
    key_roots = {
        "C": 60, "C#": 61, "DB": 61, "D": 62, "D#": 63, "EB": 63,
        "E": 64, "F": 65, "F#": 66, "GB": 66, "G": 67, "G#": 68,
        "AB": 68, "A": 69, "A#": 70, "BB": 70, "B": 71
    }
    root = key_roots.get(key_sig.upper(), 60)
    intervals = {1: 0, 2: 2, 3: 4, 4: 5, 5: 7, 6: 9, 7: 11}
    interval = intervals.get(digit, 0)
    
    GLOBAL_TRANSPOSE = 0
    
    midi_val = root + interval + octave_mod + accidental + GLOBAL_TRANSPOSE
    original_midi = midi_val
    is_transposed = False
        
    return {
        "type": "note",
        "digit": digit,
        "midi_value": midi_val,
        "original_midi": original_midi,
        "is_transposed": is_transposed,
        "duration": duration,
        "raw": sub_tok
    }

def parse_partitur_data(file_content: str) -> dict:
    metadata = {
        "T": "Unknown",
        "M": "4/4",
        "Q": 90,
        "K": "C",
        "beats_per_bar": 4.0,
        "denominator": 4
    }
    
    lines = file_content.split('\n')
    sections = []
    current_section_name = "UMUM"
    current_bar_count = 0
    
    # 1. Parse Metadata and Sections
    for line in lines:
        line = line.strip()
        if not line: continue
        if line.startswith('$'):
            current_section_name = line.replace('$', '').strip()
            sections.append({"name": current_section_name, "start_bar": current_bar_count, "end_bar": current_bar_count})
            continue
            
        if line.startswith('T:'):
            metadata["T"] = line.split(':', 1)[1].strip()
        elif line.startswith('Q:'):
            try:
                metadata["Q"] = int(line.split(':', 1)[1].strip())
            except: pass
        elif line.startswith('K:'):
            k_val = line.split(':', 1)[1].strip().upper()
            k_val = re.sub(r"[^A-Z#B]", "", k_val)
            metadata["K"] = k_val if k_val else "C"
        elif line.startswith('M:'):
            m_val = line.split(':', 1)[1].strip()
            metadata["M"] = m_val
            if '/' in m_val:
                try:
                    metadata["beats_per_bar"] = float(m_val.split('/')[0])
                    metadata["denominator"] = int(m_val.split('/')[1])
                except: pass
            else:
                try:
                    metadata["beats_per_bar"] = float(m_val)
                except: pass
        
        is_track = (line.startswith('V') or line.startswith('VB') or line.startswith('VA')) and ':' in line
        if is_track:
            parts = line.split(':', 1)
            tcontent = parts[1].strip()
            bars = [b.strip() for b in tcontent.split('|') if b.strip()]
            num_bars = len(bars)
            if not sections:
                sections.append({"name": current_section_name, "start_bar": 0, "end_bar": 0})
            sections[-1]["end_bar"] = max(sections[-1]["end_bar"], current_bar_count + num_bars - 1)
            if parts[0].strip() == 'V1':
                current_bar_count += num_bars
                
    # 2. Extract Tracks
    raw_tracks = {}
    for line in lines:
        line = line.strip()
        if not line: continue
        if line.startswith('$'): continue
        if ':' in line:
            parts = line.split(':', 1)
            prefix = parts[0].strip()
            content = parts[1].strip()
            
            if prefix.startswith('V') or prefix.startswith('VA'):
                if prefix not in raw_tracks:
                    raw_tracks[prefix] = []
                bars = [b.strip() for b in content.split('|') if b.strip()]
                raw_tracks[prefix].extend(bars)
                
    # 3. Tokenize & Parse
    parsed_tracks = {}
    key_sig = metadata["K"]
    subtoken_pattern = re.compile(r'(@[a-zA-Z0-9#]+[\-\=]*|[0-7\.a-zA-Z][^0-7\.@a-zA-Z]*)')
    
    for track_name, bars in raw_tracks.items():
        parsed_bars = []
        for bar_idx, bar_str in enumerate(bars):
            if bar_str.strip() == '%':
                if bar_idx > 0:
                    bar_str = bars[bar_idx - 1]
                else:
                    bar_str = "0"
            
            tokens = bar_str.split()
            parsed_subtokens = []
            
            for tok in tokens:
                matches = subtoken_pattern.findall(tok)
                for sub_tok in matches:
                    if sub_tok:
                        p_sub = parse_subtoken(sub_tok, key_sig)
                        if p_sub:
                            parsed_subtokens.append(p_sub)
                    
            parsed_bars.append({
                "bar_index": bar_idx,
                "tokens": parsed_subtokens
            })
        parsed_tracks[track_name] = parsed_bars
        
    return {
        "metadata": metadata,
        "tracks": parsed_tracks,
        "sections": sections
    }

ANGKLUNG1_PITCHES = [
    "g4", "a4", "a#4", "b4", "c5", "d5", "e5", "f5",
    "f#5", "g5", "a5", "a#5", "b5", "c6", "d6", "e6"
]

ANGKLUNG2_PITCHES = [
    "f4", "f#4", "g#4", "c#5", "d#5", "g#5", "c#6", "d#6",
    "f6", "f#6", "g6", "g#6", "a6", "a#6", "b6", "c7"
]

BASS_PITCHES = [
    "e3", "f3", "f#3", "g3", "g#3", "a3", "a#3", "b3",
    "c4", "c#4", "d4", "d#4", "e4", "f4", "f#4", "g4"
]

def midi_to_note_name(midi_num: int) -> str:
    names = ["c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "b"]
    octave = (midi_num // 12) - 1
    note_name = names[midi_num % 12]
    return f"{note_name}{octave}"

def resolve_chord_pitches(chord_symbol: str, key_sig: str, transpose: int = 0) -> list:
    key_roots = {
        "C": 60, "C#": 61, "DB": 61, "D": 62, "D#": 63, "EB": 63,
        "E": 64, "F": 65, "F#": 66, "GB": 66, "G": 67, "G#": 68,
        "AB": 68, "A": 69, "A#": 70, "BB": 70, "B": 71
    }
    
    symbol = chord_symbol.replace('@', '').strip()
    if not symbol:
        return []
        
    # Check if it starts with a numeral degree (1-7)
    if symbol[0].isdigit():
        root_midi = key_roots.get(key_sig.upper(), 60)
        
        # Accidentals: / raises by 1 semitone, \ lowers by 1 semitone
        accidental = 0
        accidental += symbol.count("/")
        accidental -= symbol.count("\\")
        
        clean_sym = symbol.replace("/", "").replace("\\", "")
        digit = ""
        for c in clean_sym:
            if c.isdigit():
                digit += c
        degree = int(digit) if digit else 1
        
        intervals = {1: 0, 2: 2, 3: 4, 4: 5, 5: 7, 6: 9, 7: 11}
        chord_root_midi = root_midi + intervals.get(degree, 0) + accidental
        
        is_minor = 'm' in clean_sym
        if not is_minor and not ('M' in symbol or 'maj' in symbol):
            if degree in [2, 3, 6, 7]:
                is_minor = True
                
        third_offset = 3 if is_minor else 4
        fifth_offset = 7
        
        midi_notes = [
            chord_root_midi + transpose,
            chord_root_midi + third_offset + transpose,
            chord_root_midi + fifth_offset + transpose
        ]
    else:
        # Standard alphabetical chord (e.g. C, Am, G7, F#m, Bb)
        match = re.match(r"^([A-Ga-g])([#b]?)", symbol)
        if not match:
            return []
        note_letter = match.group(1).upper()
        acc = match.group(2)
        
        chord_root_midi = key_roots.get(note_letter, 60)
        if acc == '#':
            chord_root_midi += 1
        elif acc == 'b':
            chord_root_midi -= 1
            
        modifier = symbol[len(note_letter) + len(acc):]
        is_minor = 'm' in modifier or 'min' in modifier
        
        third_offset = 3 if is_minor else 4
        fifth_offset = 7
        
        midi_notes = [
            chord_root_midi + transpose,
            chord_root_midi + third_offset + transpose,
            chord_root_midi + fifth_offset + transpose
        ]
        
    pitches = []
    for m in midi_notes:
        while m < 65:
            m += 12
        while m > 96:
            m -= 12
        pitches.append(midi_to_note_name(m))
    return pitches

def play_song_thread(file_content: str, thread_token: int):
    global song_playback_active, current_playback_token, global_v1_volume, global_v2_volume, global_vb_volume, global_va_volume
    
    try:
        parsed = parse_partitur_data(file_content)
        if not parsed["tracks"]:
            print("[PARSER] Tidak ada data musik yang ditemukan.")
            song_playback_active = False
            return
            
        bpm = parsed["metadata"]["Q"]
        key_sig = parsed["metadata"]["K"]
        beats_per_bar = parsed["metadata"]["beats_per_bar"]
        song_title = parsed["metadata"]["T"]
        print(f"[PARSER] Memulai pemutaran lagu. Tempo: {bpm} BPM, Nada Dasar: {key_sig}, Beats/Bar: {beats_per_bar}")
        
        # --- Auto-Transpose Logic ---
        min_midi = 999
        max_midi = 0
        for track_name, bars in parsed["tracks"].items():
            if track_name == 'V1':
                for bar in bars:
                    for tok in bar["tokens"]:
                        if tok["type"] == "note":
                            min_midi = min(min_midi, tok["midi_value"])
                            max_midi = max(max_midi, tok["midi_value"])
        
        auto_transpose = 0
        if min_midi < 999 and max_midi > 0:
            while min_midi + auto_transpose < 67:
                auto_transpose += 12
            while max_midi + auto_transpose > 88 and (min_midi + auto_transpose - 12) >= 67:
                auto_transpose -= 12
                
        if auto_transpose != 0:
            print(f"[PARSER] Auto-Transpose dinamis diterapkan: {auto_transpose} semitone.")
            for track_name, bars in parsed["tracks"].items():
                for bar in bars:
                    for tok in bar["tokens"]:
                        if tok["type"] == "note":
                            tok["midi_value"] += auto_transpose
        # ----------------------------
        
        events_by_time = {}
        def add_event(time_beat, action, track, data):
            time_beat = round(time_beat, 4)
            if time_beat not in events_by_time:
                events_by_time[time_beat] = []
            events_by_time[time_beat].append({"action": action, "track": track, "data": data})

        denominator = parsed["metadata"]["denominator"]
        seconds_per_beat = 60.0 / bpm
        if denominator == 8:
            seconds_per_beat /= 3.0
        gap_beats = 0.05 / seconds_per_beat

        def schedule_note_events(tok_start, total_duration, track_name, tok):
            # Check if this track is staccato
            is_staccato = False
            if track_name == 'V1' and global_v1_staccato:
                is_staccato = True
            elif track_name == 'VB' and global_vb_staccato:
                is_staccato = True
            elif track_name in ('VA', 'VA^') and global_va_staccato:
                is_staccato = True
            elif track_name not in ('V1', 'VB', 'VA', 'VA^', 'VD') and global_v2_staccato:
                is_staccato = True
                
            if is_staccato:
                # Staccato: play for only 30% of total duration (max 0.12 seconds, min 0.04 seconds)
                stac_sec = min(0.12, total_duration * seconds_per_beat * 0.3)
                stac_beats = stac_sec / seconds_per_beat
                actual_duration = max(0.04, stac_beats)
            else:
                # Legato (normal long note): play for full duration minus gap
                actual_duration = max(0.05, total_duration - gap_beats)
                
            tok_end = tok_start + actual_duration
            
            add_event(tok_start, "ON", track_name, tok)
            add_event(tok_end, "OFF", track_name, tok)
            
            # Tremolo is only triggered for normal legato notes that are long enough
            if not is_staccato:
                tremolo_interval_beats = 0.09 / seconds_per_beat
                if track_name in ('V1', 'VB') or (track_name.startswith('V') and track_name != 'VA' and track_name != 'VA^' and track_name != 'VD'):
                    hit_beat = tok_start + tremolo_interval_beats
                    while hit_beat < tok_end - (0.02 / seconds_per_beat):
                        add_event(hit_beat, "ARDUINO_HIT", track_name, tok)
                        hit_beat += tremolo_interval_beats

        for track_name, bars in parsed["tracks"].items():
            current_note_event = None
            
            for bar_idx, bar in enumerate(bars):
                bar_start_beat = bar_idx * beats_per_bar
                current_beat = bar_start_beat
                
                for tok in bar["tokens"]:
                    if tok["type"] == "sustain":
                        if current_note_event:
                            current_note_event["total_duration"] += tok["duration"]
                    elif tok["type"] == "rest":
                        if current_note_event:
                            schedule_note_events(
                                current_note_event["start_beat"], 
                                current_note_event["total_duration"], 
                                track_name, 
                                current_note_event["tok"]
                            )
                            current_note_event = None
                    else: # note, chord, drum
                        if current_note_event:
                            schedule_note_events(
                                current_note_event["start_beat"], 
                                current_note_event["total_duration"], 
                                track_name, 
                                current_note_event["tok"]
                            )
                        
                        current_note_event = {
                            "tok": tok,
                            "start_beat": current_beat,
                            "total_duration": tok["duration"]
                        }
                    
                    current_beat += tok["duration"]
                    
            if current_note_event:
                schedule_note_events(
                    current_note_event["start_beat"], 
                    current_note_event["total_duration"], 
                    track_name, 
                    current_note_event["tok"]
                )
                
        sorted_times = sorted(events_by_time.keys())
        max_beat = sorted_times[-1] if sorted_times else 0.0
        
        with current_playback_status_lock:
            current_playback_status["active"] = True
            current_playback_status["song_title"] = song_title
            current_playback_status["current_section"] = "INTRO"
            current_playback_status["current_bar"] = 1
            current_playback_status["total_bars"] = int(max_beat / beats_per_bar) + 1 if beats_per_bar > 0 else 1
            current_playback_status["elapsed_seconds"] = 0.0
            current_playback_status["total_seconds"] = round(max_beat * seconds_per_beat, 1)

        current_physical_notes_1 = set()
        current_physical_notes_3 = set()
        
        last_beat = 0.0
        event_idx = 0
        
        while event_idx < len(sorted_times):
            if not song_playback_active or thread_token != current_playback_token:
                break
                
            global seek_bar_index
            if seek_bar_index >= 0:
                seek_target_beat = seek_bar_index * beats_per_bar
                seek_bar_index = -1
                
                current_physical_notes_1.clear()
                current_physical_notes_3.clear()
                try:
                    send_to_arduino(0, 1)
                    send_to_arduino(0, 3)
                except:
                    pass
                
                while event_idx < len(sorted_times) and sorted_times[event_idx] < seek_target_beat:
                    event_idx += 1
                
                if event_idx < len(sorted_times):
                    last_beat = sorted_times[event_idx]
                continue
                
            beat_time = sorted_times[event_idx]
            wait_seconds = (beat_time - last_beat) * seconds_per_beat
            if wait_seconds > 0:
                time.sleep(wait_seconds)
                
            last_beat = beat_time
            
            current_bar = int(beat_time / beats_per_bar)
            elapsed_seconds = beat_time * seconds_per_beat
            
            active_sec = "UMUM"
            for sec in parsed["sections"]:
                if sec["start_bar"] <= current_bar <= sec["end_bar"]:
                    active_sec = sec["name"]
                    break
                    
            with current_playback_status_lock:
                current_playback_status["current_bar"] = current_bar + 1
                current_playback_status["elapsed_seconds"] = round(elapsed_seconds, 1)
                current_playback_status["current_section"] = active_sec
                
            arduino1_on_notes = []
            arduino3_on_notes = []
            
            for ev in events_by_time[beat_time]:
                action = ev["action"]
                track = ev["track"]
                tok = ev["data"]
                
                pitches_to_play = []
                if tok["type"] == "note":
                    midi_val = tok["midi_value"]
                    if track == 'VB':
                        while midi_val < 52: midi_val += 12
                        while midi_val > 67: midi_val -= 12
                        pitch = midi_to_note_name(midi_val)
                        pitches_to_play.append((pitch, "bass", False))
                    else:
                        while midi_val < 65: midi_val += 12
                        while midi_val > 96: midi_val -= 12
                        pitch = midi_to_note_name(midi_val)
                        if pitch in ANGKLUNG1_PITCHES:
                            pitches_to_play.append((pitch, "mel1", False))
                        elif pitch in ANGKLUNG2_PITCHES:
                            pitches_to_play.append((pitch, "mel2", False))
                            
                elif tok["type"] == "chord":
                    chord_pitches = resolve_chord_pitches(tok["chord_sym"], key_sig, transpose=auto_transpose)
                    for idx, pitch in enumerate(chord_pitches):
                        is_member = idx > 0
                        if pitch in ANGKLUNG1_PITCHES:
                            pitches_to_play.append((pitch, "mel1", is_member))
                        elif pitch in ANGKLUNG2_PITCHES:
                            pitches_to_play.append((pitch, "mel2", is_member))
                            
                elif tok["type"] == "drum":
                    note_num = 34 if tok["instrument"].lower() == 'x' else (35 if tok["instrument"].lower() == 'y' else 36)
                    if action == "ON":
                        play_local_sound(note_num, angklung_id=4, volume=0.10, instr_type="drum")
                    continue
                
                for pitch, ptype, is_chord_member in pitches_to_play:
                    if ptype == "bass":
                        note_num = BASS_PITCHES.index(pitch) + 1
                        physical_set = current_physical_notes_3
                        arduino_notes = arduino3_on_notes
                    elif ptype == "mel1":
                        note_num = ANGKLUNG1_PITCHES.index(pitch) + 1
                        physical_set = current_physical_notes_1
                        arduino_notes = arduino1_on_notes
                    else: # "mel2"
                        note_num = ANGKLUNG2_PITCHES.index(pitch) + 1 + 16
                        physical_set = current_physical_notes_1
                        arduino_notes = arduino1_on_notes
                        
                    if action == "ON":
                        if track == 'VB': vol = global_vb_volume
                        elif track == 'VA' or track == 'VA^': vol = global_va_volume
                        elif track == 'V1': vol = global_v1_volume
                        else: vol = global_v2_volume
                        
                        ang_id = 3 if ptype == "bass" else 1
                        play_local_sound(note_num, ang_id, vol, ptype)
                        
                        if not is_chord_member:
                            physical_set.add(note_num)
                            arduino_notes.append(note_num)
                            
                    elif action == "ARDUINO_HIT":
                        if note_num in physical_set:
                            arduino_notes.append(note_num)
                            
                    elif action == "OFF":
                        physical_set.discard(note_num)
            
            arduino1_on_notes = list(set(arduino1_on_notes))
            arduino3_on_notes = list(set(arduino3_on_notes))
            
            if arduino1_on_notes:
                send_to_arduino(arduino1_on_notes, 1, play_synth=False)
            if arduino3_on_notes:
                send_to_arduino(arduino3_on_notes, 3, play_synth=False)
                
            event_idx += 1
            
        print("[PARSER] Pemutaran lagu selesai.")
    except Exception as e:
        import traceback
        traceback.print_exc()
        print(f"[PARSER] Error fatal saat memainkan lagu: {e}")
    finally:
        song_playback_active = False
        try:
            send_to_arduino(0, 1)
            send_to_arduino(0, 3)
        except: pass

def read_file_safely(file_path: str) -> str:
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return f.read()
    except UnicodeDecodeError:
        with open(file_path, 'r', encoding='latin-1') as f:
            return f.read()

# Define absolute path to the songs directory relative to the script location
SONGS_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "songs")

@app.get("/api/songs")
def list_songs():
    import glob
    if not os.path.exists(SONGS_DIR):
        os.makedirs(SONGS_DIR)
        
    # Search recursively in all subdirectories for *.123 files
    song_files = glob.glob(os.path.join(SONGS_DIR, "**", "*.123"), recursive=True)
    results = []
    
    for file_path in song_files:
        file_basename = os.path.basename(file_path)
        title = file_basename.replace(".123", "").replace("_", " ")
        
        # Determine region from folder structure first (fallback)
        folder_name = os.path.basename(os.path.dirname(file_path))
        if folder_name and folder_name.lower() != "songs":
            region = folder_name.replace("_", " ").title()
        else:
            region = "Umum"
            
        duration_formatted = "0:00"
        try:
            content = read_file_safely(file_path)
            lines = content.split('\n')
            for line in lines[:15]:
                line = line.strip()
                if line.startswith('T:'):
                    title = line.split(':', 1)[1].strip()
                elif line.startswith('C:') or line.startswith('O:'):
                    region = line.split(':', 1)[1].strip()
            
            # Calculate duration using existing parser
            parsed = parse_partitur_data(content)
            if parsed["tracks"]:
                bpm = parsed["metadata"]["Q"]
                beats_per_bar = parsed["metadata"]["beats_per_bar"]
                denominator = parsed["metadata"]["denominator"]
                seconds_per_beat = 60.0 / bpm
                if denominator == 8:
                    seconds_per_beat /= 3.0
                
                max_beat = 0.0
                for track_name, bars in parsed["tracks"].items():
                    for bar_idx, bar in enumerate(bars):
                        bar_start = bar_idx * beats_per_bar
                        current_beat = bar_start
                        for tok in bar["tokens"]:
                            current_beat += tok["duration"]
                        max_beat = max(max_beat, current_beat)
                
                total_sec = int(round(max_beat * seconds_per_beat, 0))
                mins = total_sec // 60
                secs = total_sec % 60
                duration_formatted = f"{mins}:{secs:02d}"
        except Exception as e:
            print(f"Error reading metadata/duration from {file_basename}: {e}")
            
        # Get relative path relative to SONGS_DIR and make it web-safe
        rel_path = os.path.relpath(file_path, SONGS_DIR).replace(os.sep, '/')
        
        results.append({
            "id": rel_path,
            "title": title,
            "region": region,
            "file_name": rel_path,
            "folder": folder_name,
            "duration": duration_formatted
        })
    return results

@app.post("/api/arduino/play_song_file")
def play_song_file(data: dict):
    global song_playback_active
    file_name = data.get("file_name", "")
    if not file_name:
        raise HTTPException(status_code=400, detail="Nama file lagu tidak ditentukan.")
        
    # Resolve absolute path and block directory traversal attacks
    file_path = os.path.abspath(os.path.join(SONGS_DIR, file_name))
    if not file_path.lower().startswith(os.path.abspath(SONGS_DIR).lower()):
        raise HTTPException(status_code=400, detail="Akses file tidak diizinkan.")
        
    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="File lagu tidak ditemukan.")
        
    try:
        file_content = read_file_safely(file_path)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal membaca file: {e}")
        
    # Synchronously validate song notation structure before starting thread
    lines = file_content.split('\n')
    music_lines = []
    in_music_part = False
    for line in lines:
        line = line.strip()
        if not line:
            continue
        is_track = (line.startswith('V') or line.startswith('VB') or line.startswith('VA')) and ':' in line
        if is_track:
            in_music_part = True
        if in_music_part:
            if line.startswith('V') or line.startswith('VB') or line.startswith('VA'):
                music_lines.append(line)
                
    if not music_lines:
        raise HTTPException(status_code=400, detail="File lagu tidak valid atau tidak memiliki data notasi musik.")
        
    global song_playback_active, current_playback_thread, current_playback_token
    
    # 1. Stop existing thread if running
    song_playback_active = False
    current_playback_token += 1
    
    if current_playback_thread is not None and current_playback_thread.is_alive():
        current_playback_thread.join(timeout=1.5)
        
    song_playback_active = True
    current_playback_thread = threading.Thread(target=play_song_thread, args=(file_content, current_playback_token))
    current_playback_thread.daemon = True
    current_playback_thread.start()
    return {"status": "success", "message": f"Playback started for {file_name}."}

@app.get("/api/arduino/stop_song")
def stop_song():
    global song_playback_active, current_playback_token
    song_playback_active = False
    current_playback_token += 1
    
    # Send reset command (0) to turn off all solenoids on Board 1 and 3
    try:
        send_to_arduino(0, 1)
        send_to_arduino(0, 3)
    except Exception as e:
        print(f"[SERIAL] Gagal mengirim perintah reset ke Arduino: {e}")
        
    return {"status": "success", "message": "Song playback stopped."}

@app.get("/api/arduino/playback_status")
def get_playback_status():
    with current_playback_status_lock:
        return current_playback_status

@app.post("/api/arduino/seek_song")
def seek_song(data: dict):
    global seek_bar_index
    percent = data.get("percent", 0.0)
    percent = max(0.0, min(1.0, float(percent)))
    
    with current_playback_status_lock:
        total_bars = current_playback_status.get("total_bars", 0)
        
    if total_bars > 0:
        target_bar = int(percent * total_bars)
        seek_bar_index = max(0, min(total_bars - 1, target_bar))
        return {"status": "success", "seek_bar": seek_bar_index}
    else:
        raise HTTPException(status_code=400, detail="Tidak ada lagu yang sedang aktif diputar.")

@app.post("/api/arduino/volume")
def set_volume_settings(data: dict):
    global global_synth_volume, global_physical_power, global_v1_volume, global_v2_volume, global_vb_volume, global_va_volume
    global global_v1_staccato, global_v2_staccato, global_vb_staccato, global_va_staccato
    
    synth_vol = data.get("synth_volume", None)
    phys_power = data.get("physical_power", None)
    v1_vol = data.get("v1_volume", None)
    v2_vol = data.get("v2_volume", None)
    vb_vol = data.get("vb_volume", None)
    va_vol = data.get("va_volume", None)
    
    v1_stac = data.get("v1_staccato", None)
    v2_stac = data.get("v2_staccato", None)
    vb_stac = data.get("vb_staccato", None)
    va_stac = data.get("va_staccato", None)
    
    if synth_vol is not None:
        global_synth_volume = max(0.0, min(1.0, float(synth_vol)))
        
    if v1_vol is not None:
        global_v1_volume = max(0.0, min(1.0, float(v1_vol)))
    if v2_vol is not None:
        global_v2_volume = max(0.0, min(1.0, float(v2_vol)))
    if vb_vol is not None:
        global_vb_volume = max(0.0, min(1.0, float(vb_vol)))
    if va_vol is not None:
        global_va_volume = max(0.0, min(1.0, float(va_vol)))
        
    if v1_stac is not None:
        global_v1_staccato = bool(v1_stac)
    if v2_stac is not None:
        global_v2_staccato = bool(v2_stac)
    if vb_stac is not None:
        global_vb_staccato = bool(vb_stac)
    if va_stac is not None:
        global_va_staccato = bool(va_stac)
        
    if phys_power is not None:
        global_physical_power = max(10, min(100, int(phys_power)))
        
        # Calculate dynamic pulse width (durasiGetar) to cover the full physical range:
        # Mel/Chord (Arduino 1): 10% -> ~22ms, 100% -> 85ms (maksimal kencang)
        # Bass (Arduino 3): 10% -> ~17ms, 100% -> 60ms (maksimal kencang)
        duration_mel = int(15 + (global_physical_power / 100.0) * 70)
        duration_bass = int(12 + (global_physical_power / 100.0) * 48)
        
        # Send raw duration change command 'P<duration>' to Arduinos
        def update_arduinos():
            send_raw_command_to_arduino(f"P{duration_mel}", 1)
            send_raw_command_to_arduino(f"P{duration_bass}", 3)
            
        t = threading.Thread(target=update_arduinos)
        t.daemon = True
        t.start()
        
    return {
        "status": "success",
        "synth_volume": global_synth_volume,
        "physical_power": global_physical_power,
        "v1_volume": global_v1_volume,
        "v2_volume": global_v2_volume,
        "vb_volume": global_vb_volume,
        "va_volume": global_va_volume,
        "v1_staccato": global_v1_staccato,
        "v2_staccato": global_v2_staccato,
        "vb_staccato": global_vb_staccato,
        "va_staccato": global_va_staccato
    }

CLASS_REGION_FOLDERS = {
    "Adil": ["KALIMANTAN"],
    "Horas": ["BATAK"],
    "Kula Nuwun": ["JAWA"],
    "Peuhaba": ["ACEH"],
    "Sampurasun": ["SUNDA"],
    "Tabea": ["SULAWESI"],
    "Wawawa": ["PAPUA"]
}

def get_random_regional_song(predicted_class: str) -> dict:
    """Finds all .123 songs for the predicted regional class and selects a random one."""
    folders = CLASS_REGION_FOLDERS.get(predicted_class, [])
    candidate_songs = []
    
    for folder in folders:
        folder_path = os.path.join(SONGS_DIR, folder)
        if os.path.exists(folder_path):
            files = glob.glob(os.path.join(folder_path, "*.123"))
            candidate_songs.extend(files)
            
    if not candidate_songs:
        all_files = glob.glob(os.path.join(SONGS_DIR, "**", "*.123"), recursive=True)
        for f in all_files:
            rel_path = os.path.relpath(f, SONGS_DIR).replace('\\', '/')
            folder_name = rel_path.split('/')[0] if '/' in rel_path else ''
            if folder_name.upper() in [fol.upper() for fol in folders]:
                candidate_songs.append(f)
                
    if not candidate_songs:
        candidate_songs = glob.glob(os.path.join(SONGS_DIR, "**", "*.123"), recursive=True)
        
    if not candidate_songs:
        return None
        
    selected_path = random.choice(candidate_songs)
    rel_path = os.path.relpath(selected_path, SONGS_DIR).replace('\\', '/')
    
    title = os.path.basename(selected_path).replace(".123", "").replace("_", " ")
    try:
        content = read_file_safely(selected_path)
        for line in content.split('\n')[:15]:
            line = line.strip()
            if line.startswith('T:'):
                title = line.split(':', 1)[1].strip()
                break
    except:
        pass
        
    return {
        "file_name": rel_path,
        "title": title,
        "region": folders[0] if folders else predicted_class.upper()
    }

@app.post("/api/record-and-classify")
def record_and_classify():
    """Records 2.0 seconds of audio from the server's microphone and runs CRNN classification."""
    global model
    if model is None:
        init_model()
        if model is None:
            raise HTTPException(status_code=503, detail="Model CRNN belum dilatih atau tidak ditemukan.")
            
    try:
        rec_duration = 2.0
        print(f"[API-CRNN] Perekaman mic dimulai ({rec_duration} detik)...")
        recording = sd.rec(
            int(config.SAMPLE_RATE * rec_duration), 
            samplerate=config.SAMPLE_RATE, 
            channels=1, 
            dtype='float32'
        )
        sd.wait()
        print("[API-CRNN] Perekaman selesai. Menganalisis ucapan kata sapaan...")
        
        # CRNN Inference
        audio = recording.flatten()
        inputs = preprocess_audio_data(audio).to(device)
        with torch.no_grad():
            outputs = model(inputs)
            probabilities = torch.softmax(outputs, dim=1)[0]
            confidence, class_idx = torch.max(probabilities, 0)
            
            predicted_class = config.CLASSES[class_idx.item()]
            conf_val = confidence.item()
            
        print(f"[API-CRNN] Hasil Klasifikasi: {predicted_class} (Akurasi: {conf_val*100:.2f}%)")
        
        # Pick a random regional song matching the predicted greeting word
        song_info = get_random_regional_song(predicted_class)
        song_file = song_info["file_name"] if song_info else None
        song_title = song_info["title"] if song_info else predicted_class
        
        region_name = CLASS_REGION_FOLDERS.get(predicted_class, [predicted_class])[0]
        
        return {
            "status": "success",
            "predicted_class": predicted_class,
            "confidence": conf_val,
            "song": song_file,
            "song_title": song_title,
            "region": region_name
        }
    except Exception as e:
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Gagal melakukan perekaman/analisis CRNN: {e}")

@app.post("/api/classify-audio")
async def classify_audio(file: UploadFile = File(...)):
    """Receives an uploaded audio file from the client and runs CRNN classification."""
    if not HAS_AI:
        raise HTTPException(status_code=501, detail="AI classification is disabled on this machine")
    global model
    if model is None:
        init_model()
        if model is None:
            raise HTTPException(status_code=503, detail="Model CRNN belum dilatih atau tidak ditemukan.")
            
    try:
        temp_filename = "temp_upload.wav"
        with open(temp_filename, "wb") as buffer:
            buffer.write(await file.read())
            
        data, samplerate = sf.read(temp_filename)
        if os.path.exists(temp_filename):
            os.remove(temp_filename)
            
        if len(data.shape) > 1:
            data = data.mean(axis=1)
            
        if samplerate != config.SAMPLE_RATE:
            data = librosa.resample(data, orig_sr=samplerate, target_sr=config.SAMPLE_RATE)
            
        inputs = preprocess_audio_data(data).to(device)
        with torch.no_grad():
            outputs = model(inputs)
            probabilities = torch.softmax(outputs, dim=1)[0]
            confidence, class_idx = torch.max(probabilities, 0)
            
            predicted_class = config.CLASSES[class_idx.item()]
            conf_val = confidence.item()
            
        song_info = get_random_regional_song(predicted_class)
        song_file = song_info["file_name"] if song_info else None
        song_title = song_info["title"] if song_info else predicted_class
        
        region_name = CLASS_REGION_FOLDERS.get(predicted_class, [predicted_class])[0]
        
        return {
            "status": "success",
            "predicted_class": predicted_class,
            "confidence": conf_val,
            "song": song_file,
            "song_title": song_title,
            "region": region_name
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal memproses file audio CRNN: {e}")

@app.websocket("/ws/pitch")
async def pitch_websocket(websocket: WebSocket):
    """Streams real-time pitch detection from the server's microphone to the client."""
    if not HAS_AI:
        await websocket.accept()
        await websocket.send_json({"error": "Pitch streaming is disabled on this machine (missing PyTorch/SoundDevice)"})
        await websocket.close()
        return
    await websocket.accept()
    print("[WS] Klien terhubung ke WebSocket Pitch.")
    
    # Audio settings for streaming (1024 samples @ 16kHz = 64ms per frame)
    chunk_size = 1024
    sample_rate = 16000
    frame_dur_ms = round((chunk_size / sample_rate) * 1000.0, 1)
    
    loop = asyncio.get_event_loop()
    
    # Queue for passing audio blocks from the sounddevice thread
    audio_queue = asyncio.Queue()
    
    def audio_callback(indata, frames, time_info, status):
        if status:
            print(f"[WS-Audio] Status error: {status}")
        # Put raw audio data into the queue
        loop.call_soon_threadsafe(audio_queue.put_nowait, indata.copy())

    # Start sounddevice input stream
    stream = sd.InputStream(
        channels=1,
        samplerate=sample_rate,
        blocksize=chunk_size,
        callback=audio_callback
    )
    
    stream.start()
    
    try:
        while True:
            # Get block from queue
            indata = await audio_queue.get()
            
            # Detect pitch with confidence score & voiced flag
            sig = indata.flatten()
            rms_val = float(np.sqrt(np.mean(sig**2)))
            freq, confidence = detect_pitch_with_confidence(sig, sample_rate)
            voiced = confidence >= 0.25 and freq >= 75.0 and freq <= 850.0
            
            note_str, scale_deg, midi_num = pitch_hz_to_scale_degree(freq) if voiced else (None, None, None)
            
            # Cents deviation calculation relative to 440Hz A4
            cents_dev = 0.0
            if freq > 0:
                import math
                exact_midi = 69.0 + 12.0 * math.log2(freq / 440.0)
                nearest_midi = round(exact_midi)
                cents_dev = round((exact_midi - nearest_midi) * 100.0, 1)

            # Send enriched payload for Monophonic Vocal Transcription
            payload = {
                "frequency": float(round(freq, 2)),
                "confidence": float(confidence),
                "voiced": voiced,
                "rms": round(rms_val, 4),
                "cents_dev": float(cents_dev),
                "note": note_str,
                "scale_degree": scale_deg,
                "frame_duration_ms": frame_dur_ms
            }
            await websocket.send_json(payload)
            
    except WebSocketDisconnect:
        print("[WS] Klien terputus dari WebSocket Pitch.")
    except Exception as e:
        print(f"[WS] Error di WebSocket: {e}")
    finally:
        stream.stop()
        stream.close()
# MIDI Global State
midi_listener_active = False
midi_thread = None
connected_midi_device_id = None
connected_midi_device_name = ""
active_midi_websockets = []
main_event_loop = None

@app.on_event("startup")
def startup_event():
    global main_event_loop
    main_event_loop = asyncio.get_event_loop()
    init_midi()

def broadcast_midi_event(note_num: int, angklung_id: int, action: str):
    global main_event_loop
    if not active_midi_websockets or main_event_loop is None:
        return
    payload = {"note": note_num, "angklung": angklung_id, "action": action}
    for ws in list(active_midi_websockets):
        try:
            asyncio.run_coroutine_threadsafe(ws.send_json(payload), main_event_loop)
        except Exception as e:
            pass

# Active notes dictionary to track pressed notes and their velocities/boards
# Format: { (note_num, angklung_id): timestamp }
active_midi_notes = {}
active_midi_notes_lock = threading.Lock()
midi_repeater_thread = None
midi_repeater_active = False

def midi_repeater_loop():
    global midi_repeater_active
    while midi_repeater_active:
        with active_midi_notes_lock:
            if not active_midi_notes:
                time.sleep(0.01)
                continue
                
            # Group active notes by board
            board_notes = {1: [], 2: [], 3: []}
            for (note_num, angklung_id) in list(active_midi_notes.keys()):
                board_notes[angklung_id].append(note_num)
                
        # Send active notes to respective Arduinos
        for angklung_id, notes in board_notes.items():
            if notes:
                send_to_arduino(notes, angklung_id, play_synth=False)
                
        # Wait slightly longer than durasiGetar (85ms) for continuous vibration
        time.sleep(0.09)

def start_midi_repeater():
    global midi_repeater_active, midi_repeater_thread
    if not midi_repeater_active:
        midi_repeater_active = True
        midi_repeater_thread = threading.Thread(target=midi_repeater_loop)
        midi_repeater_thread.daemon = True
        midi_repeater_thread.start()

def stop_midi_repeater():
    global midi_repeater_active, midi_repeater_thread
    midi_repeater_active = False
    if midi_repeater_thread is not None:
        try:
            midi_repeater_thread.join(timeout=0.5)
        except:
            pass
        midi_repeater_thread = None

def init_midi():
    global HAS_MIDI
    try:
        import pygame.midi
        if not pygame.midi.get_init():
            pygame.midi.init()
            print("[MIDI] Pygame MIDI berhasil diinisialisasi secara malas (lazy-init).")
        HAS_MIDI = True
        return True
    except Exception as e:
        print(f"[MIDI] Gagal menginisialisasi Pygame MIDI: {e}")
        HAS_MIDI = False
        return False

def midi_listener_loop(device_id: int):
    global midi_listener_active
    if not init_midi():
        print("[MIDI] Pygame MIDI tidak tersedia pada sistem ini.")
        midi_listener_active = False
        return
        
    try:
        input_device = pygame.midi.Input(device_id)
        print(f"[MIDI] Terhubung ke perangkat MIDI ID {device_id}")
    except Exception as e:
        print(f"[MIDI] Gagal membuka perangkat MIDI: {e}")
        midi_listener_active = False
        return

    start_midi_repeater()

    while midi_listener_active:
        try:
            if input_device.poll():
                events = input_device.read(10)
                
                for event in events:
                    status = event[0][0]
                    note = event[0][1]
                    velocity = event[0][2]
                    
                    is_note_on = (status & 0xF0) == 0x90 and velocity > 0
                    is_note_off = ((status & 0xF0) == 0x80) or ((status & 0xF0) == 0x90 and velocity == 0)
                    
                    if is_note_on or is_note_off:
                        vol = velocity / 127.0
                        
                        resolved_note = None
                        resolved_board = None
                        
                        for octave_shift in [0, 12, -12, 24, -24]:
                            shifted_note = note + octave_shift
                            shifted_name = midi_to_note_name(shifted_note)
                            if shifted_name in ANGKLUNG1_PITCHES:
                                resolved_note = ANGKLUNG1_PITCHES.index(shifted_name) + 1
                                resolved_board = 1
                                break
                            elif shifted_name in ANGKLUNG2_PITCHES:
                                resolved_note = ANGKLUNG2_PITCHES.index(shifted_name) + 1
                                resolved_board = 2
                                break
                            elif shifted_name in BASS_PITCHES:
                                resolved_note = BASS_PITCHES.index(shifted_name) + 1
                                resolved_board = 3
                                break
                                
                        if resolved_note is not None and resolved_board is not None:
                            if is_note_on:
                                play_local_sound(resolved_note, resolved_board, vol, "melody" if resolved_board != 3 else "bass")
                                with active_midi_notes_lock:
                                    active_midi_notes[(resolved_note, resolved_board)] = time.time()
                                broadcast_midi_event(resolved_note, resolved_board, "down")
                            elif is_note_off:
                                with active_midi_notes_lock:
                                    active_midi_notes.pop((resolved_note, resolved_board), None)
                                broadcast_midi_event(resolved_note, resolved_board, "up")
                                
            time.sleep(0.005)
        except Exception as e:
            print(f"[MIDI] Error pada loop MIDI: {e}")
            break
            
    stop_midi_repeater()
    try:
        input_device.close()
    except Exception as e:
        print(f"[MIDI] Gagal menutup input device: {e}")
    print("[MIDI] Sambungan MIDI ditutup.")


@app.get("/api/midi/devices")
def get_midi_devices():
    if not init_midi():
        return []
        
    # Re-initialize pygame.midi to query OS for newly plugged-in USB MIDI devices (hot-plug refresh)
    # Only allowed when no active stream is currently reading to prevent active connection crashes
    if not midi_listener_active:
        try:
            pygame.midi.quit()
            pygame.midi.init()
        except:
            pass
            
    devices = []
    try:
        for i in range(pygame.midi.get_count()):
            info = pygame.midi.get_device_info(i)
            if info[2] == 1: # input device
                devices.append({
                    "id": i,
                    "interface": info[0].decode('utf-8', errors='ignore'),
                    "name": info[1].decode('utf-8', errors='ignore'),
                    "opened": info[4]
                })
    except Exception as e:
        print(f"[MIDI] Gagal mengambil daftar MIDI: {e}")
    return devices

@app.post("/api/midi/connect")
def connect_midi(data: dict):
    global midi_listener_active, midi_thread, connected_midi_device_id, connected_midi_device_name
    if not init_midi():
        raise HTTPException(status_code=503, detail="Pygame MIDI tidak tersedia pada sistem ini.")
        
    device_id = data.get("device_id")
    if device_id is None:
        raise HTTPException(status_code=400, detail="ID perangkat MIDI tidak ditentukan.")
        
    # Disconnect existing
    midi_listener_active = False
    if midi_thread is not None:
        midi_thread.join(timeout=1.0)
        
    dev_name = "Perangkat MIDI"
    try:
        info = pygame.midi.get_device_info(device_id)
        if info:
            dev_name = info[1].decode('utf-8', errors='ignore')
    except:
        pass
    
    connected_midi_device_id = device_id
    connected_midi_device_name = dev_name
    
    midi_listener_active = True
    midi_thread = threading.Thread(target=midi_listener_loop, args=(device_id,))
    midi_thread.daemon = True
    midi_thread.start()
    return {"status": "success", "message": f"Mencoba menyambung ke {dev_name}."}

@app.post("/api/midi/disconnect")
def disconnect_midi():
    global midi_listener_active, midi_thread, connected_midi_device_id, connected_midi_device_name
    midi_listener_active = False
    if midi_thread is not None:
        midi_thread.join(timeout=1.0)
        midi_thread = None
    connected_midi_device_id = None
    connected_midi_device_name = ""
    return {"status": "success", "message": "Koneksi MIDI diputus."}

@app.get("/api/midi/status")
def get_midi_status():
    global midi_listener_active, midi_thread, connected_midi_device_id, connected_midi_device_name
    return {
        "active": midi_listener_active and midi_thread is not None and midi_thread.is_alive(),
        "device_id": connected_midi_device_id,
        "device_name": connected_midi_device_name
    }

@app.websocket("/ws/midi")
async def midi_websocket(websocket: WebSocket):
    await websocket.accept()
    active_midi_websockets.append(websocket)
    print(f"[WS-MIDI] Klien terhubung. Total: {len(active_midi_websockets)}")
    try:
        while True:
            await websocket.receive_text()
    except WebSocketDisconnect:
        pass
    finally:
        if websocket in active_midi_websockets:
            active_midi_websockets.remove(websocket)
        print(f"[WS-MIDI] Klien terputus. Sisa: {len(active_midi_websockets)}")



if __name__ == "__main__":
    uvicorn.run("src.api:app", host="0.0.0.0", port=8000, reload=True)

