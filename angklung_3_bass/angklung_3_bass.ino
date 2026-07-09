// ====================================================================
// PROGRAM ARDUINO NANO - DEDICATED ANGKLUNG 3 CONTROLLER (BASS)
// ====================================================================
// Arduino ini mengontrol Angklung 3 (Bass) secara mandiri.
// Menggunakan 8 pin langsung dan 1 IC shift register (8 output) untuk total 16 nada.

#include <Arduino.h>

// ====================================================================
// KONFIGURASI WIRING HARDWARE
// ====================================================================
// Nada 9 - 16: Terhubung langsung ke Transistor/MOSFET Driver (Setelah swap grup)
const int directPins[8] = {3, 2, A0, A1, A2, A3, A4, A5};

// Nada 1 - 8: Terhubung melalui Shift Register (74HC595) (Setelah swap grup)
const int dataPin   = 8;  // Pin DS (Serial Data Input)
const int clockPin  = 11; // Pin SH_CP (Shift Register Clock)
const int latchPin  = 12; // Pin ST_CP (Storage Register/Latch)

// Pemetaan Nama Nada Angklung 3 (Bass) untuk Serial Monitor
const char* namaNada[16] = {
  "e3", "f3", "f#3", "g3", "g#3", "a3", "a#3", "b3",
  "c4", "c#4", "d4", "d#4", "e4", "f4", "f#4", "g4"
};

int durasiGetar = 85; 

// Prototipe fungsi
void updateShiftRegister(byte data);
void mainkanBanyakNada(String input);

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
  byte data = 0;
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
      if (note >= 1 && note <= 16) {
        hasAny = true;
        if (note >= 9 && note <= 16) {
          int indexPin = note - 9;
          directPinsActive[indexPin] = true;
        } else if (note >= 1 && note <= 8) {
          int bitPosition = 8 - note; // Peta nada 1-8 ke bit 7-0
          data |= (1 << bitPosition);
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
    updateShiftRegister(data);

    // 3. Tahan selama durasi pemicu getar (hanya delay 1 kali saja)
    delay(durasiGetar);

    // 4. Matikan semua direct pins secara serentak
    for (int i = 0; i < 8; i++) {
      digitalWrite(directPins[i], LOW);
    }
    // 5. Matikan semua register shift secara serentak
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
