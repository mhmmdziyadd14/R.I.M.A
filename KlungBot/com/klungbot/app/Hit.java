/*    */ package com.klungbot.app;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Hit
/*    */   implements Comparable
/*    */ {
/*    */   String title;
/*    */   int vote;
/*    */   
/*    */   public Hit(String t) {
/* 17 */     this.title = t;
/* 18 */     this.vote = 1;
/*    */   }
/*    */   
/*    */   public void addVote() {
/* 22 */     this.vote++;
/*    */   }
/*    */   
/*    */   public int getVote() {
/* 26 */     return this.vote;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 30 */     return this.title;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int compareTo(Object o) {
/* 38 */     Hit h = (Hit)o;
/* 39 */     return h.vote - this.vote;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\Hit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */