/*     */ package com.bric.geom;
/*     */ 
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.GeneralPath;
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
/*     */ public class ShapeStringUtils
/*     */ {
/*     */   public static String toString(Shape s) {
/*  46 */     PathIterator i = s.getPathIterator(null);
/*  47 */     return toString(i);
/*     */   }
/*     */   
/*     */   public static String toString(PathIterator i) {
/*  51 */     float[] f = new float[6];
/*     */ 
/*     */     
/*  54 */     StringBuffer sb = new StringBuffer();
/*     */     
/*  56 */     int j = 0;
/*  57 */     while (!i.isDone()) {
/*  58 */       int k = i.currentSegment(f);
/*     */       
/*  60 */       if (k == 0) {
/*  61 */         sb.append('m');
/*  62 */         j = 2;
/*  63 */       } else if (k == 1) {
/*  64 */         sb.append('l');
/*  65 */         j = 2;
/*  66 */       } else if (k == 2) {
/*  67 */         sb.append('q');
/*  68 */         j = 4;
/*  69 */       } else if (k == 3) {
/*  70 */         sb.append('c');
/*  71 */         j = 6;
/*  72 */       } else if (k == 4) {
/*  73 */         sb.append('z');
/*  74 */         j = 0;
/*     */       } 
/*  76 */       if (j != 0) {
/*  77 */         sb.append(' ');
/*  78 */         for (int a = 0; a < j; a++) {
/*  79 */           sb.append(Float.toString(f[a]));
/*  80 */           if (a < j - 1) {
/*  81 */             sb.append(' ');
/*     */           }
/*     */         } 
/*     */       } 
/*  85 */       i.next();
/*  86 */       if (!i.isDone())
/*  87 */         sb.append(' '); 
/*     */     } 
/*  89 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PathIterator createPathIterator(String s) {
/* 101 */     return createPathIterator(s, 0);
/*     */   }
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
/*     */   public static GeneralPath createGeneralPath(String s) {
/* 117 */     GeneralPath p = new GeneralPath();
/* 118 */     p.append(createPathIterator(s), true);
/* 119 */     return p;
/*     */   }
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
/*     */   public static PathIterator createPathIterator(String s, int windingRule) {
/* 137 */     int i1 = s.indexOf('[');
/* 138 */     int i2 = s.indexOf(']');
/* 139 */     if (i1 != -1 && i2 != -2 && i1 < i2) {
/* 140 */       s = s.substring(i1 + 1, i2);
/*     */     }
/* 142 */     return new SerializedPathIterator(s, 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\ShapeStringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */