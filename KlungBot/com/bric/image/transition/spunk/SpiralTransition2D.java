/*    */ package com.bric.image.transition.spunk;
/*    */ 
/*    */ import com.bric.geom.RectangularTransform;
/*    */ import com.bric.geom.ShapeStringUtils;
/*    */ import com.bric.geom.ShapeUtils;
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.BasicStroke;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Shape;
/*    */ import java.awt.geom.AffineTransform;
/*    */ import java.awt.geom.Rectangle2D;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SpiralTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   Shape spiral;
/*    */   boolean sprawl = true;
/*    */   Rectangle2D shapeBounds;
/*    */   
/*    */   public SpiralTransition2D(boolean sprawl) {
/* 51 */     this.spiral = ShapeStringUtils.createGeneralPath("m 32.574 32.527 c 21.77 23.645 42.863 21.455 42.537 32.494 c 42.212 43.533 34.209 45.303 27.629 42.58 c 21.049 39.857 17.374 35.943 18.708 27.331 c 20.043 18.72 27.036 7.229 39.603 12.433 c 52.17 17.636 56.668 23.651 53.935 37.469 c 51.202 51.287 43.916 57.222 28.139 53.074 c 12.361 48.927 0.062 39.761 7.31 20.954 c 14.558 2.147 23.188 -2.412 40.942 0.083 c 58.696 2.579 69.57 20.663 64.804 38.565 c 60.038 56.468 53.063 66.173 28.941 64.198 c 4.82 62.224 -7.552 41.196 -6.927 32.645 c -6.303 24.094 -1.187 8.315 6.772 -1.593 z");
/* 52 */     this.sprawl = sprawl;
/* 53 */     this.shapeBounds = new Rectangle2D.Float(0.0F, 0.0F, 60.0F, 60.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 58 */     if (this.sprawl)
/* 59 */       return "Spiral Sprawl"; 
/* 60 */     return "Spiral";
/*    */   }
/*    */   
/*    */   public Shape getShape(float progress) {
/* 64 */     if (this.sprawl) {
/* 65 */       double theta = ((1.0F - progress) * 3.0F) * Math.PI;
/* 66 */       AffineTransform rotate = AffineTransform.getRotateInstance(theta, 30.0D, 30.0D);
/* 67 */       return rotate.createTransformedShape(this.spiral);
/*    */     } 
/* 69 */     return this.spiral;
/*    */   }
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 74 */     Shape subShape = ShapeUtils.traceShape(getShape(progress), progress);
/* 75 */     BasicStroke stroke = new BasicStroke(16.23F, 1, 1);
/* 76 */     subShape = stroke.createStrokedShape(subShape);
/* 77 */     Rectangle2D bigRect = new Rectangle2D.Float(0.0F, 0.0F, size.width, size.height);
/* 78 */     AffineTransform t = RectangularTransform.create(this.shapeBounds, bigRect);
/*    */     
/* 80 */     subShape = t.createTransformedShape(subShape);
/*    */     
/* 82 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, null, subShape) };
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\SpiralTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */