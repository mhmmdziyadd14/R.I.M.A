# Proyek Klasifikasi Kata Sapaan Daerah Indonesia (Speech Keyword Spotting)

Proyek ini bertujuan untuk mendeteksi kata sapaan (salam) khas dari berbagai daerah di Indonesia melalui input mikrofon secara real-time. Ketika kata sapaan tertentu terdeteksi dengan tingkat kepercayaan (confidence) yang tinggi, sistem akan secara otomatis memainkan lagu daerah asal sapaan tersebut.

---

## 1. Daftar Daerah Inti, Kata Sapaan, dan Lagu Daerah

Berikut adalah daftar daerah representatif dari Sabang sampai Merauke yang dipilih karena keunikan kata sapaan dan popularitas lagu daerahnya:

| No | Daerah | Kata Sapaan (Keyword) | Lagu Daerah | Keterangan |
|----|--------|-----------------------|-------------|------------|
| 1 | **Aceh** | "Peue Haba" | Bungong Jeumpa | Berarti "Apa Kabar" dalam bahasa Aceh |
| 2 | **Sumatera Utara (Batak)** | "Horas" | Sinanggar Tulo | Salam khas suku Batak Toba |
| 3 | **Jawa Barat (Sunda)** | "Sampurasun" | Manuk Dadali | Salam hormat khas Sunda |
| 4 | **Jawa Tengah/Timur (Jawa)**| "Sugeng" / "Kulanuwun" | Suwe Ora Jamu | Sugeng (Selamat) atau Kulanuwun (Permisi) |
| 5 | **Bali** | "Om Swastyastu" | Mejangeran | Salam keagamaan/budaya Hindu Bali |
| 6 | **Sulawesi Selatan (Makassar)**| "Salama'ki" | Angin Mamiri | Berarti "Selamat untukmu/Semoga selamat" |
| 7 | **Papua** | "Amolongo" / "Apuse" | Apuse / Yamko Rambe Yamko | Salam khas dari suku Amungme / Papua |

---

## 2. Struktur Direktori Proyek

Buat struktur file berikut di folder kerja Anda (`d:\Magang`):

```text
d:\Magang\
│
├── Dataset/
│   ├── raw/                      # Letakkan rekaman suara mentah (.wav) di sini
│   │   ├── aceh/                 # Folder berisi ucapan "Peue Haba"
│   │   ├── batak/                # Folder berisi ucapan "Horas"
│   │   ├── sunda/                # Folder berisi ucapan "Sampurasun"
│   │   ├── jawa/                 # Folder berisi ucapan "Sugeng" / "Kulanuwun"
│   │   ├── bali/                 # Folder berisi ucapan "Om Swastyastu"
│   │   ├── sulsel/               # Folder berisi ucapan "Salama'ki"
│   │   ├── papua/                # Folder berisi ucapan "Amolongo"
│   │   ├── unknown/              # KELAS NEGATIF: Kata-kata lain di luar salam di atas
│   │   └── silence/              # KELAS NEGATIF: Suara hening, desah nafas, kipas angin, dll.
│   └── processed/                # (Opsional) Tempat penyimpanan fitur hasil ekstraksi
│
├── Songs/                        # Folder untuk menyimpan file lagu daerah (.mp3 / .wav)
│   ├── bungong_jeumpa.mp3
│   ├── sinanggar_tulo.mp3
│   ├── manuk_dadali.mp3
│   ├── suwe_ora_jamu.mp3
│   ├── mejangeran.mp3
│   ├── angin_mamiri.mp3
│   └── apuse.mp3
│
├── src/
│   ├── __init__.py
│   ├── config.py                 # Konfigurasi parameter (sample rate, model, dll.)
│   ├── dataset.py                # Dataset PyTorch dan ekstraksi fitur MFCC
│   ├── model.py                  # Arsitektur Neural Network (CNN 2D)
│   ├── train.py                  # Script pelatihan model
│   └── inference.py              # Script testing real-time dengan mic
│
├── requirements.txt              # Daftar library Python yang dibutuhkan
└── README.md                     # Dokumentasi ini
```

---

## 3. Mengapa 900 Rekaman Sebelumnya Belum Akurat? (Analisis & Solusi)

Meskipun 900 data terdengar cukup banyak, model speech recognition/classification sering kali gagal karena faktor-faktor berikut:

### A. Kurangnya Variasi Pembicara (Speaker Overfitting)
* **Masalah**: Jika 900 rekaman dibuat oleh 1 atau 2 orang saja, model hanya akan belajar mengenali karakteristik unik suara orang tersebut (timbre, pitch, resonansi tenggorokan), bukan pola fonetik dari kata sapaan itu sendiri.
* **Solusi**: Libatkan **minimal 5-10 orang berbeda** (campuran laki-laki dan perempuan dengan rentang usia dan karakter suara berbeda) untuk merekam kata-kata sapaan tersebut.

### B. Ketiadaan Kelas Negatif ("Unknown" & "Silence")
* **Masalah**: Jika model hanya dilatih mengenali kelas salam (misal: "Horas", "Sampurasun"), maka suara hembusan nafas, ketukan meja, batuk, atau kata lain seperti "makan", "tidur" akan **dipaksa** diklasifikasikan masuk ke salah satu kelas salam dengan probabilitas tertinggi. Ini menyebabkan banyak *false positive* (salah deteksi).
* **Solusi**: Wajib menambahkan kelas `unknown` (berisi rekaman kata sehari-hari yang bukan salam) dan kelas `silence` (berisi rekaman sunyi, derau kipas angin, ketukan laptop, dll.).

### C. Kurangnya Variasi Intonasi, Artikulasi, dan Kecepatan
* **Masalah**: Rekaman terlalu seragam (misal: semuanya diucapkan dengan jelas, datar, dan kecepatan sedang). Di dunia nyata, orang berbicara dengan sangat dinamis.
* **Solusi**: Lakukan perekaman dengan skenario berikut:
  - **Intonasi**: Nada ceria/tinggi, datar/normal, dan lemas/rendah.
  - **Artikulasi**: Artikulasi sangat jelas (formal), artikulasi santai/cepat (seperti berbicara sehari-hari), dan agak berbisik.
  - **Kecepatan**: Sangat cepat (tempo tinggi) dan lambat.

### D. Format Audio & Kebisingan (Noise)
* **Masalah**: Format audio terkompresi (.mp3/.m4a) dapat menghilangkan detail frekuensi tinggi. Selain itu, jika semua data direkam di tempat yang sangat hening, model akan langsung bingung begitu mendengar suara mic di ruangan ber-AC atau bising.
* **Solusi**:
  - Rekam dengan format **WAV mono, sample rate 16.000 Hz (16 kHz)**. Ini adalah standar industri untuk pengenalan suara karena cukup ringan namun mempertahankan kualitas pita suara manusia.
  - Tambahkan augmentasi data (misal menambahkan white noise tipis secara programatik saat training) atau rekam beberapa sampel di kondisi ruangan bising.

---

## 4. Panduan Pengambilan Dataset Baru

Untuk performa optimal, ikuti panduan berikut:
1. **Jumlah Sampel per Kelas**: Targetkan **150 - 200 sampel audio per kelas**.
   - Dengan 9 kelas (7 salam daerah + 1 unknown + 1 silence), total dataset ideal adalah **1.350 - 1.800 file audio**.
2. **Durasi Audio**: Potong setiap rekaman menjadi durasi **1.5 detik**. Kata sapaan pendek rata-rata selesai diucapkan dalam waktu kurang dari 1.5 detik. Durasi yang seragam sangat mempermudah pemrosesan neural network.
3. **Penamaan File**: Beri nama file secara teratur agar mudah dibaca, contoh: `aceh_001_pria.wav`, `aceh_002_wanita.wav`.

---

## 5. Cara Menjalankan Proyek

1. **Instalasi Dependensi**:
   Pastikan Python sudah terinstal di Windows Anda. Jalankan perintah berikut untuk menginstal pustaka yang dibutuhkan:
   ```bash
   pip install -r requirements.txt
   ```
2. **Siapkan Dataset**:
   Isi folder `Dataset/raw/` dengan file rekaman `.wav` berdurasi 1.5 detik sesuai foldernya.
3. **Latih Model**:
   Jalankan script training untuk mengekstrak MFCC, melatih CNN, dan menyimpan bobot model terbaik (`best_model.pth`):
   ```bash
   python src/train.py
   ```
4. **Jalankan Deteksi Real-Time**:
   Hubungkan mikrofon Anda, pastikan file lagu di folder `Songs/` sudah ada, lalu jalankan script berikut untuk mulai mendeteksi kata sapaan:
   ```bash
   python src/inference.py
   ```
