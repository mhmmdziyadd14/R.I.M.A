/*     */ package com.bric.geom;
/*     */ 
/*     */ import java.awt.geom.GeneralPath;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GeneralPathWriter
/*     */   extends PathWriter
/*     */ {
/*     */   GeneralPath p;
/*     */   float lastX;
/*     */   float lastY;
/*     */   boolean dataWritten = false;
/*     */   boolean debug = false;
/*     */   
/*     */   public GeneralPathWriter(GeneralPath p) {
/*  38 */     this.p = p;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  43 */     return "GeneralPathWriter[ data = " + ShapeStringUtils.toString(this.p) + " ]";
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDebug(boolean b) {
/*  48 */     this.debug = b;
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() {}
/*     */ 
/*     */   
/*     */   public void reset() {
/*  56 */     if (this.debug)
/*  57 */       System.out.println("reset()"); 
/*  58 */     this.p.reset();
/*  59 */     this.dataWritten = false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void curveTo(float cx1, float cy1, float cx2, float cy2, float x, float y) {
/*  65 */     if (this.debug)
/*  66 */       System.out.println("curveTo( " + cx1 + ", " + cy1 + ", " + cx2 + ", " + cy2 + ", " + x + ", " + y + ")"); 
/*  67 */     this.p.curveTo(cx1, cy1, cx2, cy2, x, y);
/*  68 */     this.lastX = x;
/*  69 */     this.lastY = y;
/*  70 */     this.dataWritten = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void lineTo(float x, float y) {
/*  75 */     if (equals(this.lastX, x) && equals(this.lastY, y))
/*     */       return; 
/*  77 */     if (this.debug)
/*  78 */       System.out.println("lineTo( " + x + ", " + y + ")"); 
/*  79 */     this.p.lineTo(x, y);
/*  80 */     this.lastX = x;
/*  81 */     this.lastY = y;
/*  82 */     this.dataWritten = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveTo(float x, float y) {
/*  87 */     this.p.moveTo(x, y);
/*  88 */     if (this.debug)
/*  89 */       System.out.println("moveTo( " + x + ", " + y + ")"); 
/*  90 */     this.lastX = x;
/*  91 */     this.lastY = y;
/*  92 */     this.dataWritten = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void quadTo(float cx, float cy, float x, float y) {
/*  97 */     this.p.quadTo(cx, cy, x, y);
/*  98 */     if (this.debug)
/*  99 */       System.out.println("quadTo( " + cx + ", " + cy + ", " + x + ", " + y + ")"); 
/* 100 */     this.lastX = x;
/* 101 */     this.lastY = y;
/* 102 */     this.dataWritten = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void closePath() {
/* 107 */     if (this.dataWritten) {
/* 108 */       this.p.closePath();
/* 109 */       if (this.debug)
/* 110 */         System.out.println("closePath()"); 
/* 111 */       this.dataWritten = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean equals(float z1, float z2) {
/* 116 */     float d = z2 - z1;
/* 117 */     if (d < 0.0F) d = -d; 
/* 118 */     if (d < 0.001F)
/* 119 */       return true; 
/* 120 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\GeneralPathWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */