/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.geom.GeneralPath;
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
/*    */ public class DiamondsTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int diamondSize;
/*    */   
/*    */   public DiamondsTransition2D() {
/* 43 */     this(50);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DiamondsTransition2D(int diamondSize) {
/* 53 */     if (diamondSize <= 0)
/* 54 */       throw new IllegalArgumentException("size (" + diamondSize + ") must be greater than 4"); 
/* 55 */     this.diamondSize = diamondSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 62 */     GeneralPath clipping = new GeneralPath(1);
/*    */     
/* 64 */     float dx = size.width / 2.0F;
/* 65 */     float dy = size.height / 2.0F;
/* 66 */     for (; dx > (0 + this.diamondSize); dx -= this.diamondSize);
/* 67 */     for (; dy > (0 + this.diamondSize); dy -= this.diamondSize);
/*    */     
/* 69 */     int ctr = 0;
/* 70 */     progress /= 2.0F; float y;
/* 71 */     for (y = -dy; y < (size.height + this.diamondSize); y += (this.diamondSize / 2)) {
/* 72 */       float z = 0.0F;
/* 73 */       if (ctr % 2 == 0)
/* 74 */         z = this.diamondSize / 2.0F; 
/*    */       float x;
/* 76 */       for (x = -dx; x < (size.width + this.diamondSize); x += this.diamondSize) {
/* 77 */         clipping.moveTo(x + z, y - this.diamondSize * progress);
/* 78 */         clipping.lineTo(x + this.diamondSize * progress + z, y);
/* 79 */         clipping.lineTo(x + z, y + this.diamondSize * progress);
/* 80 */         clipping.lineTo(x - this.diamondSize * progress + z, y);
/* 81 */         clipping.lineTo(x + z, y - this.diamondSize * progress);
/* 82 */         clipping.closePath();
/*    */       } 
/* 84 */       ctr++;
/*    */     } 
/*    */     
/* 87 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, null, clipping) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 96 */     return "Diamonds (" + this.diamondSize + ")";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\DiamondsTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */