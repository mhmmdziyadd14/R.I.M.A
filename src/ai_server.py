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
    import torch
    import librosa
    import soundfile as sf
    from src.model import AudioCNN
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

# Load CNN Model
device = None
model = None

def init_model():
    global model, device
    if not HAS_AI:
        return
    try:
        device = torch.device("cpu")
        if os.path.exists(config.MODEL_SAVE_PATH):
            model = AudioCNN(num_classes=len(config.CLASSES)).to(device)
            model.load_state_dict(torch.load(config.MODEL_SAVE_PATH, map_location=device))
            model.eval()
            print("[MODEL] Model PyTorch berhasil dimuat.")
        else:
            print(f"[WARNING] File model '{config.MODEL_SAVE_PATH}' belum ada. Silakan lakukan training.")
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
