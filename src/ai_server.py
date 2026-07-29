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

# 3. Protect heavy AI dependencies
try:
    import sounddevice as sd
    import librosa
    import soundfile as sf
    import os
    import os
    import torch
    from src.model import AudioCRNN
    HAS_AI = True
except ImportError as e:
    HAS_AI = False
    print(f"[WARN] Optional AI dependencies (tensorflow, librosa, sounddevice, soundfile) missing: {e}")
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

# Load CNN Model
model = None

def init_model():
    global model
    if not HAS_AI:
        return
    try:
        model_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "Deteksi Bahasa", "models", "CRNN_best.pth")
        if os.path.exists(model_path):
            model = AudioCRNN(num_classes=len(config.CLASSES))
            model.load_state_dict(torch.load(model_path, map_location=torch.device('cpu')))
            model.eval()
            print("[MODEL] Model CRNN PyTorch berhasil dimuat.")
        else:
            print(f"[WARNING] File model '{model_path}' belum ada.")
    except Exception as e:
        print(f"[MODEL] Gagal memuat model: {e}")

if HAS_AI:
    init_model()

# Angklung Note Frequencies and Hardware mapping
NOTE_FREQUENCIES = {
  1: { 1: 392.00, 2: 440.00, 3: 466.16, 4: 493.88, 5: 523.25, 6: 587.33, 7: 659.25, 8: 698.46,
       9: 739.99, 10: 783.99, 11: 880.00, 12: 932.33, 13: 987.77, 14: 1046.50, 15: 1174.66, 16: 1318.51 },
  2: { 1: 349.23, 2: 369.99, 3: 415.30, 4: 554.37, 5: 622.25, 6: 830.61, 7: 1109.73, 8: 1244.51,
       9: 1396.91, 10: 1479.98, 11: 1567.98, 12: 1661.22 },
  3: { 1: 164.81, 2: 174.61, 3: 185.00, 4: 196.00, 5: 207.65, 6: 220.00, 7: 233.08, 8: 246.94,
       9: 261.63, 10: 277.18, 11: 293.66, 12: 311.13, 13: 329.63, 14: 349.23, 15: 369.99, 16: 392.00 }
}

PITCH_TO_HARDWARE = {
  "e3": {"angklung": 3, "note": 1}, "f3": {"angklung": 3, "note": 2}, "f#3": {"angklung": 3, "note": 3},
  "g3": {"angklung": 3, "note": 4}, "g#3": {"angklung": 3, "note": 5}, "a3": {"angklung": 3, "note": 6},
  "a#3": {"angklung": 3, "note": 7}, "b3": {"angklung": 3, "note": 8}, "c4": {"angklung": 3, "note": 9},
  "c#4": {"angklung": 3, "note": 10}, "d4": {"angklung": 3, "note": 11}, "d#4": {"angklung": 3, "note": 12},
  "e4": {"angklung": 3, "note": 13}, "f4_bass": {"angklung": 3, "note": 14}, "f#4_bass": {"angklung": 3, "note": 15},
  "g4_bass": {"angklung": 3, "note": 16},
  "f4": {"angklung": 2, "note": 1}, "f#4": {"angklung": 2, "note": 2}, "g4": {"angklung": 1, "note": 1},
  "g#4": {"angklung": 2, "note": 3}, "a4": {"angklung": 1, "note": 2}, "a#4": {"angklung": 1, "note": 3},
  "b4": {"angklung": 1, "note": 4}, "c5": {"angklung": 1, "note": 5}, "c#5": {"angklung": 2, "note": 4},
  "d5": {"angklung": 1, "note": 6}, "d#5": {"angklung": 2, "note": 5}, "e5": {"angklung": 1, "note": 7},
  "f5": {"angklung": 1, "note": 8}, "f#5": {"angklung": 1, "note": 9}, "g5": {"angklung": 1, "note": 10},
  "g#5": {"angklung": 2, "note": 6}, "a5": {"angklung": 1, "note": 11}, "a#5": {"angklung": 1, "note": 12},
  "b5": {"angklung": 1, "note": 13}, "c6": {"angklung": 1, "note": 14}, "c#6": {"angklung": 2, "note": 7},
  "d6": {"angklung": 1, "note": 15}, "d#6": {"angklung": 2, "note": 8}, "e6": {"angklung": 1, "note": 16},
  "f6": {"angklung": 2, "note": 9}, "f#6": {"angklung": 2, "note": 10}, "g6": {"angklung": 2, "note": 11},
  "g#6": {"angklung": 2, "note": 12}
}

NOTE_FREQS = {}
for pitch, hw in PITCH_TO_HARDWARE.items():
    ang = hw["angklung"]
    num = hw["note"]
    if ang in NOTE_FREQUENCIES and num in NOTE_FREQUENCIES[ang]:
        NOTE_FREQS[pitch.upper()] = NOTE_FREQUENCIES[ang][num]

def frequency_to_note(freq):
    if freq < 150 or freq > 1700:
        return None
    closest_note = None
    min_cents_diff = float("inf")
    for note, note_freq in NOTE_FREQS.items():
        cents_diff = abs(1200 * np.log2(freq / note_freq))
        if cents_diff < min_cents_diff and cents_diff < 50:
            min_cents_diff = cents_diff
            closest_note = note
    return closest_note

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

def preprocess_audio_data(y, max_pad_len=150):
    """Pads/crops audio array and extracts Mel Spectrogram for PyTorch CRNN."""
    # 1. Hapus DC Offset
    y = y - np.mean(y)
    
    # 2. Trim silence (sama seperti di tahap training)
    y_trimmed, _ = librosa.effects.trim(y, top_db=20)
    if len(y_trimmed) < int(config.SAMPLE_RATE * 0.1): # Failsafe
        y_trimmed = y
        
    # 3. Pre-Emphasis
    y_preemph = librosa.effects.preemphasis(y_trimmed)
    
    # 4. Normalisasi Volume
    y_clean = librosa.util.normalize(y_preemph)
        
    # Extract Mel Spectrogram (64 Mels)
    mels = librosa.feature.melspectrogram(
        y=y_clean, 
        sr=config.SAMPLE_RATE, 
        n_mels=64,
        n_fft=1024,
        hop_length=512
    )
    
    mels_db = librosa.power_to_db(mels, ref=np.max)
    
    # Normalisasi Z-score
    mels_norm = (mels_db - np.mean(mels_db)) / (np.std(mels_db) + 1e-6)
    
    # 2D Padding (karena arsitektur temporal avg pooling, kita tidak perlu potong,
    # tapi agar aman untuk batasan tensor, kita gunakan max_pad_len)
    if mels_norm.shape[1] > max_pad_len:
        features_2d = mels_norm[:, :max_pad_len]
    else:
        pad_width = max_pad_len - mels_norm.shape[1]
        features_2d = np.pad(mels_norm, pad_width=((0, 0), (0, pad_width)), mode='constant')
        
    # PyTorch butuh shape: (Batch, Channel, Height, Width) -> (1, 1, 64, time)
    features_2d = features_2d[np.newaxis, np.newaxis, ...]
    
    # Return as torch tensor
    return torch.tensor(features_2d, dtype=torch.float32)

@app.get("/api/health")
def health_check():
    return {
        "status": "healthy",
        "model_loaded": model is not None,
        "classes": config.CLASSES
    }

@app.post("/api/classify-audio")
async def classify_audio(file: UploadFile = File(...)):
    """Receives an uploaded audio file from the Flutter client and runs classification."""
    if not HAS_AI:
        raise HTTPException(status_code=501, detail="AI classification is disabled on this machine (missing PyTorch/Librosa)")
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
            
        # Inference using PyTorch CRNN
        inputs = preprocess_audio_data(data)
        with torch.no_grad():
            outputs = model(inputs)
            probabilities = torch.nn.functional.softmax(outputs, dim=1)[0].numpy()
        
        class_idx = np.argmax(probabilities)
        conf_val = float(probabilities[class_idx])
        predicted_class = config.CLASSES[class_idx]
            
        # Bypass lagu untuk murni mengetes deteksi sapaan (sesuai request)
        song = None 
        
        display_text = config.DISPLAY_MAP.get(predicted_class, predicted_class.upper())
        
        return {
            "status": "success",
            "predicted_class": predicted_class,
            "confidence": conf_val,
            "song": song,
            "region": display_text
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Gagal memproses file audio: {e}")

@app.post("/api/repeater-audio")
async def repeater_audio(file: UploadFile = File(...)):
    """Receives an uploaded audio file, tracks pitch via librosa.yin, and returns note sequence."""
    if not HAS_AI:
        raise HTTPException(status_code=501, detail="AI pitch tracking is disabled.")
    
    try:
        temp_filename = "temp_repeater.wav"
        with open(temp_filename, "wb") as buffer:
            buffer.write(await file.read())
            
        # Target SR and Hop length to achieve exactly ~46ms per frame
        # 46ms = 0.046s. SR = 16000. hop_length = 0.046 * 16000 = 736
        target_sr = 16000
        hop_length = 736
        
        data, samplerate = sf.read(temp_filename)
        os.remove(temp_filename)
        
        if len(data.shape) > 1:
            data = data.mean(axis=1)
            
        if samplerate != target_sr:
            data = librosa.resample(data, orig_sr=samplerate, target_sr=target_sr)
            
        # Noise gate pre-processing (silence regions get 0 pitch)
        rms = librosa.feature.rms(y=data, hop_length=hop_length, frame_length=hop_length*2)[0]
        
        # librosa.yin for highly accurate monophonic pitch tracking
        f0 = librosa.yin(data, fmin=150, fmax=1700, sr=target_sr, hop_length=hop_length, frame_length=hop_length*2)
        
        sequence = []
        # Convert f0 to notes, applying rms noise gate
        for i in range(len(f0)):
            freq = float(f0[i])
            r = float(rms[i]) if i < len(rms) else 0.0
            
            note = None
            if r > 0.04 and freq > 0: # Threshold dinaikkan menjadi 0.04 agar kebal suara bising/jauh
                note = frequency_to_note(freq)
            sequence.append(note)
            
        # Temporal smoothing (Median/Mode filter over 5 frames) to remove stutter
        smoothed_seq = []
        window = 5
        for i in range(len(sequence)):
            start = max(0, i - window // 2)
            end = min(len(sequence), i + window // 2 + 1)
            neighborhood = sequence[start:end]
            
            # Find most common note (excluding None unless None is dominating)
            valid_notes = [n for n in neighborhood if n is not None]
            if len(valid_notes) < 2:
                smoothed_seq.append(None)
            else:
                from collections import Counter
                most_common = Counter(valid_notes).most_common(1)[0][0]
                smoothed_seq.append(most_common)
                
        return {
            "status": "success",
            "sequence": smoothed_seq,
            "frame_ms": 46
        }
    except Exception as e:
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Gagal memproses file repeater: {e}")

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

if __name__ == '__main__':
    uvicorn.run('src.ai_server:app', host='0.0.0.0', port=8001, reload=True)
