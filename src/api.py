import os
import socket
import serial
import serial.tools.list_ports
import pygame
import threading
import time
import asyncio
import numpy as np
import sounddevice as sd
import torch
import librosa
import soundfile as sf
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.websockets import WebSocket, WebSocketDisconnect
import uvicorn
import src.config as config
from src.model import AudioCNN

app = FastAPI(title="Angklung AI & Pitch Backend")

# Enable CORS for Flutter Web client access
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load CNN Model
device = torch.device("cpu")
model = None

def init_model():
    global model
    if os.path.exists(config.MODEL_SAVE_PATH):
        try:
            model = AudioCNN(num_classes=len(config.CLASSES)).to(device)
            model.load_state_dict(torch.load(config.MODEL_SAVE_PATH, map_location=device))
            model.eval()
            print("[MODEL] Model PyTorch berhasil dimuat.")
        except Exception as e:
            print(f"[MODEL] Gagal memuat model: {e}")
    else:
        print(f"[WARNING] File model '{config.MODEL_SAVE_PATH}' belum ada. Silakan lakukan training.")

init_model()

# Frequency to Note mapping helper
NOTE_FREQS = {
    "C4": 261.63,
    "D4": 293.66,
    "E4": 329.63,
    "F4": 349.23,
    "G4": 392.00,
    "A4": 440.00,
    "B4": 493.88,
    "C5": 523.25,
}

def frequency_to_note(freq):
    if freq < 200 or freq > 600:
        return None
    closest_note = None
    min_diff = float("inf")
    for note, note_freq in NOTE_FREQS.items():
        diff = abs(freq - note_freq)
        if diff < min_diff:
            min_diff = diff
            closest_note = note
    # Return note only if the diff is within reasonable semitone bounds (~15Hz-30Hz)
    if min_diff < 15.0:
        return closest_note
    return None

def detect_pitch(signal, sr):
    """Simple Autocorrelation Pitch Detector for real-time monophonic pitch tracking."""
    if len(signal) == 0:
        return 0.0
    signal = signal - np.mean(signal)
    
    # Avoid zero signal
    if np.max(np.abs(signal)) < 0.01:
        return 0.0
        
    corr = np.correlate(signal, signal, mode='full')
    corr = corr[len(corr)//2:]
    
    # Range of interest (80 Hz to 1000 Hz)
    min_lag = int(sr / 1000)
    max_lag = int(sr / 80)
    
    if max_lag >= len(corr) or min_lag >= len(corr):
        return 0.0
        
    search_segment = corr[min_lag:max_lag]
    if len(search_segment) == 0:
        return 0.0
        
    peak = np.argmax(search_segment) + min_lag
    
    # Thresholding to reject noisy frames
    if corr[peak] < 0.15 * corr[0]:
        return 0.0
        
    freq = sr / peak
    return freq

def preprocess_audio_data(y):
    """Pads/crops audio array to config.NUM_SAMPLES and extracts MFCC."""
    # Ensure audio length is exactly NUM_SAMPLES
    if len(y) < config.NUM_SAMPLES:
        y = np.pad(y, (0, config.NUM_SAMPLES - len(y)), mode='constant')
    elif len(y) > config.NUM_SAMPLES:
        y = y[:config.NUM_SAMPLES]
        
    # Extract MFCC
    mfcc = librosa.feature.mfcc(
        y=y, 
        sr=config.SAMPLE_RATE, 
        n_mfcc=config.N_MFCC, 
        n_fft=config.N_FFT, 
        hop_length=config.HOP_LENGTH
    )
    mfcc = np.expand_dims(mfcc, axis=0) # Add channel
    mfcc = np.expand_dims(mfcc, axis=0) # Add batch
    return torch.tensor(mfcc, dtype=torch.float32)

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

pygame_mixer_initialized = False

def init_pygame_mixer():
    global pygame_mixer_initialized
    if pygame_mixer_initialized:
        return True
    try:
        pygame.mixer.init()
        pygame_mixer_initialized = True
        print("[AUDIO] Pygame mixer berhasil diaktifkan!")
        return True
    except Exception as e:
        print(f"[AUDIO] Gagal mengaktifkan pygame mixer: {e}")
        return False

def generate_angklung_sound(frequency: float, duration: float = 1.2, sr: int = 44100):
    t = np.linspace(0, duration, int(sr * duration), endpoint=False)
    
    f1 = frequency
    f2 = frequency * 2.0
    f3 = frequency * 3.0
    
    env1 = np.exp(-3.5 * t)
    env2 = np.exp(-2.2 * t)
    env3 = np.exp(-5.5 * t)
    
    tone1 = np.sin(2.0 * np.pi * f1 * t) * env1 * 0.5
    tone2 = np.sin(2.0 * np.pi * f2 * t) * env2 * 0.4
    tone3 = np.sin(2.0 * np.pi * f3 * t) * env3 * 0.1
    
    signal = tone1 + tone2 + tone3
    
    click_len = int(sr * 0.02)
    click = (np.random.rand(click_len) - 0.5) * np.exp(-np.linspace(0, 4.0, click_len)) * 0.25
    signal[:click_len] += click
    
    max_val = np.max(np.abs(signal))
    if max_val > 0:
        signal = signal / max_val
        
    stereo_signal = np.column_stack((signal, signal))
    return (stereo_signal * 32767).astype(np.int16)

def play_synth_note_async(note_num: int, angklung_id: int):
    if not init_pygame_mixer():
        return
    try:
        freq_map = NOTE_FREQUENCIES.get(angklung_id, NOTE_FREQUENCIES[3])
        freq = freq_map.get(note_num, 261.63)
        pcm_data = generate_angklung_sound(freq)
        
        sound = pygame.sndarray.make_sound(pcm_data)
        sound.play()
    except Exception as e:
        print(f"[AUDIO] Gagal memainkan suara lokal: {e}")

def play_local_sound(note_num: int, angklung_id: int = 3):
    t = threading.Thread(target=play_synth_note_async, args=(note_num, angklung_id))
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

def get_arduino_connection(angklung_id: int):
    global arduino_serials, SERIAL_PORTS, BAUD_RATE
    if angklung_id not in arduino_serials:
        angklung_id = 3
        
    ser = arduino_serials[angklung_id]
    if ser is not None and ser.is_open:
        return ser
        
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

def send_to_arduino(note_num, angklung_id: int = 3):
    if isinstance(note_num, int):
        notes_list = [note_num]
    elif isinstance(note_num, str):
        notes_list = [int(x) for x in note_num.split(",") if x.strip().isdigit()]
    elif isinstance(note_num, list) or isinstance(note_num, tuple):
        notes_list = [int(x) for x in note_num]
    else:
        notes_list = []

    for n in notes_list:
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
    try:
        ser.reset_input_buffer()
        ser.write(f"{payload}\n".encode('utf-8'))
        response = ser.readline().decode('utf-8').strip()
        if not response:
            response = ser.readline().decode('utf-8').strip()
        return True, response if response else f"Sent {payload} to Arduino {target_id}"
    except Exception as e:
        print(f"[SERIAL] Gagal kirim nada {payload} ke Angklung {target_id}: {e}")
        try:
            ser.close()
        except:
            pass
        arduino_serials[target_id] = None
        notes_str = ",".join(str(x) for x in notes_list)
        return True, f"Error Serial ({e}) - Dimainkan di Laptop (Nada: {notes_str})"

@app.post("/api/config-arduino")
def config_arduino(data: dict):
    global SERIAL_PORTS, arduino_serials
    port1 = data.get("port1", SERIAL_PORTS[1])
    port2 = data.get("port2", SERIAL_PORTS[2])
    port3 = data.get("port3", SERIAL_PORTS[3])
    
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
        
    print(f"[API] Update ports serial -> Angklung1: {SERIAL_PORTS[1]}, Angklung2: {SERIAL_PORTS[2]}, Angklung3: {SERIAL_PORTS[3]}")
    return {"status": "success", "ports": SERIAL_PORTS}

@app.get("/api/arduino/status")
def arduino_status():
    global arduino_serials, SERIAL_PORTS
    status_res = {}
    for i in [1, 2, 3]:
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

def play_song_thread(file_content: str):
    global song_playback_active
    
    # 1. Parse Metadata
    bpm = 90
    key_sig = "F"
    lines = file_content.split('\n')
    
    music_lines = []
    in_music_part = False
    
    for line in lines:
        line = line.strip()
        if not line:
            continue
        if line.startswith('$ Music part'):
            in_music_part = True
            continue
        if not in_music_part:
            # Parse header
            if line.startswith('Q:'):
                try:
                    bpm = int(line.split(':')[1].strip())
                except:
                    pass
            elif line.startswith('K:'):
                key_sig = line.split(':')[1].strip().upper()
        else:
            # We are in the music section
            if line.startswith('V') or line.startswith('VB') or line.startswith('VA'):
                music_lines.append(line)

    if not music_lines:
        print("[PARSER] Tidak ada data musik yang ditemukan.")
        song_playback_active = False
        return
        
    print(f"[PARSER] Memulai pemutaran lagu. Tempo: {bpm} BPM, Nada Dasar: {key_sig}")
    
    # 2. Group notes by bars
    tracks = {}
    for m_line in music_lines:
        parts = m_line.split(':', 1)
        if len(parts) != 2:
            continue
        track_name = parts[0].strip()
        track_content = parts[1].strip()
        
        # Split into bars using '|'
        bars = [b.strip() for b in track_content.split('|') if b.strip()]
        if track_name not in tracks:
            tracks[track_name] = []
        tracks[track_name].extend(bars)
        
    if not tracks:
        song_playback_active = False
        return
        
    max_bars = max(len(bars) for bars in tracks.values())
    
    # Time calculations: Sub-beat duration = (60 / BPM) / 2 = 30 / BPM seconds.
    sub_beat_duration = 30.0 / bpm
    
    # 3. Main Playback Loop
    for bar_idx in range(max_bars):
        if not song_playback_active:
            break
            
        bar_steps_a1 = [[] for _ in range(8)]
        bar_steps_a2 = [[] for _ in range(8)]
        bar_steps_a3 = [[] for _ in range(8)]
        
        for track_name, bars in tracks.items():
            if bar_idx >= len(bars):
                continue
            bar_str = bars[bar_idx]
            
            cleaned_parts = [p for p in bar_str.split() if p != '-']
            num_parts = len(cleaned_parts)
            
            if num_parts == 0:
                continue
                
            for i, token in enumerate(cleaned_parts):
                if token == '.' or token == '0':
                    continue
                step_idx = int((i / num_parts) * 8)
                if step_idx < 8:
                    # Distribute channels across 3 different physical Angklungs:
                    if track_name == 'V1':
                        bar_steps_a1[step_idx].append(token)
                    elif track_name in ['V2', 'V3', 'VA']:
                        bar_steps_a2[step_idx].append(token)
                    elif track_name == 'VB':
                        bar_steps_a3[step_idx].append(token)
                    else:
                        bar_steps_a2[step_idx].append(token)
                        
        for step_idx in range(8):
            if not song_playback_active:
                break
                
            # Collect all notes for simultaneous playback in this step
            arduino1_notes = []
            arduino3_notes = []
            
            # 1. Gather Angklung 1 (High Melody) notes
            for token in bar_steps_a1[step_idx]:
                midi_val = doremi_to_midi(token, key_sig)
                pitch = midi_to_note_name(midi_val)
                if pitch in ANGKLUNG1_PITCHES:
                    arduino1_notes.append(ANGKLUNG1_PITCHES.index(pitch) + 1)
                elif pitch in ANGKLUNG2_PITCHES:
                    arduino1_notes.append(ANGKLUNG2_PITCHES.index(pitch) + 1 + 16)
                    
            # 2. Gather Angklung 2 (Medium Melody & Chords) notes
            for token in bar_steps_a2[step_idx]:
                if token.startswith('@'):
                    for pitch in resolve_chord_pitches(token, key_sig):
                        if pitch in ANGKLUNG1_PITCHES:
                            arduino1_notes.append(ANGKLUNG1_PITCHES.index(pitch) + 1)
                        elif pitch in ANGKLUNG2_PITCHES:
                            arduino1_notes.append(ANGKLUNG2_PITCHES.index(pitch) + 1 + 16)
                else:
                    midi_val = doremi_to_midi(token, key_sig)
                    pitch = midi_to_note_name(midi_val)
                    if pitch in ANGKLUNG1_PITCHES:
                        arduino1_notes.append(ANGKLUNG1_PITCHES.index(pitch) + 1)
                    elif pitch in ANGKLUNG2_PITCHES:
                        arduino1_notes.append(ANGKLUNG2_PITCHES.index(pitch) + 1 + 16)
                        
            # 3. Gather Angklung 3 (Low Bass) notes
            for token in bar_steps_a3[step_idx]:
                midi_val = doremi_to_midi(token, key_sig)
                pitch = midi_to_note_name(midi_val)
                if pitch in BASS_PITCHES:
                    arduino3_notes.append(BASS_PITCHES.index(pitch) + 1)
                    
            # Remove duplicates
            arduino1_notes = list(set(arduino1_notes))
            arduino3_notes = list(set(arduino3_notes))
            
            # Play in parallel if there are active notes
            if arduino1_notes or arduino3_notes:
                t1 = threading.Thread(target=send_to_arduino, args=(arduino1_notes, 1))
                t3 = threading.Thread(target=send_to_arduino, args=(arduino3_notes, 3))
                t1.start()
                t3.start()
                t1.join()
                t3.join()
                
            time.sleep(sub_beat_duration)

    print("[PARSER] Pemutaran lagu selesai.")
    song_playback_active = False

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

def doremi_to_midi(token: str, key_sig: str) -> int:
    key_roots = {
        "C": 60, "C#": 61, "DB": 61, "D": 62, "D#": 63, "EB": 63,
        "E": 64, "F": 65, "F#": 66, "GB": 66, "G": 67, "G#": 68,
        "AB": 68, "A": 69, "A#": 70, "BB": 70, "B": 71
    }
    root = key_roots.get(key_sig.upper(), 60)
    
    digit = ""
    for c in token:
        if c.isdigit():
            digit += c
    if not digit:
        return 0
    val = int(digit)
    
    intervals = {1: 0, 2: 2, 3: 4, 4: 5, 5: 7, 6: 9, 7: 11}
    interval = intervals.get(val, 0)
    
    octave_mod = 0
    octave_mod += token.count("'") * 12
    octave_mod -= token.count(",") * 12
    octave_mod -= token.count(";") * 24
    
    return root + interval + octave_mod

def midi_to_note_name(midi_num: int) -> str:
    names = ["c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "b"]
    octave = (midi_num // 12) - 1
    note_name = names[midi_num % 12]
    return f"{note_name}{octave}"

def resolve_chord_pitches(chord_symbol: str, key_sig: str) -> list:
    key_roots = {
        "C": 60, "C#": 61, "DB": 61, "D": 62, "D#": 63, "EB": 63,
        "E": 64, "F": 65, "F#": 66, "GB": 66, "G": 67, "G#": 68,
        "AB": 68, "A": 69, "A#": 70, "BB": 70, "B": 71
    }
    root_midi = key_roots.get(key_sig.upper(), 60)
    
    symbol = chord_symbol.replace('@', '')
    digit = ""
    for c in symbol:
        if c.isdigit():
            digit += c
    if not digit:
        return []
    degree = int(digit)
    
    intervals = {1: 0, 2: 2, 3: 4, 4: 5, 5: 7, 6: 9, 7: 11}
    chord_root_midi = root_midi + intervals.get(degree, 0)
    
    is_minor = 'm' in symbol
    third_offset = 3 if is_minor else 4
    fifth_offset = 7
    
    midi_notes = [
        chord_root_midi,
        chord_root_midi + third_offset,
        chord_root_midi + fifth_offset
    ]
    
    pitches = []
    for m in midi_notes:
        while m < 65: # f4 is midi 65
            m += 12
        while m > 96: # c7 is midi 96
            m -= 12
        pitches.append(midi_to_note_name(m))
    return pitches

@app.get("/api/songs")
def list_songs():
    import glob
    songs_dir = os.path.join(os.getcwd(), "songs")
    if not os.path.exists(songs_dir):
        os.makedirs(songs_dir)
        
    song_files = glob.glob(os.path.join(songs_dir, "*.123"))
    results = []
    
    for file_path in song_files:
        file_name = os.path.basename(file_path)
        title = file_name.replace(".123", "").replace("_", " ")
        region = "Umum"
        
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                for _ in range(15):
                    line = f.readline()
                    if not line:
                        break
                    line = line.strip()
                    if line.startswith('T:'):
                        title = line.split(':', 1)[1].strip()
                    elif line.startswith('C:'):
                        region = line.split(':', 1)[1].strip()
        except Exception as e:
            print(f"Error reading metadata from {file_name}: {e}")
            
        results.append({
            "id": file_name,
            "title": title,
            "region": region,
            "file_name": file_name
        })
    return results

@app.post("/api/arduino/play_song_file")
def play_song_file(data: dict):
    global song_playback_active
    file_name = data.get("file_name", "")
    if not file_name:
        raise HTTPException(status_code=400, detail="Nama file lagu tidak ditentukan.")
        
    file_path = os.path.join(os.getcwd(), "songs", file_name)
    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="File lagu tidak ditemukan.")
        
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            file_content = f.read()
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal membaca file: {e}")
        
    song_playback_active = False
    time.sleep(0.3)
    
    song_playback_active = True
    t = threading.Thread(target=play_song_thread, args=(file_content,))
    t.daemon = True
    t.start()
    return {"status": "success", "message": f"Playback started for {file_name}."}

@app.get("/api/arduino/stop_song")
def stop_song():
    global song_playback_active
    song_playback_active = False
    return {"status": "success", "message": "Song playback stopped."}

@app.post("/api/record-and-classify")
def record_and_classify():
    """Records 1.5 seconds of audio from the server's microphone and runs classification."""
    global model
    if model is None:
        init_model()
        if model is None:
            raise HTTPException(status_code=503, detail="Model belum dilatih atau tidak ditemukan.")
            
    try:
        print("[API] Perekaman dimulai (1.5 detik)...")
        recording = sd.rec(
            int(config.NUM_SAMPLES), 
            samplerate=config.SAMPLE_RATE, 
            channels=1, 
            dtype='float32'
        )
        sd.wait()
        print("[API] Perekaman selesai. Menganalisis...")
        
        # Inference
        inputs = preprocess_audio_data(recording.flatten()).to(device)
        with torch.no_grad():
            outputs = model(inputs)
            probabilities = torch.softmax(outputs, dim=1)[0]
            confidence, class_idx = torch.max(probabilities, 0)
            
            predicted_class = config.CLASSES[class_idx.item()]
            conf_val = confidence.item()
            
        print(f"[API] Hasil: {predicted_class} ({conf_val:.2f})")
        
        song = config.SONG_MAP.get(predicted_class, None)
        
        return {
            "status": "success",
            "predicted_class": predicted_class,
            "confidence": conf_val,
            "song": song,
            "region": predicted_class.upper() if predicted_class in config.SONG_MAP else "UNKNOWN"
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal melakukan perekaman/analisis: {e}")

@app.post("/api/classify-audio")
async def classify_audio(file: UploadFile = File(...)):
    """Receives an uploaded audio file from the Flutter client and runs classification."""
    global model
    if model is None:
        init_model()
        if model is None:
            raise HTTPException(status_code=503, detail="Model belum dilatih atau tidak ditemukan.")
            
    try:
        # Save temp file
        temp_filename = "temp_upload.wav"
        with open(temp_filename, "wb") as buffer:
            buffer.write(await file.read())
            
        # Load audio file using soundfile
        data, samplerate = sf.read(temp_filename)
        os.remove(temp_filename)
        
        # Convert to mono if stereo
        if len(data.shape) > 1:
            data = data.mean(axis=1)
            
        # Resample if needed
        if samplerate != config.SAMPLE_RATE:
            data = librosa.resample(data, orig_sr=samplerate, target_sr=config.SAMPLE_RATE)
            
        # Inference
        inputs = preprocess_audio_data(data).to(device)
        with torch.no_grad():
            outputs = model(inputs)
            probabilities = torch.softmax(outputs, dim=1)[0]
            confidence, class_idx = torch.max(probabilities, 0)
            
            predicted_class = config.CLASSES[class_idx.item()]
            conf_val = confidence.item()
            
        song = config.SONG_MAP.get(predicted_class, None)
        
        return {
            "status": "success",
            "predicted_class": predicted_class,
            "confidence": conf_val,
            "song": song,
            "region": predicted_class.upper() if predicted_class in config.SONG_MAP else "UNKNOWN"
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal memproses file audio: {e}")

@app.websocket("/ws/pitch")
async def pitch_websocket(websocket: WebSocket):
    """Streams real-time pitch detection from the server's microphone to the client."""
    await websocket.accept()
    print("[WS] Klien terhubung ke WebSocket Pitch.")
    
    # Audio settings for streaming
    chunk_size = 2048
    sample_rate = 16000
    
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
            
            # Detect pitch
            freq = detect_pitch(indata.flatten(), sample_rate)
            note = frequency_to_note(freq) if freq > 0 else None
            
            # Send results back
            payload = {
                "frequency": float(freq),
                "note": note
            }
            await websocket.send_json(payload)
            
    except WebSocketDisconnect:
        print("[WS] Klien terputus dari WebSocket Pitch.")
    except Exception as e:
        print(f"[WS] Error di WebSocket: {e}")
    finally:
        stream.stop()
        stream.close()

if __name__ == "__main__":
    uvicorn.run("src.api:app", host="0.0.0.0", port=8000, reload=True)
