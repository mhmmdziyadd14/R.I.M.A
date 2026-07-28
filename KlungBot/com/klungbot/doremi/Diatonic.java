/*     */ package com.klungbot.doremi;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Diatonic
/*     */   extends Scale
/*     */ {
/*  33 */   public static final int[] numSymbols = new int[] { 48, 49, 50, 51, 52, 53, 54, 55 };
/*  34 */   public static final char[] wholeNoteSymbols = new char[] { 'c', 'd', 'e', 'f', 'g', 'a', 'b' };
/*     */ 
/*     */   
/*  37 */   public static final String[] noteSymbols = new String[] { "c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "b" };
/*     */ 
/*     */   
/*  40 */   public static final String[] bigNoteSymbols = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
/*     */ 
/*     */ 
/*     */   
/*  44 */   public static final String[] keySymbols = new String[] { "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   public static final int[] diatonicNumValues = new int[] { 0, 25, 27, 29, 30, 32, 34, 36 };
/*     */   
/*  52 */   public static final int[] diatonicWholeSymbolValues = new int[] { 25, 27, 29, 30, 32, 34, 36 };
/*     */   
/*  54 */   public static final int[] diatonicSymbolValues = new int[] { 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 };
/*     */   
/*  56 */   public static final int[] diatonicKeyValues = new int[] { -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   int[] numValues = diatonicNumValues;
/*  67 */   int[] symbolValues = diatonicSymbolValues;
/*  68 */   int[] wholeSymbolValues = diatonicWholeSymbolValues;
/*  69 */   int[] keyValues = diatonicKeyValues; public static final int OCTAVE = 12; static final int DIATONIC_OCTAVE = 12; public static final int C = 25; public static final int Cis = 26; public static final int D = 27; public static final int Dis = 28;
/*     */   public static final int E = 29;
/*     */   public static final int F = 30;
/*     */   
/*     */   public String getDefaultChordType() {
/*  74 */     return "M";
/*     */   }
/*     */   public static final int Fis = 31; public static final int G = 32; public static final int Gis = 33; public static final int A = 34; public static final int Ais = 35; public static final int B = 36; public static final int KEY_OFFSET = -5; public static final String defaultMajorChord = "M";
/*     */   public int getOctave() {
/*  78 */     return 12;
/*     */   }
/*     */   
/*     */   public String symbolOfIndex(int n) {
/*  82 */     return noteSymbols[(n - 1) % noteSymbols.length];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int indexOfSymbol(char symbol) throws Exception {
/*  94 */     for (int i = 0; i < wholeNoteSymbols.length; i++) {
/*  95 */       if (symbol == wholeNoteSymbols[i]) return this.wholeSymbolValues[i]; 
/*     */     } 
/*  97 */     throw new Exception("Unknown note symbol (" + symbol + ")");
/*     */   }
/*     */   
/*     */   public int indexOfSymbol(String symbol) throws Exception {
/* 101 */     int note = indexOfSymbol(symbol.charAt(0));
/*     */ 
/*     */     
/* 104 */     for (int i = 1; i < symbol.length(); i++) {
/* 105 */       char ch = symbol.charAt(i);
/* 106 */       switch (ch) { case '#':
/* 107 */           note++; break;
/* 108 */         case '\'': note += 12; break;
/* 109 */         case '"': note += 24; break;
/* 110 */         case ',': note -= 12; break;
/* 111 */         case ';': note -= 24; break;
/* 112 */         case '0': note -= 48; break;
/* 113 */         case '1': note -= 36; break;
/* 114 */         case '2': note -= 24; break;
/* 115 */         case '3': note -= 12; break;
/*     */         case '4': break;
/* 117 */         case '5': note += 12; break;
/* 118 */         case '6': note += 24; break;
/* 119 */         case '7': note += 36; break;
/* 120 */         case '8': note += 48; break;
/* 121 */         case '9': note += 60; break;
/*     */         default:
/* 123 */           throw new Exception("Unknown note symbol (" + symbol + ")"); }
/*     */     
/*     */     } 
/* 126 */     if (note <= 0 || note > 61)
/* 127 */       throw new Exception("Note " + symbol + " is out of bound"); 
/* 128 */     return note;
/*     */   }
/*     */   
/*     */   public int indexOfNum(int n) throws Exception {
/* 132 */     if (n > 0 && n < this.numValues.length) {
/* 133 */       return this.numValues[n];
/*     */     }
/* 135 */     throw new Exception("Unknown numeric note symbol (" + n + ")");
/*     */   }
/*     */   
/*     */   public int indexOfNum(char n) throws Exception {
/* 139 */     for (int i = 0; i < numSymbols.length; i++) {
/* 140 */       if (n == numSymbols[i]) return this.numValues[i]; 
/*     */     } 
/* 142 */     throw new Exception("Unknown numeric note symbol (" + n + ")");
/*     */   }
/*     */   
/*     */   public int indexOfNum(int n, int shift) throws Exception {
/* 146 */     return indexOfNum(n) + shift;
/*     */   }
/*     */   public int indexOfNum(char n, int shift) throws Exception {
/* 149 */     return indexOfNum(n) + shift;
/*     */   }
/*     */   
/*     */   public int shiftOfKey(String key) throws Exception {
/* 153 */     for (int i = 0; i < keySymbols.length; i++) {
/* 154 */       if (key.equals(keySymbols[i])) {
/* 155 */         return this.keyValues[i];
/*     */       }
/*     */     } 
/* 158 */     throw new Exception("Unknown key symbol (" + key + ")");
/*     */   }
/*     */   
/*     */   public String indexToSymbol(byte idx) {
/* 162 */     int i = (idx - 1) % 12;
/* 163 */     int octave = (idx - 1) / 12 + 2;
/* 164 */     return noteSymbols[i] + octave;
/*     */   }
/*     */   
/*     */   public static String nameOfMidiNote(int midi) {
/* 168 */     int octave = midi / 12 - 2;
/* 169 */     int idx = midi % 12;
/* 170 */     return bigNoteSymbols[idx] + octave;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\Diatonic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */