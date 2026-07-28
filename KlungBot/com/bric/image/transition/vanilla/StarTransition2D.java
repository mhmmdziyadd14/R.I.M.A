/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import java.awt.Shape;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StarTransition2D
/*    */   extends AbstractShapeTransition2D
/*    */ {
/*    */   public StarTransition2D() {}
/*    */   
/*    */   public StarTransition2D(int type) {
/* 43 */     super(type);
/*    */   }
/*    */ 
/*    */   
/*    */   public Shape getShape() {
/* 48 */     GeneralPath p = new GeneralPath();
/* 49 */     double angle = 0.3141592653589793D;
/* 50 */     float r2 = 2.5F;
/* 51 */     double k = 0.6283185307179586D;
/* 52 */     p.moveTo((float)Math.cos(angle), (float)Math.sin(angle));
/* 53 */     for (int a = 0; a < 5; a++) {
/* 54 */       p.lineTo((float)(r2 * Math.cos(angle + k)), (float)(r2 * Math.sin(angle + k)));
/* 55 */       angle += 1.2566370614359172D;
/* 56 */       p.lineTo((float)Math.cos(angle), (float)Math.sin(angle));
/*    */     } 
/* 58 */     p.closePath();
/* 59 */     return p;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getShapeName() {
/* 64 */     return "Star";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\StarTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */