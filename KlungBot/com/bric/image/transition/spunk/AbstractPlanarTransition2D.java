/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.RectangularTransform;
/*     */ import com.bric.geom.TransformUtils;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.ShapeInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import javax.media.jai.PerspectiveTransform;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractPlanarTransition2D
/*     */   extends Transition2D
/*     */ {
/*     */   Color background;
/*     */   
/*     */   public AbstractPlanarTransition2D() {
/*  53 */     this(Color.black);
/*     */   }
/*     */   
/*     */   public AbstractPlanarTransition2D(Color background) {
/*  57 */     this.background = background;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*  66 */     double upperY = (size.height * 7 / 10);
/*  67 */     double lowerY = size.height;
/*  68 */     double x = (size.width * 5 / 20);
/*  69 */     PerspectiveTransform transform = PerspectiveTransform.getQuadToQuad(0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, x, upperY, size.width - x, upperY, 0.0D, lowerY, size.width, lowerY);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  79 */     Point2D p = new Point2D.Double(0.0D, 0.5D);
/*  80 */     transform.transform(p, p);
/*     */     
/*  82 */     Point2D pA = getFrameALocation(progress);
/*  83 */     Point2D pB = getFrameBLocation(progress);
/*  84 */     transform.transform(pA, pA);
/*  85 */     transform.transform(pB, pB);
/*     */ 
/*     */     
/*  88 */     Rectangle2D r1 = new Rectangle2D.Double();
/*  89 */     Rectangle2D r2 = new Rectangle2D.Double();
/*     */     
/*  91 */     double height = lowerY - (lowerY - pA.getY()) * 2.0D;
/*  92 */     double ratio = height / lowerY;
/*  93 */     double width = size.getWidth() * ratio;
/*  94 */     r1.setFrame(pA
/*  95 */         .getX() - width / 2.0D, pA.getY() - height, width, height);
/*     */ 
/*     */     
/*  98 */     height = lowerY - (lowerY - pB.getY()) * 2.0D;
/*  99 */     ratio = height / lowerY;
/* 100 */     width = size.getWidth() * ratio;
/* 101 */     r2.setFrame(pB
/* 102 */         .getX() - width / 2.0D, pB.getY() - height, width, height);
/*     */     
/* 104 */     Rectangle big = new Rectangle(0, 0, size.width, size.height);
/*     */     
/* 106 */     AffineTransform transform1 = RectangularTransform.create(big, r1);
/* 107 */     AffineTransform transform2 = RectangularTransform.create(big, r2);
/* 108 */     float opacity1 = getFrameAOpacity(progress);
/* 109 */     float opacity2 = getFrameBOpacity(progress);
/* 110 */     ImageInstruction i1A = new ImageInstruction(true, transform1, null);
/* 111 */     ShapeInstruction i1B = new ShapeInstruction(r1, getShade(1.0F - opacity1));
/* 112 */     ImageInstruction i2A = new ImageInstruction(false, transform2, null);
/* 113 */     ShapeInstruction i2B = new ShapeInstruction(r2, getShade(1.0F - opacity2));
/*     */     
/* 115 */     AffineTransform transform1z = TransformUtils.createAffineTransform(0.0D, 0.0D, big
/* 116 */         .getWidth(), 0.0D, 0.0D, big
/* 117 */         .getHeight(), r1
/* 118 */         .getX(), r1.getY() + r1.getHeight() * 2.0D, r1
/* 119 */         .getX() + r1.getWidth(), r1.getY() + r1.getHeight() * 2.0D, r1
/* 120 */         .getX(), r1.getY() + r1.getHeight() + 1.0D);
/* 121 */     AffineTransform transform2z = TransformUtils.createAffineTransform(0.0D, 0.0D, big
/* 122 */         .getWidth(), 0.0D, 0.0D, big
/* 123 */         .getHeight(), r2
/* 124 */         .getX(), r2.getY() + r2.getHeight() * 2.0D, r2
/* 125 */         .getX() + r2.getWidth(), r2.getY() + r2.getHeight() * 2.0D, r2
/* 126 */         .getX(), r2.getY() + r2.getHeight() + 1.0D);
/*     */ 
/*     */     
/* 129 */     Rectangle2D shadow1Rect = new Rectangle2D.Double(r1.getX(), r1.getY() + r1.getHeight() + 1.0D, r1.getWidth(), r1.getHeight());
/*     */ 
/*     */ 
/*     */     
/* 133 */     Rectangle2D shadow2Rect = new Rectangle2D.Double(r2.getX(), r2.getY() + r2.getHeight() + 1.0D, r2.getWidth(), r2.getHeight());
/*     */     
/* 135 */     ImageInstruction i1ShadowA = new ImageInstruction(true, transform1z, null);
/* 136 */     ShapeInstruction i1ShadowB = new ShapeInstruction(shadow1Rect, getShade(1.0F - opacity1 * 0.3F));
/* 137 */     ImageInstruction i2ShadowA = new ImageInstruction(false, transform2z, null);
/* 138 */     ShapeInstruction i2ShadowB = new ShapeInstruction(shadow2Rect, getShade(1.0F - opacity2 * 0.3F));
/*     */     
/* 140 */     ShapeInstruction backgroundRect = new ShapeInstruction(new Rectangle(0, 0, size.width, size.height), this.background, null, 0.0F);
/*     */ 
/*     */ 
/*     */     
/* 144 */     if (r1.getHeight() > r2.getHeight()) {
/* 145 */       return new Transition2DInstruction[] { (Transition2DInstruction)backgroundRect, (Transition2DInstruction)i2A, (Transition2DInstruction)i2B, (Transition2DInstruction)i2ShadowA, (Transition2DInstruction)i2ShadowB, (Transition2DInstruction)i1A, (Transition2DInstruction)i1B, (Transition2DInstruction)i1ShadowA, (Transition2DInstruction)i1ShadowB };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     return new Transition2DInstruction[] { (Transition2DInstruction)backgroundRect, (Transition2DInstruction)i1A, (Transition2DInstruction)i1B, (Transition2DInstruction)i1ShadowA, (Transition2DInstruction)i1ShadowB, (Transition2DInstruction)i2A, (Transition2DInstruction)i2B, (Transition2DInstruction)i2ShadowA, (Transition2DInstruction)i2ShadowB };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Color getShade(float opacity) {
/* 162 */     return new Color(this.background.getRed(), this.background.getGreen(), this.background.getBlue(), (int)(255.0F * opacity));
/*     */   }
/*     */   
/*     */   public abstract Point2D getFrameALocation(float paramFloat);
/*     */   
/*     */   public abstract Point2D getFrameBLocation(float paramFloat);
/*     */   
/*     */   public abstract float getFrameAOpacity(float paramFloat);
/*     */   
/*     */   public abstract float getFrameBOpacity(float paramFloat);
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\AbstractPlanarTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */