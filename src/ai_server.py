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
        model_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "Deteksi Bahasa", "models", "best_model_CRNN.pth")
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

def preprocess_audio_data(y, max_pad_len=100):
    """Pads/crops audio array and extracts MFCC for Keras CNN 2D."""
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
        
    # Extract MFCC (gunakan 64 untuk model CRNN)
    mfcc = librosa.feature.mfcc(
        y=y_clean, 
        sr=config.SAMPLE_RATE, 
        n_mfcc=64
    )
    
    # 2D Padding ke ukuran statis 100
    if mfcc.shape[1] > max_pad_len:
        mfccs_2d = mfcc[:, :max_pad_len]
    else:
        pad_width = max_pad_len - mfcc.shape[1]
        mfccs_2d = np.pad(mfcc, pad_width=((0, 0), (0, pad_width)), mode='constant')
        
    # PyTorch butuh shape: (Batch, Channel, Height, Width) -> (1, 1, 64, 100)
    mfccs_2d = mfccs_2d[np.newaxis, np.newaxis, ...]
    
    # Return as torch tensor
    return torch.tensor(mfccs_2d, dtype=torch.float32)

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
