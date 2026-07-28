/*     */ package com.bric.image.transition.vanilla;
/*     */ 
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.util.Random;
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BarsTransition2D
/*     */   extends Transition2D
/*     */ {
/*  43 */   static Random random = new Random(System.currentTimeMillis());
/*     */ 
/*     */   
/*     */   int type;
/*     */   
/*     */   boolean isRandom;
/*     */ 
/*     */   
/*     */   public BarsTransition2D() {
/*  52 */     this(9, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BarsTransition2D(int type, boolean random) {
/*  62 */     if (type != 9 && type != 10) {
/*  63 */       throw new IllegalArgumentException("Type must be HORIZONTAL or VERTICAL.");
/*     */     }
/*  65 */     this.type = type;
/*  66 */     this.isRandom = random;
/*     */   }
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*     */     boolean[] k;
/*     */     Random r;
/*  73 */     if (this.type == 9) {
/*  74 */       k = new boolean[size.height];
/*     */     } else {
/*  76 */       k = new boolean[size.width];
/*     */     } 
/*     */     
/*  79 */     if (this.isRandom) {
/*  80 */       r = random;
/*     */     } else {
/*  82 */       r = new Random(0L);
/*     */     } 
/*  84 */     for (int a = 0; a < k.length; a++) {
/*  85 */       k[a] = (r.nextFloat() > progress);
/*     */     }
/*  87 */     Vector<Transition2DInstruction> v = new Vector<>();
/*  88 */     v.add(new ImageInstruction(false));
/*  89 */     if (this.type == 9) {
/*  90 */       int i = 0;
/*  91 */       while (i < k.length) {
/*  92 */         int run = 0;
/*  93 */         while (i + run < k.length && k[i + run]) {
/*  94 */           run++;
/*     */         }
/*  96 */         if (run != 0) {
/*  97 */           Rectangle2D r2 = new Rectangle2D.Float(0.0F, i, size.width, run);
/*  98 */           v.add(new ImageInstruction(true, null, r2));
/*  99 */           i += run;
/*     */         } 
/* 101 */         i++;
/*     */       } 
/*     */     } else {
/* 104 */       int i = 0;
/* 105 */       while (i < k.length) {
/* 106 */         int run = 0;
/* 107 */         while (i + run < k.length && k[i + run]) {
/* 108 */           run++;
/*     */         }
/* 110 */         if (run != 0) {
/* 111 */           Rectangle2D r2 = new Rectangle2D.Float(i, 0.0F, run, size.height);
/* 112 */           v.add(new ImageInstruction(true, null, r2));
/* 113 */           i += run;
/*     */         } 
/* 115 */         i++;
/*     */       } 
/*     */     } 
/* 118 */     return v.<Transition2DInstruction>toArray(new Transition2DInstruction[v.size()]);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 123 */     if (this.type == 9) {
/* 124 */       if (this.isRandom) {
/* 125 */         return "Bars Horizontal Random";
/*     */       }
/* 127 */       return "Bars Horizontal";
/*     */     } 
/* 129 */     if (this.isRandom) {
/* 130 */       return "Bars Vertical Random";
/*     */     }
/* 132 */     return "Bars Vertical";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\BarsTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */