/*    */ package com.bric.image.transition;
/*    */ 
/*    */ import java.awt.BasicStroke;
/*    */ import java.awt.Color;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.Paint;
/*    */ import java.awt.Shape;
/*    */ import java.awt.Stroke;
/*    */ import java.awt.geom.GeneralPath;
/*    */ import java.awt.image.BufferedImage;
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
/*    */ public class ShapeInstruction
/*    */   extends Transition2DInstruction
/*    */ {
/*    */   public Color fillColor;
/*    */   public Color strokeColor;
/*    */   public float strokeWidth;
/*    */   public Shape shape;
/*    */   
/*    */   public ShapeInstruction(Shape shape, Color fillColor, Color strokeColor, float strokeWidth) {
/* 53 */     if (shape == null) throw new NullPointerException("A ShapeInstruction cannot have a null shape."); 
/* 54 */     this.fillColor = fillColor;
/* 55 */     this.strokeColor = strokeColor;
/* 56 */     this.strokeWidth = strokeWidth;
/* 57 */     this.shape = shape;
/*    */   }
/*    */ 
/*    */   
/*    */   public ShapeInstruction(Shape shape, float opacity) {
/* 62 */     this(shape, new Color(0, 0, 0, (int)(255.0F * opacity)));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ShapeInstruction(Shape shape, Color fillColor) {
/* 68 */     this.shape = new GeneralPath(shape);
/* 69 */     this.fillColor = fillColor;
/*    */   }
/*    */ 
/*    */   
/*    */   public void paint(Graphics2D g, BufferedImage frameA, BufferedImage frameB) {
/* 74 */     Paint oldPaint = g.getPaint();
/*    */     
/* 76 */     if (this.fillColor != null) {
/* 77 */       g.setColor(this.fillColor);
/* 78 */       g.fill(this.shape);
/*    */     } 
/* 80 */     if (this.strokeColor != null && this.strokeWidth > 0.0F) {
/* 81 */       Stroke oldStroke = g.getStroke();
/*    */       
/* 83 */       g.setStroke(new BasicStroke(this.strokeWidth));
/* 84 */       g.setColor(this.strokeColor);
/* 85 */       g.draw(this.shape);
/*    */       
/* 87 */       g.setStroke(oldStroke);
/*    */     } 
/*    */     
/* 90 */     g.setPaint(oldPaint);
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\ShapeInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */