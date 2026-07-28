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
/*    */ public class RotateTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int type;
/*    */   
/*    */   public RotateTransition2D() {
/* 40 */     this(7);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RotateTransition2D(int type) {
/* 48 */     if (type != 7 && type != 8) {
/* 49 */       throw new IllegalArgumentException("type must be IN or OUT");
/*    */     }
/* 51 */     this.type = type;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 57 */     if (this.type == 8) {
/* 58 */       progress = 1.0F - progress;
/*    */     }
/* 60 */     AffineTransform transform = new AffineTransform();
/* 61 */     transform.translate((size.width / 2), (size.height / 2));
/* 62 */     transform.scale(progress, progress);
/* 63 */     transform.rotate(((1.0F - progress) * 6.0F));
/* 64 */     transform.translate((-size.width / 2), (-size.height / 2));
/*    */     
/* 66 */     return (Transition2DInstruction[])new ImageInstruction[] { new ImageInstruction((this.type == 7)), new ImageInstruction((this.type != 7), transform, null) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     if (this.type == 7) {
/* 75 */       return "Rotate In";
/*    */     }
/* 77 */     return "Roate Out";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\RotateTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */