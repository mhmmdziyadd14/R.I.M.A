/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import java.awt.Shape;
/*    */ import java.awt.geom.Ellipse2D;
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
/*    */ public class CircleTransition2D
/*    */   extends AbstractShapeTransition2D
/*    */ {
/*    */   public CircleTransition2D() {}
/*    */   
/*    */   public CircleTransition2D(int type) {
/* 43 */     super(type);
/*    */   }
/*    */ 
/*    */   
/*    */   public Shape getShape() {
/* 48 */     return new Ellipse2D.Float(0.0F, 0.0F, 100.0F, 100.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getShapeName() {
/* 53 */     return "Circle";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\CircleTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */