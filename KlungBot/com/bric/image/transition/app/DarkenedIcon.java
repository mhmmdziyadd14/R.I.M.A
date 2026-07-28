/*    */ package com.bric.image.transition.app;
/*    */ 
/*    */ import java.awt.AlphaComposite;
/*    */ import java.awt.Component;
/*    */ import java.awt.Composite;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import javax.swing.AbstractButton;
/*    */ import javax.swing.Icon;
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
/*    */ public class DarkenedIcon
/*    */   implements Icon
/*    */ {
/*    */   AbstractButton button;
/*    */   float f;
/*    */   Icon icon;
/*    */   
/*    */   public DarkenedIcon(AbstractButton b, float f) {
/* 38 */     this.button = b;
/* 39 */     this.f = f;
/*    */   }
/*    */   
/*    */   public DarkenedIcon(Icon icon, float f) {
/* 43 */     this.icon = icon;
/* 44 */     this.f = f;
/*    */   }
/*    */   
/*    */   private Icon getIcon() {
/* 48 */     if (this.button != null)
/* 49 */       return this.button.getIcon(); 
/* 50 */     return this.icon;
/*    */   }
/*    */   
/*    */   public int getIconHeight() {
/* 54 */     return getIcon().getIconHeight();
/*    */   }
/*    */   
/*    */   public int getIconWidth() {
/* 58 */     return getIcon().getIconWidth();
/*    */   }
/*    */   
/*    */   public void paintIcon(Component c, Graphics g, int x, int y) {
/* 62 */     Icon i = getIcon();
/* 63 */     i.paintIcon(c, g, x, y);
/* 64 */     Graphics2D g2 = (Graphics2D)g;
/* 65 */     Composite oldComposite = g2.getComposite();
/* 66 */     Composite composite = AlphaComposite.getInstance(8, this.f);
/* 67 */     g2.setComposite(composite);
/* 68 */     i.paintIcon(c, g, x, y);
/* 69 */     g2.setComposite(oldComposite);
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\app\DarkenedIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */