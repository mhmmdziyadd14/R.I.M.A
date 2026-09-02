import numpy as np
import librosa
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
