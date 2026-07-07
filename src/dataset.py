import os
import glob
import numpy as np
import librosa
import torch
from torch.utils.data import Dataset
import src.config as config

def extract_mfcc(file_path):
    """Loads an audio file, pads/truncates to a fixed length, and extracts MFCC features."""
    try:
        # Load audio file (force target sample rate and mono channel)
        y, sr = librosa.load(file_path, sr=config.SAMPLE_RATE, mono=True)
        
        # Pad or truncate to exact length
        if len(y) < config.NUM_SAMPLES:
            y = np.pad(y, (0, config.NUM_SAMPLES - len(y)), 'constant')
        else:
            y = y[:config.NUM_SAMPLES]
            
        # Extract MFCC
        mfcc = librosa.feature.mfcc(
            y=y, 
            sr=config.SAMPLE_RATE, 
            n_mfcc=config.N_MFCC, 
            n_fft=config.N_FFT, 
            hop_length=config.HOP_LENGTH
        )
        
        # mfcc has shape (n_mfcc, time_steps)
        # Add channel dimension (1, n_mfcc, time_steps) for CNN input
        mfcc = np.expand_dims(mfcc, axis=0)
        return mfcc
    except Exception as e:
        print(f"Error loading {file_path}: {e}")
        return None

class GreetingDataset(Dataset):
    def __init__(self, data_dir, classes, is_train=True):
        self.data_dir = data_dir
        self.classes = classes
        self.is_train = is_train
        self.file_paths = []
        self.labels = []
        
        # Walk through the directories and find all wav files
        for label_idx, class_name in enumerate(self.classes):
            class_folder = os.path.join(data_dir, class_name)
            if not os.path.isdir(class_folder):
                # Auto-create directory to help user
                os.makedirs(class_folder, exist_ok=True)
                continue
                
            files = glob.glob(os.path.join(class_folder, "*.wav"))
            for f in files:
                self.file_paths.append(f)
                self.labels.append(label_idx)
                
        print(f"Loaded {len(self.file_paths)} audio files across {len(self.classes)} classes.")

    def __len__(self):
        return len(self.file_paths)

    def __getitem__(self, idx):
        file_path = self.file_paths[idx]
        label = self.labels[idx]
        
        # Extract MFCC
        features = extract_mfcc(file_path)
        
        if features is None:
            # Return dummy zero features if load fails to avoid crash
            # Calculate output time steps: num_samples / hop_length + 1 approximately
            time_steps = int(np.ceil(config.NUM_SAMPLES / config.HOP_LENGTH)) + 1
            features = np.zeros((1, config.N_MFCC, time_steps), dtype=np.float32)
            
        # Data Augmentation (only for training)
        if self.is_train and np.random.random() < 0.3:
            # Add random noise
            noise = np.random.normal(0, 0.05, features.shape)
            features = features + noise
            
        return torch.tensor(features, dtype=torch.float32), torch.tensor(label, dtype=torch.long)
