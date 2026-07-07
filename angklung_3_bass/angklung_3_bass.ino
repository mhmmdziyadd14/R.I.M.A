// ====================================================================
// PROGRAM ARDUINO NANO - DEDICATED ANGKLUNG 3 CONTROLLER (BASS)
// ====================================================================
// Arduino ini mengontrol Angklung 3 (Bass) secara mandiri.
// Menggunakan 8 pin langsung dan 1 IC shift register (8 output) untuk total 16 nada.

#include <Arduino.h>

// ====================================================================
// KONFIGURASI WIRING HARDWARE
// ====================================================================
// Nada 1 - 8: Terhubung langsung ke Transistor/MOSFET Driver
const int directPins[8] = {3, 2, A0, A1, A2, A3, A4, A5};

// Nada 9 - 16: Terhubung melalui Shift Register (74HC595)
const int dataPin   = 8;  // Pin DS (Serial Data Input)
const int clockPin  = 11; // Pin SH_CP (Shift Register Clock)
const int latchPin  = 12; // Pin ST_CP (Storage Register/Latch)

// Pemetaan Nama Nada Angklung 3 (Bass) untuk Serial Monitor
const char* namaNada[16] = {
  "e3", "f3", "f#3", "g3", "g#3", "a3", "a#3", "b3",
  "c4", "c#4", "d4", "d#4", "e4", "f4", "f#4", "g4"
};

const int durasiGetar = 150; 

// Prototipe fungsi kustom shift out
void updateShiftRegister(byte data);

void setup() {
  Serial.begin(9600);
  
  // Set output langsung ke LOW sebelum pinMode untuk menghindari getaran saat boot/koneksi
  for (int i = 0; i < 8; i++) {
    digitalWrite(directPins[i], LOW);
    pinMode(directPins[i], OUTPUT);
  }
  
  // Set pin register ke LOW sebelum pinMode
  digitalWrite(dataPin, LOW);
  digitalWrite(clockPin, LOW);
  digitalWrite(latchPin, LOW);
  
  pinMode(dataPin, OUTPUT);
  pinMode(clockPin, OUTPUT);
  pinMode(latchPin, OUTPUT);
  
  // Reset output register ke LOW di awal secara instan
  updateShiftRegister(0b00000000);

  // Menggunakan makro F() untuk menghemat SRAM
  Serial.println(F("=============================================="));
  Serial.println(F("Koneksi USB Serial Angklung 3 (Bass) Aktif!"));
  Serial.println(F("Menunggu perintah nada dari Laptop..."));
  Serial.println(F("=============================================="));
}

void loop() {
  if (Serial.available() > 0) {
    int noteNumber = Serial.parseInt();
    
    // Pastikan nomor nada valid (1 sampai 16)
    if (noteNumber >= 1 && noteNumber <= 16) {
      int indexNada = noteNumber - 1;
      
      mainkanNada(noteNumber);
      
      Serial.print(F("OK_"));
      Serial.println(namaNada[indexNada]);
    }
  }
}

// ====================================================================
// FUNGSI UTAMA PEMICU MOTOR
// ====================================================================
void mainkanNada(int note) {
  if (note >= 1 && note <= 8) {
    // Grup 1: Pin Langsung (Nada 1 - 8)
    int indexPin = note - 1;
    digitalWrite(directPins[indexPin], HIGH);
    delay(durasiGetar);
    digitalWrite(directPins[indexPin], LOW);
    
  } else if (note >= 9 && note <= 16) {
    // Grup 2: Shift Register (Nada 9 - 16)
    int bitPosition = 16 - note; // Peta nada 9-16 ke bit 7-0
    byte data = (1 << bitPosition);
    
    updateShiftRegister(data);
    delay(durasiGetar);
    
    // Matikan kembali setelah durasi getar selesai
    updateShiftRegister(0b00000000);
  }
}

// ====================================================================
// FUNGSI KUSTOM SHIFT OUT
// ====================================================================
void updateShiftRegister(byte data) {
  digitalWrite(latchPin, LOW);
  shiftOut(dataPin, clockPin, MSBFIRST, data);
  digitalWrite(latchPin, HIGH);
}
