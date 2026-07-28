/*     */ package com.klungbot;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.StringReader;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TextReader
/*     */ {
/*     */   BufferedReader reader;
/*  30 */   StringBuffer token = new StringBuffer();
/*     */   String str;
/*     */   int sl;
/*     */   int si;
/*     */   char last_char;
/*     */   
/*     */   void startTokenizer(String l) {
/*  37 */     this.str = l;
/*  38 */     this.sl = l.length();
/*  39 */     this.si = 0;
/*     */   }
/*     */   
/*     */   char getChar() {
/*  43 */     if (this.si < this.sl) {
/*  44 */       this.last_char = this.str.charAt(this.si++);
/*     */     } else {
/*     */       
/*  47 */       this.last_char = Character.MIN_VALUE;
/*     */     } 
/*  49 */     return this.last_char;
/*     */   }
/*     */   
/*     */   char getToken() {
/*  53 */     while (this.si < this.sl) {
/*  54 */       this.last_char = this.str.charAt(this.si++);
/*  55 */       if (this.last_char != ' ') return this.last_char; 
/*     */     } 
/*  57 */     this.last_char = Character.MIN_VALUE;
/*  58 */     return Character.MIN_VALUE;
/*     */   }
/*     */   
/*     */   char getLast() {
/*  62 */     return this.last_char;
/*     */   }
/*     */   
/*     */   void skipWhite() {
/*  66 */     for (; this.str.charAt(this.si) == ' '; this.si++);
/*     */   }
/*     */   
/*     */   String getToken(char delim) {
/*  70 */     StringBuilder s = new StringBuilder();
/*  71 */     skipWhite();
/*  72 */     while (this.si < this.sl) {
/*  73 */       this.last_char = this.str.charAt(this.si++);
/*  74 */       if (this.last_char == delim)
/*  75 */         break;  s.append(this.last_char);
/*     */     } 
/*  77 */     return s.toString();
/*     */   }
/*     */   
/*     */   String getToken(String delim) {
/*  81 */     StringBuilder s = new StringBuilder();
/*  82 */     while (this.si < this.sl) {
/*  83 */       this.last_char = this.str.charAt(this.si++);
/*  84 */       for (int i = 0; i < delim.length(); i++) {
/*  85 */         if (this.last_char == delim.charAt(i)) {
/*  86 */           return s.toString();
/*     */         }
/*     */       } 
/*  89 */       s.append(this.last_char);
/*     */     } 
/*  91 */     return s.toString();
/*     */   }
/*     */   
/*     */   public BufferedReader createReader(String buffer) throws Exception {
/*  95 */     return new BufferedReader(new StringReader(buffer));
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedReader createReader(File fname) throws FileNotFoundException, Exception {
/* 100 */     return new BufferedReader(new FileReader(fname));
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\TextReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */