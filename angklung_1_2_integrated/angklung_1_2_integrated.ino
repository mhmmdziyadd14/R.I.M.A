// ====================================================================
// PROGRAM ARDUINO NANO - INTEGRATED ANGKLUNG 1 & 2 CONTROLLER
// ====================================================================

#include <Arduino.h>

// ====================================================================
// KONFIGURASI WIRING HARDWARE
// ====================================================================
// Angklung 1 (Lokal)
const int directPins[8] = {3, 2, A0, A1, A2, A3, A4, A5}; // Nada 9 - 16 (Setelah swap grup)
const int dataPin8  = 8;  // DS Angklung 1 (Nada 1 - 8 setelah swap grup)

// Angklung 2 (Via LAN)
const int dataPin9  = 9;  // DS Angklung 2 IC Pertama (Nada 17 - 24)
const int dataPin10 = 10; // DS Angklung 2 IC Kedua (Nada 25 - 32)

// Sinyal Bersama (Lokal & LAN)
const int clockPin  = 11; // SH_CP (Clock)
const int latchPin  = 12; // ST_CP (Latch)

// ====================================================================
// PEMETAAN NADA
// ====================================================================
const char* namaNada[32] = {
  // Angklung 1 (High): Nada 1 - 16
  "g4", "a4", "a#4", "b4", "c5", "d5", "e5", "f5",
  "f#5", "g5", "a5", "a#5", "b5", "c6", "d6", "e6",
  // Angklung 2 (Medium): Nada 17 - 32
  "f4", "f#4", "g#4", "c#5", "d#5", "g#5", "c#6", "d#6",
  "f6", "f#6", "g6", "g#6", "a6", "a#6", "b6", "c7"
};

int durasiGetar = 85; 

// Prototipe fungsi
void updateAllShiftRegisters(byte d8, byte d9, byte d10);
void mainkanBanyakNada(String input);

void setup() {
  Serial.begin(9600);
  
  // Set output langsung ke LOW sebelum pinMode untuk menghindari getaran saat boot/koneksi
  for (int i = 0; i < 8; i++) {
    digitalWrite(directPins[i], LOW);
    pinMode(directPins[i], OUTPUT);
  }
  
  // Set pin register ke LOW sebelum pinMode
  digitalWrite(dataPin8, LOW);
  digitalWrite(dataPin9, LOW);
  digitalWrite(dataPin10, LOW);
  digitalWrite(clockPin, LOW);
  digitalWrite(latchPin, LOW);
  
  // Setup Pin Shift Register
  pinMode(dataPin8, OUTPUT);
  pinMode(dataPin9, OUTPUT);
  pinMode(dataPin10, OUTPUT);
  pinMode(clockPin, OUTPUT);
  pinMode(latchPin, OUTPUT);
  
  // Reset semua output register ke LOW di awal secara instan
  updateAllShiftRegisters(0, 0, 0);

  // Menggunakan makro F() untuk menghemat SRAM
  Serial.println(F("=============================================="));
  Serial.println(F("Koneksi USB Serial Angklung 1 & 2 Terintegrasi!"));
  Serial.println(F("Menunggu perintah nada dari Laptop..."));
  Serial.println(F("=============================================="));
}

void loop() {
  if (Serial.available() > 0) {
    String input = Serial.readStringUntil('\n');
    input.trim();
    if (input.length() > 0) {
      if (input.startsWith("P")) {
        int newDur = input.substring(1).toInt();
        if (newDur >= 10 && newDur <= 200) {
          durasiGetar = newDur;
          Serial.println(F("DUR_OK"));
        } else {
          Serial.println(F("DUR_ERR"));
        }
      } else {
        mainkanBanyakNada(input);
        Serial.println(F("OK")); // Kirim respon balik ke laptop
      }
    }
  }
}

// ====================================================================
// FUNGSI UTAMA PEMICU BANYAK MOTOR SIMULTAN
// ====================================================================
void mainkanBanyakNada(String input) {
  byte data8 = 0;
  byte data9 = 0;
  byte data10 = 0;
  bool directPinsActive[8] = {false, false, false, false, false, false, false, false};
  bool hasAny = false;

  int startIndex = 0;
  while (true) {
    int commaIndex = input.indexOf(',', startIndex);
    String noteStr;
    if (commaIndex == -1) {
      noteStr = input.substring(startIndex);
    } else {
      noteStr = input.substring(startIndex, commaIndex);
    }
    noteStr.trim();
    if (noteStr.length() > 0) {
      int note = noteStr.toInt();
      if (note >= 1 && note <= 32) {
        hasAny = true;
        if (note >= 9 && note <= 16) {
          int indexPin = note - 9;
          directPinsActive[indexPin] = true;
        } else {
          if (note >= 1 && note <= 8) {
            data8 |= (1 << (8 - note));
          }
          else if (note >= 17 && note <= 24) {
            data9 |= (1 << (24 - note));
          }
          else if (note >= 25 && note <= 32) {
            data10 |= (1 << (32 - note));
          }
        }
      }
    }
    if (commaIndex == -1) {
      break;
    }
    startIndex = commaIndex + 1;
  }

  if (hasAny) {
    // 1. Nyalakan direct pins yang aktif secara serentak
    for (int i = 0; i < 8; i++) {
      if (directPinsActive[i]) {
        digitalWrite(directPins[i], HIGH);
      }
    }
    // 2. Nyalakan register shift yang aktif secara serentak
    updateAllShiftRegisters(data8, data9, data10);

    // 3. Tahan selama durasi pemicu getar (hanya delay 1 kali saja)
    delay(durasiGetar);

    // 4. Matikan semua direct pins secara serentak
    for (int i = 0; i < 8; i++) {
      digitalWrite(directPins[i], LOW);
    }
    // 5. Matikan semua register shift secara serentak
    updateAllShiftRegisters(0, 0, 0);
  }
}

// ====================================================================
// FUNGSI KUSTOM PARALLEL SHIFT OUT
// ====================================================================
void updateAllShiftRegisters(byte d8, byte d9, byte d10) {
  digitalWrite(latchPin, LOW); // Tahan output agar motor tidak bergetar saat data dikirim

  // Kirim 8 bit data secara berurutan (dari MSB ke LSB)
  for (int i = 7; i >= 0; i--) {
    // Siapkan nilai 1 or 0 di masing-masing jalur Data
    digitalWrite(dataPin8, bitRead(d8, i));
    digitalWrite(dataPin9, bitRead(d9, i));
    digitalWrite(dataPin10, bitRead(d10, i));
    
    // Pompa sinyal Clock SATU KALI agar dibaca oleh ke-3 IC secara bersamaan
    digitalWrite(clockPin, HIGH);
    digitalWrite(clockPin, LOW);
  }

  digitalWrite(latchPin, HIGH); // Buka kunci, eksekusi tegangan ke motor DC
}
