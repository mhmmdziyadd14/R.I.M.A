/*     */ package com.klungbot;
/*     */ 
/*     */ import javax.sound.midi.Instrument;
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
/*     */ public class Synthesizer
/*     */   extends Player
/*     */ {
/*     */   Instrument[] instruments;
/*     */   final int[] channels;
/*     */   
/*     */   public Synthesizer(String id, Instrument i1) {
/*  21 */     super(id);
/*  22 */     this.instruments = new Instrument[1];
/*  23 */     this.instruments[0] = i1;
/*  24 */     this.channels = new int[1];
/*     */   }
/*     */   
/*     */   public Synthesizer(String id, Instrument i1, Instrument i2) {
/*  28 */     super(id);
/*  29 */     this.instruments = new Instrument[2];
/*  30 */     this.instruments[0] = i1;
/*  31 */     this.instruments[1] = i2;
/*  32 */     this.channels = new int[2];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEngineer(AudioEngineer ae) {
/*  37 */     this.engineer = ae;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean attach(int track) {
/*  55 */     synchronized (this.channels) {
/*  56 */       if (super.attach(track)) {
/*  57 */         this.engineer.attach(this);
/*  58 */         return true;
/*     */       } 
/*  60 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean detach() {
/*  65 */     synchronized (this.channels) {
/*  66 */       midiOff();
/*  67 */       if (super.detach()) {
/*  68 */         this.engineer.detach(this);
/*  69 */         return true;
/*     */       } 
/*     */     } 
/*  72 */     return false;
/*     */   }
/*     */   
/*     */   void channelOn(int i, int forte, long l) {
/*  76 */     int f = this.volume * forte / 100;
/*  77 */     this.engineer.channelOn(this.channels[i], f, l);
/*     */   }
/*     */   
/*     */   void channelOff(int i, long l) {
/*  81 */     if (this.channels[i] < 0)
/*  82 */       return;  this.engineer.channelOff(this.channels[i], l);
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOn(long l, int forte) {
/*  87 */     channelOn(0, forte, l);
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOff(long l) {
/*  92 */     channelOff(0, l);
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOn(long l, int force, int accent) {
/*  97 */     if (this.channels.length < accent) {
/*  98 */       channelOn(0, force, l);
/*     */     } else {
/* 100 */       channelOn(accent - 1, force, l);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOff(long l, int accent) {
/* 106 */     if (this.channels.length < accent) {
/* 107 */       channelOff(0, l);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void playOff() {
/* 116 */     midiOff();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void midiOn(byte note, int forte) {
/* 122 */     int f = this.volume * forte / 100;
/* 123 */     if (this.channels[0] >= 0) this.engineer.midiOn(this.channels[0], note, f);
/*     */   
/*     */   }
/*     */   
/*     */   public void midiOff(byte note) {
/* 128 */     if (this.channels[0] >= 0) this.engineer.midiOff(this.channels[0], note);
/*     */   
/*     */   }
/*     */   
/*     */   public void midiOff() {
/* 133 */     for (int i = 0; i < this.channels.length; i++) {
/* 134 */       if (this.channels[i] >= 0)
/* 135 */         this.engineer.midiOff(this.channels[i]); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Synthesizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */