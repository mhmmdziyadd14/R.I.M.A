/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import com.bric.geom.RectangularTransform;
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.geom.AffineTransform;
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
/*    */ public class MotionBlendTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 42 */     Vector<ImageInstruction> v = new Vector<>();
/*    */     
/* 44 */     int max_wchange = size.width / 4;
/* 45 */     int max_hchange = size.height / 4;
/*    */     
/* 47 */     int x2 = (int)-(max_wchange * (1.0F - progress));
/* 48 */     int y2 = (int)-(max_hchange * (1.0F - progress));
/* 49 */     int w2 = (int)(size.width + max_wchange * (1.0F - progress));
/* 50 */     int h2 = (int)(size.height + max_hchange * (1.0F - progress));
/* 51 */     AffineTransform transform = RectangularTransform.create(new Rectangle(0, 0, size.width, size.height), new Rectangle(x2, y2, w2 - x2, h2 - y2));
/*    */ 
/*    */ 
/*    */     
/* 55 */     v.add(new ImageInstruction(true, 1.0F, transform, null));
/*    */     
/* 57 */     x2 = (int)-(max_wchange * progress);
/* 58 */     y2 = (int)-(max_hchange * progress);
/* 59 */     w2 = (int)(size.width + max_wchange * progress);
/* 60 */     h2 = (int)(size.height + max_hchange * progress);
/* 61 */     transform = RectangularTransform.create(new Rectangle(0, 0, size.width, size.height), new Rectangle(x2, y2, w2 - x2, h2 - y2));
/*    */ 
/*    */ 
/*    */     
/* 65 */     v.add(new ImageInstruction(false, 1.0F - progress, transform, null));
/*    */     
/* 67 */     return (Transition2DInstruction[])v.toArray((Object[])new ImageInstruction[v.size()]);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 72 */     return "Motion Blend";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\MotionBlendTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */