/*    */ package com.klungbot;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Part
/*    */   extends ArrayList<Track>
/*    */ {
/*    */   int num;
/*    */   String name;
/*    */   Iterator<Track> it;
/*    */   
/*    */   public Part(int num, String name) {
/* 21 */     this.num = num;
/* 22 */     this.name = name;
/*    */   }
/*    */   
/*    */   public int getNum() {
/* 26 */     return this.num;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 30 */     return this.name;
/*    */   }
/*    */   
/*    */   public Track getLast() {
/* 34 */     return get(size() - 1);
/*    */   }
/*    */   
/*    */   public Track removeLast() {
/* 38 */     return remove(size() - 1);
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Part.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */