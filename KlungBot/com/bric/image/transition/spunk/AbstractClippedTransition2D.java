/*    */ package com.bric.image.transition.spunk;
/*    */ 
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.ShapeInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Shape;
/*    */ import java.awt.geom.Area;
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
/*    */ 
/*    */ public abstract class AbstractClippedTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   public abstract Shape[] getShapes(float paramFloat, Dimension paramDimension);
/*    */   
/*    */   public abstract float getStrokeWidth(float paramFloat);
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 55 */     Shape[] data = getShapes(progress, size);
/* 56 */     Area area = new Area();
/* 57 */     for (int a = 0; a < data.length; a++) {
/* 58 */       Area newShape = new Area(data[a]);
/* 59 */       area.add(newShape);
/*    */     } 
/*    */     
/* 62 */     float w = getStrokeWidth(progress);
/* 63 */     if (w == 0.0F) {
/* 64 */       return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, null, area) };
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 69 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, null, area), (Transition2DInstruction)new ShapeInstruction(area, null, Color.black, w) };
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\AbstractClippedTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */