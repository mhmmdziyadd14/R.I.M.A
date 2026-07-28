/*    */ package com.bric.image.transition.spunk;
/*    */ 
/*    */ import java.awt.geom.Point2D;
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
/*    */ public class ZoomTransition2D
/*    */   extends AbstractPlanarTransition2D
/*    */ {
/*    */   int multiplier;
/*    */   
/*    */   public ZoomTransition2D() {
/* 33 */     this(1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ZoomTransition2D(int direction) {
/* 41 */     if (direction == 1) {
/* 42 */       this.multiplier = 1;
/* 43 */     } else if (direction == 2) {
/* 44 */       this.multiplier = -1;
/*    */     } else {
/* 46 */       throw new IllegalArgumentException("The direction must be LEFT or RIGHT");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public float getFrameBOpacity(float p) {
/* 52 */     return 0.5F + 0.5F * p;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getFrameAOpacity(float p) {
/* 57 */     return 1.0F;
/*    */   }
/*    */ 
/*    */   
/*    */   public Point2D getFrameBLocation(float p) {
/* 62 */     double y = p;
/* 63 */     double x = 0.5D - (this.multiplier * (1.0F - p));
/* 64 */     return new Point2D.Double(x, y);
/*    */   }
/*    */ 
/*    */   
/*    */   public Point2D getFrameALocation(float p) {
/* 69 */     double y = (p + 1.0F);
/* 70 */     double x = 0.5D + (this.multiplier * p);
/* 71 */     return new Point2D.Double(x, y);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 77 */     if (this.multiplier == 1) {
/* 78 */       return "Zoom Right";
/*    */     }
/* 80 */     return "Zoom Left";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\ZoomTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */