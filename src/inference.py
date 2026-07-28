import os
import sys
# Add project root to sys.path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import time
import numpy as np
import sounddevice as sd
import torch
import torch.nn.functional as F
import librosa
import pygame

import src.config as config
from src.dataset import extract_mel_spectrogram
from src.model import get_model

# Initialize pygame mixer for playing audio
try:
    pygame.mixer.init()
except Exception as e:
    print(f"Peringatan: Gagal menginisialisasi Pygame Mixer ({e}).")

def detect_model_type_from_state_dict(state_dict):
    """Auto-detects whether state_dict belongs to CNN, CRNN, or ResNet."""
    keys = list(state_dict.keys())
    if any("lstm" in k for k in keys):
        return "CRNN"
    elif any("layer1" in k for k in keys):
        return "ResNet"
    else:
        return "CNN"

def play_regional_song(class_name):
    """Plays corresponding regional song for detected class."""
    if class_name not in config.SONG_MAP:
        return
        
    song_file = config.SONG_MAP[class_name]
    song_path = os.path.join(config.SONGS_DIR, song_file)
    
    if not os.path.exists(song_path):
        print(f"\n[LAGU] File lagu '{song_file}' belum ada di {config.SONGS_DIR}.")
        print(f" -> Silakan letakkan file lagu daerah di folder {config.SONGS_DIR} untuk mendengarkannya.")
        return
        
    try:
        print(f"\n[LAGU] Memainkan lagu daerah untuk {class_name.upper()} ({song_file})...")
        pygame.mixer.music.load(song_path)
        pygame.mixer.music.play()
    except Exception as e:
        print(f"Gagal memutar lagu: {e}")

def main():
    device = torch.device("cpu")
    model_path = os.path.join(config.MODELS_DIR, "best_overall_model.pth")
    
    if not os.path.exists(model_path):
        print(f"Error: File model '{model_path}' tidak ditemukan!")
        print("Silakan jalankan training terlebih dahulu dengan perintah: python src/train.py")
        return
        
    state_dict = torch.load(model_path, map_location=device)
    model_type = detect_model_type_from_state_dict(state_dict)
    
    model = get_model(model_type, num_classes=len(config.CLASSES)).to(device)
    model.load_state_dict(state_dict)
    model.eval()
    print(f"Model ({model_type}) berhasil dimuat dari: {model_path}")
    
    threshold = 60.0  # Percentage confidence threshold
    rec_duration = 2.0 # 2 seconds mic capture
    min_volume_rms = 0.008 # Energy threshold to ignore silent mic noise
    
    print("\n" + "="*55)
    print(" DETEKSI SALAM DAERAH INDONESIA REAL-TIME (MIC) ")
    print("="*55)
    print(f"Daftar Kelas Salam: {', '.join(config.CLASSES)}")
    print(f"Model Aktif        : {model_type}")
    print("Tekan Ctrl+C untuk keluar.")
    print("="*55 + "\n")
    
    try:
        while True:
            input("\nTekan ENTER lalu ucapkan kata sapaan (bicaralah dengan jelas dekat mic)...")
            print(f"MENDENGARKAN ({rec_duration} detik)...")
            
            recording = sd.rec(
                int(config.SAMPLE_RATE * rec_duration),
                samplerate=config.SAMPLE_RATE,
                channels=1,
                dtype='float32'
            )
            sd.wait()
            print("Perekaman selesai. Menganalisis...")
            
            audio = recording.flatten()
            
            # Check volume energy
            rms_energy = np.sqrt(np.mean(audio**2))
            if rms_energy < min_volume_rms:
                print(f"Sinyal terlalu pelan/hening (Energy: {rms_energy:.5f}). Silakan bicara lebih lantang & dekat ke mikrofon.")
                continue
                
            mel_spec = extract_mel_spectrogram(audio, sr=config.SAMPLE_RATE)
            inputs = torch.tensor(mel_spec, dtype=torch.float32).unsqueeze(0).unsqueeze(0).to(device)
            
            with torch.no_grad():
                logits = model(inputs)
                probs = F.softmax(logits, dim=1)[0]
                
            prob_val, pred_idx = torch.max(probs, dim=0)
            pred_class = config.CLASSES[pred_idx.item()]
            confidence = prob_val.item() * 100
            
            print(f"\nHasil Deteksi: {pred_class.upper()} ({confidence:.2f}%)")
            
            if confidence >= threshold:
                play_regional_song(pred_class)
            else:
                print(f"Confidence ({confidence:.2f}%) di bawah threshold ({threshold}%).")
                
    except KeyboardInterrupt:
        print("\nProgram dihentikan. Sampai jumpa!")
        pygame.mixer.quit()

if __name__ == "__main__":
    main()
