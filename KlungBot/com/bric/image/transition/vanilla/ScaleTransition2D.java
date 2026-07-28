/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import com.bric.geom.RectangularTransform;
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.geom.AffineTransform;
/*    */ import java.awt.geom.Point2D;
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
/*    */ public class ScaleTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int type;
/*    */   
/*    */   public ScaleTransition2D() {
/* 42 */     this(8);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ScaleTransition2D(int type) {
/* 50 */     if (type != 7 && type != 8)
/* 51 */       throw new IllegalArgumentException("type must be IN or OUT"); 
/* 52 */     this.type = type;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 58 */     Point2D center = new Point2D.Float(size.width / 2.0F, size.height / 2.0F);
/*    */ 
/*    */     
/* 61 */     if (this.type == 8) {
/* 62 */       progress = 1.0F - progress;
/*    */     }
/*    */     
/* 65 */     float w = size.width * progress;
/* 66 */     float h = size.height * progress;
/* 67 */     AffineTransform transform = RectangularTransform.create(new Rectangle2D.Float(0.0F, 0.0F, size.width, size.height), new Rectangle2D.Double(center
/*    */           
/* 69 */           .getX() - (w / 2.0F), center.getY() - (h / 2.0F), w, h));
/*    */     
/* 71 */     return (Transition2DInstruction[])new ImageInstruction[] { new ImageInstruction((this.type == 7)), new ImageInstruction((this.type != 7), transform, null) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 79 */     if (this.type == 7) {
/* 80 */       return "Scale In";
/*    */     }
/* 82 */     return "Scale Out";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\ScaleTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */