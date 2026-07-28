import os

# Paths
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATASET_RAW_DIR = os.path.join(BASE_DIR, "Dataset", "raw")
SONGS_DIR = os.path.join(BASE_DIR, "Songs")
MODEL_SAVE_PATH = os.path.join(BASE_DIR, "best_model.pth")

# Audio Config
SAMPLE_RATE = 16000
DURATION = 1.5  # seconds
NUM_SAMPLES = int(SAMPLE_RATE * DURATION)  # 24000 samples

# Feature Extraction (MFCC) Config
N_MFCC = 40
N_FFT = 1024
HOP_LENGTH = 512

# Training Config
BATCH_SIZE = 32
LEARNING_RATE = 0.001
EPOCHS = 30
VAL_SPLIT = 0.2

# Classes mapping (Urutan harus sama persis dengan y_labels.npy saat training)
CLASSES = [
    "horas",
    "sampurasun",
    "adilkatalino",
    "wawawa",
    "kulanuwun",
    "tabea",
    "peuhaba"
]

# Display label mapping (Format: "Kata Sapaan (Suku/Daerah)")
DISPLAY_MAP = {
    "horas": "Horas (Batak)",
    "sampurasun": "Sampurasun (Sunda)",
    "adilkatalino": "Adil Ka' Talino (Dayak)",
    "wawawa": "Wa Wa Wa (Papua)",
    "kulanuwun": "Kulanuwun (Jawa)",
    "tabea": "Tabea (Maluku/Minahasa)",
    "peuhaba": "Peue Haba (Aceh)"
}

# Songs mapping (Bisa disesuaikan nanti dengan file Klungbot)
SONG_MAP = {
    "horas": "BATAK/sinanggar_tulo.txt",
    "sampurasun": "SUNDA/manuk_dadali.txt"
}
