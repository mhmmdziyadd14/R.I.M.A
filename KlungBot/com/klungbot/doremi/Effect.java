/*    */ package com.klungbot.doremi;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Effect
/*    */ {
/*    */   public static final int INTERACTIVE_NONE = 0;
/*    */   public static final int INTERACTIVE_0 = 1;
/*    */   public static final int INTERACTIVE_1 = 2;
/*    */   public static final int INTERACTIVE_ALL = 3;
/*    */   public static final int INTERACTIVE_A = 4;
/*    */   public static final int INTERACTIVE_B = 5;
/*    */   public static final int INTERACTIVE_C = 6;
/*    */   public static final int INTERACTIVE_D = 7;
/*    */   public boolean highUnison;
/*    */   public boolean lowUnison;
/*    */   public boolean multinote;
/*    */   public boolean legato;
/*    */   public boolean stacatto;
/*    */   public boolean sustain;
/*    */   public int interactive;
/*    */   public boolean[] mutes;
/*    */   public boolean[] track_mutes;
/*    */   
/*    */   public Effect(int max, int max_track) {
/* 30 */     this.legato = true;
/* 31 */     this.sustain = true;
/* 32 */     this.mutes = new boolean[max];
/* 33 */     this.track_mutes = new boolean[max_track];
/* 34 */     this.interactive = 0;
/*    */   }
/*    */   
/*    */   public void setAutoHighUnison(boolean hu) {
/* 38 */     this.highUnison = hu;
/*    */   }
/*    */   
/*    */   public void setAutoLowUnison(boolean lu) {
/* 42 */     this.lowUnison = lu;
/*    */   }
/*    */   
/*    */   public void setMultinote(boolean s) {
/* 46 */     this.multinote = s;
/*    */   }
/*    */   
/*    */   public void setLegato(boolean s) {
/* 50 */     this.legato = s;
/*    */   }
/*    */   
/*    */   public void setSustain(boolean s) {
/* 54 */     this.sustain = s;
/*    */   }
/*    */   
/*    */   public void setStacatto(boolean s) {
/* 58 */     this.stacatto = s;
/*    */   }
/*    */   
/*    */   public void setInteractive(int s) {
/* 62 */     this.interactive = s;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setMute(int i, boolean m) {
/* 67 */     if (i > 0 && i < this.mutes.length) {
/* 68 */       this.mutes[i - 1] = m;
/*    */     } else {
/*    */       
/* 71 */       this.mutes[this.mutes.length - 1] = m;
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean isMuted(int i) {
/* 76 */     if (i > 0 && i < this.mutes.length) {
/* 77 */       return this.mutes[i - 1];
/*    */     }
/* 79 */     return this.mutes[this.mutes.length - 1];
/*    */   }
/*    */   
/*    */   public void setTrackMute(int i, boolean m) {
/* 83 */     if (i < this.track_mutes.length) {
/* 84 */       this.track_mutes[i] = m;
/*    */     } else {
/*    */       
/* 87 */       this.track_mutes[this.track_mutes.length - 1] = m;
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean isTrackMuted(int i) {
/* 92 */     if (i >= 0 && i < this.track_mutes.length) {
/* 93 */       return this.track_mutes[i];
/*    */     }
/* 95 */     return this.track_mutes[this.track_mutes.length - 1];
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\Effect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */