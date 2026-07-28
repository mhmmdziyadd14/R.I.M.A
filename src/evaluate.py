import os
import sys
# Add project root to sys.path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import numpy as np
import torch
import torch.nn.functional as F

import src.config as config
from src.dataset import load_audio_universal, extract_mel_spectrogram
from src.model import get_model

def detect_model_type_from_state_dict(state_dict):
    """Auto-detects whether state_dict belongs to CNN, CRNN, or ResNet."""
    keys = list(state_dict.keys())
    if any("lstm" in k for k in keys):
        return "CRNN"
    elif any("layer1" in k for k in keys):
        return "ResNet"
    else:
        return "CNN"

def predict_single_file(file_path, model_path=None):
    """
    Predicts the greeting class for a single audio file (.m4a, .mp3, .wav).
    Auto-detects model architecture from state_dict.
    """
    if model_path is None:
        model_path = os.path.join(config.MODELS_DIR, "best_overall_model.pth")
        
    if not os.path.exists(model_path):
        print(f"Error: File model {model_path} tidak ditemukan!")
        return None
        
    device = torch.device("cpu")
    state_dict = torch.load(model_path, map_location=device)
    model_type = detect_model_type_from_state_dict(state_dict)
    
    print(f"Memuat model '{model_type}' dari: {model_path}")
    model = get_model(model_type, num_classes=len(config.CLASSES)).to(device)
    model.load_state_dict(state_dict)
    model.eval()
    
    # Load and preprocess audio
    print(f"Membaca file audio: {file_path}...")
    audio, sr = load_audio_universal(file_path, target_sr=config.SAMPLE_RATE)
    
    if len(audio) == 0:
        print("Gagal membaca data audio.")
        return None
        
    mel_spec = extract_mel_spectrogram(audio, sr=sr)
    # Shape: (1, 1, n_mels, time_steps)
    inputs = torch.tensor(mel_spec, dtype=torch.float32).unsqueeze(0).unsqueeze(0).to(device)
    
    with torch.no_grad():
        logits = model(inputs)
        probs = F.softmax(logits, dim=1)[0]
        
    pred_idx = torch.argmax(probs).item()
    pred_class = config.CLASSES[pred_idx]
    confidence = probs[pred_idx].item() * 100
    
    print("\n" + "="*50)
    print(" HASIL PREDIKSI SALAM DAERAH ")
    print("="*50)
    print(f"File Audio         : {os.path.basename(file_path)}")
    print(f"Arsitektur Model   : {model_type}")
    print(f"Prediksi Kelas     : {pred_class.upper()}")
    print(f"Tingkat Kepercayaan: {confidence:.2f}%")
    print("-" * 50)
    print("Probabilitas Semua Kelas:")
    for idx, cls_name in enumerate(config.CLASSES):
        print(f" - {cls_name:<15}: {probs[idx].item()*100:6.2f}%")
    print("="*50 + "\n")
    
    return pred_class, confidence

if __name__ == "__main__":
    if len(sys.argv) > 1:
        audio_file = sys.argv[1]
        predict_single_file(audio_file)
    else:
        print("Penggunaan: python src/evaluate.py <path_to_audio_file>")
        print("Contoh    : python src/evaluate.py d:/Magang/Dataset/Adil/Adil1.m4a")
