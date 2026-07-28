# Rencana Kerja Training Data Suara Salam Daerah (210 Dataset)

Rencana ini menyusun secara sistematis alur kerja pelatihan model pengenalan kata sapaan (greeting speech classification) dari **210 dataset rekaman** yang mencakup **7 kelas bahasa daerah di Indonesia**, mendukung variasi durasi audio dan format file audio (`.m4a`, `.mp3`, `.wav`, dll.).

---

## 1. Inventarisasi Dataset saat Ini

Berdasarkan inspeksi direktori `d:\Magang\Dataset`, ditemukan **210 file audio** (30 file per kelas) pada 7 folder daerah:

| No | Kelas (Folder) | Bahasa / Daerah | Contoh Keyword | Sampel | Format Ditemukan |
|----|----------------|-----------------|----------------|--------|------------------|
| 1  | `Adil`         | Dayak (Kalimantan) | *Adil Ka' Talino* | 30 file | `.m4a`, `.mp3` |
| 2  | `Horas`        | Batak (Sumut)   | *Horas*        | 30 file | `.m4a`, `.mp3` |
| 3  | `Kula Nuwun`   | Jawa (Jateng/Jatim) | *Kula Nuwun* / *Sugeng* | 30 file | `.m4a`, `.mp3` |
| 4  | `Peuhaba`      | Aceh            | *Peue Haba*    | 30 file | `.m4a`, `.mp3` |
| 5  | `Sampurasun`   | Sunda (Jabar)   | *Sampurasun*   | 30 file | `.m4a`, `.mp3` |
| 6  | `Tabea`        | Minahasa/Maluku | *Tabea*        | 30 file | `.m4a`, `.mp3` |
| 7  | `Wawawa`       | Papua           | *Wawawa*       | 30 file | `.m4a`, `.mp3` |

---

## 2. Strategi Preprocessing & Fitur Audio

1. **Format File Universal (`.m4a`, `.mp3`, `.wav`, dll.):**
   - Menggunakan decoder PyAV (`av`) dikombinasikan dengan `librosa` untuk mampu membaca format AAC/M4A, MP3, WAV secara transparan tanpa error *unrecognized format*.
2. **Standardisasi Frekuensi (Uniform Sample Rate):**
   - Seluruh audio secara otomatis di-resample ke frekuensi target **16.000 Hz (16 kHz) mono** saat loading.
3. **Fleksibilitas Panjang Audio (Variable Audio Length):**
   - Fitur diekstraksi dalam bentuk **Mel-Spectrogram (64 Mel bins)** atau **MFCC (40 coefficients)**.
   - Panjang waktu (jumlah frame spektrogram) diperbolehkan bervariasi.
   - Pada lapisan neural network, digunakan **`nn.AdaptiveAvgPool2d((8, 8))`** sehingga bentuk spektrogram berapapun lebarnya akan dipetakan secara mulus menjadi fitur 2D tetap untuk diklasifikasikan oleh Fully Connected Layer.
4. **Augmentasi Data (Training Enhancement):**
   - Penambahan *Gaussian noise* tipis, *pitch shift*, dan variasi amplitudo volume untuk mencegah *overfitting* mengingat jumlah data 210 sampel.

---

## 3. Arsitektur Model (`GreetingCNN`)

Bentuk jaringan syaraf konvolusi (2D CNN):
```text
Input Mel-Spectrogram (Batch, 1, 64, Time_Frames)
   │
   ├── Conv2D(1 -> 32, kernel=3, stride=1, padding=1) + BatchNorm + ReLU + MaxPool2D(2, 2)
   ├── Conv2D(32 -> 64, kernel=3, stride=1, padding=1) + BatchNorm + ReLU + MaxPool2D(2, 2)
   ├── Conv2D(64 -> 128, kernel=3, stride=1, padding=1) + BatchNorm + ReLU + MaxPool2D(2, 2)
   │
   ├── AdaptiveAvgPool2d((8, 8))  <-- Penyesuai Panjang Audio Bebas
   ├── Flatten (128 * 8 * 8 = 8192)
   ├── Linear(8192 -> 256) + ReLU + Dropout(0.4)
   └── Linear(256 -> 7 Classes)
```

---

## 4. Evaluasi & Metrik Output

Proses training akan menghasilkan laporan evaluasi komprehensif:
1. **Grafik Loss & Akurasi (`training_evaluation_plots.png`):** Kurva grafik Train/Val Loss dan Train/Val Accuracy per epoch.
2. **Confusion Matrix (`evaluation_confusion_matrix.png` & Text Grid):** Menunjukkan detail prediksi benar vs salah per kategori sapaan.
3. **Classification Report:** Metrik *Precision*, *Recall*, *F1-Score*, dan *Accuracy* keseluruhan.
4. **Penyimpanan Checkpoint Model:** Menyimpan `best_model.pth` berdasarkan nilai akurasi evaluasi tertinggi.

---

## 5. Komponen File yang Akan Dibuat / Diperbarui

### [MODIFY] [config.py](file:///d:/Magang/src/config.py)
Menyesuaikan nama kelas sesuai folder aktual (`Adil`, `Horas`, `Kula Nuwun`, `Peuhaba`, `Sampurasun`, `Tabea`, `Wawawa`) dan jalur direktori dataset.

### [MODIFY] [dataset.py](file:///d:/Magang/src/dataset.py)
Menambahkan universal audio loader berbasis PyAV + Librosa untuk mendukung `.m4a`, `.mp3`, dan `.wav`, serta fungsi pencarian file multi-ekstensi.

### [MODIFY] [model.py](file:///d:/Magang/src/model.py)
Memperbarui arsitektur CNN 2D dengan *Adaptive Average Pooling* untuk fleksibilitas panjang audio.

### [MODIFY] [train.py](file:///d:/Magang/src/train.py)
Mengimplementasikan alur pembagian dataset (Stratified Train-Val Split 80:20), pencatatan loss/akurasi, dan pembuatan plot grafik serta Confusion Matrix.

### [NEW] [evaluate.py](file:///d:/Magang/src/evaluate.py)
Script khusus untuk mengevaluasi dataset secara mendalam dan mencetak Confusion Matrix & Classification Report.

---

## Plan Verification

Setelah script training selesai dibuat dan dijalankan:
1. Jalankan `python src/train.py` untuk melatih model pada 210 file audio.
2. Verifikasi bahwa file `.m4a` dan `.mp3` terbaca tanpa error.
3. Cek hasil evaluasi, plot `training_evaluation_plots.png`, dan `evaluation_confusion_matrix.png`.
4. Jalankan pengujian pada file sampel menggunakan `python src/evaluate.py`.
