import os
import sys
# Add project root to sys.path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import glob
import numpy as np
import librosa
import torch
from torch.utils.data import Dataset
import av
import src.config as config

def load_audio_universal(file_path, target_sr=config.SAMPLE_RATE):
    """
    Universal audio loader supporting .m4a, .mp3, .wav, .flac, .ogg.
    Uses PyAV (av) for AAC/M4A decoding and librosa as fallback.
    Resamples to target_sr and converts to mono float32 array.
    """
    try:
        container = av.open(file_path)
        stream = container.streams.audio[0]
        sr_orig = stream.rate
        frames = [f.to_ndarray() for f in container.decode(audio=0)]
        if not frames:
            raise ValueError("No audio frames decoded by PyAV")
            
        audio = np.concatenate(frames, axis=1)
        # Convert stereo/multi-channel to mono
        if audio.shape[0] > 1:
            audio = np.mean(audio, axis=0)
        else:
            audio = audio.squeeze(0)
            
        audio = audio.astype(np.float32)
        # Normalize int16 or int32 audio to [-1.0, 1.0]
        max_val = np.max(np.abs(audio))
        if max_val > 1.0:
            audio = audio / 32768.0
            
        # Resample if needed
        if sr_orig != target_sr:
            audio = librosa.resample(audio, orig_sr=sr_orig, target_sr=target_sr)
            
        return audio, target_sr
    except Exception as e:
        # Fallback to librosa.load
        try:
            audio, sr = librosa.load(file_path, sr=target_sr, mono=True)
            return audio, sr
        except Exception as e_fallback:
            print(f"Error loading {file_path}: {e_fallback}")
            return np.zeros(target_sr, dtype=np.float32), target_sr

def extract_mel_spectrogram(audio, sr=config.SAMPLE_RATE):
    """
    Extracts Mel-Spectrogram features from 1D audio array.
    Returns 2D array of shape (N_MELS, Time_Frames).
    """
    mel_spec = librosa.feature.melspectrogram(
        y=audio,
        sr=sr,
        n_mels=config.N_MELS,
        n_fft=config.N_FFT,
        hop_length=config.HOP_LENGTH
    )
    # Convert to log scale (dB)
    mel_db = librosa.power_to_db(mel_spec, ref=np.max)
    # Normalize dB features
    mel_norm = (mel_db - mel_db.mean()) / (mel_db.std() + 1e-6)
    return mel_norm

class GreetingDataset(Dataset):
    """
    PyTorch Dataset for regional greeting audio files.
    Scans DATASET_DIR subfolders and loads audio files of all supported formats.
    """
    def __init__(self, data_dir, classes, is_train=True):
        self.data_dir = data_dir
        self.classes = classes
        self.is_train = is_train
        self.file_paths = []
        self.labels = []
        
        supported_exts = ["*.m4a", "*.mp3", "*.wav", "*.ogg", "*.flac"]
        
        for label_idx, class_name in enumerate(self.classes):
            class_folder = os.path.join(data_dir, class_name)
            if not os.path.exists(class_folder):
                continue
                
            files_found = []
            for ext in supported_exts:
                files_found.extend(glob.glob(os.path.join(class_folder, ext)))
                files_found.extend(glob.glob(os.path.join(class_folder, ext.upper())))
                
            files_found = list(set(files_found))
            
            for f in files_found:
                self.file_paths.append(f)
                self.labels.append(label_idx)
                
        print(f"Loaded {len(self.file_paths)} audio files across {len(self.classes)} classes.")

    def __len__(self):
        return len(self.file_paths)

    def __getitem__(self, idx):
        file_path = self.file_paths[idx]
        label = self.labels[idx]
        
        audio, sr = load_audio_universal(file_path, target_sr=config.SAMPLE_RATE)
        
        if self.is_train and np.random.random() < 0.4:
            noise_level = np.random.uniform(0.001, 0.015)
            audio = audio + noise_level * np.random.normal(0, 1, audio.shape)
            
        if self.is_train and np.random.random() < 0.3:
            gain = np.random.uniform(0.7, 1.3)
            audio = audio * gain

        mel_spec = extract_mel_spectrogram(audio, sr=config.SAMPLE_RATE)
        mel_spec = np.expand_dims(mel_spec, axis=0)
        
        return torch.tensor(mel_spec, dtype=torch.float32), torch.tensor(label, dtype=torch.long)

def pad_collate_fn(batch):
    """
    Custom collate function for DataLoader that dynamically pads Mel-Spectrogram 
    time frames to the maximum length in the current mini-batch.
    """
    specs, labels = zip(*batch)
    
    max_time = max(spec.shape[2] for spec in specs)
    
    padded_specs = []
    for spec in specs:
        time_len = spec.shape[2]
        if time_len < max_time:
            pad_amount = max_time - time_len
            padded = torch.nn.functional.pad(spec, (0, pad_amount), mode='constant', value=0)
        else:
            padded = spec
        padded_specs.append(padded)
        
    padded_specs = torch.stack(padded_specs, dim=0)
    labels = torch.stack(labels, dim=0)
    
    return padded_specs, labels
