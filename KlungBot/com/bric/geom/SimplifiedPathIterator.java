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
/*     */ public class SimplifiedPathIterator
/*     */   implements PathIterator
/*     */ {
/*     */   private static final double TOL = 1.0E-4D;
/*     */   PathIterator i;
/*     */   double lastX;
/*     */   double lastY;
/*     */   
/*     */   public SimplifiedPathIterator(PathIterator i) {
/*  44 */     this.i = i;
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
/*     */   public static boolean collinear(double x1, double y1, double x2, double y2, double x3, double y3) {
/*  60 */     double determinant = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
/*     */     
/*  62 */     return (Math.abs(determinant) < 1.0E-8D);
/*     */   }
/*     */   
/*  65 */   private static double[] doubleArray = new double[6];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double[] d;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int simplify(int type, float lastX, float lastY, float[] data) {
/*  76 */     synchronized (doubleArray) {
/*  77 */       for (int a = 0; a < data.length; a++) {
/*  78 */         doubleArray[a] = data[a];
/*     */       }
/*  80 */       int returnValue = simplify(type, lastX, lastY, doubleArray);
/*  81 */       for (int i = 0; i < data.length; i++) {
/*  82 */         data[i] = (float)doubleArray[i];
/*     */       }
/*  84 */       return returnValue;
/*     */     } 
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
/*     */   public static int simplify(int type, double lastX, double lastY, double[] data) {
/*  98 */     if (type == 3) {
/*  99 */       if (collinear(lastX, lastY, data[4], data[5], data[0], data[1]) && 
/* 100 */         collinear(lastX, lastY, data[4], data[5], data[2], data[3])) {
/* 101 */         data[0] = data[4];
/* 102 */         data[1] = data[5];
/* 103 */         return 1;
/*     */       } 
/*     */ 
/*     */       
/* 107 */       double ax = -lastX + 3.0D * data[0] - 3.0D * data[2] + data[4];
/* 108 */       double ay = -lastY + 3.0D * data[1] - 3.0D * data[3] + data[5];
/*     */       
/* 110 */       if (Math.abs(ax) < 1.0E-6D && Math.abs(ay) < 1.0E-6D) {
/* 111 */         double bx = 3.0D * lastX - 6.0D * data[0] + 3.0D * data[2];
/* 112 */         double cx = -3.0D * lastX + 3.0D * data[0];
/*     */         
/* 114 */         double by = 3.0D * lastY - 6.0D * data[1] + 3.0D * data[3];
/* 115 */         double cy = -3.0D * lastY + 3.0D * data[1];
/*     */ 
/*     */         
/* 118 */         data[1] = (cy + 2.0D * lastY) / 2.0D;
/* 119 */         data[3] = by - lastY + 2.0D * data[1];
/*     */         
/* 121 */         data[0] = (cx + 2.0D * lastX) / 2.0D;
/* 122 */         data[2] = bx - lastX + 2.0D * data[0];
/*     */         
/* 124 */         return simplify(2, lastX, lastY, data);
/*     */       } 
/* 126 */     } else if (type == 2) {
/* 127 */       if (collinear(lastX, lastY, data[2], data[3], data[0], data[1])) {
/* 128 */         data[0] = data[2];
/* 129 */         data[1] = data[3];
/* 130 */         return 1;
/*     */       } 
/*     */       
/* 133 */       double ax = lastX - 2.0D * data[0] + data[2];
/* 134 */       double ay = lastY - 2.0D * data[1] + data[3];
/* 135 */       if (Math.abs(ax) < 1.0E-6D && Math.abs(ay) < 1.0E-6D) {
/* 136 */         double bx = -2.0D * lastX + 2.0D * data[0];
/*     */         
/* 138 */         double by = -2.0D * lastY + 2.0D * data[1];
/*     */ 
/*     */         
/* 141 */         data[0] = (bx + 2.0D * lastX) / 2.0D;
/* 142 */         data[1] = (by + 2.0D * lastY) / 2.0D;
/* 143 */         return 1;
/*     */       } 
/*     */     } 
/*     */     
/* 147 */     return type;
/*     */   }
/*     */   
/*     */   public int currentSegment(double[] f) {
/* 151 */     int type = this.i.currentSegment(f);
/* 152 */     type = simplify(type, this.lastX, this.lastY, f);
/* 153 */     if (type == 1 || type == 0) {
/* 154 */       this.lastX = f[0];
/* 155 */       this.lastY = f[1];
/* 156 */     } else if (type == 2) {
/* 157 */       this.lastX = f[2];
/* 158 */       this.lastY = f[3];
/* 159 */     } else if (type == 3) {
/* 160 */       this.lastX = f[4];
/* 161 */       this.lastY = f[5];
/*     */     } 
/* 163 */     return type;
/*     */   }
/*     */ 
/*     */   
/*     */   public int currentSegment(float[] f) {
/* 168 */     if (this.d == null) {
/* 169 */       this.d = new double[6];
/*     */     }
/* 171 */     int k = currentSegment(this.d);
/* 172 */     f[0] = (float)this.d[0];
/* 173 */     f[1] = (float)this.d[1];
/* 174 */     f[2] = (float)this.d[2];
/* 175 */     f[3] = (float)this.d[3];
/* 176 */     f[4] = (float)this.d[4];
/* 177 */     f[5] = (float)this.d[5];
/* 178 */     return k;
/*     */   }
/*     */   
/*     */   public int getWindingRule() {
/* 182 */     return this.i.getWindingRule();
/*     */   }
/*     */   
/*     */   public boolean isDone() {
/* 186 */     return this.i.isDone();
/*     */   }
/*     */   
/*     */   public void next() {
/* 190 */     this.i.next();
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\SimplifiedPathIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */