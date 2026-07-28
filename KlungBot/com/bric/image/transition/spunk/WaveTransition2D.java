/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.TransformUtils;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Rectangle2D;
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
/*     */ public class WaveTransition2D
/*     */   extends Transition2D
/*     */ {
/*  36 */   int type = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WaveTransition2D() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WaveTransition2D(int type) {
/*  48 */     if (type != 2 && type != 1 && type != 4 && type != 3)
/*  49 */       throw new IllegalArgumentException("Type must be LEFT, UP, RIGHT or DOWN"); 
/*  50 */     this.type = type;
/*     */   }
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*  55 */     int k = size.height / 5;
/*  56 */     Transition2DInstruction[] i = new Transition2DInstruction[k + 1];
/*  57 */     float progress2 = (float)Math.sqrt(progress);
/*  58 */     i[0] = (Transition2DInstruction)new ImageInstruction(true);
/*     */     
/*  60 */     int measurement = size.width;
/*  61 */     if (this.type == 3 || this.type == 4) {
/*  62 */       measurement = size.height;
/*     */     }
/*     */     
/*  65 */     double lastD = 0.0D;
/*     */     
/*  67 */     for (int a = 0; a < i.length; a++) {
/*  68 */       float z1 = (a - 1) / (i.length - 1);
/*  69 */       float z2 = a / (i.length - 1);
/*  70 */       AffineTransform transform = new AffineTransform();
/*  71 */       float wave = (float)(0.3D * Math.sin((1.0F + 3.0F * z1 + 8.0F * progress)));
/*  72 */       float dip = 1.0F - 0.5F * (2.0F * progress - 1.0F) * (2.0F * progress - 1.0F) * (2.0F * progress - 1.0F) - 0.5F;
/*     */ 
/*     */ 
/*     */       
/*  76 */       double d = ((1.0F - progress2) * measurement) + ((1.0F - z2) * dip) * ((z2 + wave) + 0.4D) * measurement;
/*     */ 
/*     */       
/*  79 */       if (a > 0) {
/*  80 */         Rectangle2D clipping; if (this.type == 2 || this.type == 1) {
/*     */           
/*  82 */           clipping = new Rectangle2D.Float(0.0F, z1 * size.height, size.width, z2 * size.height - z1 * size.height + 1.0F);
/*  83 */           if (this.type == 2) {
/*  84 */             transform = TransformUtils.createAffineTransform(0.0D, (z2 * size.height), 0.0D, (z1 * size.height), size.width, (z2 * size.height), d, (z2 * size.height), lastD, (z1 * size.height), d + size.width, (z2 * size.height));
/*     */ 
/*     */ 
/*     */           
/*     */           }
/*     */           else {
/*     */ 
/*     */ 
/*     */             
/*  93 */             transform = TransformUtils.createAffineTransform(size.width, (z2 * size.height), size.width, (z1 * size.height), 0.0D, (z2 * size.height), size.width - d, (z2 * size.height), size.width - lastD, (z1 * size.height), size.width - d - size.width, (z2 * size.height));
/*     */ 
/*     */           
/*     */           }
/*     */ 
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 104 */           clipping = new Rectangle2D.Float(z1 * size.width, 0.0F, z2 * size.width - z1 * size.width + 1.0F, size.height);
/* 105 */           if (this.type == 3) {
/* 106 */             transform = TransformUtils.createAffineTransform((z2 * size.width), 0.0D, (z1 * size.width), 0.0D, (z2 * size.width), size.height, (z2 * size.width), d, (z1 * size.width), lastD, (z2 * size.width), d + size.height);
/*     */ 
/*     */ 
/*     */           
/*     */           }
/*     */           else {
/*     */ 
/*     */ 
/*     */             
/* 115 */             transform = TransformUtils.createAffineTransform((z2 * size.width), size.height, (z1 * size.width), size.height, (z2 * size.width), 0.0D, (z2 * size.width), size.height - d, (z1 * size.width), size.height - lastD, (z2 * size.width), size.height - d - size.height);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 125 */         i[a] = (Transition2DInstruction)new ImageInstruction(false, transform, transform.createTransformedShape(clipping));
/*     */       } 
/* 127 */       lastD = d;
/*     */     } 
/* 129 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 134 */     if (this.type == 3)
/* 135 */       return "Wave Up"; 
/* 136 */     if (this.type == 4)
/* 137 */       return "Wave Down"; 
/* 138 */     if (this.type == 1) {
/* 139 */       return "Wave Right";
/*     */     }
/* 141 */     return "Wave Left";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\WaveTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */