/*     */ package com.bric.geom;
/*     */ 
/*     */ import java.awt.geom.PathIterator;
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
/*     */ 
/*     */ class SerializedPathIterator
/*     */   implements PathIterator
/*     */ {
/*     */   char[] c;
/*  28 */   int ctr = 0;
/*  29 */   double[] data = new double[6];
/*  30 */   int currentSegment = -1;
/*     */   int windingRule;
/*     */   
/*     */   public SerializedPathIterator(String s, int windingRule) {
/*  34 */     if (windingRule != 0 && windingRule != 1) {
/*  35 */       throw new IllegalArgumentException("The winding rule must be PathIterator.WIND_NON_ZERO or PathIterator.WIND_EVEN_ODD");
/*     */     }
/*  37 */     this.c = s.toCharArray();
/*  38 */     this.windingRule = windingRule;
/*  39 */     next();
/*     */   }
/*     */   
/*     */   public int getWindingRule() {
/*  43 */     return this.windingRule;
/*     */   }
/*     */   
/*     */   protected void consumeWhiteSpace(boolean expectingWhiteSpace) {
/*  47 */     if (this.ctr >= this.c.length) {
/*  48 */       this.ctr = this.c.length + 2;
/*     */       
/*     */       return;
/*     */     } 
/*  52 */     char ch = this.c[this.ctr];
/*  53 */     if (!Character.isWhitespace(ch)) {
/*  54 */       if (!expectingWhiteSpace)
/*     */         return; 
/*  56 */       throw new ParserException("expected whitespace", this.ctr, 1);
/*     */     } 
/*     */     do {
/*  59 */       this.ctr++;
/*  60 */       if (this.ctr >= this.c.length) {
/*  61 */         this.ctr = this.c.length + 2;
/*     */         
/*     */         return;
/*     */       } 
/*  65 */       ch = this.c[this.ctr];
/*  66 */     } while (Character.isWhitespace(ch));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void next() {
/*     */     int terms;
/*  73 */     consumeWhiteSpace(false);
/*     */     
/*  75 */     if (this.ctr >= this.c.length) {
/*  76 */       this.ctr = this.c.length + 2;
/*     */       
/*     */       return;
/*     */     } 
/*  80 */     char k = this.c[this.ctr];
/*     */     
/*  82 */     switch (k) {
/*     */       case 'M':
/*     */       case 'm':
/*  85 */         this.currentSegment = 0;
/*  86 */         terms = 2;
/*     */         break;
/*     */       case 'L':
/*     */       case 'l':
/*  90 */         this.currentSegment = 1;
/*  91 */         terms = 2;
/*     */         break;
/*     */       case 'Q':
/*     */       case 'q':
/*  95 */         this.currentSegment = 2;
/*  96 */         terms = 4;
/*     */         break;
/*     */       case 'C':
/*     */       case 'c':
/* 100 */         this.currentSegment = 3;
/* 101 */         terms = 6;
/*     */         break;
/*     */       case 'Z':
/*     */       case 'z':
/* 105 */         this.currentSegment = 4;
/* 106 */         terms = 0;
/*     */         break;
/*     */       default:
/* 109 */         throw new ParserException("Unrecognized character in shape data: '" + this.c[this.ctr] + "'", this.ctr, 1);
/*     */     } 
/*     */     
/* 112 */     this.ctr++;
/* 113 */     if (terms > 0) {
/* 114 */       parseTerms(terms);
/*     */     }
/* 116 */     else if (this.ctr < this.c.length && 
/* 117 */       !Character.isWhitespace(this.c[this.ctr])) {
/* 118 */       throw new ParserException("expected whitespace after z", this.ctr, 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   class ParserException
/*     */     extends RuntimeException {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     ParserException(String msg, int ptr, int length) {
/* 127 */       super(msg);
/* 128 */       System.err.println("\"" + new String(SerializedPathIterator.this.c) + "\"");
/* 129 */       StringBuffer sb = new StringBuffer(); int a;
/* 130 */       for (a = 0; a < ptr + 1; a++) {
/* 131 */         sb.append(' ');
/*     */       }
/* 133 */       for (a = 0; a < length; a++) {
/* 134 */         sb.append('^');
/*     */       }
/* 136 */       System.err.println(sb);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void parseTerms(int terms) {
/* 141 */     for (int a = 0; a < terms; a++) {
/* 142 */       this.data[a] = parseTerm();
/*     */     }
/*     */   }
/*     */   
/*     */   protected double parseTerm() {
/* 147 */     consumeWhiteSpace(true);
/* 148 */     int i = this.ctr;
/* 149 */     while (i < this.c.length && !Character.isWhitespace(this.c[i])) {
/* 150 */       i++;
/*     */     }
/* 152 */     String string = new String(this.c, this.ctr, i - this.ctr);
/*     */     try {
/* 154 */       return Double.parseDouble(string);
/* 155 */     } catch (RuntimeException e) {
/*     */       
/* 157 */       ParserException e2 = new ParserException(e.getMessage(), this.ctr, i - this.ctr);
/* 158 */       throw e2;
/*     */     } finally {
/* 160 */       this.ctr = i;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int currentSegment(double[] d) {
/* 165 */     d[0] = this.data[0];
/* 166 */     d[1] = this.data[1];
/* 167 */     d[2] = this.data[2];
/* 168 */     d[3] = this.data[3];
/* 169 */     d[4] = this.data[4];
/* 170 */     d[5] = this.data[5];
/* 171 */     return this.currentSegment;
/*     */   }
/*     */   
/*     */   public int currentSegment(float[] f) {
/* 175 */     f[0] = (float)this.data[0];
/* 176 */     f[1] = (float)this.data[1];
/* 177 */     f[2] = (float)this.data[2];
/* 178 */     f[3] = (float)this.data[3];
/* 179 */     f[4] = (float)this.data[4];
/* 180 */     f[5] = (float)this.data[5];
/* 181 */     return this.currentSegment;
/*     */   }
/*     */   
/*     */   public boolean isDone() {
/* 185 */     return (this.ctr > this.c.length + 1);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\SerializedPathIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */