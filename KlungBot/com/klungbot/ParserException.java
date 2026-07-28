/*    */ package com.klungbot;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ParserException
/*    */   extends Exception
/*    */ {
/*    */   int row;
/*    */   int column;
/*    */   
/*    */   public ParserException(String msg, int row, int col) {
/* 15 */     super(msg);
/* 16 */     this.row = row;
/* 17 */     this.column = col;
/*    */   }
/*    */   
/*    */   public ParserException(String msg) {
/* 21 */     super(msg);
/* 22 */     this.row = 0;
/* 23 */     this.column = 0;
/*    */   }
/*    */   
/*    */   public void setLocation(int row, int col) {
/* 27 */     this.row = row;
/* 28 */     this.column = col;
/*    */   }
/*    */   
/*    */   public int getRow() {
/* 32 */     return this.row;
/*    */   }
/*    */   
/*    */   public int getCol() {
/* 36 */     return this.column;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 42 */     return "At [" + this.row + "," + this.column + "] : " + super.getMessage();
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\ParserException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */