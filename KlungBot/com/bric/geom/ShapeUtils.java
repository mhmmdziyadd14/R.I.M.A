/*     */ package com.bric.geom;
/*     */ 
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.PathIterator;
/*     */ import java.awt.geom.Point2D;
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
/*     */ public class ShapeUtils
/*     */ {
/*     */   public static GeneralPath traceShape(Shape shape, float progress) {
/*  42 */     if (progress < 0.0F || progress > 1.0F) {
/*     */ 
/*     */       
/*  45 */       if (progress < -0.01D)
/*  46 */         throw new IllegalArgumentException("progress cannot be less than zero (" + progress + ")"); 
/*  47 */       if (progress > 1.01D) {
/*  48 */         throw new IllegalArgumentException("progress cannot be greater than one (" + progress + ")");
/*     */       }
/*  50 */       if (progress < 0.0F)
/*  51 */         progress = 0.0F; 
/*  52 */       if (progress > 1.0F)
/*  53 */         progress = 1.0F; 
/*     */     } 
/*  55 */     float[] f = new float[6];
/*  56 */     PathIterator i = shape.getPathIterator(null);
/*  57 */     float ctr = 0.0F;
/*     */     
/*  59 */     while (!i.isDone()) {
/*  60 */       int k = i.currentSegment(f);
/*  61 */       if (k != 0 && k != 4)
/*  62 */         ctr++; 
/*  63 */       i.next();
/*     */     } 
/*     */     
/*  66 */     GeneralPath path = new GeneralPath(i.getWindingRule());
/*  67 */     i = shape.getPathIterator(null);
/*  68 */     float lastX = 0.0F;
/*  69 */     float lastY = 0.0F;
/*  70 */     float ctr2 = 0.0F;
/*  71 */     while (!i.isDone()) {
/*  72 */       int k = i.currentSegment(f);
/*     */       
/*  74 */       float t = (progress - ctr2 / ctr) * ctr;
/*  75 */       if (t <= 0.0F) return path; 
/*  76 */       if (t >= 1.0F) t = 1.0F;
/*     */       
/*  78 */       if (k == 0) {
/*  79 */         path.moveTo(f[0], f[1]);
/*  80 */       } else if (k == 1) {
/*  81 */         path.lineTo(lastX * (1.0F - t) + t * f[0], lastY * (1.0F - t) + t * f[1]);
/*  82 */       } else if (k == 2) {
/*  83 */         if (t > 0.999999D) {
/*  84 */           path.quadTo(f[0], f[1], f[2], f[3]);
/*     */         } else {
/*  86 */           double t0 = 0.0D;
/*  87 */           double t1 = t;
/*     */           
/*  89 */           double ay = (lastY - 2.0F * f[1] + f[3]);
/*  90 */           double by = (-2.0F * lastY + 2.0F * f[1]);
/*  91 */           double cy = lastY;
/*     */           
/*  93 */           double ax = (lastX - 2.0F * f[0] + f[2]);
/*  94 */           double bx = (-2.0F * lastX + 2.0F * f[0]);
/*  95 */           double cx = lastX;
/*     */           
/*  97 */           double tZ = (t0 + t1) / 2.0D;
/*     */           
/*  99 */           double f0 = ay * t0 * t0 + by * t0 + cy;
/* 100 */           double f1 = ay * tZ * tZ + by * tZ + cy;
/* 101 */           double f2 = ay * t1 * t1 + by * t1 + cy;
/*     */           
/* 103 */           double ay2 = 2.0D * f2 - 4.0D * f1 + 2.0D * f0;
/* 104 */           double cy2 = f0;
/* 105 */           double by2 = f2 - cy2 - ay2;
/*     */           
/* 107 */           f0 = ax * t0 * t0 + bx * t0 + cx;
/* 108 */           f1 = ax * tZ * tZ + bx * tZ + cx;
/* 109 */           f2 = ax * t1 * t1 + bx * t1 + cx;
/*     */           
/* 111 */           double ax2 = 2.0D * f2 - 4.0D * f1 + 2.0D * f0;
/* 112 */           double cx2 = f0;
/* 113 */           double bx2 = f2 - cx2 - ax2;
/*     */           
/* 115 */           double ctrlY = (2.0D * cy2 + by2) / 2.0D;
/* 116 */           double y1 = ay2 - cy2 + 2.0D * ctrlY;
/*     */           
/* 118 */           double ctrlX = (2.0D * cx2 + bx2) / 2.0D;
/* 119 */           double x1 = ax2 - cx2 + 2.0D * ctrlX;
/*     */           
/* 121 */           path.quadTo((float)ctrlX, (float)ctrlY, (float)x1, (float)y1);
/*     */         }
/*     */       
/* 124 */       } else if (k == 3) {
/* 125 */         if (t > 0.999999D) {
/* 126 */           path.curveTo(f[0], f[1], f[2], f[3], f[4], f[5]);
/*     */         } else {
/* 128 */           double t0 = 0.0D;
/* 129 */           double t1 = t;
/* 130 */           double ay = (-lastY + 3.0F * f[1] - 3.0F * f[3] + f[5]);
/* 131 */           double by = (3.0F * lastY - 6.0F * f[1] + 3.0F * f[3]);
/* 132 */           double cy = (-3.0F * lastY + 3.0F * f[1]);
/* 133 */           double dy = lastY;
/*     */           
/* 135 */           double ax = (-lastX + 3.0F * f[0] - 3.0F * f[2] + f[4]);
/* 136 */           double bx = (3.0F * lastX - 6.0F * f[0] + 3.0F * f[2]);
/* 137 */           double cx = (-3.0F * lastX + 3.0F * f[0]);
/* 138 */           double dx = lastX;
/*     */           
/* 140 */           double tW = 2.0D * t0 / 3.0D + t1 / 3.0D;
/* 141 */           double tZ = t0 / 3.0D + 2.0D * t1 / 3.0D;
/*     */           
/* 143 */           double f0 = ay * t0 * t0 * t0 + by * t0 * t0 + cy * t0 + dy;
/* 144 */           double f1 = ay * tW * tW * tW + by * tW * tW + cy * tW + dy;
/* 145 */           double f2 = ay * tZ * tZ * tZ + by * tZ * tZ + cy * tZ + dy;
/* 146 */           double f3 = ay * t1 * t1 * t1 + by * t1 * t1 + cy * t1 + dy;
/*     */           
/* 148 */           double dy2 = f0;
/* 149 */           double cy2 = (-11.0D * f0 + 18.0D * f1 - 9.0D * f2 + 2.0D * f3) / 2.0D;
/* 150 */           double by2 = (-19.0D * f0 + 27.0D * f2 - 8.0D * f3 - 10.0D * cy2) / 4.0D;
/* 151 */           double ay2 = f3 - by2 - cy2 - f0;
/*     */           
/* 153 */           f0 = ax * t0 * t0 * t0 + bx * t0 * t0 + cx * t0 + dx;
/* 154 */           f1 = ax * tW * tW * tW + bx * tW * tW + cx * tW + dx;
/* 155 */           f2 = ax * tZ * tZ * tZ + bx * tZ * tZ + cx * tZ + dx;
/* 156 */           f3 = ax * t1 * t1 * t1 + bx * t1 * t1 + cx * t1 + dx;
/*     */           
/* 158 */           double dx2 = f0;
/* 159 */           double cx2 = (-11.0D * f0 + 18.0D * f1 - 9.0D * f2 + 2.0D * f3) / 2.0D;
/* 160 */           double bx2 = (-19.0D * f0 + 27.0D * f2 - 8.0D * f3 - 10.0D * cx2) / 4.0D;
/* 161 */           double ax2 = f3 - bx2 - cx2 - f0;
/*     */           
/* 163 */           double cy0 = (3.0D * dy2 + cy2) / 3.0D;
/* 164 */           double cy1 = (by2 - 3.0D * dy2 + 6.0D * cy0) / 3.0D;
/* 165 */           double y1 = ay2 + dy2 - 3.0D * cy0 + 3.0D * cy1;
/*     */           
/* 167 */           double cx0 = (3.0D * dx2 + cx2) / 3.0D;
/* 168 */           double cx1 = (bx2 - 3.0D * dx2 + 6.0D * cx0) / 3.0D;
/* 169 */           double x1 = ax2 + dx2 - 3.0D * cx0 + 3.0D * cx1;
/*     */           
/* 171 */           path.curveTo((float)cx0, (float)cy0, (float)cx1, (float)cy1, (float)x1, (float)y1);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 177 */       if (k != 0 && k != 4)
/* 178 */         ctr2++; 
/* 179 */       i.next();
/* 180 */       if (k == 0 || k == 1) {
/* 181 */         lastX = f[0];
/* 182 */         lastY = f[1]; continue;
/* 183 */       }  if (k == 2) {
/* 184 */         lastX = f[2];
/* 185 */         lastY = f[3]; continue;
/* 186 */       }  if (k == 3) {
/* 187 */         lastX = f[4];
/* 188 */         lastY = f[5];
/*     */       } 
/*     */     } 
/* 191 */     return path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Point2D getPoint(Shape shape, float progress) {
/* 200 */     if (progress < 0.0F || progress > 1.0F) {
/*     */ 
/*     */       
/* 203 */       if (progress < -0.01D)
/* 204 */         throw new IllegalArgumentException("progress cannot be less than zero (" + progress + ")"); 
/* 205 */       if (progress > 1.01D) {
/* 206 */         throw new IllegalArgumentException("progress cannot be greater than one (" + progress + ")");
/*     */       }
/* 208 */       if (progress < 0.0F)
/* 209 */         progress = 0.0F; 
/* 210 */       if (progress > 1.0F)
/* 211 */         progress = 1.0F; 
/*     */     } 
/* 213 */     float[] f = new float[6];
/* 214 */     PathIterator i = shape.getPathIterator(null);
/* 215 */     float ctr = 0.0F;
/*     */     
/* 217 */     while (!i.isDone()) {
/* 218 */       int k = i.currentSegment(f);
/* 219 */       if (k != 0 && k != 4)
/* 220 */         ctr++; 
/* 221 */       i.next();
/*     */     } 
/*     */     
/* 224 */     i = shape.getPathIterator(null);
/* 225 */     float lastX = 0.0F;
/* 226 */     float lastY = 0.0F;
/* 227 */     float ctr2 = 0.0F;
/*     */     
/* 229 */     while (!i.isDone()) {
/* 230 */       int k = i.currentSegment(f);
/*     */       
/* 232 */       float t = (progress - ctr2 / ctr) * ctr;
/* 233 */       if (t <= 0.0F) return new Point2D.Double(lastX, lastY); 
/* 234 */       if (t >= 1.0F) t = 1.0F;
/*     */       
/* 236 */       if (k == 0) {
/* 237 */         lastX = f[0];
/* 238 */         lastY = f[1];
/* 239 */       } else if (k == 1) {
/* 240 */         lastX = lastX * (1.0F - t) + t * f[0];
/* 241 */         lastY = lastY * (1.0F - t) + t * f[1];
/* 242 */       } else if (k == 2) {
/* 243 */         if (t > 0.999999D) {
/* 244 */           lastX = f[2];
/* 245 */           lastY = f[3];
/*     */         } else {
/* 247 */           double ay = (lastY - 2.0F * f[1] + f[3]);
/* 248 */           double by = (-2.0F * lastY + 2.0F * f[1]);
/* 249 */           double cy = lastY;
/*     */           
/* 251 */           double ax = (lastX - 2.0F * f[0] + f[2]);
/* 252 */           double bx = (-2.0F * lastX + 2.0F * f[0]);
/* 253 */           double cx = lastX;
/*     */           
/* 255 */           lastX = (float)(ax * t * t + bx * t + cx);
/* 256 */           lastY = (float)(ay * t * t + by * t + cy);
/*     */         } 
/* 258 */       } else if (k == 3) {
/* 259 */         if (t > 0.999999D) {
/* 260 */           lastX = f[4];
/* 261 */           lastY = f[5];
/*     */         } else {
/* 263 */           double ay = (-lastY + 3.0F * f[1] - 3.0F * f[3] + f[5]);
/* 264 */           double by = (3.0F * lastY - 6.0F * f[1] + 3.0F * f[3]);
/* 265 */           double cy = (-3.0F * lastY + 3.0F * f[1]);
/* 266 */           double dy = lastY;
/*     */           
/* 268 */           double ax = (-lastX + 3.0F * f[0] - 3.0F * f[2] + f[4]);
/* 269 */           double bx = (3.0F * lastX - 6.0F * f[0] + 3.0F * f[2]);
/* 270 */           double cx = (-3.0F * lastX + 3.0F * f[0]);
/* 271 */           double dx = lastX;
/*     */           
/* 273 */           lastX = (float)(ax * t * t * t + bx * t * t + cx * t + dx);
/* 274 */           lastY = (float)(ay * t * t * t + by * t * t + cy * t + dy);
/*     */         } 
/*     */       } 
/*     */       
/* 278 */       if (k != 0 && k != 4)
/* 279 */         ctr2++; 
/* 280 */       i.next();
/*     */     } 
/* 282 */     return new Point2D.Double(lastX, lastY);
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getSubPathCount(Shape s) {
/* 287 */     PathIterator i = s.getPathIterator(null);
/* 288 */     int ctr = 0;
/* 289 */     float[] coords = new float[6];
/* 290 */     while (!i.isDone()) {
/* 291 */       if (i.currentSegment(coords) == 0) {
/* 292 */         ctr++;
/*     */       }
/* 294 */       i.next();
/*     */     } 
/* 296 */     return ctr++;
/*     */   }
/*     */ 
/*     */   
/*     */   public static GeneralPath[] getSubPaths(Shape s) {
/* 301 */     String s2 = ShapeStringUtils.toString(s);
/* 302 */     int ctr = 0;
/* 303 */     int i = 0;
/* 304 */     while (i < s2.length()) {
/* 305 */       int k = s2.indexOf('m', i);
/* 306 */       if (k == -1) {
/* 307 */         i = s2.length(); continue;
/*     */       } 
/* 309 */       ctr++;
/* 310 */       i = k + 1;
/*     */     } 
/*     */     
/* 313 */     int[] indices = new int[ctr];
/* 314 */     ctr = 0;
/* 315 */     i = 0;
/* 316 */     while (i < s2.length()) {
/* 317 */       int k = s2.indexOf('m', i);
/* 318 */       if (k == -1) {
/* 319 */         i = s2.length(); continue;
/*     */       } 
/* 321 */       indices[ctr++] = k;
/* 322 */       i = k + 1;
/*     */     } 
/*     */ 
/*     */     
/* 326 */     GeneralPath[] p = new GeneralPath[ctr];
/* 327 */     for (i = 0; i < indices.length; i++) {
/*     */       String text;
/* 329 */       if (i < indices.length - 1) {
/* 330 */         text = s2.substring(indices[i], indices[i + 1] - 1);
/*     */       } else {
/* 332 */         text = s2.substring(indices[i]);
/*     */       } 
/* 334 */       p[i] = ShapeStringUtils.createGeneralPath(text);
/*     */     } 
/* 336 */     return p;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\ShapeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */