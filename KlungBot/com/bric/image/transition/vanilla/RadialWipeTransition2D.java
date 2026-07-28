/*    */ package com.bric.image.transition.vanilla;
/*    */ 
/*    */ import com.bric.image.transition.ImageInstruction;
/*    */ import com.bric.image.transition.Transition2D;
/*    */ import com.bric.image.transition.Transition2DInstruction;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.geom.Arc2D;
/*    */ import java.awt.geom.Area;
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
/*    */ public class RadialWipeTransition2D
/*    */   extends Transition2D
/*    */ {
/*    */   int type;
/*    */   
/*    */   public RadialWipeTransition2D() {
/* 44 */     this(6);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RadialWipeTransition2D(int type) {
/* 52 */     if (type != 6 && type != 5) {
/* 53 */       throw new IllegalArgumentException("Type must be CLOCKWISE or COUNTER_CLOCKWISE.");
/*    */     }
/* 55 */     this.type = type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 62 */     int multiplier2 = -1;
/* 63 */     if (this.type == 5) {
/* 64 */       multiplier2 = 1;
/*    */     }
/* 66 */     int multiplier1 = 0;
/* 67 */     int k = Math.max(size.width, size.height);
/* 68 */     Area area = new Area(new Arc2D.Double(new Rectangle2D.Double((size.width / 2 - 2 * k), (size.height / 2 - 2 * k), (k * 4), (k * 4)), (90.0F + multiplier1 * progress * 360.0F), (multiplier2 * progress * 360.0F), 2));
/*    */     
/* 70 */     area.intersect(new Area(new Rectangle(0, 0, size.width, size.height)));
/*    */     
/* 72 */     return (Transition2DInstruction[])new ImageInstruction[] { new ImageInstruction(true), new ImageInstruction(false, null, area) };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 80 */     if (this.type == 6) {
/* 81 */       return "Radial Wipe Clockwise";
/*    */     }
/* 83 */     return "Radial Wipe Counterclockwise";
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\RadialWipeTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */