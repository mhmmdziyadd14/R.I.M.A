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
/*    */ public class BatTransition2D
/*    */   extends AbstractShapeTransition2D
/*    */ {
/*    */   public BatTransition2D() {}
/*    */   
/*    */   public BatTransition2D(int type) {
/* 43 */     super(type);
/*    */   }
/*    */ 
/*    */   
/*    */   public Shape getShape() {
/* 48 */     GeneralPath batPath = new GeneralPath(0);
/* 49 */     batPath.moveTo(0.0F, 2.0F);
/* 50 */     batPath.lineTo(2.0F, 0.0F);
/* 51 */     batPath.curveTo(2.0F, 1.0F, 2.0F, 1.0F, 3.0F, 1.0F);
/* 52 */     batPath.lineTo(3.0F, 0.0F);
/* 53 */     batPath.curveTo(3.5F, 1.0F, 3.5F, 1.0F, 4.0F, 0.0F);
/* 54 */     batPath.lineTo(4.0F, 1.0F);
/* 55 */     batPath.curveTo(5.0F, 1.0F, 5.0F, 1.0F, 5.0F, 0.0F);
/* 56 */     batPath.lineTo(7.0F, 2.0F);
/* 57 */     batPath.curveTo(6.5F, 1.5F, 6.5F, 1.5F, 6.0F, 2.0F);
/* 58 */     batPath.curveTo(5.5F, 1.5F, 5.5F, 1.5F, 5.0F, 2.0F);
/* 59 */     batPath.curveTo(4.5F, 1.5F, 4.5F, 1.5F, 4.0F, 2.0F);
/* 60 */     batPath.curveTo(3.5F, 3.0F, 3.5F, 3.0F, 3.0F, 2.0F);
/* 61 */     batPath.curveTo(2.5F, 1.5F, 2.5F, 1.5F, 2.0F, 2.0F);
/* 62 */     batPath.curveTo(1.5F, 1.5F, 1.5F, 1.5F, 1.0F, 2.0F);
/* 63 */     batPath.curveTo(0.5F, 1.5F, 0.5F, 1.5F, 0.0F, 2.0F);
/* 64 */     batPath.closePath();
/*    */     
/* 66 */     return batPath;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getShapeName() {
/* 71 */     return "Bat";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\BatTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */