import sys
import os
import re
# Auto-resolve parent folder in python path to prevent import errors
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import socket
import threading
import time
import asyncio
import numpy as np
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.websockets import WebSocket, WebSocketDisconnect
import uvicorn
import src.config as config

from fastapi.staticfiles import StaticFiles
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
except ImportError:
    HAS_PYGAME = False
    print("[WARN] 'pygame' is not installed. Local laptop synth audio is disabled.")

app = FastAPI(title="Angklung AI & Pitch Backend")

# Enable CORS for Flutter Web client access
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

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
        9: 1396.91, 10: 1479.98, 11: 1567.98, 12: 1661.22
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

def generate_angklung_sound(frequency: float, duration: float = 1.2, sr: int = 44100, volume: float = 1.0, instr_type: str = "melody"):
    t = np.linspace(0, duration, int(sr * duration), endpoint=False)
    
    if instr_type == "bass":
        # Bass: Deep fundamental, no harsh high harmonics, slower envelope decay
        f1 = frequency
        f2 = frequency * 2.0
        env1 = np.exp(-2.0 * t)
        env2 = np.exp(-4.5 * t)
        
        tone1 = np.sin(2.0 * np.pi * f1 * t) * env1 * 0.8
        tone2 = np.sin(2.0 * np.pi * f2 * t) * env2 * 0.2
        signal = tone1 + tone2
        
        # Soft click for bass
        click_len = int(sr * 0.02)
        click = (np.random.rand(click_len) - 0.5) * np.exp(-np.linspace(0, 4.0, click_len)) * 0.1
        signal[:click_len] += click
        
    elif instr_type == "chord":
        # Chord/Rhythm: Detuned stereo chorus backing wash (left slightly higher, right slightly lower)
        f1_L = frequency * 1.002
        f1_R = frequency * 0.998
        f2_L = frequency * 2.002
        f2_R = frequency * 1.998
        
        env1 = np.exp(-4.5 * t)  # Faster decay so it sits nicely in background
        env2 = np.exp(-3.5 * t)
        
        tone1_L = np.sin(2.0 * np.pi * f1_L * t) * env1 * 0.5
        tone1_R = np.sin(2.0 * np.pi * f1_R * t) * env1 * 0.5
        tone2_L = np.sin(2.0 * np.pi * f2_L * t) * env2 * 0.4
        tone2_R = np.sin(2.0 * np.pi * f2_R * t) * env2 * 0.4
        
        signal_L = tone1_L + tone2_L
        signal_R = tone1_R + tone2_R
        
        # Click (strike sound)
        click_len = int(sr * 0.025)
        click = (np.random.rand(click_len) - 0.5) * np.exp(-np.linspace(0, 5.0, click_len)) * 0.15
        signal_L[:click_len] += click
        signal_R[:click_len] += click
        
        # Normalize and scale
        max_L = np.max(np.abs(signal_L))
        max_R = np.max(np.abs(signal_R))
        if max_L > 0: signal_L = (signal_L / max_L) * volume
        if max_R > 0: signal_R = (signal_R / max_R) * volume
        
        stereo_signal = np.column_stack((signal_L, signal_R))
        return (stereo_signal * 32767).astype(np.int16)
        
    else: # "melody"
        # Melody: Sharp, bright, clear centered lead with full harmonics
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
        
        # Bright strike click
        click_len = int(sr * 0.02)
        click = (np.random.rand(click_len) - 0.5) * np.exp(-np.linspace(0, 4.0, click_len)) * 0.25
        signal[:click_len] += click
        
    max_val = np.max(np.abs(signal))
    if max_val > 0:
        signal = (signal / max_val) * volume
        
    stereo_signal = np.column_stack((signal, signal))
    return (stereo_signal * 32767).astype(np.int16)

def play_synth_note_async(note_num: int, angklung_id: int, volume: float = 1.0, instr_type: str = "melody"):
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
        pcm_data = generate_angklung_sound(freq, volume=volume, instr_type=instr_type)
        
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

def send_to_arduino(note_num, angklung_id: int = 3, play_synth: bool = True):
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
            finally:
                lock.release()
        else:
            # Lock timeout: skip serial write to prevent server hang
            print(f"[SERIAL] Lock timeout untuk Angklung {target_id} - Melompati pengiriman untuk mencegah deadlock.")
            notes_str = ",".join(str(x) for x in notes_list)
            return True, f"Lock Timeout - Dimainkan di Laptop (Nada: {notes_str})"
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

def play_song_thread(file_content: str, thread_token: int):
    global song_playback_active, current_playback_token
    
    try:
        # 1. Parse Metadata & find all track names
        bpm = 90
        key_sig = "F"
        beats_per_bar = 4.0
        denominator = 4
        lines = file_content.split('\n')
        
        # Helper to process blocks with alignment
        def process_block(block_lines, target_tracks, all_tracks):
            block_tracks = {}
            for bline in block_lines:
                parts = bline.split(':', 1)
                if len(parts) != 2:
                    continue
                tname = parts[0].strip()
                tcontent = parts[1].strip()
                
                # Split into bars using '|'
                bars = [b.strip() for b in tcontent.split('|') if b.strip()]
                block_tracks[tname] = bars
                
            if not block_tracks:
                return
                
            num_bars = max(len(b) for b in block_tracks.values())
            for tname in all_tracks:
                if tname in block_tracks:
                    bars = block_tracks[tname]
                    while len(bars) < num_bars:
                        bars.append("0")
                    target_tracks[tname].extend(bars)
                else:
                    target_tracks[tname].extend(["0"] * num_bars)

        # First pass: find all track names in the entire file
        all_track_names = set()
        for line in lines:
            line = line.strip()
            if not line:
                continue
            is_track = (line.startswith('V') or line.startswith('VB') or line.startswith('VA')) and ':' in line
            if is_track:
                tname = line.split(':', 1)[0].strip()
                all_track_names.add(tname)
                
        # Second pass: group into blocks and parse
        tracks = {tname: [] for tname in all_track_names}
        current_block = []
        in_music_part = False
        
        for line in lines:
            line = line.strip()
            if not line:
                if current_block:
                    process_block(current_block, tracks, all_track_names)
                    current_block = []
                continue
                
            is_track = (line.startswith('V') or line.startswith('VB') or line.startswith('VA')) and ':' in line
            if is_track:
                in_music_part = True
                current_block.append(line)
            else:
                if current_block:
                    process_block(current_block, tracks, all_track_names)
                    current_block = []
                
                if not in_music_part:
                    # Parse header
                    if line.startswith('Q:'):
                        try:
                            bpm = int(line.split(':')[1].strip())
                        except:
                            pass
                    elif line.startswith('K:'):
                        key_sig = line.split(':')[1].strip().upper()
                        key_sig = re.sub(r"[^A-Z#B]", "", key_sig)
                        if not key_sig:
                            key_sig = "C"
                    elif line.startswith('M:'):
                        try:
                            m_val = line.split(':')[1].strip()
                            if '/' in m_val:
                                beats_per_bar = float(m_val.split('/')[0])
                                denominator = int(m_val.split('/')[1])
                            else:
                                beats_per_bar = float(m_val)
                                denominator = 4
                        except:
                            pass
                            
        if current_block:
            process_block(current_block, tracks, all_track_names)
            
        # Check if we parsed any music
        has_music = any(len(b) > 0 for b in tracks.values())
        if not has_music:
            print("[PARSER] Tidak ada data musik yang ditemukan.")
            song_playback_active = False
            return
            
        print(f"[PARSER] Memulai pemutaran lagu. Tempo: {bpm} BPM, Nada Dasar: {key_sig}, Beats/Bar: {beats_per_bar}")
            
        if not tracks:
            song_playback_active = False
            return
            
        max_bars = max(len(bars) for bars in tracks.values())
        
        # Determine dynamic steps per bar based on beats_per_bar
        # 12/8 -> 12 steps, 3/4 -> 6 steps, 2/4 -> 8 steps, 4/4 -> 8 steps
        if beats_per_bar == 12.0:
            steps_per_bar = 12
        elif beats_per_bar == 3.0:
            steps_per_bar = 6
        elif beats_per_bar == 2.0:
            steps_per_bar = 8
        elif beats_per_bar == 4.0:
            steps_per_bar = 8
        else:
            steps_per_bar = 8

        # Compound meter correction (denominator = 8 like 12/8, 6/8):
        # The tempo Q refers to dotted quarter notes, which consist of 3 eighth notes.
        if denominator == 8:
            tempo_beats_per_bar = beats_per_bar / 3.0
        else:
            tempo_beats_per_bar = beats_per_bar

        sub_beat_duration = ((60.0 / bpm) * tempo_beats_per_bar) / steps_per_bar
        
        # 3. Main Playback Loop
        last_active_notes = {track: [] for track in tracks.keys()}
        
        for bar_idx in range(max_bars):
            if not song_playback_active or thread_token != current_playback_token:
                break
                
            bar_steps = [{} for _ in range(steps_per_bar)]
            
            for track_name, bars in tracks.items():
                if bar_idx >= len(bars):
                    continue
                bar_str = bars[bar_idx]
                tokens = bar_str.split()
                
                # Calculate token positions using note duration weights
                current_beat = 0.0
                for token in tokens:
                    # Full-beat sustain or half-beat duration detection
                    if token == '-' or token == '.':
                        token_dur = 1.0
                    elif token.endswith('-'):
                        token_dur = 0.5
                    else:
                        token_dur = 1.0
                    
                    # Convert beat start to one of the steps_per_bar steps
                    step_idx = int((current_beat / beats_per_bar) * steps_per_bar)
                    if step_idx < steps_per_bar:
                        bar_steps[step_idx][track_name] = token
                        
                    current_beat += token_dur
                        
            for step_idx in range(steps_per_bar):
                if not song_playback_active or thread_token != current_playback_token:
                    break
                    
                arduino1_notes = []
                arduino3_notes = []
                
                for track_name in tracks.keys():
                    token = bar_steps[step_idx].get(track_name, None)
                    is_new_trigger = token is not None and token != '.' and token != '-'
                    
                    if token is None or token == '.' or token == '-':
                        # Sustain previous notes for this track
                        active = last_active_notes.get(track_name, [])
                    else:
                        # Clean trailing speed indicators (-) and accents (^) before parsing
                        cleaned_token = token.rstrip('-').rstrip('^')
                        
                        if cleaned_token == '0':
                            # Rest: Clear notes
                            active = []
                            last_active_notes[track_name] = []
                        else:
                            active = []
                            if cleaned_token.startswith('@'):
                                # Chord: resolve pitches and fit to Melody range [65, 92]
                                for pitch in resolve_chord_pitches(cleaned_token, key_sig):
                                    if pitch in ANGKLUNG1_PITCHES:
                                        active.append({"pitch": pitch, "type": "mel1"})
                                    elif pitch in ANGKLUNG2_PITCHES:
                                        active.append({"pitch": pitch, "type": "mel2"})
                            else:
                                # Single note: resolve pitch and fit to physical ranges
                                midi_val = doremi_to_midi(cleaned_token, key_sig)
                                if track_name == 'VB':
                                    # Shift to Bass physical range [52, 67] (e3 to g4)
                                    while midi_val < 52:
                                        midi_val += 12
                                    while midi_val > 67:
                                        midi_val -= 12
                                    pitch = midi_to_note_name(midi_val)
                                    if pitch in BASS_PITCHES:
                                        active.append({"pitch": pitch, "type": "bass"})
                                else:
                                    # Shift to Melody physical range [65, 92] (f4 to g#6)
                                    while midi_val < 65:
                                        midi_val += 12
                                    while midi_val > 92:
                                        midi_val -= 12
                                    pitch = midi_to_note_name(midi_val)
                                    if pitch in ANGKLUNG1_PITCHES:
                                        active.append({"pitch": pitch, "type": "mel1"})
                                    elif pitch in ANGKLUNG2_PITCHES:
                                        active.append({"pitch": pitch, "type": "mel2"})
                            last_active_notes[track_name] = active
                        
                    # Collect resolved notes and play synth sound for new triggers
                    for note_info in active:
                        p = note_info["pitch"]
                        ntype = note_info["type"]
                        
                        if ntype == "mel1":
                            note_num = ANGKLUNG1_PITCHES.index(p) + 1
                            ang_id = 1
                        elif ntype == "mel2":
                            note_num = ANGKLUNG2_PITCHES.index(p) + 1 + 16
                            ang_id = 1
                        elif ntype == "bass":
                            note_num = BASS_PITCHES.index(p) + 1
                            ang_id = 3
                            
                        # Play local sound only if it's a new trigger
                        if is_new_trigger:
                            # DAW track mixer volume panel
                            if track_name == 'VB':
                                vol = 0.65  # Bass volume
                            elif track_name == 'VA^' or track_name == 'VA':
                                vol = 0.35  # Chord / Rhythm volume (quieter background)
                            elif track_name == 'V1':
                                vol = 1.00  # Lead Melody volume (loudest)
                            else:
                                vol = 0.70  # Supporting melody volume (V2, V3, etc.)
                            play_local_sound(note_num, ang_id, vol)
                            
                        if ntype == "mel1" or ntype == "mel2":
                            arduino1_notes.append(note_num)
                        elif ntype == "bass":
                            arduino3_notes.append(note_num)
                            
                # Remove duplicates
                arduino1_notes = list(set(arduino1_notes))
                arduino3_notes = list(set(arduino3_notes))
                
                # Play in parallel if there are active notes (disable play_synth inside send_to_arduino)
                if arduino1_notes or arduino3_notes:
                    t1 = threading.Thread(target=send_to_arduino, args=(arduino1_notes, 1, False))
                    t3 = threading.Thread(target=send_to_arduino, args=(arduino3_notes, 3, False))
                    t1.start()
                    t3.start()
                    t1.join(timeout=0.5)
                    t3.join(timeout=0.5)
                    
                time.sleep(sub_beat_duration)
        print("[PARSER] Pemutaran lagu selesai.")
    except Exception as e:
        print(f"[PARSER] Error fatal saat memainkan lagu: {e}")
    finally:
        song_playback_active = False
        try:
            send_to_arduino(0, 1)
            send_to_arduino(0, 3)
        except:
            pass


ANGKLUNG1_PITCHES = [
    "g4", "a4", "a#4", "b4", "c5", "d5", "e5", "f5",
    "f#5", "g5", "a5", "a#5", "b5", "c6", "d6", "e6"
]

ANGKLUNG2_PITCHES = [
    "f4", "f#4", "g#4", "c#5", "d#5", "g#5", "c#6", "d#6",
    "f6", "f#6", "g6", "g#6"
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
    
    # Accidentals: / raises by 1 semitone, \ lowers by 1 semitone
    octave_mod += token.count("/")
    octave_mod -= token.count("\\")
    
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
    
    # Accidentals: / raises by 1 semitone, \ lowers by 1 semitone
    accidental = 0
    accidental += symbol.count("/")
    accidental -= symbol.count("\\")
    
    # Strip accidental markers from symbol to extract digit
    clean_sym = symbol.replace("/", "").replace("\\", "")
    
    digit = ""
    for c in clean_sym:
        if c.isdigit():
            digit += c
    if not digit:
        return []
    degree = int(digit)
    
    intervals = {1: 0, 2: 2, 3: 4, 4: 5, 5: 7, 6: 9, 7: 11}
    chord_root_midi = root_midi + intervals.get(degree, 0) + accidental
    
    is_minor = 'm' in clean_sym
    # Diatonic major key defaults: degrees 2, 3, 6, 7 are naturally Minor/Diminished
    if not is_minor and not ('M' in symbol or 'maj' in symbol):
        if degree in [2, 3, 6, 7]:
            is_minor = True
            
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
        while m > 92: # g#6 is midi 92
            m -= 12
        pitches.append(midi_to_note_name(m))
    return pitches

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
            
        try:
            content = read_file_safely(file_path)
            lines = content.split('\n')
            for line in lines[:15]:
                line = line.strip()
                if line.startswith('T:'):
                    title = line.split(':', 1)[1].strip()
                elif line.startswith('C:') or line.startswith('O:'):
                    region = line.split(':', 1)[1].strip()
        except Exception as e:
            print(f"Error reading metadata from {file_basename}: {e}")
            
        # Get relative path relative to SONGS_DIR and make it web-safe
        rel_path = os.path.relpath(file_path, SONGS_DIR).replace(os.sep, '/')
        
        results.append({
            "id": rel_path,
            "title": title,
            "region": region,
            "file_name": rel_path
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
    if not file_path.startswith(os.path.abspath(SONGS_DIR)):
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

app.mount('/', StaticFiles(directory='public', html=True), name='static')
if __name__ == '__main__':
    uvicorn.run('src.mini_app:app', host='0.0.0.0', port=8000, reload=True)
