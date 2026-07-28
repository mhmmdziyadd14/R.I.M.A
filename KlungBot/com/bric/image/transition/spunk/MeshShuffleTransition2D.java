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
/*    */ public class MeshShuffleTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 45 */     progress = (float)Math.pow(progress, 0.45D);
/* 46 */     float stripHeight = (size.height * 10 / 200);
/*    */     
/* 48 */     Vector<Rectangle2D> v = new Vector<>(); int y;
/* 49 */     for (y = size.height; y > -stripHeight; y = (int)(y - stripHeight)) {
/* 50 */       v.add(new Rectangle2D.Float(0.0F, y, size.width, stripHeight));
/*    */     }
/* 52 */     Transition2DInstruction[] instr = new Transition2DInstruction[v.size()];
/* 53 */     instr[0] = (Transition2DInstruction)new ImageInstruction(true);
/* 54 */     for (int a = 1; a < v.size(); a++) {
/* 55 */       Rectangle2D r = v.get(a);
/* 56 */       AffineTransform transform = new AffineTransform();
/* 57 */       float k = (1.0F - progress) * a / v.size();
/* 58 */       float theta = (float)(Math.PI * k / 2.0D + (1.0F - progress) * Math.PI / 2.0D);
/* 59 */       if (theta > 1.5707963267948966D) theta = 1.5707964F; 
/* 60 */       if (a % 2 == 0) {
/* 61 */         transform.rotate(-theta, (-size.width * (1.0F - progress) / 2.0F), (size.height * progress));
/*    */       } else {
/* 63 */         transform.rotate(theta, (size.width + (1.0F - progress) * size.width / 2.0F), (size.height * progress));
/*    */       } 
/* 65 */       instr[a] = (Transition2DInstruction)new ImageInstruction(false, transform, transform.createTransformedShape(r));
/*    */     } 
/* 67 */     return instr;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 72 */     return "Mesh Shuffle";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\MeshShuffleTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */