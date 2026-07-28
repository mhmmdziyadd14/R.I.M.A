/*    */ package com.klungbot.app;
/*    */ 
/*    */ import javax.swing.JTextPane;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FindAndReplace
/*    */ {
/* 16 */   String lastStr = "";
/*    */   int lastStart;
/*    */   JTextPane editor;
/*    */   
/*    */   FindAndReplace(JTextPane editor) {
/* 21 */     this.editor = editor;
/* 22 */     this.lastStart = 0;
/*    */   }
/*    */   
/*    */   public boolean find(String str1) {
/* 26 */     String txt = this.editor.getText();
/* 27 */     if (!this.lastStr.equals(str1)) {
/* 28 */       this.lastStart = this.editor.getCaretPosition();
/* 29 */       if (this.lastStart >= txt.length())
/* 30 */         this.lastStart = 0; 
/* 31 */       this.lastStr = str1;
/*    */     } 
/* 33 */     int idx = txt.indexOf(str1, this.lastStart);
/* 34 */     if (idx >= 0) {
/* 35 */       this.lastStart = idx + str1.length();
/* 36 */       this.editor.setSelectionStart(idx);
/* 37 */       this.editor.setSelectionEnd(this.lastStart);
/* 38 */       if (this.lastStart < txt.length()) {
/* 39 */         this.lastStart++;
/*    */       }
/* 41 */       return true;
/*    */     } 
/* 43 */     this.lastStart = 0;
/* 44 */     return false;
/*    */   }
/*    */   
/*    */   public boolean replace(String str1, String str2) {
/* 48 */     String txt = this.editor.getText();
/* 49 */     this.lastStart = this.editor.getCaretPosition();
/* 50 */     if (!this.lastStr.equals(str1)) {
/* 51 */       if (this.lastStart >= txt.length())
/* 52 */         this.lastStart = 0; 
/* 53 */       this.lastStr = str1;
/*    */     } 
/* 55 */     int idx = txt.indexOf(str1, this.lastStart);
/* 56 */     if (idx >= 0) {
/* 57 */       this.lastStart = idx + str1.length();
/*    */ 
/*    */       
/* 60 */       this.editor.setSelectionStart(idx);
/* 61 */       this.editor.setSelectionEnd(this.lastStart);
/* 62 */       this.editor.replaceSelection(str2);
/*    */       
/* 64 */       this.lastStart = idx + str2.length();
/* 65 */       if (this.lastStart < txt.length()) {
/* 66 */         this.lastStart++;
/*    */       }
/* 68 */       return true;
/*    */     } 
/* 70 */     this.lastStart = 0;
/* 71 */     return false;
/*    */   }
/*    */   
/*    */   public boolean replaceAll(String str1, String str2) {
/* 75 */     String txt = this.editor.getText();
/* 76 */     if (!this.lastStr.equals(str1)) {
/* 77 */       this.lastStart = this.editor.getCaretPosition();
/* 78 */       if (this.lastStart >= txt.length())
/* 79 */         this.lastStart = 0; 
/* 80 */       this.lastStr = str1;
/*    */     } 
/* 82 */     int idx = txt.indexOf(str1, this.lastStart);
/* 83 */     if (idx >= 0) {
/* 84 */       this.lastStart = idx + str1.length();
/* 85 */       this.editor.setSelectionStart(idx);
/* 86 */       this.editor.setSelectionEnd(this.lastStart);
/* 87 */       this.editor.replaceSelection(str2);
/* 88 */       this.lastStart = idx + str2.length();
/* 89 */       if (this.lastStart < txt.length()) {
/* 90 */         this.lastStart++;
/*    */       }
/* 92 */       return true;
/*    */     } 
/* 94 */     this.lastStart = 0;
/* 95 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\FindAndReplace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */