/*     */ package com.klungbot;
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
/*     */ public abstract class Player
/*     */ {
/*     */   public static final int MAX_VOLUME = 100;
/*     */   public static final int MAX_FORTE = 127;
/*     */   String id;
/*  18 */   int volume = 100;
/*     */   
/*     */   long currentOn;
/*     */   long nextOn;
/*     */   long nextOff;
/*     */   long nextStacato;
/*  24 */   int track = -1; long nextPause; boolean nextPlay; boolean percussion; int connections; AudioEngineer engineer;
/*  25 */   static int latency = 240;
/*     */   
/*     */   public Player(String id) {
/*  28 */     this.id = id;
/*  29 */     this.percussion = false;
/*  30 */     this.currentOn = this.nextOn = 0L;
/*  31 */     this.engineer = null;
/*     */   }
/*     */   
/*     */   public Player(String id, boolean percussion) {
/*  35 */     this.id = id;
/*  36 */     this.percussion = percussion;
/*  37 */     this.currentOn = this.nextOn = 0L;
/*  38 */     this.engineer = null;
/*     */   }
/*     */   
/*  41 */   public String getId() { return this.id; } public boolean isPercussion() {
/*  42 */     return this.percussion;
/*     */   }
/*     */   public void setEngineer(AudioEngineer ae) {
/*  45 */     this.engineer = ae;
/*     */   }
/*     */   
/*     */   public int getPortCount() {
/*  49 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean attach(int track) {
/*  54 */     this.connections++;
/*  55 */     if (this.connections == 1) {
/*  56 */       this.track = track;
/*  57 */       return true;
/*     */     } 
/*  59 */     return false;
/*     */   }
/*     */   
/*     */   public boolean detach() {
/*  63 */     this.connections--;
/*  64 */     if (this.connections == 0) {
/*  65 */       this.track = -1;
/*  66 */       return true;
/*     */     } 
/*  68 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isAttached() {
/*  72 */     return (this.connections > 0);
/*     */   }
/*     */   
/*     */   public void start() {
/*  76 */     this.currentOn = this.nextOn = this.nextOff = this.nextStacato = 0L;
/*  77 */     this.nextPlay = false;
/*     */   }
/*     */   
/*     */   public void setVolume(int volume) {
/*  81 */     this.volume = volume;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVolume() {
/*  86 */     return this.volume;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getForte(int f) {
/*  91 */     return (byte)(this.volume * f / 100);
/*     */   }
/*     */   
/*     */   public void playOn(long notes, int forte) {
/*  95 */     notes &= Long.MAX_VALUE;
/*  96 */     byte note = 36;
/*  97 */     while (notes != 0L) {
/*  98 */       if ((notes & 0x1L) != 0L) {
/*  99 */         midiOn(note, forte);
/*     */       }
/* 101 */       note = (byte)(note + 1);
/* 102 */       notes >>= 1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void playOff(long notes) {
/* 107 */     if (notes == 0L) {
/* 108 */       midiOff();
/*     */       return;
/*     */     } 
/* 111 */     notes &= Long.MAX_VALUE;
/* 112 */     byte note = 36;
/*     */     try {
/* 114 */       while (notes != 0L) {
/* 115 */         if ((notes & 0x1L) != 0L) {
/* 116 */           midiOff(note);
/*     */         }
/* 118 */         note = (byte)(note + 1);
/* 119 */         notes >>= 1L;
/*     */       }
/*     */     
/* 122 */     } catch (Exception ex) {}
/*     */   }
/*     */   
/*     */   public void playOn(long notes, int forte, int accent) {
/* 126 */     playOn(notes, forte);
/*     */   }
/*     */   
/*     */   public void playOff(long notes, int accent) {
/* 130 */     playOff(notes);
/*     */   }
/*     */   
/*     */   public void playOff() {
/* 134 */     playOff(0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOn(byte note, int forte) {
/* 139 */     playOn(1L << note + 25 - 60 - 1, forte);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff(byte note) {
/* 144 */     playOff(1L << note + 25 - 60 - 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff() {
/* 149 */     playOff(0L);
/*     */   }
/*     */   
/*     */   public void mappedOn(byte note, int forte) {
/* 153 */     midiOn(note, forte);
/*     */   }
/*     */   
/*     */   public void mappedOff(byte note) {
/* 157 */     midiOff(note);
/*     */   }
/*     */ 
/*     */   
/*     */   public void holdOn() {}
/*     */   
/*     */   public void holdOff() {}
/*     */   
/*     */   public String toString() {
/* 166 */     return this.id;
/*     */   }
/*     */   
/*     */   public int getLatency() {
/* 170 */     return latency;
/*     */   }
/*     */   public void setLatency(int ms) {
/* 173 */     latency = ms;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Player.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */