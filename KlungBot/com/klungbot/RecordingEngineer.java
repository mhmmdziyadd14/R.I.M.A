/*     */ package com.klungbot;
/*     */ 
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.MidiUnavailableException;
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
/*     */ public class RecordingEngineer
/*     */   extends AudioEngineer
/*     */ {
/*  18 */   RecordingListener synListener = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open() {
/*     */     try {
/*  26 */       this.msyn = MidiSystem.getSynthesizer();
/*  27 */       this.msyn.open();
/*  28 */       System.out.println("Opening Synthesizer " + this.msyn.getDeviceInfo());
/*  29 */       initSynthesizer(this.msyn);
/*  30 */       this.channels = this.msyn.getChannels();
/*  31 */       this.channelCounts = new int[this.channels.length];
/*  32 */     } catch (MidiUnavailableException e) {
/*  33 */       System.err.print(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void close() {
/*  38 */     this.msyn.close();
/*     */   }
/*     */   
/*     */   void channelOn(int chn, int forte, long l) {
/*  42 */     int note = 36;
/*  43 */     l &= Long.MAX_VALUE;
/*     */     try {
/*  45 */       while (l != 0L) {
/*  46 */         if ((l & 0x1L) != 0L) {
/*  47 */           this.channels[chn].noteOn(note, forte);
/*  48 */           if (this.synListener != null)
/*  49 */             this.synListener.recordNoteOn(chn, note, forte); 
/*     */         } 
/*  51 */         note++;
/*  52 */         l >>= 1L;
/*     */       }
/*     */     
/*  55 */     } catch (Exception ex) {}
/*     */   }
/*     */   
/*     */   void channelOff(int chn, long l) {
/*  59 */     if (l == 0L) {
/*  60 */       this.channels[chn].allNotesOff();
/*     */       return;
/*     */     } 
/*  63 */     l &= Long.MAX_VALUE;
/*  64 */     int note = 36;
/*     */     
/*     */     try {
/*  67 */       while (l != 0L) {
/*  68 */         if ((l & 0x1L) != 0L) {
/*  69 */           this.channels[chn].noteOff(note);
/*  70 */           if (this.synListener != null)
/*  71 */             this.synListener.recordNoteOff(chn, note); 
/*     */         } 
/*  73 */         note++;
/*  74 */         l >>= 1L;
/*     */       }
/*     */     
/*  77 */     } catch (Exception ex) {}
/*     */   }
/*     */   
/*     */   public void midiOn(int chn, byte note, int forte) {
/*  81 */     this.channels[chn].noteOn(note, forte);
/*  82 */     if (this.synListener != null) this.synListener.recordNoteOn(chn, note, forte); 
/*     */   }
/*     */   
/*     */   public void midiOff(int chn, byte note) {
/*  86 */     if (note == 0) {
/*  87 */       midiOff(chn);
/*     */     } else {
/*     */       
/*  90 */       this.channels[chn].noteOff(note, 0);
/*  91 */       if (this.synListener != null) this.synListener.recordNoteOff(chn, note); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void midiOff(int chn) {
/*  96 */     this.channels[chn].allNotesOff();
/*  97 */     if (this.synListener != null) this.synListener.recordAllNotesOff(chn);
/*     */   
/*     */   }
/*     */   
/*     */   public void setTempo(int bpm) {}
/*     */   
/*     */   public void setRecordingListener(RecordingListener l) {
/* 104 */     this.synListener = l;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\RecordingEngineer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */