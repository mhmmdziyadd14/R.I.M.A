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

# Classes mapping
CLASSES = [
    "aceh",       # Peue Haba
    "batak",      # Horas
    "sunda",      # Sampurasun
    "jawa",       # Sugeng / Kulanuwun
    "bali",       # Om Swastyastu
    "sulsel",     # Salama'ki
    "papua",      # Amolongo / Apuse
    "unknown",    # Kelas negatif (kata lain)
    "silence"     # Kelas negatif (hening/ambient)
]

# Songs mapping corresponding to classes
SONG_MAP = {
    "aceh": "bungong_jeumpa.mp3",
    "batak": "sinanggar_tulo.mp3",
    "sunda": "manuk_dadali.mp3",
    "jawa": "suwe_ora_jamu.mp3",
    "bali": "mejangeran.mp3",
    "sulsel": "angin_mamiri.mp3",
    "papua": "apuse.mp3"
}
