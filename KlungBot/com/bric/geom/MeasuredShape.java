/*     */ package com.bric.geom;
/*     */ 
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.PathIterator;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.io.Serializable;
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
/*     */ public class MeasuredShape
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final float DEFAULT_SPACING = 0.05F;
/*     */   Segment[] segments;
/*     */   
/*     */   public static MeasuredShape[] getSubpaths(Shape s) {
/*  46 */     return getSubpaths(s.getPathIterator(null), 0.05F);
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
/*     */   public static MeasuredShape[] getSubpaths(Shape s, float spacing) {
/*  58 */     return getSubpaths(s.getPathIterator(null), spacing);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MeasuredShape[] getSubpaths(PathIterator i) {
/*  69 */     return getSubpaths(i, 0.05F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MeasuredShape[] getSubpaths(PathIterator i, float spacing) {
/*  80 */     Vector<MeasuredShape> v = new Vector<>();
/*  81 */     GeneralPath path = null;
/*  82 */     float[] coords = new float[6];
/*  83 */     while (!i.isDone()) {
/*  84 */       int k = i.currentSegment(coords);
/*  85 */       if (k == 0) {
/*  86 */         if (path != null) {
/*  87 */           v.add(new MeasuredShape(path, spacing));
/*  88 */           path = null;
/*     */         } 
/*  90 */         path = new GeneralPath();
/*  91 */         path.moveTo(coords[0], coords[1]);
/*  92 */       } else if (k == 1) {
/*  93 */         path.lineTo(coords[0], coords[1]);
/*  94 */       } else if (k == 2) {
/*  95 */         path.quadTo(coords[0], coords[1], coords[2], coords[3]);
/*  96 */       } else if (k == 3) {
/*  97 */         path.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
/*  98 */       } else if (k == 4) {
/*  99 */         path.closePath();
/*     */       } 
/* 101 */       i.next();
/*     */     } 
/* 103 */     if (path != null) {
/* 104 */       v.add(new MeasuredShape(path, spacing));
/* 105 */       path = null;
/*     */     } 
/* 107 */     return v.<MeasuredShape>toArray(new MeasuredShape[v.size()]);
/*     */   }
/*     */   
/*     */   static class Segment
/*     */     implements Serializable {
/*     */     private static final long serialVersionUID = 1L;
/*     */     int type;
/*     */     float[] data;
/*     */     float realDistance;
/*     */     float normalizedDistance;
/*     */     
/*     */     public void write(PathWriter path, float t0, float t1) {
/* 119 */       if (t0 == 0.0F && t1 == 1.0F) {
/* 120 */         if (this.type == 0) {
/* 121 */           path.moveTo(this.data[0], this.data[1]);
/* 122 */         } else if (this.type == 1) {
/* 123 */           path.lineTo(this.data[2], this.data[3]);
/* 124 */         } else if (this.type == 2) {
/* 125 */           path.quadTo(this.data[2], this.data[3], this.data[4], this.data[5]);
/* 126 */         } else if (this.type == 3) {
/* 127 */           path.curveTo(this.data[2], this.data[3], this.data[4], this.data[5], this.data[6], this.data[7]);
/*     */         } else {
/* 129 */           throw new RuntimeException();
/*     */         }  return;
/*     */       } 
/* 132 */       if (t0 == 1.0F && t1 == 0.0F) {
/* 133 */         if (this.type == 0) {
/* 134 */           path.moveTo(this.data[0], this.data[1]);
/* 135 */         } else if (this.type == 1) {
/* 136 */           path.lineTo(this.data[0], this.data[1]);
/* 137 */         } else if (this.type == 2) {
/* 138 */           path.quadTo(this.data[2], this.data[3], this.data[0], this.data[1]);
/* 139 */         } else if (this.type == 3) {
/* 140 */           path.curveTo(this.data[4], this.data[5], this.data[2], this.data[3], this.data[0], this.data[1]);
/*     */         } else {
/* 142 */           throw new RuntimeException();
/*     */         } 
/*     */         return;
/*     */       } 
/* 146 */       if (this.type == 0) {
/* 147 */         path.moveTo(this.data[0], this.data[1]);
/* 148 */       } else if (this.type == 1) {
/* 149 */         path.lineTo(getX(t1), getY(t1));
/* 150 */       } else if (this.type == 2) {
/* 151 */         float ax = this.data[0] - 2.0F * this.data[2] + this.data[4];
/* 152 */         float bx = -2.0F * this.data[0] + 2.0F * this.data[2];
/* 153 */         float cx = this.data[0];
/* 154 */         float ay = this.data[1] - 2.0F * this.data[3] + this.data[5];
/* 155 */         float by = -2.0F * this.data[1] + 2.0F * this.data[3];
/* 156 */         float cy = this.data[1];
/*     */         
/* 158 */         PathWriter.quadTo(path, t0, t1, ax, bx, cx, ay, by, cy);
/* 159 */       } else if (this.type == 3) {
/* 160 */         float ax = -this.data[0] + 3.0F * this.data[2] - 3.0F * this.data[4] + this.data[6];
/* 161 */         float bx = 3.0F * this.data[0] - 6.0F * this.data[2] + 3.0F * this.data[4];
/* 162 */         float cx = -3.0F * this.data[0] + 3.0F * this.data[2];
/* 163 */         float dx = this.data[0];
/* 164 */         float ay = -this.data[1] + 3.0F * this.data[3] - 3.0F * this.data[5] + this.data[7];
/* 165 */         float by = 3.0F * this.data[1] - 6.0F * this.data[3] + 3.0F * this.data[5];
/* 166 */         float cy = -3.0F * this.data[1] + 3.0F * this.data[3];
/* 167 */         float dy = this.data[1];
/* 168 */         PathWriter.cubicTo(path, t0, t1, ax, bx, cx, dx, ay, by, cy, dy);
/* 169 */       } else if (this.type == 4) {
/* 170 */         path.closePath();
/*     */       } else {
/* 172 */         throw new RuntimeException();
/*     */       } 
/*     */     }
/*     */     
/*     */     public float getTangentSlope(float t) {
/* 177 */       if (this.type == 1) {
/* 178 */         float ax = this.data[2] - this.data[0];
/* 179 */         float ay = this.data[3] - this.data[1];
/* 180 */         return (float)Math.atan2(ay, ax);
/* 181 */       }  if (this.type == 2) {
/* 182 */         float ax = this.data[0] - 2.0F * this.data[2] + this.data[4];
/* 183 */         float bx = -2.0F * this.data[0] + 2.0F * this.data[2];
/* 184 */         float ay = this.data[1] - 2.0F * this.data[3] + this.data[5];
/* 185 */         float by = -2.0F * this.data[1] + 2.0F * this.data[3];
/* 186 */         return (float)Math.atan2((2.0F * ay * t + by), (2.0F * ax * t + bx));
/* 187 */       }  if (this.type == 3) {
/* 188 */         float ax = -this.data[0] + 3.0F * this.data[2] - 3.0F * this.data[4] + this.data[6];
/* 189 */         float bx = 3.0F * this.data[0] - 6.0F * this.data[2] + 3.0F * this.data[4];
/* 190 */         float cx = -3.0F * this.data[0] + 3.0F * this.data[2];
/* 191 */         float ay = -this.data[1] + 3.0F * this.data[3] - 3.0F * this.data[5] + this.data[7];
/* 192 */         float by = 3.0F * this.data[1] - 6.0F * this.data[3] + 3.0F * this.data[5];
/* 193 */         float cy = -3.0F * this.data[1] + 3.0F * this.data[3];
/* 194 */         return (float)Math.atan2((3.0F * ay * t * t + 2.0F * by * t + cy), (3.0F * ax * t * t + 2.0F * bx * t + cx));
/* 195 */       }  if (this.type == 0)
/* 196 */         return this.data[0]; 
/* 197 */       if (this.type == 4) {
/* 198 */         throw new RuntimeException();
/*     */       }
/* 200 */       throw new RuntimeException();
/*     */     }
/*     */ 
/*     */     
/*     */     public float getX(float t) {
/* 205 */       if (this.type == 1) {
/* 206 */         float ax = this.data[2] - this.data[0];
/* 207 */         return ax * t + this.data[0];
/* 208 */       }  if (this.type == 2) {
/* 209 */         float ax = this.data[0] - 2.0F * this.data[2] + this.data[4];
/* 210 */         float bx = -2.0F * this.data[0] + 2.0F * this.data[2];
/* 211 */         float cx = this.data[0];
/* 212 */         return (ax * t + bx) * t + cx;
/* 213 */       }  if (this.type == 3) {
/* 214 */         float ax = -this.data[0] + 3.0F * this.data[2] - 3.0F * this.data[4] + this.data[6];
/* 215 */         float bx = 3.0F * this.data[0] - 6.0F * this.data[2] + 3.0F * this.data[4];
/* 216 */         float cx = -3.0F * this.data[0] + 3.0F * this.data[2];
/* 217 */         float dx = this.data[0];
/* 218 */         return ((ax * t + bx) * t + cx) * t + dx;
/* 219 */       }  if (this.type == 0)
/* 220 */         return this.data[0]; 
/* 221 */       if (this.type == 4) {
/* 222 */         throw new RuntimeException();
/*     */       }
/* 224 */       throw new RuntimeException();
/*     */     }
/*     */ 
/*     */     
/*     */     public float getY(float t) {
/* 229 */       if (this.type == 1) {
/* 230 */         float ay = this.data[3] - this.data[1];
/* 231 */         return ay * t + this.data[1];
/* 232 */       }  if (this.type == 2) {
/* 233 */         float ay = this.data[1] - 2.0F * this.data[3] + this.data[5];
/* 234 */         float by = -2.0F * this.data[1] + 2.0F * this.data[3];
/* 235 */         float cy = this.data[1];
/* 236 */         return (ay * t + by) * t + cy;
/* 237 */       }  if (this.type == 3) {
/* 238 */         float ay = -this.data[1] + 3.0F * this.data[3] - 3.0F * this.data[5] + this.data[7];
/* 239 */         float by = 3.0F * this.data[1] - 6.0F * this.data[3] + 3.0F * this.data[5];
/* 240 */         float cy = -3.0F * this.data[1] + 3.0F * this.data[3];
/* 241 */         float dy = this.data[1];
/* 242 */         return ((ay * t + by) * t + cy) * t + dy;
/* 243 */       }  if (this.type == 0)
/* 244 */         return this.data[1]; 
/* 245 */       if (this.type == 4) {
/* 246 */         throw new RuntimeException();
/*     */       }
/* 248 */       throw new RuntimeException();
/*     */     }
/*     */ 
/*     */     
/*     */     public Segment(int type, float lastX, float lastY, float[] coords, float spacing) {
/* 253 */       this.type = type;
/* 254 */       if (type == 0) {
/* 255 */         this.data = new float[] { coords[0], coords[1] };
/* 256 */         this.realDistance = 0.0F;
/* 257 */       } else if (type == 1) {
/* 258 */         this.data = new float[] { lastX, lastY, coords[0], coords[1] };
/* 259 */         this.realDistance = (float)Math.sqrt(((coords[0] - lastX) * (coords[0] - lastX) + (coords[1] - lastY) * (coords[1] - lastY)));
/*     */       }
/* 261 */       else if (type == 4) {
/* 262 */         this.data = new float[0];
/*     */       } else {
/*     */         double ax, bx, cx, dx, ay, by, cy, dy;
/* 265 */         if (type == 2) {
/* 266 */           ay = 0.0D;
/* 267 */           by = (lastY - 2.0F * coords[1] + coords[3]);
/* 268 */           cy = (-2.0F * lastY + 2.0F * coords[1]);
/* 269 */           dy = lastY;
/*     */           
/* 271 */           ax = 0.0D;
/* 272 */           bx = (lastX - 2.0F * coords[0] + coords[2]);
/* 273 */           cx = (-2.0F * lastX + 2.0F * coords[0]);
/* 274 */           dx = lastX;
/* 275 */           this.data = new float[] { lastX, lastY, coords[0], coords[1], coords[2], coords[3] };
/* 276 */         } else if (type == 3) {
/* 277 */           ay = (-lastY + 3.0F * coords[1] - 3.0F * coords[3] + coords[5]);
/* 278 */           by = (3.0F * lastY - 6.0F * coords[1] + 3.0F * coords[3]);
/* 279 */           cy = (-3.0F * lastY + 3.0F * coords[1]);
/* 280 */           dy = lastY;
/*     */           
/* 282 */           ax = (-lastX + 3.0F * coords[0] - 3.0F * coords[2] + coords[4]);
/* 283 */           bx = (3.0F * lastX - 6.0F * coords[0] + 3.0F * coords[2]);
/* 284 */           cx = (-3.0F * lastX + 3.0F * coords[0]);
/* 285 */           dx = lastX;
/* 286 */           this.data = new float[] { lastX, lastY, coords[0], coords[1], coords[2], coords[3], coords[4], coords[5] };
/*     */         } else {
/* 288 */           throw new RuntimeException("Unrecognized type: " + type);
/*     */         } 
/* 290 */         this.realDistance = calculateDistance(ax, bx, cx, dx, ay, by, cy, dy, spacing);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private float calculateDistance(double ax, double bx, double cx, double dx, double ay, double by, double cy, double dy, float spacing) {
/* 296 */       double x0 = dx;
/* 297 */       double y0 = dy;
/*     */ 
/*     */       
/* 300 */       double sum = 0.0D; double t;
/* 301 */       for (t = spacing; t < 1.0D; t += spacing) {
/* 302 */         double x1 = ((ax * t + bx) * t + cx) * t + dx;
/* 303 */         double y1 = ((ay * t + by) * t + cy) * t + dy;
/* 304 */         sum += Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
/* 305 */         x0 = x1;
/* 306 */         y0 = y1;
/*     */       } 
/* 308 */       return (float)sum;
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
/* 321 */   float totalDistance = 0.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   float originalDistance;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MeasuredShape(Shape s) {
/* 331 */     this(s.getPathIterator(null), 0.05F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MeasuredShape(Shape s, float spacing) {
/* 342 */     this(s.getPathIterator(null), spacing);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MeasuredShape(PathIterator i) {
/* 352 */     this(i, 0.05F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MeasuredShape(PathIterator i, float spacing) {
/* 363 */     Vector<Segment> v = new Vector<>();
/* 364 */     float lastX = 0.0F;
/* 365 */     float lastY = 0.0F;
/* 366 */     float moveX = 0.0F;
/* 367 */     float moveY = 0.0F;
/* 368 */     int pathCount = 0;
/* 369 */     boolean closed = false;
/*     */     
/* 371 */     float[] coords = new float[6];
/* 372 */     while (!i.isDone()) {
/* 373 */       int k = i.currentSegment(coords);
/* 374 */       if (k == 4) {
/* 375 */         closed = true;
/* 376 */       } else if (k == 0) {
/* 377 */         if (pathCount == 1)
/* 378 */           throw new IllegalArgumentException("this object can only contain 1 subpath"); 
/* 379 */         moveX = coords[0];
/* 380 */         moveY = coords[1];
/* 381 */         lastX = moveX;
/* 382 */         lastY = moveY;
/* 383 */         pathCount++;
/* 384 */       } else if (k == 1 || k == 2 || k == 3) {
/*     */ 
/*     */         
/* 387 */         if (pathCount != 1)
/* 388 */           throw new IllegalArgumentException("this shape data did not begin with a moveTo"); 
/* 389 */         Segment s = new Segment(k, lastX, lastY, coords, spacing);
/* 390 */         lastX = s.data[s.data.length - 2];
/* 391 */         lastY = s.data[s.data.length - 1];
/* 392 */         v.add(s);
/* 393 */         this.totalDistance += s.realDistance;
/*     */       } 
/* 395 */       i.next();
/*     */     } 
/* 397 */     float t = this.totalDistance;
/* 398 */     if (v.size() > 0) {
/* 399 */       Segment last = v.get(v.size() - 1);
/* 400 */       if (Math.abs(last.data[last.data.length - 2] - moveX) > 0.001D || 
/* 401 */         Math.abs(last.data[last.data.length - 1] - moveY) > 0.001D) {
/* 402 */         coords[0] = moveX;
/* 403 */         coords[1] = moveY;
/* 404 */         Segment s = new Segment(1, lastX, lastY, coords, spacing);
/* 405 */         v.add(s);
/* 406 */         this.totalDistance += s.realDistance;
/*     */       } 
/*     */     } 
/* 409 */     if (!closed) {
/* 410 */       this.originalDistance = t;
/*     */     } else {
/* 412 */       this.originalDistance = this.totalDistance;
/*     */     } 
/*     */     
/* 415 */     this.segments = v.<Segment>toArray(new Segment[v.size()]);
/*     */     
/* 417 */     for (int a = 0; a < this.segments.length; a++) {
/* 418 */       (this.segments[a]).normalizedDistance = (this.segments[a]).realDistance / this.totalDistance;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeShape(PathWriter w) {
/* 426 */     w.moveTo(this.segments[0].getX(0.0F), this.segments[0]
/* 427 */         .getY(0.0F));
/* 428 */     for (int a = 0; a < this.segments.length; a++) {
/* 429 */       this.segments[a].write(w, 0.0F, 1.0F);
/*     */     }
/* 431 */     w.closePath();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getDistance() {
/* 439 */     return this.totalDistance;
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
/*     */   public float getOriginalDistance() {
/* 453 */     return this.originalDistance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeShapeBackwards(PathWriter w) {
/* 460 */     w.moveTo(this.segments[this.segments.length - 1].getX(1.0F), this.segments[this.segments.length - 1]
/* 461 */         .getY(1.0F));
/* 462 */     for (int a = this.segments.length - 1; a >= 0; a--) {
/* 463 */       this.segments[a].write(w, 1.0F, 0.0F);
/*     */     }
/* 465 */     w.closePath();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getMoveToX() {
/* 475 */     Segment s = this.segments[0];
/* 476 */     return s.getX(0.0F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getMoveToY() {
/* 487 */     Segment s = this.segments[0];
/* 488 */     return s.getY(0.0F);
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
/*     */   public void writeShape(float position, float length, PathWriter w) {
/* 500 */     writeShape(position, length, w, true);
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
/*     */   public void writeShape(float position, float length, PathWriter w, boolean includeMoveTo) {
/* 515 */     if (length >= 0.999999F) {
/* 516 */       writeShape(w); return;
/*     */     } 
/* 518 */     if (length <= -0.999999F) {
/* 519 */       writeShapeBackwards(w); return;
/*     */     } 
/* 521 */     if (length < 1.0E-6D && length > -1.0E-6D) {
/*     */       return;
/*     */     }
/*     */     
/* 525 */     Position i1 = getIndexOfPosition(position);
/* 526 */     Position i2 = getIndexOfPosition(position + length);
/*     */     
/* 528 */     if (includeMoveTo) {
/* 529 */       w.moveTo(this.segments[i1.i].getX(i1.innerPosition), this.segments[i1.i]
/* 530 */           .getY(i1.innerPosition));
/*     */     }
/* 532 */     if (i1.i == i2.i && ((length > 0.0F && i2.innerPosition > i1.innerPosition) || (length < 0.0F && i2.innerPosition < i1.innerPosition))) {
/*     */       
/* 534 */       this.segments[i1.i].write(w, i1.innerPosition, i2.innerPosition);
/*     */     }
/* 536 */     else if (length > 0.0F) {
/* 537 */       this.segments[i1.i].write(w, i1.innerPosition, 1.0F);
/* 538 */       int i = i1.i + 1;
/* 539 */       if (i >= this.segments.length)
/* 540 */         i = 0; 
/* 541 */       while (i != i2.i) {
/* 542 */         this.segments[i].write(w, 0.0F, 1.0F);
/* 543 */         i++;
/* 544 */         if (i >= this.segments.length)
/* 545 */           i = 0; 
/*     */       } 
/* 547 */       this.segments[i2.i].write(w, 0.0F, i2.innerPosition);
/*     */     } else {
/* 549 */       this.segments[i1.i].write(w, i1.innerPosition, 0.0F);
/* 550 */       int i = i1.i - 1;
/* 551 */       if (i < 0)
/* 552 */         i = this.segments.length - 1; 
/* 553 */       while (i != i2.i) {
/* 554 */         this.segments[i].write(w, 1.0F, 0.0F);
/* 555 */         i--;
/* 556 */         if (i < 0)
/* 557 */           i = this.segments.length - 1; 
/*     */       } 
/* 559 */       this.segments[i2.i].write(w, 1.0F, i2.innerPosition);
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
/*     */   public Point2D getPoint(float distance, Point2D dest) {
/* 574 */     if (distance < 0.0F) throw new IllegalArgumentException("distance (" + distance + ") must not be negative"); 
/* 575 */     if (distance > this.totalDistance) throw new IllegalArgumentException("distance (" + distance + ") must not be greater than the total distance of this shape (" + this.totalDistance + ")"); 
/* 576 */     if (dest == null) dest = new Point2D.Float(); 
/* 577 */     for (int a = 0; a < this.segments.length; a++) {
/* 578 */       float t = distance / (this.segments[a]).realDistance;
/* 579 */       if (t >= 1.0F) {
/* 580 */         distance -= (this.segments[a]).realDistance;
/*     */       } else {
/* 582 */         dest.setLocation(this.segments[a].getX(t), this.segments[a].getY(t));
/* 583 */         return dest;
/*     */       } 
/*     */     } 
/* 586 */     dest.setLocation(this.segments[0].getX(0.0F), this.segments[0].getY(0.0F));
/* 587 */     return dest;
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
/*     */   public float getTangentSlope(float distance) {
/* 599 */     if (distance < 0.0F) throw new IllegalArgumentException("distance (" + distance + ") must not be negative"); 
/* 600 */     if (distance > this.totalDistance) throw new IllegalArgumentException("distance (" + distance + ") must not be greater than the total distance of this shape (" + this.totalDistance + ")"); 
/* 601 */     for (int a = 0; a < this.segments.length; a++) {
/* 602 */       float t = distance / (this.segments[a]).realDistance;
/* 603 */       if (t >= 1.0F) {
/* 604 */         distance -= (this.segments[a]).realDistance;
/*     */       } else {
/* 606 */         return this.segments[a].getTangentSlope(t);
/*     */       } 
/*     */     } 
/* 609 */     return this.segments[0].getTangentSlope(0.0F);
/*     */   }
/*     */   
/*     */   private static boolean equal(float f1, float f2) {
/* 613 */     float d = f1 - f2;
/* 614 */     if (d < 0.0F) d = -d; 
/* 615 */     return (d < 1.0E-4D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getCommonDistance(MeasuredShape s) {
/* 624 */     float distance = 0.0F;
/* 625 */     int m = Math.min(this.segments.length, s.segments.length);
/* 626 */     for (int a = 0; a < m; a++) {
/* 627 */       if ((this.segments[a]).type != 0 && (s.segments[a]).type != 0) {
/*     */         
/* 629 */         if (equal((this.segments[a]).data[0], (s.segments[a]).data[0]) && 
/* 630 */           equal((this.segments[a]).data[1], (s.segments[a]).data[1]) && 
/* 631 */           equal((this.segments[a]).data[(this.segments[a]).data.length - 2], (s.segments[a]).data[(s.segments[a]).data.length - 2]) && 
/* 632 */           equal((this.segments[a]).data[(this.segments[a]).data.length - 1], (s.segments[a]).data[(s.segments[a]).data.length - 1]) && 
/* 633 */           equal((this.segments[a]).realDistance, (s.segments[a]).realDistance)) {
/* 634 */           distance += (this.segments[a]).realDistance;
/*     */         } else {
/* 636 */           return distance;
/*     */         } 
/* 638 */       } else if ((this.segments[a]).type != 0 || (s.segments[a]).type != 0) {
/*     */ 
/*     */ 
/*     */         
/* 642 */         return distance;
/*     */       } 
/*     */     } 
/* 645 */     return distance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GeneralPath getShape(float position, float length) {
/* 656 */     GeneralPath dest = new GeneralPath(1);
/* 657 */     PathWriter w = new GeneralPathWriter(dest);
/* 658 */     writeShape(position, length, w, true);
/* 659 */     return dest;
/*     */   }
/*     */   
/*     */   static class Position {
/*     */     int i;
/*     */     float innerPosition;
/*     */     
/*     */     public Position(int segmentIndex, float p) {
/* 667 */       this.i = segmentIndex;
/* 668 */       this.innerPosition = p;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 673 */       return "Position[ i=" + this.i + " t=" + this.innerPosition + "]";
/*     */     }
/*     */   }
/*     */   
/*     */   private Position getIndexOfPosition(float p) {
/* 678 */     for (; p < 0.0F; p++);
/* 679 */     for (; p > 1.0F; p--);
/* 680 */     if (p > 0.99999F) {
/* 681 */       p = 0.0F;
/*     */     }
/* 683 */     int i = 0;
/* 684 */     float original = p;
/* 685 */     while (i < this.segments.length) {
/* 686 */       if (p <= (this.segments[i]).normalizedDistance && (this.segments[i]).normalizedDistance != 0.0F) {
/* 687 */         return new Position(i, p / (this.segments[i]).normalizedDistance);
/*     */       }
/* 689 */       p -= (this.segments[i]).normalizedDistance;
/* 690 */       i++;
/*     */     } 
/* 692 */     System.err.println("p = " + p);
/* 693 */     throw new RuntimeException("the position " + original + " could not be found.");
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\MeasuredShape.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */