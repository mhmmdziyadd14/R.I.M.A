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
/*    */ public class Label
/*    */ {
/*    */   long tick;
/*    */   int num;
/*    */   int count;
/*    */   
/*    */   public Label(long tick, int num) {
/* 18 */     this.num = num;
/* 19 */     this.tick = tick;
/* 20 */     this.count = 0;
/*    */   }
/*    */   
/*    */   public long tick() {
/* 24 */     return this.tick;
/*    */   }
/*    */   
/*    */   public int id() {
/* 28 */     return this.num;
/*    */   }
/*    */   
/*    */   public void incCount() {
/* 32 */     this.count++;
/*    */   }
/*    */   
/*    */   public int count() {
/* 36 */     return this.count;
/*    */   }
/*    */   
/*    */   public long countId() {
/* 40 */     return (this.count << 16L) + this.num;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Label.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */