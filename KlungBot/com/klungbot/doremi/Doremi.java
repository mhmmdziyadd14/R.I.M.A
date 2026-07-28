/*    */ package com.klungbot.doremi;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Doremi
/*    */ {
/*    */   public static final int DIATONIC = 12;
/*    */   public static final int PENTATONIC = 5;
/* 15 */   public static int DEFAULT_TEMPO = 110;
/* 16 */   public static int MAX_FORTE = 100;
/*    */   
/*    */   public static final int MAX_TRACK = 10;
/* 19 */   public static final String[] trackNames = new String[] { "V", "VA", "VB", "VC", "VD", "VE", "VF", "VG", "VH", "VI" };
/*    */   
/*    */   public static final int MAX_VOICE = 8;
/*    */   
/*    */   public static final int MELODY = 0;
/*    */   
/*    */   public static final int ACCOMP = 1;
/*    */   
/*    */   public static final int BASS = 2;
/*    */   public static final int CADANTE = 3;
/*    */   public static final int DRUM = 4;
/*    */   public static final int ETNICH = 5;
/*    */   public static final int FANFARE = 6;
/*    */   public static final int VG = 7;
/*    */   public static final int VH = 8;
/*    */   public static final int VI = 9;
/*    */   static final int MELODY_TRACK = 0;
/*    */   static final int ACCOMP_TRACK = 1;
/*    */   static final int BASS_TRACK = 2;
/*    */   static final int CHORD_TRACK = 3;
/*    */   static final int DRUM_TRACK = 4;
/*    */   
/*    */   public static String getTrackName(int t) {
/* 42 */     if (t < 0 || t >= 10) return null; 
/* 43 */     return trackNames[t];
/*    */   }
/*    */   
/*    */   public static String getTrackName(int t, int v) {
/* 47 */     return getTrackName(t) + v;
/*    */   }
/*    */   
/*    */   public static int getTrackIndex(String ts) {
/* 51 */     for (int i = 0; i < 10; i++) {
/* 52 */       if (ts.compareTo(trackNames[i]) == 0) return i; 
/*    */     } 
/* 54 */     return -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\Doremi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */