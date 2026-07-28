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
/*     */ 
/*     */ 
/*     */ public class Converter
/*     */ {
/*     */   long last;
/*     */   long current;
/*     */   int tempo;
/*     */   int forte;
/*     */   boolean keyChanged;
/*     */   long nextOn;
/*     */   long nextStacato;
/*     */   long nextOff;
/*     */   long nextOff1;
/*     */   long currentOn;
/*     */   long nextPause;
/*     */   
/*     */   void doCommand(long tick, Track v) {
/*  30 */     if (v.current == null)
/*  31 */       return;  while (tick >= v.current.tick) {
/*  32 */       switch (v.current.accent) {
/*     */         case 4096:
/*  34 */           this.keyChanged = true;
/*     */           break;
/*     */       } 
/*  37 */       if (v.next() == null) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   boolean doMix(long tick, Track v) {
/*  45 */     if (v.current == null) {
/*  46 */       return false;
/*     */     }
/*  48 */     if (tick < v.current.tick) {
/*  49 */       return false;
/*     */     }
/*  51 */     long noteOn = 0L;
/*  52 */     long stacatoOn = 0L;
/*     */     do {
/*  54 */       switch (v.current.accent) {
/*     */         case 1:
/*  56 */           noteOn |= v.current.data;
/*     */           break;
/*     */         case 2:
/*  59 */           stacatoOn |= v.current.data;
/*     */           break;
/*     */       } 
/*  62 */       if (v.next() == null) {
/*     */         break;
/*     */       }
/*  65 */     } while (tick >= v.current.tick);
/*  66 */     this.nextOn |= noteOn;
/*  67 */     this.nextStacato |= stacatoOn;
/*  68 */     this.nextOff |= v.lastNote;
/*  69 */     v.lastNote = noteOn;
/*  70 */     return true;
/*     */   }
/*     */   
/*     */   boolean play(int tick, Sequence seq, int channel) {
/*  74 */     boolean mixed = false;
/*  75 */     for (Track t : seq.tracks) {
/*  76 */       if (t.getChannel() != channel)
/*  77 */         continue;  if (t.getVoice() == 0) {
/*  78 */         doCommand(tick, t);
/*     */         continue;
/*     */       } 
/*  81 */       mixed |= doMix(tick, t);
/*     */     } 
/*     */     
/*  84 */     return mixed;
/*     */   }
/*     */   
/*     */   public long[] convert(Sequence seq, int channel) {
/*  88 */     long[] data = new long[seq.max_tick + 1];
/*  89 */     for (Track v : seq.tracks) {
/*  90 */       v.start();
/*     */     }
/*  92 */     this.last = this.current = 0L;
/*  93 */     this.keyChanged = false;
/*  94 */     this.currentOn = 0L;
/*  95 */     this.nextOn = 0L;
/*  96 */     this.nextStacato = 0L;
/*  97 */     this.nextOff = 0L;
/*  98 */     this.nextOff1 = 0L;
/*  99 */     for (int tick = 0; tick < seq.max_tick; tick++) {
/*     */       
/* 101 */       this.nextPause = this.currentOn & this.nextOn | this.currentOn & this.nextStacato;
/* 102 */       if (this.nextPause != 0L && tick > 0) {
/* 103 */         data[tick - 1] = data[tick - 1] & (this.nextPause ^ 0xFFFFFFFFFFFFFFFFL);
/*     */       }
/* 105 */       if (this.nextOff != 0L) this.currentOn &= this.nextOff ^ 0xFFFFFFFFFFFFFFFFL; 
/* 106 */       this.currentOn |= this.nextOn | this.nextStacato;
/* 107 */       data[tick] = this.currentOn;
/* 108 */       this.currentOn &= this.nextStacato ^ 0xFFFFFFFFFFFFFFFFL;
/* 109 */       this.nextOff1 = this.nextOff;
/* 110 */       this.nextOff = this.nextOn = this.nextStacato = 0L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 117 */       data[tick] = this.currentOn;
/*     */     } 
/*     */     
/* 120 */     return data;
/*     */   }
/*     */   
/*     */   public long[] convert(Sequence seq) {
/* 124 */     return convert(seq, 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Converter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */