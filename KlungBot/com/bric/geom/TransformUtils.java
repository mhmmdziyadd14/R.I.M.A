/*     */ package com.bric.geom;
/*     */ 
/*     */ import com.bric.math.Equations;
/*     */ import java.awt.geom.AffineTransform;
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
/*     */ 
/*     */ 
/*     */ public class TransformUtils
/*     */ {
/*     */   public static double getRotationAngle(AffineTransform transform) {
/*  43 */     transform = (AffineTransform)transform.clone();
/*     */ 
/*     */     
/*  46 */     transform.preConcatenate(AffineTransform.getTranslateInstance(
/*  47 */           -transform.getTranslateX(), -transform.getTranslateY()));
/*     */     
/*  49 */     Point2D p1 = new Point2D.Double(1.0D, 0.0D);
/*  50 */     p1 = transform.transform(p1, p1);
/*     */     
/*  52 */     return Math.atan2(p1.getY(), p1.getX());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static AffineTransform createAffineTransform(Point2D initialP1, Point2D initialP2, Point2D initialP3, Point2D finalP1, Point2D finalP2, Point2D finalP3) {
/*  61 */     return createAffineTransform(initialP1.getX(), initialP1.getY(), initialP2
/*  62 */         .getX(), initialP2.getY(), initialP3
/*  63 */         .getX(), initialP3.getY(), finalP1
/*  64 */         .getX(), finalP1.getY(), finalP2
/*  65 */         .getX(), finalP2.getY(), finalP3
/*  66 */         .getX(), finalP3.getY());
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
/*     */   public static AffineTransform createAffineTransform(double oldX1, double oldY1, double oldX2, double oldY2, double oldX3, double oldY3, double newX1, double newY1, double newX2, double newY2, double newX3, double newY3) {
/*  80 */     double[][] matrix = { { oldX1, oldY1, 1.0D, newX1 }, { oldX2, oldY2, 1.0D, newX2 }, { oldX3, oldY3, 1.0D, newX3 } };
/*     */ 
/*     */     
/*     */     try {
/*  84 */       Equations.solve(matrix, true);
/*  85 */     } catch (RuntimeException e) {
/*  86 */       System.err.println("( " + oldX1 + ", " + oldY1 + ") -> ( " + newX1 + ", " + newY1 + ")");
/*  87 */       System.err.println("( " + oldX2 + ", " + oldY2 + ") -> ( " + newX2 + ", " + newY2 + ")");
/*  88 */       System.err.println("( " + oldX3 + ", " + oldY3 + ") -> ( " + newX3 + ", " + newY3 + ")");
/*  89 */       throw e;
/*     */     } 
/*  91 */     double m00 = matrix[0][3];
/*  92 */     double m01 = matrix[1][3];
/*  93 */     double m02 = matrix[2][3];
/*     */     
/*  95 */     matrix = new double[][] { { oldX1, oldY1, 1.0D, newY1 }, { oldX2, oldY2, 1.0D, newY2 }, { oldX3, oldY3, 1.0D, newY3 } };
/*     */ 
/*     */     
/*  98 */     Equations.solve(matrix, true);
/*  99 */     double m10 = matrix[0][3];
/* 100 */     double m11 = matrix[1][3];
/* 101 */     double m12 = matrix[2][3];
/*     */     
/* 103 */     return new AffineTransform(m00, m10, m01, m11, m02, m12);
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
/*     */   public static AffineTransform tween(AffineTransform a, AffineTransform b, float progress, boolean createNewObject) {
/* 120 */     AffineTransform dest = createNewObject ? new AffineTransform() : a;
/* 121 */     dest.setTransform(a
/* 122 */         .getScaleX() * (1.0F - progress) + b.getScaleX() * progress, a
/* 123 */         .getShearY() * (1.0F - progress) + b.getShearY() * progress, a
/* 124 */         .getShearX() * (1.0F - progress) + b.getShearX() * progress, a
/* 125 */         .getScaleY() * (1.0F - progress) + b.getScaleY() * progress, a
/* 126 */         .getTranslateX() * (1.0F - progress) + b.getTranslateX() * progress, a
/* 127 */         .getTranslateY() * (1.0F - progress) + b.getTranslateY() * progress);
/* 128 */     return dest;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\geom\TransformUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */