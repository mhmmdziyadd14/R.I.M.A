/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.geom.AffineTransform;
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
/*    */ public class DropTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*    */     float dy;
/* 44 */     if (progress < 0.8D) {
/* 45 */       progress /= 0.8F;
/* 46 */       dy = -progress * progress + 1.0F;
/* 47 */       dy = 1.0F - dy;
/*    */     } else {
/* 49 */       progress = (progress - 0.8F) / 0.2F;
/* 50 */       dy = -4.0F * (progress - 0.5F) * (progress - 0.5F) + 1.0F;
/* 51 */       dy = 1.0F - dy * 0.1F;
/*    */     } 
/* 53 */     AffineTransform transform = AffineTransform.getTranslateInstance(0.0D, (dy * size.height - size.height));
/*    */     
/* 55 */     return (Transition2DInstruction[])new ImageInstruction[] { new ImageInstruction(true), new ImageInstruction(false, transform, null) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 63 */     return "Drop";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\DropTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */