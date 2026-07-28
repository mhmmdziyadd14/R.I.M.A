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
/*    */ public class CollapseTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 44 */     progress = (float)Math.pow(progress, 2.0D);
/* 45 */     float stripHeight = (size.height / 6);
/*    */     
/* 47 */     Vector<Rectangle2D> v = new Vector<>(); int y;
/* 48 */     for (y = 0; y < size.height; y = (int)(y + stripHeight)) {
/* 49 */       v.add(new Rectangle2D.Float(0.0F, y, size.width, stripHeight));
/*    */     }
/* 51 */     ImageInstruction[] instr = new ImageInstruction[v.size() + 1];
/* 52 */     instr[0] = new ImageInstruction(false);
/* 53 */     for (int a = 0; a < v.size(); a++) {
/* 54 */       Rectangle2D r = v.get(a);
/* 55 */       AffineTransform transform = new AffineTransform();
/* 56 */       float angleProgress = (float)Math.pow(progress, 0.6D);
/* 57 */       float xProgress = 1.0F / (1.0F + progress);
/* 58 */       float k = angleProgress * a / v.size();
/* 59 */       float theta = (float)(Math.PI * k / 2.0D + progress * Math.PI / 2.0D);
/* 60 */       if (theta > 1.5707963267948966D) theta = 1.5707964F;
/*    */       
/* 62 */       theta /= 1.0F + progress;
/* 63 */       float k2 = 1.0F * progress;
/* 64 */       if (a % 2 == 0) {
/* 65 */         transform.rotate(theta, (-size.width * (1.0F - xProgress * xProgress * xProgress) / 2.0F), (size.height * k2));
/*    */       } else {
/* 67 */         transform.rotate(-theta, (size.width + (1.0F - xProgress * xProgress * xProgress) * size.width / 2.0F), (size.height * k2));
/*    */       } 
/* 69 */       transform.translate(0.0D, (progress * progress * size.height) * 1.5D);
/* 70 */       instr[a + 1] = new ImageInstruction(true, transform, transform.createTransformedShape(r));
/*    */     } 
/* 72 */     return (Transition2DInstruction[])instr;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 77 */     return "Collapse";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\CollapseTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */