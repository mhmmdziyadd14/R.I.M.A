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
/*    */ public class RevealTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int direction;
/*    */   
/*    */   public RevealTransition2D() {
/* 42 */     this(2);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RevealTransition2D(int direction) {
/* 50 */     if (direction != 2 && direction != 1 && direction != 3 && direction != 4)
/* 51 */       throw new IllegalArgumentException("Direction must be LEFT, UP, RIGHT or DOWN"); 
/* 52 */     this.direction = direction;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*    */     AffineTransform transform;
/* 60 */     if (this.direction == 2) {
/* 61 */       transform = AffineTransform.getTranslateInstance((-progress * size.width), 0.0D);
/* 62 */     } else if (this.direction == 1) {
/* 63 */       transform = AffineTransform.getTranslateInstance((progress * size.width), 0.0D);
/* 64 */     } else if (this.direction == 3) {
/* 65 */       transform = AffineTransform.getTranslateInstance(0.0D, (-progress * size.height));
/*    */     } else {
/* 67 */       transform = AffineTransform.getTranslateInstance(0.0D, (progress * size.height));
/*    */     } 
/*    */     
/* 70 */     return (Transition2DInstruction[])new ImageInstruction[] { new ImageInstruction(false), new ImageInstruction(true, transform, null) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 78 */     if (this.direction == 3)
/* 79 */       return "Reveal Up"; 
/* 80 */     if (this.direction == 2)
/* 81 */       return "Reveal Left"; 
/* 82 */     if (this.direction == 1) {
/* 83 */       return "Reveal Right";
/*    */     }
/* 85 */     return "Reveal Down";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\RevealTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */