/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Dimension;
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
/*    */ public class WipeTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int direction;
/*    */   
/*    */   public WipeTransition2D() {
/* 40 */     this(1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WipeTransition2D(int direction) {
/* 48 */     this.direction = direction;
/* 49 */     if (direction != 2 && direction != 3 && direction != 1 && direction != 4)
/*    */     {
/* 51 */       throw new IllegalArgumentException();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 57 */     Rectangle2D clipping = null;
/* 58 */     if (this.direction == 1) {
/* 59 */       clipping = new Rectangle2D.Double(0.0D, 0.0D, (progress * size.width), size.height);
/* 60 */     } else if (this.direction == 2) {
/* 61 */       double x = ((1.0F - progress) * size.width);
/* 62 */       clipping = new Rectangle2D.Double(x, 0.0D, size.width - x, size.height);
/* 63 */     } else if (this.direction == 4) {
/* 64 */       clipping = new Rectangle2D.Double(0.0D, 0.0D, size.width, (progress * size.width));
/* 65 */     } else if (this.direction == 3) {
/* 66 */       double y = ((1.0F - progress) * size.height);
/* 67 */       clipping = new Rectangle2D.Double(0.0D, y, size.width, size.height - y);
/*    */     } 
/* 69 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, null, clipping) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 77 */     if (this.direction == 1)
/* 78 */       return "Wipe Right"; 
/* 79 */     if (this.direction == 2)
/* 80 */       return "Wipe Left"; 
/* 81 */     if (this.direction == 4) {
/* 82 */       return "Wipe Down";
/*    */     }
/* 84 */     return "Wipe Up";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\WipeTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */