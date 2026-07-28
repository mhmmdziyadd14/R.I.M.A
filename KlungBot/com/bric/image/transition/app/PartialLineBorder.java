/*    */ package com.bric.image.transition.app;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Insets;
/*    */ import java.awt.Paint;
/*    */ import javax.swing.border.Border;
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
/*    */ public class PartialLineBorder
/*    */   implements Border
/*    */ {
/*    */   Paint p;
/*    */   Insets i;
/*    */   
/*    */   public PartialLineBorder(Paint p, Insets i) {
/* 34 */     this.p = p;
/* 35 */     this.i = (Insets)i.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   public Insets getBorderInsets(Component c) {
/* 40 */     return (Insets)this.i.clone();
/*    */   }
/*    */   
/*    */   public boolean isBorderOpaque() {
/* 44 */     return false;
/*    */   }
/*    */   
/*    */   public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
/* 48 */     ((Graphics2D)g).setPaint(this.p); int a;
/* 49 */     for (a = y; a < y + this.i.top; a++) {
/* 50 */       g.drawLine(x, a, x + w, a);
/*    */     }
/* 52 */     for (a = x; a < x + this.i.left; a++) {
/* 53 */       g.drawLine(a, y, a, y + h);
/*    */     }
/* 55 */     for (a = y + h - this.i.bottom; a < y + h; a++) {
/* 56 */       g.drawLine(x, a, x + w, a);
/*    */     }
/* 58 */     for (a = x + w - this.i.right; a < x + w; a++)
/* 59 */       g.drawLine(a, y, a, y + h); 
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\app\PartialLineBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */