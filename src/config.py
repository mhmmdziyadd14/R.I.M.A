import os

# Base Directories
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATASET_DIR = os.path.join(BASE_DIR, "Dataset")
SONGS_DIR = os.path.join(BASE_DIR, "Songs")
MODELS_DIR = os.path.join(BASE_DIR, "models_saved")
OUTPUT_DIR = os.path.join(BASE_DIR, "output_evaluations")

# Audio Config
SAMPLE_RATE = 16000
N_MELS = 64
N_FFT = 1024
HOP_LENGTH = 512
N_MFCC = 40

# Training Config
BATCH_SIZE = 8
LEARNING_RATE = 0.001
EPOCHS = 50
VAL_SPLIT = 0.2
RANDOM_SEED = 42

# Classes mapping from Dataset folder names
CLASSES = [
    "Adil",          # Dayak (Adil Ka' Talino)
    "Horas",         # Batak
    "Kula Nuwun",    # Jawa
    "Peuhaba",       # Aceh
    "Sampurasun",    # Sunda
    "Tabea",         # Minahasa / Maluku
    "Wawawa"         # Papua
]

# Songs mapping for inference audio playback
SONG_MAP = {
    "Adil": "adil_dayak.mp3",
    "Horas": "sinanggar_tulo.mp3",
    "Kula Nuwun": "suwe_ora_jamu.mp3",
    "Peuhaba": "bungong_jeumpa.mp3",
    "Sampurasun": "manuk_dadali.mp3",
    "Tabea": "mejangeran.mp3",
    "Wawawa": "apuse.mp3"
}
