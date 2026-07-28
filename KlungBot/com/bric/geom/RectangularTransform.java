/*     */ package com.bric.geom;
/*     */ 
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
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
/*     */ public class RectangularTransform
/*     */ {
/*  31 */   double translateX = 0.0D;
/*  32 */   double translateY = 0.0D;
/*  33 */   double scaleX = 1.0D;
/*  34 */   double scaleY = 1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RectangularTransform() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RectangularTransform(Rectangle2D oldRect, Rectangle2D newRect) {
/*  45 */     setTransform(oldRect, newRect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RectangularTransform(double sx, double sy, double tx, double ty) {
/*  56 */     this.scaleX = sx;
/*  57 */     this.scaleY = sy;
/*  58 */     this.translateX = tx;
/*  59 */     this.translateY = ty;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Rectangle2D transform(Rectangle2D src) {
/*  65 */     return transform(src, (Rectangle2D)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Rectangle2D transform(Rectangle2D src, Rectangle2D dst) {
/*  73 */     if (dst == null) {
/*  74 */       dst = new Rectangle2D.Double();
/*     */     }
/*  76 */     dst.setFrame(src.getX() * this.scaleX + this.translateX, src.getY() * this.scaleY + this.translateY, src.getWidth() * this.scaleX, src.getHeight() * this.scaleY);
/*     */     
/*  78 */     return dst;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Point2D transform(Point2D src, Point2D dst) {
/*  86 */     if (dst == null) {
/*  87 */       dst = new Point2D.Double();
/*     */     }
/*  89 */     dst.setLocation(src.getX() * this.scaleX + this.translateX, src.getY() * this.scaleY + this.translateY);
/*     */     
/*  91 */     return dst;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AffineTransform create(Rectangle2D oldRect, Rectangle2D newRect) {
/* 102 */     double scaleX = newRect.getWidth() / oldRect.getWidth();
/* 103 */     double scaleY = newRect.getHeight() / oldRect.getHeight();
/*     */     
/* 105 */     double translateX = -oldRect.getX() * scaleX + newRect.getX();
/* 106 */     double translateY = -oldRect.getY() * scaleY + newRect.getY();
/* 107 */     return new AffineTransform(scaleX, 0.0D, 0.0D, scaleY, translateX, translateY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTransform(Rectangle2D oldRect, Rectangle2D newRect) {
/* 116 */     this.scaleX = newRect.getWidth() / oldRect.getWidth();
/* 117 */     this.scaleY = newRect.getHeight() / oldRect.getHeight();
/*     */     
/* 119 */     this.translateX = -oldRect.getX() * this.scaleX + newRect.getX();
/* 120 */     this.translateY = -oldRect.getY() * this.scaleY + newRect.getY();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void translate(double tx, double ty) {
/* 129 */     this.translateX = tx * this.scaleX + this.translateX;
/* 130 */     this.translateY = ty * this.scaleY + this.translateY;
/*     */   }
/*     */   
/*     */   public double getScaleX() {
/* 134 */     return this.scaleX;
/*     */   }
/*     */   
/*     */   public double getScaleY() {
/* 138 */     return this.scaleY;
/*     */   }
/*     */   
/*     */   public double getTranslateX() {
/* 142 */     return this.translateX;
/*     */   }
/*     */   
/*     */   public double getTranslateY() {
/* 146 */     return this.translateY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void scale(double sx, double sy) {
/* 155 */     this.scaleX *= sx;
/* 156 */     this.scaleY *= sy;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AffineTransform createAffineTransform() {
/* 162 */     return new AffineTransform(this.scaleX, 0.0D, 0.0D, this.scaleY, this.translateX, this.translateY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RectangularTransform createInverse() {
/* 168 */     return new RectangularTransform(1.0D / this.scaleX, 1.0D / this.scaleY, -this.translateX / this.scaleX, -this.translateY / this.scaleY);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\RectangularTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */