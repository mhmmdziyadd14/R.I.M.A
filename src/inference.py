import os
import sys
# Auto-resolve parent folder in python path to prevent import errors
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import time
import numpy as np
import sounddevice as sd
import librosa
import src.config as config

import torch
from src.model import AudioCRNN

def play_regional_song(class_name):
    """(DINONAKTIFKAN) Plays the corresponding regional song for the detected class."""
    # Fitur pemutaran lagu dinonaktifkan sementara untuk fokus pada pengujian akurasi deteksi
    pass

def preprocess_live_audio(audio_data, max_pad_len=100):
    """Converts raw audio data array from sounddevice to MFCC 2D Tensor."""
    # Ensure audio is float32 and mono
    y = audio_data.flatten()
    
    # 1. Hapus DC Offset
    y = y - np.mean(y)
    
    # 2. Trim silence
    y_trimmed, _ = librosa.effects.trim(y, top_db=20)
    if len(y_trimmed) < int(config.SAMPLE_RATE * 0.1): # Failsafe jika terlalu banyak terpotong
        y_trimmed = y
        
    # 3. Pre-Emphasis
    y_preemph = librosa.effects.preemphasis(y_trimmed)
    
    # 4. Normalisasi Volume
    y_clean = librosa.util.normalize(y_preemph)
    
    # Extract MFCC
    mfcc = librosa.feature.mfcc(
        y=y_clean, 
        sr=config.SAMPLE_RATE, 
        n_mfcc=64
    )
    
    # CNN 2D Padding / Cropping ke max_pad_len (100)
    if mfcc.shape[1] > max_pad_len:
        mfccs_2d = mfcc[:, :max_pad_len]
    else:
        pad_width = max_pad_len - mfcc.shape[1]
        mfccs_2d = np.pad(mfcc, pad_width=((0, 0), (0, pad_width)), mode='constant')
        
    # Reshape for PyTorch input: (Batch, Channel, Height, Width) -> (1, 1, 64, 100)
    mfccs_2d = mfccs_2d[np.newaxis, np.newaxis, ...]
    return torch.tensor(mfccs_2d, dtype=torch.float32)

def main():
    model_path = os.path.join("Deteksi Bahasa", "models", "best_model_CRNN.pth")
    
    print("Memuat arsitektur PyTorch CRNN...")
    if not os.path.exists(model_path):
        print(f"Error: File model '{model_path}' tidak ditemukan!")
        return
        
    try:
        model = AudioCRNN(num_classes=len(config.CLASSES))
        model.load_state_dict(torch.load(model_path, map_location=torch.device('cpu')))
        model.eval()
        print("Model CRNN (PyTorch) berhasil dimuat.")
    except Exception as e:
        print(f"Gagal memuat model PyTorch: {e}")
        return
    
    threshold = 0.75  # Confidence threshold to trigger action
    
    print("\n" + "="*50)
    print("APLIKASI DETEKSI SALAM DAERAH REAL-TIME (CNN)")
    print("="*50)
    print("Tekan Ctrl+C untuk keluar.")
    print("Silakan bicara setelah muncul tulisan 'MENDENGARKAN...'")
    print("="*50 + "\n")
    
    try:
        while True:
            input("\nTekan ENTER lalu ucapkan salam...")
            print("MENDENGARKAN (1.5 detik)...")
            
            # Record audio from mic
            recording = sd.rec(
                int(config.NUM_SAMPLES), 
                samplerate=config.SAMPLE_RATE, 
                channels=1, 
                dtype='float32'
            )
            sd.wait() # Wait until recording is finished
            print("Perekaman selesai. Menganalisis...")
            
            # Preprocess and predict
            inputs = preprocess_live_audio(recording)
            
            # PyTorch Predict
            with torch.no_grad():
                outputs = model(inputs)
                probabilities = torch.nn.functional.softmax(outputs, dim=1)[0].numpy()
            
            # Get prediction
            pred_idx = np.argmax(probabilities)
            prob = probabilities[pred_idx]
            
            classes = config.CLASSES
            if pred_idx < len(classes):
                raw_class = classes[pred_idx]
                display_text = config.DISPLAY_MAP.get(raw_class, raw_class.upper())
            else:
                display_text = "TIDAK DIKENAL"
                
            prob_percent = prob * 100
            
            print(f">>> HASIL KLASIFIKASI: {display_text} ({prob_percent:.2f}%) <<<")
            
            if prob >= threshold:
                print(f"✅ Sapaan Terdeteksi dengan Kuat!")
                # Lagu sengaja dimatikan: play_regional_song(pred_class)
            else:
                print(f"⚠️ Sapaan terdeteksi tapi masih ragu-ragu (di bawah threshold {threshold*100}%).")
                    
    except KeyboardInterrupt:
        print("\nAplikasi dihentikan. Sampai jumpa!")

if __name__ == "__main__":
    main()
