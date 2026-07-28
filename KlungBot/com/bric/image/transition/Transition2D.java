/*    */ package com.bric.image.transition;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.image.BufferedImage;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Transition2D
/*    */   implements Transition
/*    */ {
/*    */   public abstract Transition2DInstruction[] getInstructions(float paramFloat, Dimension paramDimension);
/*    */   
/*    */   public final void paint(Graphics2D g, BufferedImage frameA, BufferedImage frameB, float progress) {
/* 47 */     Transition2DInstruction[] i = getInstructions(progress, new Dimension(frameA.getWidth(), frameA.getHeight()));
/* 48 */     for (int a = 0; a < i.length; a++)
/* 49 */       i[a].paint(g, frameA, frameB); 
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\Transition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */