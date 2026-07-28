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
/*     */ public abstract class PathWriter
/*     */ {
/*     */   public abstract void moveTo(float paramFloat1, float paramFloat2);
/*     */   
/*     */   public abstract void lineTo(float paramFloat1, float paramFloat2);
/*     */   
/*     */   public abstract void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
/*     */   
/*     */   public abstract void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);
/*     */   
/*     */   public abstract void closePath();
/*     */   
/*     */   public abstract void flush();
/*     */   
/*     */   public void write(Shape s) {
/*  70 */     write(s.getPathIterator(null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(PathIterator i) {
/*  80 */     float[] coords = new float[6];
/*     */     
/*  82 */     while (!i.isDone()) {
/*  83 */       int k = i.currentSegment(coords);
/*  84 */       if (k == 0) {
/*  85 */         moveTo(coords[0], coords[1]);
/*  86 */       } else if (k == 1) {
/*  87 */         lineTo(coords[0], coords[1]);
/*  88 */       } else if (k == 2) {
/*  89 */         quadTo(coords[0], coords[1], coords[2], coords[3]);
/*  90 */       } else if (k == 3) {
/*  91 */         curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
/*  92 */       } else if (k == 4) {
/*  93 */         closePath();
/*     */       } else {
/*  95 */         throw new RuntimeException("Unexpected segment: " + k);
/*     */       } 
/*  97 */       i.next();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void cubicTo(GeneralPath path, double t0, double t1, double ax, double bx, double cx, double dx, double ay, double by, double cy, double dy) {
/* 116 */     cubicTo2(path, t0, t1, ax, bx, cx, dx, ay, by, cy, dy);
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
/*     */   public static void cubicTo(PathWriter path, double t0, double t1, double ax, double bx, double cx, double dx, double ay, double by, double cy, double dy) {
/* 133 */     cubicTo2(path, t0, t1, ax, bx, cx, dx, ay, by, cy, dy);
/*     */   }
/*     */   private static void cubicTo2(Object obj, double t0, double t1, double ax, double bx, double cx, double dx, double ay, double by, double cy, double dy) {
/* 136 */     double tW = 2.0D * t0 / 3.0D + t1 / 3.0D;
/* 137 */     double tZ = t0 / 3.0D + 2.0D * t1 / 3.0D;
/*     */     
/* 139 */     double f0 = ay * t0 * t0 * t0 + by * t0 * t0 + cy * t0 + dy;
/* 140 */     double f1 = ay * tW * tW * tW + by * tW * tW + cy * tW + dy;
/* 141 */     double f2 = ay * tZ * tZ * tZ + by * tZ * tZ + cy * tZ + dy;
/* 142 */     double f3 = ay * t1 * t1 * t1 + by * t1 * t1 + cy * t1 + dy;
/*     */     
/* 144 */     double dy2 = f0;
/* 145 */     double cy2 = (-11.0D * f0 + 18.0D * f1 - 9.0D * f2 + 2.0D * f3) / 2.0D;
/* 146 */     double by2 = (-19.0D * f0 + 27.0D * f2 - 8.0D * f3 - 10.0D * cy2) / 4.0D;
/* 147 */     double ay2 = f3 - by2 - cy2 - f0;
/*     */     
/* 149 */     f0 = ax * t0 * t0 * t0 + bx * t0 * t0 + cx * t0 + dx;
/* 150 */     f1 = ax * tW * tW * tW + bx * tW * tW + cx * tW + dx;
/* 151 */     f2 = ax * tZ * tZ * tZ + bx * tZ * tZ + cx * tZ + dx;
/* 152 */     f3 = ax * t1 * t1 * t1 + bx * t1 * t1 + cx * t1 + dx;
/*     */     
/* 154 */     double dx2 = f0;
/* 155 */     double cx2 = (-11.0D * f0 + 18.0D * f1 - 9.0D * f2 + 2.0D * f3) / 2.0D;
/* 156 */     double bx2 = (-19.0D * f0 + 27.0D * f2 - 8.0D * f3 - 10.0D * cx2) / 4.0D;
/* 157 */     double ax2 = f3 - bx2 - cx2 - f0;
/*     */     
/* 159 */     double cy0 = (3.0D * dy2 + cy2) / 3.0D;
/* 160 */     double cy1 = (by2 - 3.0D * dy2 + 6.0D * cy0) / 3.0D;
/* 161 */     double y1 = ay2 + dy2 - 3.0D * cy0 + 3.0D * cy1;
/*     */     
/* 163 */     double cx0 = (3.0D * dx2 + cx2) / 3.0D;
/* 164 */     double cx1 = (bx2 - 3.0D * dx2 + 6.0D * cx0) / 3.0D;
/* 165 */     double x1 = ax2 + dx2 - 3.0D * cx0 + 3.0D * cx1;
/*     */     
/* 167 */     if (obj instanceof GeneralPath) {
/* 168 */       ((GeneralPath)obj).curveTo((float)cx0, (float)cy0, (float)cx1, (float)cy1, (float)x1, (float)y1);
/*     */     
/*     */     }
/* 171 */     else if (obj instanceof PathWriter) {
/* 172 */       ((PathWriter)obj).curveTo((float)cx0, (float)cy0, (float)cx1, (float)cy1, (float)x1, (float)y1);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void quadTo(GeneralPath path, double t0, double t1, double ax, double bx, double cx, double ay, double by, double cy) {
/* 191 */     quadTo2(path, t0, t1, ax, bx, cx, ay, by, cy);
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
/*     */   public static void quadTo(PathWriter path, double t0, double t1, double ax, double bx, double cx, double ay, double by, double cy) {
/* 207 */     quadTo2(path, t0, t1, ax, bx, cx, ay, by, cy);
/*     */   }
/*     */   
/*     */   private static void quadTo2(Object obj, double t0, double t1, double ax, double bx, double cx, double ay, double by, double cy) {
/* 211 */     double tZ = (t0 + t1) / 2.0D;
/*     */     
/* 213 */     double f0 = ay * t0 * t0 + by * t0 + cy;
/* 214 */     double f1 = ay * tZ * tZ + by * tZ + cy;
/* 215 */     double f2 = ay * t1 * t1 + by * t1 + cy;
/*     */     
/* 217 */     double ay2 = 2.0D * f2 - 4.0D * f1 + 2.0D * f0;
/* 218 */     double cy2 = f0;
/* 219 */     double by2 = f2 - cy2 - ay2;
/*     */     
/* 221 */     f0 = ax * t0 * t0 + bx * t0 + cx;
/* 222 */     f1 = ax * tZ * tZ + bx * tZ + cx;
/* 223 */     f2 = ax * t1 * t1 + bx * t1 + cx;
/*     */     
/* 225 */     double ax2 = 2.0D * f2 - 4.0D * f1 + 2.0D * f0;
/* 226 */     double cx2 = f0;
/* 227 */     double bx2 = f2 - cx2 - ax2;
/*     */     
/* 229 */     double ctrlY = (2.0D * cy2 + by2) / 2.0D;
/* 230 */     double y1 = ay2 - cy2 + 2.0D * ctrlY;
/*     */     
/* 232 */     double ctrlX = (2.0D * cx2 + bx2) / 2.0D;
/* 233 */     double x1 = ax2 - cx2 + 2.0D * ctrlX;
/*     */     
/* 235 */     if (obj instanceof GeneralPath) {
/* 236 */       ((GeneralPath)obj).quadTo((float)ctrlX, (float)ctrlY, (float)x1, (float)y1);
/*     */     }
/* 238 */     else if (obj instanceof PathWriter) {
/* 239 */       ((PathWriter)obj).quadTo((float)ctrlX, (float)ctrlY, (float)x1, (float)y1);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\PathWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */