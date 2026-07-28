/*     */ package com.bric.image.transition.vanilla;
/*     */ 
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.geom.AffineTransform;
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
/*     */ 
/*     */ 
/*     */ public class PivotTransition2D
/*     */   extends Transition2D
/*     */ {
/*     */   boolean in;
/*     */   int type;
/*     */   
/*     */   public PivotTransition2D() {
/*  43 */     this(14, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PivotTransition2D(int type, boolean in) {
/*  53 */     if (type != 14 && type != 15 && type != 16 && type != 17)
/*  54 */       throw new IllegalArgumentException("Type must be TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT or BOTTOM_RIGHT"); 
/*  55 */     this.type = type;
/*  56 */     this.in = in;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*     */     AffineTransform transform;
/*  63 */     if (this.in) {
/*  64 */       if (this.type == 14) {
/*  65 */         transform = AffineTransform.getRotateInstance((float)(-(1.0F - progress) * Math.PI / 2.0D), 0.0D, 0.0D);
/*  66 */       } else if (this.type == 15) {
/*  67 */         transform = AffineTransform.getRotateInstance((float)((1.0F - progress) * Math.PI / 2.0D), size.width, 0.0D);
/*  68 */       } else if (this.type == 16) {
/*  69 */         transform = AffineTransform.getRotateInstance((float)((1.0F - progress) * Math.PI / 2.0D), 0.0D, size.height);
/*     */       } else {
/*  71 */         transform = AffineTransform.getRotateInstance((float)((1.0F - progress) * Math.PI / 2.0D), size.width, size.height);
/*     */       } 
/*  73 */       return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, transform, null) };
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  80 */     if (this.type == 14) {
/*  81 */       transform = AffineTransform.getRotateInstance((float)(progress * Math.PI / 2.0D), 0.0D, 0.0D);
/*  82 */     } else if (this.type == 15) {
/*  83 */       transform = AffineTransform.getRotateInstance((float)(-progress * Math.PI / 2.0D), size.width, 0.0D);
/*  84 */     } else if (this.type == 16) {
/*  85 */       transform = AffineTransform.getRotateInstance((float)(-progress * Math.PI / 2.0D), 0.0D, size.height);
/*     */     } else {
/*  87 */       transform = AffineTransform.getRotateInstance((float)(-progress * Math.PI / 2.0D), size.width, size.height);
/*     */     } 
/*  89 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(false), (Transition2DInstruction)new ImageInstruction(true, transform, null) };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     String s;
/*  98 */     if (this.in) {
/*  99 */       s = "Pivot In ";
/*     */     } else {
/* 101 */       s = "Pivot Out ";
/*     */     } 
/* 103 */     if (this.type == 14)
/* 104 */       return s + "Top Left"; 
/* 105 */     if (this.type == 15)
/* 106 */       return s + "Top Right"; 
/* 107 */     if (this.type == 16) {
/* 108 */       return s + "Bottom Left";
/*     */     }
/* 110 */     return s + "Bottom Right";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\PivotTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */