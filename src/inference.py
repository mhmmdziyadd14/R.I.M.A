import os
import time
import numpy as np
import sounddevice as sd
import torch
import torch.nn.functional as F
import librosa
import pygame
import src.config as config
from src.model import AudioCNN

# Initialize pygame mixer for audio playback
try:
    pygame.mixer.init()
except Exception as e:
    print(f"Peringatan: Gagal menginisialisasi Pygame Mixer ({e}). Lagu tidak dapat diputar otomatis.")

def play_regional_song(class_name):
    """Plays the corresponding regional song for the detected class."""
    if class_name not in config.SONG_MAP:
        return
        
    song_file = config.SONG_MAP[class_name]
    song_path = os.path.join(config.SONGS_DIR, song_file)
    
    if not os.path.exists(song_path):
        print(f"\n[LAGU] File lagu '{song_file}' tidak ditemukan di {config.SONGS_DIR}!")
        print(f" -> Silakan letakkan file lagu daerah di folder {config.SONGS_DIR} untuk memainkannya.")
        return
        
    try:
        print(f"\n[LAGU] Memainkan lagu '{song_file}' untuk daerah {class_name.upper()}...")
        pygame.mixer.music.load(song_path)
        pygame.mixer.music.play()
    except Exception as e:
        print(f"Gagal memutar lagu: {e}")

def preprocess_live_audio(audio_data):
    """Converts raw audio data array from sounddevice to MFCC tensor."""
    # Ensure audio is float32 and mono
    y = audio_data.flatten()
    
    # Extract MFCC
    mfcc = librosa.feature.mfcc(
        y=y, 
        sr=config.SAMPLE_RATE, 
        n_mfcc=config.N_MFCC, 
        n_fft=config.N_FFT, 
        hop_length=config.HOP_LENGTH
    )
    
    # Reshape for CNN input: (1, 1, n_mfcc, time_steps)
    mfcc = np.expand_dims(mfcc, axis=0) # Add channel dim
    mfcc = np.expand_dims(mfcc, axis=0) # Add batch dim
    return torch.tensor(mfcc, dtype=torch.float32)

def main():
    # Set PyTorch to CPU for inference as it's lightweight and faster to startup
    device = torch.device("cpu")
    print(f"Running inference on: {device}")
    
    model = AudioCNN(num_classes=len(config.CLASSES)).to(device)
    
    if not os.path.exists(config.MODEL_SAVE_PATH):
        print(f"Error: File model '{config.MODEL_SAVE_PATH}' tidak ditemukan!")
        print("Silakan jalankan training terlebih dahulu dengan perintah: python src/train.py")
        return
        
    model.load_state_dict(torch.load(config.MODEL_SAVE_PATH, map_location=device))
    model.eval()
    print("Model berhasil dimuat.")
    
    threshold = 0.75  # Confidence threshold to trigger song
    
    print("\n" + "="*50)
    print("APLIKASI DETEKSI SALAM DAERAH REAL-TIME")
    print("="*50)
    print("Tekan Ctrl+C untuk keluar.")
    print("Silakan bicara setelah muncul tulisan 'MENDENGARKAN...'")
    print("="*50 + "\n")
    
    try:
        while True:
            input("\nTekan ENTER lalu ucapkan salam...")
            print("MENDENGARKAN (1.5 detik)...")
            
            # Record audio from mic
            # channels=1 (mono), samplerate=16000
            recording = sd.rec(
                int(config.NUM_SAMPLES), 
                samplerate=config.SAMPLE_RATE, 
                channels=1, 
                dtype='float32'
            )
            sd.wait() # Wait until recording is finished
            print("Perekaman selesai. Menganalisis...")
            
            # Preprocess and predict
            inputs = preprocess_live_audio(recording).to(device)
            with torch.no_grad():
                outputs = model(inputs)
                probabilities = F.softmax(outputs, dim=1)[0]
                
            # Get prediction
            prob, pred_idx = torch.max(probabilities, dim=0)
            pred_class = config.CLASSES[pred_idx.item()]
            prob_percent = prob.item() * 100
            
            print(f"Hasil Klasifikasi: {pred_class.upper()} ({prob_percent:.2f}%)")
            
            # If a greeting class is detected above threshold, play its song
            if pred_class not in ["unknown", "silence"]:
                if prob.item() >= threshold:
                    play_regional_song(pred_class)
                else:
                    print(f"Sapaan '{pred_class.upper()}' terdeteksi tapi confidence ({prob_percent:.2f}%) di bawah threshold ({threshold*100}%).")
            else:
                if pred_class == "unknown":
                    print("Bukan kata sapaan daerah yang dikenal.")
                else:
                    print("Hening atau suara bising terdeteksi.")
                    
    except KeyboardInterrupt:
        print("\nAplikasi dihentikan. Sampai jumpa!")
        pygame.mixer.quit()

if __name__ == "__main__":
    main()
