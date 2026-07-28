/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import java.awt.Shape;
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
/*    */ 
/*    */ 
/*    */ public class BoxTransition2D
/*    */   extends AbstractShapeTransition2D
/*    */ {
/*    */   public BoxTransition2D() {}
/*    */   
/*    */   public BoxTransition2D(int type) {
/* 40 */     super(type);
/*    */   }
/*    */ 
/*    */   
/*    */   public Shape getShape() {
/* 45 */     return new Rectangle2D.Float(0.0F, 0.0F, 100.0F, 100.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getShapeName() {
/* 50 */     return "Box";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\BoxTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */