/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.RectangularTransform;
/*     */ import com.bric.geom.ShapeStringUtils;
/*     */ import com.bric.geom.ShapeUtils;
/*     */ import com.bric.geom.TransformUtils;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
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
/*     */ public class ScribbleTransition2D
/*     */   extends Transition2D
/*     */ {
/*     */   Shape scribble;
/*     */   Rectangle2D shapeBounds;
/*  47 */   int direction = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   boolean twoPasses;
/*     */ 
/*     */ 
/*     */   
/*     */   public ScribbleTransition2D(boolean twoPasses) {
/*  56 */     this.scribble = ShapeStringUtils.createGeneralPath("m -6.286 19.763 l 10.03 -2.175 l 2.183 58.034 l 24.854 -3.692 l 18.468 60.811 l 39.862 -5.543 l 35.711 58.651 l 53.273 -4.926 l 48.934 54.989 l 55.852 27.084 l 60.936 55.565 z");
/*  57 */     this.twoPasses = twoPasses;
/*  58 */     this.shapeBounds = new Rectangle2D.Float(0.0F, 0.0F, 60.0F, 60.0F);
/*     */   }
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*     */     AffineTransform t;
/*  63 */     Rectangle2D bigRect = new Rectangle2D.Float(0.0F, 0.0F, size.width, size.height);
/*     */     
/*  65 */     if (this.direction == 1) {
/*  66 */       t = RectangularTransform.create(this.shapeBounds, bigRect);
/*     */     } else {
/*     */       
/*  69 */       Rectangle2D r1 = this.shapeBounds;
/*  70 */       t = TransformUtils.createAffineTransform(r1.getX(), r1.getY(), r1
/*  71 */           .getX() + r1.getWidth(), r1.getY(), r1
/*  72 */           .getX(), r1.getY() + r1.getHeight(), bigRect
/*  73 */           .getX() + bigRect.getWidth(), bigRect.getY(), bigRect
/*  74 */           .getX(), bigRect.getY(), bigRect
/*  75 */           .getX() + bigRect.getWidth(), bigRect.getY() + bigRect.getHeight());
/*     */     } 
/*     */     
/*  78 */     if (!this.twoPasses) {
/*  79 */       BasicStroke basicStroke = new BasicStroke(16.7F, 1, 1);
/*  80 */       Shape subShape = ShapeUtils.traceShape(this.scribble, progress);
/*  81 */       subShape = basicStroke.createStrokedShape(subShape);
/*  82 */       subShape = t.createTransformedShape(subShape);
/*     */       
/*  84 */       return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, null, subShape) };
/*     */     } 
/*     */ 
/*     */     
/*  88 */     float progress1 = progress / 0.5F;
/*  89 */     if (progress1 > 1.0F) progress1 = 1.0F; 
/*  90 */     Shape subShape1 = ShapeUtils.traceShape(this.scribble, progress1);
/*  91 */     BasicStroke stroke = new BasicStroke(8.35F, 1, 1);
/*  92 */     subShape1 = stroke.createStrokedShape(subShape1);
/*  93 */     t.translate(2.0D, 5.0D);
/*  94 */     subShape1 = t.createTransformedShape(subShape1);
/*  95 */     t.translate(-2.0D, -5.0D);
/*     */     
/*  97 */     stroke = new BasicStroke(16.7F, 1, 1);
/*  98 */     float progress2 = (progress - 0.5F) / 0.5F;
/*  99 */     if (progress2 < 0.0F) progress2 = 0.0F; 
/* 100 */     Shape subShape2 = ShapeUtils.traceShape(this.scribble, progress2);
/* 101 */     subShape2 = stroke.createStrokedShape(subShape2);
/* 102 */     subShape2 = t.createTransformedShape(subShape2);
/*     */ 
/*     */     
/* 105 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, null, subShape1), (Transition2DInstruction)new ImageInstruction(false, null, subShape2) };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 115 */     if (this.twoPasses)
/* 116 */       return "Scribble Twice"; 
/* 117 */     return "Scribble";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\ScribbleTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */