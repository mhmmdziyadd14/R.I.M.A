/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import com.bric.geom.RectangularTransform;
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Dimension;
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
/*    */ 
/*    */ 
/*    */ public class CurtainTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 45 */     progress = 1.0F - progress;
/*    */     
/* 47 */     Rectangle2D rect1 = new Rectangle2D.Double(0.0D, 0.0D, (size.width / 2.0F * progress), size.height);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 54 */     Rectangle2D rect2 = new Rectangle2D.Double(size.width - rect1.getWidth(), 0.0D, rect1.getWidth(), rect1.getHeight());
/*    */ 
/*    */     
/* 57 */     AffineTransform transform1 = RectangularTransform.create(new Rectangle2D.Float(0.0F, 0.0F, size.width / 2.0F, size.height), rect1);
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 62 */     AffineTransform transform2 = RectangularTransform.create(new Rectangle2D.Float(size.width / 2.0F, 0.0F, size.width / 2.0F, size.height), rect2);
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 67 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(false), (Transition2DInstruction)new ImageInstruction(true, transform1, rect1), (Transition2DInstruction)new ImageInstruction(true, transform2, rect2) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "Curtain";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\CurtainTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */