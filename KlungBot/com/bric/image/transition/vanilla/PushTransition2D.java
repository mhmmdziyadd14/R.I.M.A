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
/*    */ public class PushTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int type;
/*    */   
/*    */   public PushTransition2D() {
/* 39 */     this(1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PushTransition2D(int type) {
/* 47 */     if (type != 1 && type != 2 && type != 3 && type != 4) {
/* 48 */       throw new IllegalArgumentException("The type must be LEFT, RIGHT, UP or DOWN");
/*    */     }
/* 50 */     this.type = type;
/*    */   }
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 55 */     AffineTransform transform1 = new AffineTransform();
/* 56 */     AffineTransform transform2 = new AffineTransform();
/*    */     
/* 58 */     if (this.type == 2) {
/* 59 */       transform2.translate((size.width * (1.0F - progress)), 0.0D);
/* 60 */       transform1.translate((size.width * (1.0F - progress) - size.width), 0.0D);
/* 61 */     } else if (this.type == 1) {
/* 62 */       transform2.translate((size.width * (progress - 1.0F)), 0.0D);
/* 63 */       transform1.translate((size.width * (progress - 1.0F) + size.width), 0.0D);
/* 64 */     } else if (this.type == 3) {
/* 65 */       transform2.translate(0.0D, (size.height * (1.0F - progress)));
/* 66 */       transform1.translate(0.0D, (size.height * (1.0F - progress) - size.height));
/*    */     } else {
/* 68 */       transform2.translate(0.0D, (size.height * (progress - 1.0F)));
/* 69 */       transform1.translate(0.0D, (size.height * (progress - 1.0F) + size.height));
/*    */     } 
/*    */     
/* 72 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true, transform1, null), (Transition2DInstruction)new ImageInstruction(false, transform2, null) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 80 */     if (this.type == 1)
/* 81 */       return "Push Right"; 
/* 82 */     if (this.type == 2)
/* 83 */       return "Push Left"; 
/* 84 */     if (this.type == 4) {
/* 85 */       return "Push Down";
/*    */     }
/* 87 */     return "Push Up";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\PushTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */