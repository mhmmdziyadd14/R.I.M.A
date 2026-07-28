/*     */ package com.bric.geom;
/*     */ 
/*     */ import com.bric.math.MathG;
/*     */ import java.awt.Rectangle;
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
/*     */ public class RectangleReader
/*     */ {
/*     */   private static final double TOL = 1.0E-12D;
/*     */   
/*     */   public static boolean isRectangle(Shape s) {
/*  42 */     return (convert(s) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isRectangle(Shape s, AffineTransform tx) {
/*  47 */     return (convert(s, tx) != null);
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
/*     */   public static final Rectangle2D convert(Shape shape) {
/*  59 */     return convert(shape, null);
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
/*     */   public static final Rectangle2D convert(Shape shape, AffineTransform transform) {
/*  72 */     if (shape == null) {
/*  73 */       return null;
/*     */     }
/*  75 */     if (transform != null && transform.isIdentity()) {
/*  76 */       transform = null;
/*     */     }
/*  78 */     if (shape instanceof Rectangle && transform == null) {
/*  79 */       return (Rectangle)shape;
/*     */     }
/*  81 */     if (shape instanceof Rectangle2D && transform == null) {
/*  82 */       Rectangle2D rect = (Rectangle2D)shape;
/*  83 */       return getRectangle(rect);
/*     */     } 
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
/*  96 */     double[] data = new double[6];
/*     */ 
/*     */ 
/*     */     
/* 100 */     double lastX = 0.0D;
/* 101 */     double lastY = 0.0D;
/*     */     
/* 103 */     PathIterator i = shape.getPathIterator(transform);
/*     */     
/* 105 */     double left = 0.0D;
/* 106 */     double right = 0.0D;
/* 107 */     double top = 0.0D;
/* 108 */     double bottom = 0.0D;
/* 109 */     boolean defined = false;
/* 110 */     double moveX = 0.0D;
/* 111 */     double moveY = 0.0D;
/*     */     
/* 113 */     while (!i.isDone()) {
/* 114 */       int k = i.currentSegment(data);
/* 115 */       k = SimplifiedPathIterator.simplify(k, lastX, lastY, data);
/* 116 */       if (k == 4) {
/* 117 */         k = 1;
/* 118 */         data[0] = moveX;
/* 119 */         data[1] = moveY;
/*     */       } 
/*     */       
/* 122 */       if (k == 0) {
/* 123 */         moveX = data[0];
/* 124 */         moveY = data[1];
/* 125 */         lastX = data[0];
/* 126 */         lastY = data[1];
/*     */         
/* 128 */         if (defined)
/* 129 */           return null; 
/* 130 */       } else if (k != 4) {
/*     */         
/* 132 */         if (k == 1) {
/*     */           
/* 134 */           left = right = lastX;
/* 135 */           top = bottom = lastY;
/* 136 */           defined = true;
/*     */           
/* 138 */           if (lastX < left) left = lastX; 
/* 139 */           if (lastY < top) top = lastY; 
/* 140 */           if (lastX > right) right = lastX; 
/* 141 */           if (lastY > bottom) bottom = lastY;
/*     */ 
/*     */ 
/*     */           
/* 145 */           if (lastX != data[0] && lastY != data[1]) {
/* 146 */             return null;
/*     */           }
/*     */           
/* 149 */           if (data[0] < left) left = data[0]; 
/* 150 */           if (data[1] < top) top = data[1]; 
/* 151 */           if (data[0] > right) right = data[0]; 
/* 152 */           if (data[1] > bottom) bottom = data[1]; 
/* 153 */           lastX = data[0];
/* 154 */           lastY = data[1];
/*     */         } else {
/* 156 */           return null;
/*     */         } 
/* 158 */       }  i.next();
/*     */     } 
/*     */     
/* 161 */     if (!defined) {
/* 162 */       return null;
/*     */     }
/* 164 */     if (lastX != moveX && lastY != moveY) {
/* 165 */       return null;
/*     */     }
/* 167 */     i = shape.getPathIterator(transform);
/*     */     
/* 169 */     while (!i.isDone()) {
/* 170 */       int k = i.currentSegment(data);
/* 171 */       k = SimplifiedPathIterator.simplify(k, lastX, lastY, data);
/* 172 */       if (k == 0) {
/* 173 */         lastX = data[0];
/* 174 */         lastY = data[1];
/* 175 */       } else if (k == 1) {
/* 176 */         double midX = (data[0] + lastX) / 2.0D;
/* 177 */         double midY = (data[1] + lastY) / 2.0D;
/* 178 */         if (data[1] == top) {
/* 179 */           if (!SimplifiedPathIterator.collinear(left, top, right, top, data[0], data[1])) {
/* 180 */             return null;
/*     */           }
/* 182 */         } else if (data[1] == bottom) {
/* 183 */           if (!SimplifiedPathIterator.collinear(left, bottom, right, bottom, data[0], data[1])) {
/* 184 */             return null;
/*     */           }
/* 186 */         } else if (data[0] == left) {
/* 187 */           if (!SimplifiedPathIterator.collinear(left, top, left, bottom, data[0], data[1])) {
/* 188 */             return null;
/*     */           }
/* 190 */         } else if (data[0] == right) {
/* 191 */           if (!SimplifiedPathIterator.collinear(right, top, right, bottom, data[0], data[1])) {
/* 192 */             return null;
/*     */           }
/*     */         } else {
/* 195 */           return null;
/*     */         } 
/*     */         
/* 198 */         if (midY == top) {
/* 199 */           if (!SimplifiedPathIterator.collinear(left, top, right, top, midX, midY)) {
/* 200 */             return null;
/*     */           }
/* 202 */         } else if (midY == bottom) {
/* 203 */           if (!SimplifiedPathIterator.collinear(left, bottom, right, bottom, midX, midY)) {
/* 204 */             return null;
/*     */           }
/* 206 */         } else if (midX == left) {
/* 207 */           if (!SimplifiedPathIterator.collinear(left, top, left, bottom, midX, midY)) {
/* 208 */             return null;
/*     */           }
/* 210 */         } else if (midX == right) {
/* 211 */           if (!SimplifiedPathIterator.collinear(right, top, right, bottom, midX, midY)) {
/* 212 */             return null;
/*     */           }
/*     */         } else {
/* 215 */           return null;
/*     */         } 
/* 217 */         lastX = data[0];
/* 218 */         lastY = data[1];
/*     */       } 
/*     */       
/* 221 */       i.next();
/*     */     } 
/*     */     
/* 224 */     Rectangle intRect = getRectangle(left, top, right - left, bottom - top);
/* 225 */     if (intRect != null) return intRect;
/*     */     
/* 227 */     return new Rectangle2D.Double(left, top, right - left, bottom - top);
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
/*     */   private static final Rectangle2D getRectangle(Rectangle2D r) {
/* 239 */     double x = r.getX();
/* 240 */     double y = r.getY();
/* 241 */     double w = r.getWidth();
/* 242 */     double h = r.getHeight();
/* 243 */     Rectangle newRect = getRectangle(x, y, w, h);
/* 244 */     if (newRect != null)
/* 245 */       return newRect; 
/* 246 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Rectangle getRectangle(double x, double y, double w, double h) {
/* 256 */     if (w < 0.0D) {
/* 257 */       x += w;
/* 258 */       w = -w;
/*     */     } 
/* 260 */     if (h < 0.0D) {
/* 261 */       y += w;
/* 262 */       h = -h;
/*     */     } 
/*     */     
/* 265 */     int iw = MathG.roundInt(w);
/* 266 */     int ih = MathG.roundInt(h);
/* 267 */     if (Math.abs(iw - w) > 1.0E-12D)
/* 268 */       return null; 
/* 269 */     if (Math.abs(ih - h) > 1.0E-12D)
/* 270 */       return null; 
/* 271 */     int ix = MathG.roundInt(x);
/* 272 */     int iy = MathG.roundInt(y);
/* 273 */     if (Math.abs(ix - x) > 1.0E-12D)
/* 274 */       return null; 
/* 275 */     if (Math.abs(iy - y) > 1.0E-12D) {
/* 276 */       return null;
/*     */     }
/* 278 */     return new Rectangle(ix, iy, iw, ih);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\RectangleReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */