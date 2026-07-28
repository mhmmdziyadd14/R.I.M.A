/*     */ package com.bric.geom;
/*     */ 
/*     */ import com.bric.util.FloatArrayFactory;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.CubicCurve2D;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.PathIterator;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.util.Arrays;
/*     */ import java.util.Stack;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Clipper
/*     */ {
/*  40 */   private static final FloatArrayFactory floatFactory = new FloatArrayFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float TOLERANCE = 1.0E-4F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class ClippedPath
/*     */   {
/*     */     public final GeneralPath g;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  64 */     private Stack<float[]> uncommittedPoints = (Stack)new Stack<>(); private float initialX;
/*     */     private float initialY;
/*     */     
/*     */     public ClippedPath(int windingRule) {
/*  68 */       this.g = new GeneralPath(windingRule);
/*     */     }
/*     */     
/*     */     public void moveTo(float x, float y) {
/*  72 */       flush();
/*  73 */       this.g.moveTo(x, y);
/*  74 */       this.initialX = x;
/*  75 */       this.initialY = y;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void curveTo(Clipper.Function xf, Clipper.Function yf, double t0, double t1) {
/*  83 */       flush();
/*     */       
/*  85 */       double dt = t1 - t0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  91 */       double dx0 = xf.getDerivative(t0) * dt;
/*  92 */       double dx1 = xf.getDerivative(t1) * dt;
/*  93 */       double dy0 = yf.getDerivative(t0) * dt;
/*  94 */       double dy1 = yf.getDerivative(t1) * dt;
/*  95 */       double x0 = xf.evaluate(t0);
/*  96 */       double x1 = xf.evaluate(t1);
/*  97 */       double y0 = yf.evaluate(t0);
/*  98 */       double y1 = yf.evaluate(t1);
/*     */       
/* 100 */       this.g.curveTo((float)(x0 + dx0 / 3.0D), (float)(y0 + dy0 / 3.0D), (float)(x1 - dx1 / 3.0D), (float)(y1 - dy1 / 3.0D), (float)x1, (float)y1);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void lineTo(float x, float y) {
/* 119 */       if (this.uncommittedPoints.size() > 0) {
/* 120 */         float[] last = this.uncommittedPoints.peek();
/*     */         
/* 122 */         if (Math.abs(last[0] - x) < 1.0E-4F && Math.abs(last[1] - y) < 1.0E-4F) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */       
/* 127 */       float[] f = Clipper.floatFactory.getArray(2);
/* 128 */       f[0] = x;
/* 129 */       f[1] = y;
/* 130 */       this.uncommittedPoints.push(f);
/*     */     }
/*     */     
/*     */     public void closePath() {
/* 134 */       lineTo(this.initialX, this.initialY);
/* 135 */       flush();
/* 136 */       this.g.closePath();
/*     */     }
/*     */ 
/*     */     
/*     */     public void flush() {
/* 141 */       while (this.uncommittedPoints.size() > 0) {
/* 142 */         while (this.uncommittedPoints.size() >= 3) {
/* 143 */           float[] first = this.uncommittedPoints.get(0);
/* 144 */           float[] middle = this.uncommittedPoints.get(1);
/* 145 */           float[] last = this.uncommittedPoints.get(2);
/*     */           
/* 147 */           if (Math.abs(first[0] - middle[0]) < 1.0E-4F && Math.abs(first[0] - last[0]) < 1.0E-4F) {
/*     */             
/* 149 */             float[] array = this.uncommittedPoints.remove(1);
/* 150 */             Clipper.floatFactory.putArray(array); continue;
/* 151 */           }  if (Math.abs(first[1] - middle[1]) < 1.0E-4F && Math.abs(first[1] - last[1]) < 1.0E-4F) {
/*     */             
/* 153 */             float[] array = this.uncommittedPoints.remove(1);
/* 154 */             Clipper.floatFactory.putArray(array);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 160 */         float[] point = this.uncommittedPoints.remove(0);
/* 161 */         this.g.lineTo(point[0], point[1]);
/* 162 */         Clipper.floatFactory.putArray(point);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static interface Function
/*     */   {
/*     */     double evaluate(double param1Double);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int evaluateInverse(double param1Double, double[] param1ArrayOfdouble, int param1Int);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     double getDerivative(double param1Double);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class LFunction
/*     */     implements Function
/*     */   {
/*     */     double slope;
/*     */ 
/*     */ 
/*     */     
/*     */     double intercept;
/*     */ 
/*     */ 
/*     */     
/*     */     public void define(double x1, double x2) {
/* 200 */       this.slope = x2 - x1;
/* 201 */       this.intercept = x1;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 206 */       return this.slope + "*t+" + this.intercept;
/*     */     }
/*     */     
/*     */     public double evaluate(double t) {
/* 210 */       return this.slope * t + this.intercept;
/*     */     }
/*     */     
/*     */     public int evaluateInverse(double x, double[] dest, int offset) {
/* 214 */       dest[offset] = (x - this.intercept) / this.slope;
/* 215 */       return 1;
/*     */     }
/*     */     
/*     */     public double getDerivative(double t) {
/* 219 */       return this.slope;
/*     */     }
/*     */   }
/*     */   
/*     */   static class QFunction
/*     */     implements Function
/*     */   {
/*     */     double a;
/*     */     double b;
/*     */     double c;
/*     */     
/*     */     public String toString() {
/* 231 */       return this.a + "*t*t+" + this.b + "*t+" + this.c;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void define(double x0, double x1, double x2) {
/* 237 */       this.a = x0 - 2.0D * x1 + x2;
/* 238 */       this.b = -2.0D * x0 + 2.0D * x1;
/* 239 */       this.c = x0;
/*     */     }
/*     */     
/*     */     public double evaluate(double t) {
/* 243 */       return this.a * t * t + this.b * t + this.c;
/*     */     }
/*     */     
/*     */     public double getDerivative(double t) {
/* 247 */       return 2.0D * this.a * t + this.b;
/*     */     }
/*     */     
/*     */     public int evaluateInverse(double x, double[] dest, int offset) {
/* 251 */       double C = this.c - x;
/* 252 */       double det = this.b * this.b - 4.0D * this.a * C;
/* 253 */       if (det < 0.0D)
/* 254 */         return 0; 
/* 255 */       if (det == 0.0D) {
/* 256 */         dest[offset] = -this.b / 2.0D * this.a;
/* 257 */         return 1;
/*     */       } 
/* 259 */       det = Math.sqrt(det);
/* 260 */       dest[offset++] = (-this.b + det) / 2.0D * this.a;
/* 261 */       dest[offset++] = (-this.b - det) / 2.0D * this.a;
/* 262 */       return 2;
/*     */     } }
/*     */   
/*     */   static class CFunction implements Function {
/*     */     double a;
/*     */     double b;
/*     */     double c;
/*     */     double d;
/*     */     double[] t2;
/*     */     double[] eqn;
/*     */     
/*     */     public String toString() {
/* 274 */       return this.a + "*t*t*t+" + this.b + "*t*t+" + this.c + "*t+" + this.d;
/*     */     }
/*     */     
/*     */     public void define(double x0, double x1, double x2, double x3) {
/* 278 */       this.a = -x0 + 3.0D * x1 - 3.0D * x2 + x3;
/* 279 */       this.b = 3.0D * x0 - 6.0D * x1 + 3.0D * x2;
/* 280 */       this.c = -3.0D * x0 + 3.0D * x1;
/* 281 */       this.d = x0;
/*     */     }
/*     */     
/*     */     public double evaluate(double t) {
/* 285 */       return this.a * t * t * t + this.b * t * t + this.c * t + this.d;
/*     */     }
/*     */     
/*     */     public double getDerivative(double t) {
/* 289 */       return 3.0D * this.a * t * t + 2.0D * this.b * t + this.c;
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
/*     */     public int evaluateInverse(double x, double[] dest, int offset) {
/* 303 */       if (this.eqn == null)
/* 304 */         this.eqn = new double[4]; 
/* 305 */       this.eqn[0] = this.d - x;
/* 306 */       this.eqn[1] = this.c;
/* 307 */       this.eqn[2] = this.b;
/* 308 */       this.eqn[3] = this.a;
/* 309 */       if (offset == 0) {
/* 310 */         int j = CubicCurve2D.solveCubic(this.eqn, dest);
/* 311 */         if (j < 0) return 0; 
/* 312 */         return j;
/*     */       } 
/* 314 */       if (this.t2 == null)
/* 315 */         this.t2 = new double[3]; 
/* 316 */       int k = CubicCurve2D.solveCubic(this.eqn, this.t2);
/* 317 */       if (k < 0) return 0; 
/* 318 */       for (int i = 0; i < k; i++) {
/* 319 */         dest[offset + i] = this.t2[i];
/*     */       }
/* 321 */       return k;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static GeneralPath clipToRect(Shape s, Rectangle2D r) {
/* 332 */     return clipToRect(s, null, r);
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
/*     */   public static GeneralPath clipToRect(Shape s, AffineTransform t, Rectangle2D r) {
/* 345 */     Clipper clipper = new RectangleClipper(r);
/* 346 */     return clipper.clip(s, t);
/*     */   }
/*     */   
/*     */   private static class RectangleClipper extends Clipper {
/*     */     final float rTop;
/*     */     final float rLeft;
/*     */     final float rRight;
/*     */     final float rBottom;
/*     */     
/*     */     private RectangleClipper(Rectangle2D rect) {
/* 356 */       this.rTop = (float)rect.getY();
/* 357 */       this.rLeft = (float)rect.getX();
/* 358 */       this.rRight = (float)(rect.getX() + rect.getWidth());
/* 359 */       this.rBottom = (float)(rect.getY() + rect.getHeight());
/*     */     }
/*     */ 
/*     */     
/*     */     boolean contains(float x, float y) {
/* 364 */       return (x >= this.rLeft && x <= this.rRight && y >= this.rTop && y <= this.rBottom);
/*     */     }
/*     */ 
/*     */     
/*     */     void cap(Point2D.Float p) {
/* 369 */       if (p.x < this.rLeft)
/* 370 */         p.x = this.rLeft; 
/* 371 */       if (p.x > this.rRight)
/* 372 */         p.x = this.rRight; 
/* 373 */       if (p.y < this.rTop)
/* 374 */         p.y = this.rTop; 
/* 375 */       if (p.y > this.rBottom) {
/* 376 */         p.y = this.rBottom;
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     int collectIntersectionTimes(Clipper.Function xf, Clipper.Function yf, double[] intersectionTimes) {
/* 382 */       int sum = 0;
/* 383 */       sum += xf.evaluateInverse(this.rLeft, intersectionTimes, sum);
/* 384 */       sum += xf.evaluateInverse(this.rRight, intersectionTimes, sum);
/* 385 */       sum += yf.evaluateInverse(this.rTop, intersectionTimes, sum);
/* 386 */       sum += yf.evaluateInverse(this.rBottom, intersectionTimes, sum);
/* 387 */       return sum;
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
/*     */   GeneralPath clip(Shape incomingShape, AffineTransform transform) {
/* 404 */     PathIterator i = incomingShape.getPathIterator(transform);
/* 405 */     ClippedPath p = new ClippedPath(i.getWindingRule());
/* 406 */     float initialX = 0.0F;
/* 407 */     float initialY = 0.0F;
/*     */     
/* 409 */     float[] f = floatFactory.getArray(6);
/* 410 */     boolean shouldClose = false;
/* 411 */     float lastX = 0.0F;
/* 412 */     float lastY = 0.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 418 */     LFunction lxf = new LFunction();
/* 419 */     LFunction lyf = new LFunction();
/* 420 */     QFunction qxf = new QFunction();
/* 421 */     QFunction qyf = new QFunction();
/* 422 */     CFunction cxf = new CFunction();
/* 423 */     CFunction cyf = new CFunction();
/* 424 */     Function xf = null;
/* 425 */     Function yf = null;
/* 426 */     Point2D.Float point = new Point2D.Float();
/* 427 */     double[] intersectionTimes = new double[16];
/*     */ 
/*     */     
/* 430 */     while (!i.isDone()) {
/* 431 */       int k = i.currentSegment(f);
/* 432 */       if (k == 0) {
/* 433 */         initialX = f[0];
/* 434 */         initialY = f[1];
/* 435 */         point.setLocation(f[0], f[1]);
/* 436 */         cap(point);
/*     */         
/* 438 */         p.moveTo(point.x, point.y);
/*     */         
/* 440 */         lastX = f[0];
/* 441 */         lastY = f[1];
/* 442 */       } else if (k == 4) {
/* 443 */         f[0] = initialX;
/* 444 */         f[1] = initialY;
/* 445 */         k = 1;
/* 446 */         shouldClose = true;
/*     */       } 
/* 448 */       xf = null;
/* 449 */       if (k == 1) {
/* 450 */         lxf.define(lastX, f[0]);
/* 451 */         lyf.define(lastY, f[1]);
/*     */         
/* 453 */         xf = lxf;
/* 454 */         yf = lyf;
/* 455 */       } else if (k == 2) {
/* 456 */         qxf.define(lastX, f[0], f[2]);
/* 457 */         qyf.define(lastY, f[1], f[3]);
/*     */         
/* 459 */         xf = qxf;
/* 460 */         yf = qyf;
/* 461 */       } else if (k == 3) {
/* 462 */         cxf.define(lastX, f[0], f[2], f[4]);
/* 463 */         cyf.define(lastY, f[1], f[3], f[5]);
/*     */         
/* 465 */         xf = cxf;
/* 466 */         yf = cyf;
/*     */       } 
/* 468 */       if (xf != null) {
/*     */ 
/*     */ 
/*     */         
/* 472 */         int tCtr = collectIntersectionTimes(xf, yf, intersectionTimes);
/* 473 */         intersectionTimes[tCtr++] = 1.0D;
/*     */         
/* 475 */         intersectionTimes[tCtr++] = 0.0D;
/*     */ 
/*     */         
/* 478 */         Arrays.sort(intersectionTimes, 0, tCtr);
/*     */         
/* 480 */         boolean lastValueWasCapped = !contains(lastX, lastY);
/*     */         
/* 482 */         for (int a = 0; a < tCtr; a++) {
/* 483 */           if (a <= 0 || intersectionTimes[a] != intersectionTimes[a - 1])
/*     */           {
/* 485 */             if (intersectionTimes[a] > 0.0D && intersectionTimes[a] <= 1.0D) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 491 */               float x = (float)xf.evaluate(intersectionTimes[a]);
/* 492 */               float y = (float)yf.evaluate(intersectionTimes[a]);
/* 493 */               point.setLocation(x, y);
/* 494 */               cap(point);
/*     */               
/* 496 */               boolean thisValueIsCapped = (Math.abs(x - point.x) >= 1.0E-4F || Math.abs(y - point.y) >= 1.0E-4F);
/*     */               
/* 498 */               float x2 = (float)xf.evaluate((intersectionTimes[a] + intersectionTimes[a - 1]) / 2.0D);
/* 499 */               float y2 = (float)yf.evaluate((intersectionTimes[a] + intersectionTimes[a - 1]) / 2.0D);
/* 500 */               boolean midValueInvalid = !contains(x2, y2);
/*     */               
/* 502 */               if (xf instanceof LFunction || thisValueIsCapped || lastValueWasCapped || midValueInvalid) {
/* 503 */                 p.lineTo(point.x, point.y);
/* 504 */               } else if (xf instanceof QFunction || xf instanceof CFunction) {
/* 505 */                 p.curveTo(xf, yf, intersectionTimes[a - 1], intersectionTimes[a]);
/*     */               } else {
/* 507 */                 throw new RuntimeException("Unexpected condition.");
/*     */               } 
/*     */               
/* 510 */               lastValueWasCapped = thisValueIsCapped;
/*     */             }  } 
/*     */         } 
/* 513 */         lastX = (float)xf.evaluate(1.0D);
/* 514 */         lastY = (float)yf.evaluate(1.0D);
/*     */       } 
/* 516 */       if (shouldClose) {
/* 517 */         p.closePath();
/* 518 */         shouldClose = false;
/*     */       } 
/* 520 */       i.next();
/*     */     } 
/* 522 */     p.flush();
/* 523 */     floatFactory.putArray(f);
/* 524 */     return p.g;
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
/*     */   public static void clip(Graphics2D g, Shape newClip) {
/* 540 */     Shape oldClip = g.getClip();
/* 541 */     if (oldClip == null) {
/* 542 */       g.setClip(newClip);
/*     */       return;
/*     */     } 
/* 545 */     Rectangle2D oldRect = RectangleReader.convert(oldClip);
/* 546 */     Rectangle2D newRect = RectangleReader.convert(newClip);
/*     */     
/* 548 */     if (oldRect != null && newRect != null) {
/* 549 */       Rectangle2D intersectedClip = oldRect.createIntersection(newRect);
/* 550 */       if (intersectedClip.getWidth() < 0.0D || intersectedClip.getHeight() < 0.0D)
/*     */       {
/* 552 */         intersectedClip.setFrame(intersectedClip.getX(), intersectedClip.getY(), 0.0D, 0.0D);
/*     */       }
/* 554 */       g.setClip(intersectedClip);
/*     */       
/*     */       return;
/*     */     } 
/* 558 */     if (newRect != null && oldRect == null) {
/* 559 */       GeneralPath intersectedClip = clipToRect(oldClip, newRect);
/* 560 */       g.setClip(intersectedClip);
/*     */       
/*     */       return;
/*     */     } 
/* 564 */     if (newRect == null && oldRect != null) {
/* 565 */       GeneralPath intersectedClip = clipToRect(newClip, oldRect);
/* 566 */       g.setClip(intersectedClip);
/*     */       
/*     */       return;
/*     */     } 
/* 570 */     g.clip(newClip);
/*     */   }
/*     */   
/*     */   abstract void cap(Point2D.Float paramFloat);
/*     */   
/*     */   abstract boolean contains(float paramFloat1, float paramFloat2);
/*     */   
/*     */   abstract int collectIntersectionTimes(Function paramFunction1, Function paramFunction2, double[] paramArrayOfdouble);
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\Clipper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */