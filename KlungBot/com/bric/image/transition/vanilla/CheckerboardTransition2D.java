/*     */ package com.bric.image.transition.vanilla;
/*     */ 
/*     */ import com.bric.geom.TransformUtils;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.GeneralPath;
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
/*     */ public class CheckerboardTransition2D
/*     */   extends Transition2D
/*     */ {
/*     */   int type;
/*  40 */   int rowCount = 20;
/*  41 */   int columnCount = 20;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CheckerboardTransition2D() {
/*  47 */     this(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CheckerboardTransition2D(int type) {
/*  55 */     if (type != 1 && type != 2 && type != 3 && type != 4) {
/*  56 */       throw new IllegalArgumentException("The type must be RIGHT, LEFT, UP or DOWN.");
/*     */     }
/*  58 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*  65 */     GeneralPath clipping = new GeneralPath();
/*     */     
/*  67 */     if (this.type == 1 || this.type == 2) {
/*  68 */       float k = size.width / this.columnCount * 2.0F;
/*  69 */       float k2 = size.height / this.rowCount;
/*     */       
/*  71 */       for (int row = 0; row < this.rowCount; row++) {
/*  72 */         float dx = 0.0F;
/*  73 */         if (row % 2 == 0) {
/*  74 */           dx = k / 2.0F;
/*     */         }
/*  76 */         for (int column = -1; column < this.columnCount; column++) {
/*  77 */           clipping.moveTo(column * k + dx, row * k2);
/*  78 */           clipping.lineTo(column * k + dx, row * k2 + k2);
/*  79 */           clipping.lineTo(column * k + k * progress + dx, row * k2 + k2);
/*  80 */           clipping.lineTo(column * k + k * progress + dx, row * k2);
/*  81 */           clipping.lineTo(column * k + dx, row * k2);
/*  82 */           clipping.closePath();
/*     */         } 
/*     */       } 
/*     */       
/*  86 */       if (this.type == 2) {
/*  87 */         AffineTransform flip = TransformUtils.createAffineTransform(0.0D, 0.0D, size.width, 0.0D, 0.0D, size.height, size.width, 0.0D, 0.0D, 0.0D, size.width, size.height);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  95 */         clipping.transform(flip);
/*     */       } 
/*     */     } else {
/*  98 */       float k = size.height / this.rowCount * 2.0F;
/*  99 */       float k2 = size.width / this.columnCount;
/*     */       
/* 101 */       for (int column = 0; column < this.columnCount; column++) {
/* 102 */         float dy = 0.0F;
/* 103 */         if (column % 2 == 0) {
/* 104 */           dy = k / 2.0F;
/*     */         }
/* 106 */         for (int row = -1; row < this.rowCount; row++) {
/* 107 */           clipping.moveTo(column * k2, row * k + dy);
/* 108 */           clipping.lineTo(column * k2 + k2, row * k + dy);
/* 109 */           clipping.lineTo(column * k2 + k2, row * k + k * progress + dy);
/* 110 */           clipping.lineTo(column * k2, row * k + k * progress + dy);
/* 111 */           clipping.lineTo(column * k2, row * k + dy);
/* 112 */           clipping.closePath();
/*     */         } 
/*     */       } 
/*     */       
/* 116 */       if (this.type == 3) {
/* 117 */         AffineTransform flip = TransformUtils.createAffineTransform(0.0D, 0.0D, size.width, 0.0D, 0.0D, size.height, 0.0D, size.height, size.width, size.height, 0.0D, 0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 125 */         clipping.transform(flip);
/*     */       } 
/*     */     } 
/*     */     
/* 129 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, null, clipping) };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 138 */     if (this.type == 1)
/* 139 */       return "Checkerboard Right"; 
/* 140 */     if (this.type == 2)
/* 141 */       return "Checkerboard Left"; 
/* 142 */     if (this.type == 3) {
/* 143 */       return "Checkerboard Up";
/*     */     }
/* 145 */     return "Checkerboard Down";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\CheckerboardTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */