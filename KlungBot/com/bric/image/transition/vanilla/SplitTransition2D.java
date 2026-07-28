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
/*    */ 
/*    */ public class SplitTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int type;
/*    */   boolean in;
/*    */   
/*    */   public SplitTransition2D() {
/* 42 */     this(9, false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SplitTransition2D(int type, boolean in) {
/* 51 */     if (type != 9 && type != 10)
/* 52 */       throw new IllegalArgumentException("The type must be HORIZONTAL or VERTICAL."); 
/* 53 */     this.type = type;
/* 54 */     this.in = in;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*    */     Rectangle2D rect;
/* 61 */     if (this.in)
/* 62 */       progress = 1.0F - progress; 
/* 63 */     if (this.type == 9) {
/* 64 */       float k = size.height / 2.0F * progress;
/* 65 */       rect = new Rectangle2D.Float(0.0F, size.height / 2.0F - k, size.width, 2.0F * k);
/*    */     }
/*    */     else {
/*    */       
/* 69 */       float k = size.width / 2.0F * progress;
/* 70 */       rect = new Rectangle2D.Float(size.width / 2.0F - k, 0.0F, 2.0F * k, size.height);
/*    */     } 
/*    */ 
/*    */     
/* 74 */     return (Transition2DInstruction[])new ImageInstruction[] { new ImageInstruction(!this.in), new ImageInstruction(this.in, null, rect) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 82 */     if (this.in && this.type == 9)
/* 83 */       return "Split Horizontal In"; 
/* 84 */     if (this.type == 9)
/* 85 */       return "Split Horizontal Out"; 
/* 86 */     if (this.in && this.type == 10) {
/* 87 */       return "Split Vertical In";
/*    */     }
/* 89 */     return "Split Vertical Out";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\SplitTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */