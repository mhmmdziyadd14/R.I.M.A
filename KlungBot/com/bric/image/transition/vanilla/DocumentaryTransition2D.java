/*     */ package com.bric.image.transition.vanilla;
/*     */ 
/*     */ import com.bric.geom.RectangularTransform;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.util.Vector;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DocumentaryTransition2D
/*     */   extends Transition2D
/*     */ {
/*     */   int type;
/*     */   
/*     */   public DocumentaryTransition2D() {
/*  44 */     this(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DocumentaryTransition2D(int type) {
/*  52 */     if (type != 1 && type != 2 && type != 3 && type != 4) {
/*  53 */       throw new IllegalArgumentException("Type must be LEFT, RIGHT, UP or DOWN.");
/*     */     }
/*  55 */     this.type = type;
/*     */   }
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*     */     Rectangle2D r2;
/*     */     float zoomProgress;
/*  62 */     Rectangle r1 = new Rectangle(0, 0, size.width, size.height);
/*  63 */     float k1 = 0.6F;
/*  64 */     float k2 = 0.95F - k1;
/*  65 */     float k3 = (1.0F - k1) / 2.0F;
/*     */ 
/*     */     
/*  68 */     float cutOff = 0.4F;
/*     */     
/*  70 */     if (this.type == 1) {
/*  71 */       r2 = new Rectangle2D.Float(k2 * r1.width, k3 * r1.height, k1 * r1.width, k1 * r1.height);
/*     */ 
/*     */     
/*     */     }
/*  75 */     else if (this.type == 2) {
/*  76 */       r2 = new Rectangle2D.Float(0.05F * r1.width, k3 * r1.height, k1 * r1.width, k1 * r1.height);
/*     */ 
/*     */     
/*     */     }
/*  80 */     else if (this.type == 4) {
/*  81 */       r2 = new Rectangle2D.Float(k3 * r1.width, k2 * r1.height, k1 * r1.width, k1 * r1.height);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/*  86 */       r2 = new Rectangle2D.Float(k3 * r1.width, 0.05F * r1.height, k1 * r1.width, k1 * r1.height);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  92 */     float panProgress = (float)(0.5D + 0.5D * Math.sin(Math.PI * ((progress * progress) - 0.5D)));
/*  93 */     if (progress < cutOff) {
/*     */       
/*  95 */       zoomProgress = progress / cutOff;
/*     */     } else {
/*  97 */       zoomProgress = 1.0F;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 104 */     Rectangle2D r3 = new Rectangle2D.Float((float)(r2.getX() * (1.0F - panProgress) + r1.getX() * panProgress), (float)(r2.getY() * (1.0F - panProgress) + r1.getY() * panProgress), (float)(r2.getWidth() * (1.0F - panProgress) + r1.getWidth() * panProgress), (float)(r2.getHeight() * (1.0F - panProgress) + r1.getHeight() * panProgress));
/*     */ 
/*     */     
/* 107 */     Vector<Transition2DInstruction> v = new Vector<>();
/*     */     
/* 109 */     AffineTransform t = RectangularTransform.create(r3, r1);
/* 110 */     v.add(new ImageInstruction(false, t, r1));
/*     */     
/* 112 */     if (zoomProgress != 1.0F) {
/* 113 */       v.add(new ImageInstruction(true, 1.0F - zoomProgress));
/*     */     }
/*     */     
/* 116 */     return v.<Transition2DInstruction>toArray(new Transition2DInstruction[v.size()]);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     if (this.type == 2)
/* 122 */       return "Documentary Left"; 
/* 123 */     if (this.type == 1)
/* 124 */       return "Documentary Right"; 
/* 125 */     if (this.type == 3)
/* 126 */       return "Documentary Up"; 
/* 127 */     return "Documentary Down";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\DocumentaryTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */