/*     */ package com.bric.geom;
/*     */ 
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.PathIterator;
/*     */ import java.awt.geom.Rectangle2D;
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
/*     */ public class ShapeBounds
/*     */ {
/*     */   public static Rectangle2D getBounds(Shape shape) throws EmptyPathException {
/*  41 */     return getBounds(shape, null, null);
/*     */   }
/*     */   
/*     */   public static Rectangle2D getBounds(Shape[] shapes) {
/*  45 */     Rectangle2D r = null;
/*  46 */     for (int a = 0; a < shapes.length; a++) {
/*     */       try {
/*  48 */         Rectangle2D t = getBounds(shapes[a]);
/*  49 */         if (r == null) {
/*  50 */           r = t;
/*     */         } else {
/*  52 */           r.add(t);
/*     */         } 
/*  54 */       } catch (EmptyPathException e) {}
/*     */     } 
/*  56 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Rectangle2D getBounds(Shape shape, AffineTransform transform) throws EmptyPathException {
/*  67 */     return getBounds(shape, transform, null);
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
/*     */   public static Rectangle2D getBounds(Shape shape, AffineTransform transform, Rectangle2D r) throws EmptyPathException {
/*  81 */     PathIterator i = shape.getPathIterator(transform);
/*  82 */     return getBounds(i, r);
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
/*     */   public static Rectangle2D getBounds(Shape shape, Rectangle2D r) throws EmptyPathException {
/*  95 */     return getBounds(shape, null, r);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Rectangle2D getBounds(PathIterator i) {
/* 104 */     return getBounds(i, (Rectangle2D)null);
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
/*     */   public static Rectangle2D getBounds(PathIterator i, Rectangle2D r) {
/* 116 */     float[] f = new float[6];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     float[] bounds = null;
/*     */     
/* 123 */     float lastX = 0.0F;
/* 124 */     float lastY = 0.0F;
/*     */ 
/*     */ 
/*     */     
/* 128 */     float[] x_coeff = new float[4];
/* 129 */     float[] y_coeff = new float[4];
/*     */ 
/*     */     
/* 132 */     while (!i.isDone()) {
/* 133 */       int k = i.currentSegment(f);
/* 134 */       if (k == 0) {
/* 135 */         lastX = f[0];
/* 136 */         lastY = f[1];
/* 137 */       } else if (k != 4) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 144 */         if (bounds == null) {
/* 145 */           bounds = new float[] { lastX, lastY, lastX, lastY };
/*     */         } else {
/* 147 */           if (lastX < bounds[0]) bounds[0] = lastX; 
/* 148 */           if (lastY < bounds[1]) bounds[1] = lastY; 
/* 149 */           if (lastX > bounds[2]) bounds[2] = lastX; 
/* 150 */           if (lastY > bounds[3]) bounds[3] = lastY;
/*     */         
/*     */         } 
/* 153 */         if (k == 1) {
/* 154 */           if (f[0] < bounds[0]) bounds[0] = f[0]; 
/* 155 */           if (f[1] < bounds[1]) bounds[1] = f[1]; 
/* 156 */           if (f[0] > bounds[2]) bounds[2] = f[0]; 
/* 157 */           if (f[1] > bounds[3]) bounds[3] = f[1]; 
/* 158 */           lastX = f[0];
/* 159 */           lastY = f[1];
/* 160 */         } else if (k == 2) {
/*     */           
/* 162 */           if (f[2] < bounds[0]) bounds[0] = f[2]; 
/* 163 */           if (f[3] < bounds[1]) bounds[1] = f[3]; 
/* 164 */           if (f[2] > bounds[2]) bounds[2] = f[2]; 
/* 165 */           if (f[3] > bounds[3]) bounds[3] = f[3];
/*     */ 
/*     */           
/* 168 */           x_coeff[0] = lastX - 2.0F * f[0] + f[2];
/* 169 */           x_coeff[1] = -2.0F * lastX + 2.0F * f[0];
/* 170 */           x_coeff[2] = lastX;
/* 171 */           y_coeff[0] = lastY - 2.0F * f[1] + f[3];
/* 172 */           y_coeff[1] = -2.0F * lastY + 2.0F * f[1];
/* 173 */           y_coeff[2] = lastY;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 178 */           float t = -x_coeff[1] / 2.0F * x_coeff[0];
/* 179 */           if (t > 0.0F && t < 1.0F) {
/* 180 */             float x = x_coeff[0] * t * t + x_coeff[1] * t + x_coeff[2];
/* 181 */             if (x < bounds[0]) bounds[0] = x; 
/* 182 */             if (x > bounds[2]) bounds[2] = x; 
/*     */           } 
/* 184 */           t = -y_coeff[1] / 2.0F * y_coeff[0];
/* 185 */           if (t > 0.0F && t < 1.0F) {
/* 186 */             float y = y_coeff[0] * t * t + y_coeff[1] * t + y_coeff[2];
/* 187 */             if (y < bounds[1]) bounds[1] = y; 
/* 188 */             if (y > bounds[3]) bounds[3] = y; 
/*     */           } 
/* 190 */           lastX = f[2];
/* 191 */           lastY = f[3];
/* 192 */         } else if (k == 3) {
/* 193 */           if (f[4] < bounds[0]) bounds[0] = f[4]; 
/* 194 */           if (f[5] < bounds[1]) bounds[1] = f[5]; 
/* 195 */           if (f[4] > bounds[2]) bounds[2] = f[4]; 
/* 196 */           if (f[5] > bounds[3]) bounds[3] = f[5];
/*     */           
/* 198 */           x_coeff[0] = -lastX + 3.0F * f[0] - 3.0F * f[2] + f[4];
/* 199 */           x_coeff[1] = 3.0F * lastX - 6.0F * f[0] + 3.0F * f[2];
/* 200 */           x_coeff[2] = -3.0F * lastX + 3.0F * f[0];
/* 201 */           x_coeff[3] = lastX;
/*     */           
/* 203 */           y_coeff[0] = -lastY + 3.0F * f[1] - 3.0F * f[3] + f[5];
/* 204 */           y_coeff[1] = 3.0F * lastY - 6.0F * f[1] + 3.0F * f[3];
/* 205 */           y_coeff[2] = -3.0F * lastY + 3.0F * f[1];
/* 206 */           y_coeff[3] = lastY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 215 */           float det = 4.0F * x_coeff[1] * x_coeff[1] - 12.0F * x_coeff[0] * x_coeff[2];
/* 216 */           if (det >= 0.0F)
/*     */           {
/* 218 */             if (det == 0.0F) {
/*     */               
/* 220 */               float t = -2.0F * x_coeff[1] / 6.0F * x_coeff[0];
/* 221 */               if (t > 0.0F && t < 1.0F) {
/* 222 */                 float x = x_coeff[0] * t * t * t + x_coeff[1] * t * t + x_coeff[2] * t + x_coeff[3];
/* 223 */                 if (x < bounds[0]) bounds[0] = x; 
/* 224 */                 if (x > bounds[2]) bounds[2] = x;
/*     */               
/*     */               } 
/*     */             } else {
/* 228 */               det = (float)Math.sqrt(det);
/* 229 */               float t = (-2.0F * x_coeff[1] + det) / 6.0F * x_coeff[0];
/* 230 */               if (t > 0.0F && t < 1.0F) {
/* 231 */                 float x = x_coeff[0] * t * t * t + x_coeff[1] * t * t + x_coeff[2] * t + x_coeff[3];
/* 232 */                 if (x < bounds[0]) bounds[0] = x; 
/* 233 */                 if (x > bounds[2]) bounds[2] = x;
/*     */               
/*     */               } 
/* 236 */               t = (-2.0F * x_coeff[1] - det) / 6.0F * x_coeff[0];
/* 237 */               if (t > 0.0F && t < 1.0F) {
/* 238 */                 float x = x_coeff[0] * t * t * t + x_coeff[1] * t * t + x_coeff[2] * t + x_coeff[3];
/* 239 */                 if (x < bounds[0]) bounds[0] = x; 
/* 240 */                 if (x > bounds[2]) bounds[2] = x; 
/*     */               } 
/*     */             } 
/*     */           }
/* 244 */           det = 4.0F * y_coeff[1] * y_coeff[1] - 12.0F * y_coeff[0] * y_coeff[2];
/* 245 */           if (det >= 0.0F)
/*     */           {
/* 247 */             if (det == 0.0F) {
/*     */               
/* 249 */               float t = -2.0F * y_coeff[1] / 6.0F * y_coeff[0];
/* 250 */               if (t > 0.0F && t < 1.0F) {
/* 251 */                 float y = y_coeff[0] * t * t * t + y_coeff[1] * t * t + y_coeff[2] * t + y_coeff[3];
/* 252 */                 if (y < bounds[1]) bounds[1] = y; 
/* 253 */                 if (y > bounds[3]) bounds[3] = y;
/*     */               
/*     */               } 
/*     */             } else {
/* 257 */               det = (float)Math.sqrt(det);
/* 258 */               float t = (-2.0F * y_coeff[1] + det) / 6.0F * y_coeff[0];
/* 259 */               if (t > 0.0F && t < 1.0F) {
/* 260 */                 float y = y_coeff[0] * t * t * t + y_coeff[1] * t * t + y_coeff[2] * t + y_coeff[3];
/* 261 */                 if (y < bounds[1]) bounds[1] = y; 
/* 262 */                 if (y > bounds[3]) bounds[3] = y;
/*     */               
/*     */               } 
/* 265 */               t = (-2.0F * y_coeff[1] - det) / 6.0F * y_coeff[0];
/* 266 */               if (t > 0.0F && t < 1.0F) {
/* 267 */                 float y = y_coeff[0] * t * t * t + y_coeff[1] * t * t + y_coeff[2] * t + y_coeff[3];
/* 268 */                 if (y < bounds[1]) bounds[1] = y; 
/* 269 */                 if (y > bounds[3]) bounds[3] = y; 
/*     */               } 
/*     */             } 
/*     */           }
/* 273 */           lastX = f[4];
/* 274 */           lastY = f[5];
/*     */         } 
/*     */       } 
/* 277 */       i.next();
/*     */     } 
/*     */     
/* 280 */     if (bounds == null) {
/* 281 */       throw new EmptyPathException();
/*     */     }
/* 283 */     if (r != null) {
/* 284 */       r.setFrame(bounds[0], bounds[1], (bounds[2] - bounds[0]), (bounds[3] - bounds[1]));
/* 285 */       return r;
/*     */     } 
/* 287 */     return new Rectangle2D.Float(bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1]);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\ShapeBounds.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */