/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.util.Random;
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
/*     */ public class GooTransition2D
/*     */   extends AbstractClippedTransition2D
/*     */ {
/*     */   float[] offset;
/*     */   float[] accel;
/*     */   
/*     */   public GooTransition2D() {
/*  38 */     this(20);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GooTransition2D(int columns) {
/*  46 */     Random r = new Random();
/*  47 */     this.offset = new float[columns];
/*  48 */     this.accel = new float[columns];
/*  49 */     boolean ok = false;
/*  50 */     long seed = System.currentTimeMillis();
/*  51 */     while (!ok) {
/*  52 */       seed++;
/*  53 */       r.setSeed(seed);
/*  54 */       ok = true;
/*  55 */       for (int a = 0; a < columns && ok; a++) {
/*  56 */         this.offset[a] = -r.nextFloat();
/*  57 */         this.accel[a] = 4.0F * r.nextFloat() + 0.2F;
/*  58 */         if ((this.accel[a] + 1.0F + this.offset[a]) < 1.2D)
/*  59 */           ok = false; 
/*     */       } 
/*  61 */       if (ok) {
/*     */         
/*  63 */         boolean atLeastOneSlowOne = false;
/*  64 */         for (int i = 0; i < columns && !atLeastOneSlowOne; i++) {
/*  65 */           atLeastOneSlowOne = ((this.accel[i] + 1.0F + this.offset[i]) < 1.3D);
/*     */         }
/*  67 */         ok = atLeastOneSlowOne;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Shape[] getShapes(float progress, Dimension size) {
/*  74 */     float[] f = new float[this.offset.length];
/*  75 */     for (int a = 0; a < f.length; a++) {
/*  76 */       f[a] = size.height * (this.offset[a] + progress + progress * progress * this.accel[a]);
/*     */     }
/*  78 */     float w = size.width / f.length;
/*     */     
/*  80 */     int k = 4;
/*     */     
/*  82 */     GeneralPath path = new GeneralPath();
/*  83 */     path.moveTo(-k, -k);
/*     */     
/*  85 */     path.lineTo(-k, f[0]);
/*  86 */     path.lineTo(w / 2.0F, f[0]);
/*     */     
/*  88 */     for (int i = 1; i < f.length; i++) {
/*  89 */       float x1 = (i - 1) * w + w / 2.0F;
/*  90 */       float x2 = i * w + w / 2.0F;
/*  91 */       path.curveTo(x1 + w / 2.0F, f[i - 1], x2 - w / 2.0F, f[i], x2, f[i]);
/*     */     } 
/*  93 */     path.lineTo((size.width + k), f[f.length - 1]);
/*     */     
/*  95 */     path.lineTo((size.width + k), -k);
/*  96 */     path.lineTo(-k, -k);
/*  97 */     path.closePath();
/*     */     
/*  99 */     return new Shape[] { path };
/*     */   }
/*     */ 
/*     */   
/*     */   public float getStrokeWidth(float progress) {
/* 104 */     return 1.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 109 */     return "Goo";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\GooTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */