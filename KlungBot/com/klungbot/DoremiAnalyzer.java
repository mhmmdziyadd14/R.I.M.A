/*    */ package com.klungbot;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DoremiAnalyzer
/*    */ {
/*    */   static final int NOTE_MIN = 48;
/*    */   static final int NOTE_MAX = 85;
/*    */   static final int NOTE_SPAN = 37;
/*    */   long last;
/*    */   long current;
/*    */   int tempo;
/*    */   int forte;
/*    */   boolean keyChanged;
/*    */   long[] notes;
/* 22 */   int[] note_on = new int[37];
/* 23 */   long[] note_length = new long[37];
/*    */   Converter converter;
/*    */   
/*    */   public DoremiAnalyzer() {
/* 27 */     this.converter = new Converter();
/* 28 */     reset();
/*    */   }
/*    */   
/*    */   private void reset() {
/* 32 */     for (int i = 0; i < this.note_on.length; i++) {
/* 33 */       this.note_on[i] = 0;
/* 34 */       this.note_length[i] = 0L;
/*    */     } 
/*    */   }
/*    */   
/*    */   void analyze(long last, long note) {
/* 39 */     long mask = 4096L;
/* 40 */     for (int i = 0; i < this.note_on.length; i++) {
/* 41 */       boolean n1 = ((last & mask) != 0L);
/* 42 */       boolean n2 = ((note & mask) != 0L);
/* 43 */       if (n2) {
/* 44 */         this.note_length[i] = this.note_length[i] + 1L;
/* 45 */         if (!n1) this.note_on[i] = this.note_on[i] + 1; 
/*    */       } 
/* 47 */       mask <<= 1L;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void analyze(Sequence seq, int channel) {
/* 52 */     reset();
/* 53 */     this.notes = this.converter.convert(seq, channel);
/* 54 */     long last_note = 0L;
/* 55 */     for (int i = 0; i < this.notes.length; i++) {
/* 56 */       analyze(last_note, this.notes[i]);
/* 57 */       last_note = this.notes[i];
/*    */     } 
/*    */   }
/*    */   
/*    */   public int[] getNoteOn() {
/* 62 */     return this.note_on;
/*    */   }
/*    */   
/*    */   public long[] getNoteLength() {
/* 66 */     return this.note_length;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\DoremiAnalyzer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */