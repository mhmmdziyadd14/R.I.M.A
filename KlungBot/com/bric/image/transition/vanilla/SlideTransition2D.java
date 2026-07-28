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
/*    */ public class SlideTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int type;
/*    */   
/*    */   public SlideTransition2D() {
/* 39 */     this(1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SlideTransition2D(int type) {
/* 47 */     if (type != 1 && type != 2 && type != 3 && type != 4) {
/* 48 */       throw new IllegalArgumentException("The type must be LEFT, RIGHT, UP or DOWN");
/*    */     }
/* 50 */     this.type = type;
/*    */   }
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 55 */     AffineTransform transform = new AffineTransform();
/*    */     
/* 57 */     if (this.type == 2) {
/* 58 */       transform.translate((size.width * (1.0F - progress)), 0.0D);
/* 59 */     } else if (this.type == 1) {
/* 60 */       transform.translate((size.width * (progress - 1.0F)), 0.0D);
/* 61 */     } else if (this.type == 3) {
/* 62 */       transform.translate(0.0D, (size.height * (1.0F - progress)));
/*    */     } else {
/* 64 */       transform.translate(0.0D, (size.height * progress - 1.0F));
/*    */     } 
/*    */     
/* 67 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction((this.type != 4)), (Transition2DInstruction)new ImageInstruction((this.type == 4), transform, null) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 75 */     if (this.type == 1)
/* 76 */       return "Slide Right"; 
/* 77 */     if (this.type == 2)
/* 78 */       return "Slide Left"; 
/* 79 */     if (this.type == 4) {
/* 80 */       return "Slide Down";
/*    */     }
/* 82 */     return "Slide Up";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\SlideTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */