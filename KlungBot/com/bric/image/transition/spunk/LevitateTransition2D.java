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
/*    */ 
/*    */ public class LevitateTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 45 */     progress = (float)Math.pow(progress, 2.0D);
/* 46 */     float stripHeight = size.height / 6.0F;
/*    */     
/* 48 */     Vector<Rectangle2D> v = new Vector<>(); int y;
/* 49 */     for (y = 0; y < size.height; y = (int)(y + stripHeight)) {
/* 50 */       v.add(new Rectangle2D.Float(0.0F, y, size.width, stripHeight));
/*    */     }
/* 52 */     Transition2DInstruction[] instr = new Transition2DInstruction[v.size() + 1];
/* 53 */     instr[0] = (Transition2DInstruction)new ImageInstruction(false);
/* 54 */     for (int a = 0; a < v.size(); a++) {
/* 55 */       Rectangle2D r = v.get(a);
/* 56 */       AffineTransform transform = new AffineTransform();
/* 57 */       float angleProgress = (float)Math.pow(progress, 0.6D);
/* 58 */       float xProgress = 1.0F / (1.0F + progress);
/* 59 */       float k = angleProgress * a / v.size();
/* 60 */       float theta = (float)(Math.PI * k / 2.0D + progress * Math.PI / 2.0D);
/* 61 */       if (theta > 1.5707963267948966D) theta = 1.5707964F;
/*    */       
/* 63 */       theta = 0.2F + (float)(0.2D * Math.cos((-3.0F * theta)));
/* 64 */       float k2 = 1.0F - progress;
/* 65 */       theta *= progress;
/* 66 */       if (a % 2 == 0) {
/* 67 */         transform.rotate(theta, (-size.width * (1.0F - xProgress * xProgress * xProgress) / 2.0F), (size.height * k2));
/*    */       } else {
/* 69 */         transform.rotate(-theta, (size.width + (1.0F - xProgress * xProgress * xProgress) * size.width / 2.0F), (size.height * k2));
/*    */       } 
/* 71 */       transform.translate(0.0D, (-progress * progress * 1.5F * size.height));
/*    */       
/* 73 */       instr[a + 1] = (Transition2DInstruction)new ImageInstruction(true, transform, transform.createTransformedShape(r));
/*    */     } 
/* 75 */     return instr;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 80 */     return "Levitate";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\LevitateTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */