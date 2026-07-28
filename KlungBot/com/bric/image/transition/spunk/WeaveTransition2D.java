/*    */ package com.bric.image.transition.spunk;
/*    */ 
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.geom.AffineTransform;
/*    */ import java.awt.geom.Rectangle2D;
/*    */ import java.util.Vector;
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
/*    */ public class WeaveTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 44 */     float stripHeight = 5.0F;
/* 45 */     progress = (float)(-1.6666666666666186D * progress * progress + 2.6666666666666203D * progress);
/*    */ 
/*    */     
/* 48 */     float progress2 = (float)Math.pow((1.0F - progress), 3.0D) * 0.5F + (1.0F - progress) * 0.5F;
/* 49 */     if (progress > 1.0F)
/* 50 */       progress2 = (float)Math.pow((1.0F - progress), 2.0D); 
/* 51 */     float dip = -(2.0F * progress - 1.0F) * (2.0F * progress - 1.0F) + 1.0F;
/* 52 */     Vector<Rectangle2D> v = new Vector<>(); int y;
/* 53 */     for (y = size.height; y > -stripHeight; y = (int)(y - stripHeight)) {
/* 54 */       v.add(new Rectangle2D.Float(0.0F, y, size.width, stripHeight));
/*    */     }
/* 56 */     Transition2DInstruction[] instr = new Transition2DInstruction[v.size() + 1];
/* 57 */     instr[0] = (Transition2DInstruction)new ImageInstruction(true);
/* 58 */     for (int a = 0; a < v.size(); a++) {
/* 59 */       Rectangle2D r = v.get(a);
/* 60 */       AffineTransform transform = new AffineTransform();
/* 61 */       float dx = (float)(Math.sin(1.5707963267948966D * (1.0F - progress)) * size.width);
/* 62 */       float k = progress2 * 1000.0F * dip * a / v.size();
/* 63 */       dx += k;
/* 64 */       if (a % 2 == 0) {
/* 65 */         transform.translate(dx, 0.0D);
/*    */       } else {
/* 67 */         transform.translate(-dx, 0.0D);
/*    */       } 
/* 69 */       instr[a + 1] = (Transition2DInstruction)new ImageInstruction(false, transform, transform.createTransformedShape(r));
/*    */     } 
/* 71 */     return instr;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "Weave";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\WeaveTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */