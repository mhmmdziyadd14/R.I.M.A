/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import java.awt.Color;
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
/*     */ public class SwivelTransition2D
/*     */   extends AbstractPlanarTransition2D
/*     */ {
/*     */   int multiplier;
/*     */   
/*     */   public SwivelTransition2D() {
/*  38 */     this(6);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SwivelTransition2D(int direction) {
/*  46 */     this(Color.black, direction);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SwivelTransition2D(Color background, int direction) {
/*  54 */     super(background);
/*  55 */     if (direction == 6) {
/*  56 */       this.multiplier = 1;
/*  57 */     } else if (direction == 5) {
/*  58 */       this.multiplier = -1;
/*     */     } else {
/*  60 */       throw new IllegalArgumentException("The direction must be CLOCKWISE or COUNTER_CLOCKWISE");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  67 */     if (this.multiplier == -1) {
/*  68 */       return "Swivel Counterclockwise";
/*     */     }
/*  70 */     return "Swivel Clockwise";
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFrameAOpacity(float p) {
/*  75 */     if (p < 0.5F) {
/*  76 */       return 1.0F;
/*     */     }
/*  78 */     p = 1.0F - (p - 0.5F) / 0.5F;
/*  79 */     p = (float)Math.sqrt(p);
/*  80 */     return p;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getFrameBOpacity(float p) {
/*  85 */     if (p > 0.5F)
/*  86 */       return 1.0F; 
/*  87 */     p /= 0.5F;
/*  88 */     p = (float)Math.pow(p, 0.5D);
/*  89 */     return p;
/*     */   }
/*     */ 
/*     */   
/*     */   public Point2D getFrameALocation(float p) {
/*  94 */     p = this.multiplier * p;
/*  95 */     return new Point2D.Double(0.5D * Math.cos(Math.PI * p + 1.5707963267948966D) + 0.5D, 0.5D * 
/*  96 */         Math.sin(Math.PI * p + 1.5707963267948966D) + 0.5D);
/*     */   }
/*     */ 
/*     */   
/*     */   public Point2D getFrameBLocation(float p) {
/* 101 */     p = this.multiplier * p;
/* 102 */     return new Point2D.Double(0.5D * Math.cos(Math.PI * p + 4.71238898038469D) + 0.5D, 0.5D * 
/* 103 */         Math.sin(Math.PI * p + 4.71238898038469D) + 0.5D);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\SwivelTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */