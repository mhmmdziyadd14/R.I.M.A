/*     */ package com.klungbot.doremi;
/*     */ 
/*     */ import com.klungbot.util.Options;
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
/*     */ public abstract class Scale
/*     */ {
/*     */   public static final int INDEX_MID = 25;
/*     */   public static final int BIT_MIN = 1;
/*     */   public static final int MIDI_MID = 60;
/*     */   public static final int MIDI_OFFSET = 35;
/*  31 */   public static String[] doremiChordSymbols = new String[] { "1", "1#", "2", "2#", "3", "4", "4#", "5", "5#", "6", "6#", "7" };
/*     */   
/*     */   public static final int INDEX_MIN = 1;
/*     */   
/*     */   public static final int INDEX_MAX = 61;
/*     */   
/*     */   public static final int INDEX_MASK = 255;
/*     */   
/*     */   public static final int LEGATO = 256;
/*     */   
/*     */   public static final int STACATO = 512;
/*     */   
/*     */   public static final int ACCENT = 1024;
/*     */   
/*     */   public static final int CRESSENDO = 4096;
/*     */   public static final int DECRESSENDO = 8192;
/*     */   public static final int SINAMBUNG = 256;
/*     */   public static final int CENTOK = 512;
/*     */   public static final int TENGKEP = 1024;
/*  50 */   static Chord defaultChord = new Chord();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Chord getChord() {
/*  56 */     return defaultChord;
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract String getDefaultChordType();
/*     */ 
/*     */   
/*     */   public abstract int getOctave();
/*     */   
/*     */   public long transpose(long bits, int shift) {
/*  66 */     if (shift > 0) {
/*  67 */       return bits << shift;
/*     */     }
/*  69 */     if (shift < 0) {
/*  70 */       return bits >> -shift;
/*     */     }
/*  72 */     return bits;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long transposeOctave(long bits, int octave) {
/*  78 */     return transpose(bits, octave * getOctave());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String symbolOfIndex(int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int indexOfSymbol(char paramChar) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int indexOfSymbol(String paramString) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int indexOfNum(int paramInt) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int indexOfNum(char paramChar) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int indexOfNum(int paramInt1, int paramInt2) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int indexOfNum(char paramChar, int paramInt) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract int shiftOfKey(String paramString) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long bitsOf(int note) {
/* 145 */     return 1L << note - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long polynotesOf(long bits) {
/* 153 */     return bits | bits << getOctave();
/*     */   }
/*     */   
/* 156 */   static char[] notes = new char[] { 'C', 'c', 'D', 'd', 'E', 'F', 'f', 'G', 'g', 'A', 'a', 'B' };
/*     */   public static String toBin(long l) {
/* 158 */     StringBuilder s = new StringBuilder();
/* 159 */     for (int i = 0; i < 5; i++) {
/* 160 */       for (int j = 0; j < 12; j++) {
/* 161 */         s.append(((l & 0x1L) == 0L) ? 45 : notes[j]);
/* 162 */         l >>= 1L;
/*     */       } 
/* 164 */       s.append(' ');
/*     */     } 
/* 166 */     return s.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public static byte indexToMidi(int note) {
/* 171 */     return (byte)(note + 35);
/*     */   }
/*     */   
/*     */   public static byte midiToIndex(byte midi) {
/* 175 */     return (byte)(midi - 35);
/*     */   }
/*     */   
/*     */   public static long midiToBits(byte midi) {
/* 179 */     return 1L << midi - 35 - 1;
/*     */   }
/*     */   
/*     */   public abstract String indexToSymbol(byte paramByte);
/*     */   
/*     */   public String midiToSymbol(byte midi) {
/* 185 */     return indexToSymbol((byte)(midi - 35));
/*     */   }
/*     */   
/*     */   public static Scale createScale() {
/* 189 */     return new Diatonic();
/*     */   }
/*     */   
/*     */   public static Scale createScale(String name) throws Exception {
/* 193 */     String className = Options.get("scale." + name);
/* 194 */     if (className == null) {
/* 195 */       throw new Exception("Unknown scale");
/*     */     }
/*     */     try {
/* 198 */       Class<?> cn = Class.forName(className);
/* 199 */       return (Scale)cn.newInstance();
/*     */     }
/* 201 */     catch (Exception ex) {
/* 202 */       throw new Exception("Unsupported scale");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\Scale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */