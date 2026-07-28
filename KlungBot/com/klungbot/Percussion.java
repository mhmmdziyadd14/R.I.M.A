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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Percussion
/*     */   extends Synthesizer
/*     */ {
/*     */   protected byte[][] maps;
/*     */   
/*     */   public Percussion(String id, Instrument i1) {
/*  24 */     super(id, i1);
/*  25 */     this.maps = new byte[1][12];
/*     */   }
/*     */   
/*     */   public void setMap(int idx, int midi_note) throws Exception {
/*  29 */     if (idx < 25 || idx >= 37)
/*  30 */       throw new Exception("Drum index out of bound"); 
/*  31 */     this.maps[0][idx - 25] = (byte)midi_note;
/*     */   }
/*     */   
/*     */   public void setMap(int idx, int[] midi_notes) throws Exception {
/*  35 */     if (idx < 25 || idx >= 37)
/*  36 */       throw new Exception("Drum index out of bound"); 
/*  37 */     for (int i = 0; i < midi_notes.length && i < this.maps.length; i++) {
/*  38 */       this.maps[i][idx - 25] = (byte)midi_notes[i];
/*     */     }
/*     */   }
/*     */   
/*     */   public byte getMappedNote(int idx) {
/*  43 */     int n = idx % 12;
/*  44 */     return this.maps[0][n];
/*     */   }
/*     */   
/*     */   public byte getMappedNote(int idx, int accent) {
/*  48 */     int n = idx % 12;
/*  49 */     int a = accent % this.maps.length;
/*  50 */     return this.maps[a][n];
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOff(long l) {
/*  55 */     if (l == 0L) {
/*  56 */       this.engineer.midiOff(this.channels[0]);
/*     */     }
/*  58 */     int pattern = 0;
/*     */     try {
/*  60 */       while (l != 0L) {
/*  61 */         if ((l & 0x1L) != 0L) {
/*  62 */           this.engineer.midiOff(this.channels[0], getMappedNote(pattern));
/*     */         }
/*  64 */         pattern++;
/*  65 */         l >>= 1L;
/*     */       }
/*     */     
/*  68 */     } catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOn(long l, int forte) {
/*  73 */     int idx = 0;
/*     */     try {
/*  75 */       while (l != 0L) {
/*  76 */         if ((l & 0x1L) != 0L) {
/*  77 */           this.engineer.midiOn(this.channels[0], getMappedNote(idx), getForte(forte));
/*     */         }
/*  79 */         idx++;
/*  80 */         l >>= 1L;
/*     */       }
/*     */     
/*  83 */     } catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOff(long l, int accent) {
/*  88 */     playOff(l);
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOn(long l, int forte, int accent) {
/*  93 */     playOn(l, forte);
/*     */   }
/*     */ 
/*     */   
/*     */   public void mappedOn(byte note, int forte) {
/*  98 */     int f = this.volume * forte / 100;
/*  99 */     byte n = getMappedNote(note);
/* 100 */     if (this.channels[0] >= 0) this.engineer.midiOn(this.channels[0], n, f);
/*     */   
/*     */   }
/*     */   
/*     */   public void mappedOff(byte note) {
/* 105 */     byte n = getMappedNote(note);
/* 106 */     if (this.channels[0] >= 0) this.engineer.midiOff(this.channels[0], n); 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Percussion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */