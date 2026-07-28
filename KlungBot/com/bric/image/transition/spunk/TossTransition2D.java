/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.TransformUtils;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
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
/*     */ public class TossTransition2D
/*     */   extends Transition2D
/*     */ {
/*  36 */   int type = 1;
/*     */ 
/*     */ 
/*     */   
/*     */   public TossTransition2D() {
/*  41 */     this(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TossTransition2D(int type) {
/*  49 */     if (type != 2 && type != 1)
/*  50 */       throw new IllegalArgumentException("The transition must use RIGHT or LEFT"); 
/*  51 */     this.type = type;
/*     */   }
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*     */     AffineTransform transform;
/*  57 */     double angle1 = -0.2617993877991494D;
/*  58 */     double angle2 = 0.08726646259971647D;
/*  59 */     double angle3 = -0.03490658503988659D;
/*  60 */     AffineTransform flipped = TransformUtils.createAffineTransform(0.0D, 0.0D, size.width, 0.0D, 0.0D, size.height, size.width, 0.0D, 0.0D, 0.0D, size.width, size.height);
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
/*  71 */     AffineTransform untouched = TransformUtils.createAffineTransform(0.0D, size.height, size.width, size.height, 0.0D, 0.0D, (size.width * 4 / 5), (size.height - 5 * size.height / 4), (size.width * 4 / 5 + size.width), (size.height - 5 * size.height / 4), (size.width * 4 / 5), (0 - 5 * size.height / 4));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  80 */     if (this.type == 1) {
/*  81 */       untouched.preConcatenate(flipped);
/*  82 */       untouched.concatenate(flipped);
/*     */     } 
/*     */     
/*  85 */     Point2D p1 = new Point2D.Double(size.width, size.height);
/*  86 */     Point2D p2 = new Point2D.Double(0.0D, 0.0D);
/*  87 */     Point2D p3 = new Point2D.Double();
/*  88 */     Point2D p4 = new Point2D.Double();
/*  89 */     AffineTransform t1 = new AffineTransform();
/*  90 */     t1.setToRotation(angle1, 0.0D, size.height);
/*  91 */     t1.transform(p1, p3);
/*  92 */     t1.transform(p2, p4);
/*     */     
/*  94 */     AffineTransform transform1 = TransformUtils.createAffineTransform(0.0D, size.height, (0 + size.width), size.height, 0.0D, 0.0D, 0.0D, size.height, p3
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 100 */         .getX(), p3.getY(), p4
/* 101 */         .getX(), p4.getY());
/*     */     
/* 103 */     if (this.type == 1) {
/* 104 */       transform1.preConcatenate(flipped);
/* 105 */       transform1.concatenate(flipped);
/*     */     } 
/*     */     
/* 108 */     p1.setLocation(0.0D, size.height);
/* 109 */     p2.setLocation(0.0D, 0.0D);
/* 110 */     t1.setToRotation(angle2, size.width, size.height);
/* 111 */     t1.transform(p1, p3);
/* 112 */     t1.transform(p2, p4);
/*     */     
/* 114 */     AffineTransform transform2 = TransformUtils.createAffineTransform(0.0D, size.height, size.width, size.height, 0.0D, 0.0D, p3
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 119 */         .getX(), p3.getY(), size.width, size.height, p4
/*     */         
/* 121 */         .getX(), p4.getY());
/*     */     
/* 123 */     if (this.type == 1) {
/* 124 */       transform2.preConcatenate(flipped);
/* 125 */       transform2.concatenate(flipped);
/*     */     } 
/*     */     
/* 128 */     p1.setLocation(size.width, size.height);
/* 129 */     p2.setLocation(0.0D, 0.0D);
/* 130 */     t1.setToRotation(angle3, 0.0D, size.height);
/* 131 */     t1.transform(p1, p3);
/* 132 */     t1.transform(p2, p4);
/*     */     
/* 134 */     AffineTransform transform3 = TransformUtils.createAffineTransform(0.0D, size.height, size.width, size.height, 0.0D, 0.0D, 0.0D, size.height, p3
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 140 */         .getX(), p3.getY(), p4
/* 141 */         .getX(), p4.getY());
/*     */     
/* 143 */     if (this.type == 1) {
/* 144 */       transform3.preConcatenate(flipped);
/* 145 */       transform3.concatenate(flipped);
/*     */     } 
/*     */ 
/*     */     
/* 149 */     float cut1 = 0.35F;
/* 150 */     float cut2 = 0.65F;
/* 151 */     float cut3 = 0.85F;
/* 152 */     if (progress < cut1) {
/* 153 */       progress /= cut1;
/* 154 */       transform = TransformUtils.tween(untouched, transform1, progress, true);
/* 155 */     } else if (progress < cut2) {
/* 156 */       AffineTransform identity = new AffineTransform();
/* 157 */       progress = (progress - cut1) / (cut2 - cut1);
/* 158 */       progress = 3.125F * progress * progress - 2.125F * progress;
/*     */       
/* 160 */       transform = TransformUtils.tween(transform1, identity, progress, true);
/* 161 */     } else if (progress < cut3) {
/* 162 */       AffineTransform identity = new AffineTransform();
/* 163 */       progress = (progress - cut2) / (cut3 - cut2);
/* 164 */       progress = -4.8F * progress * progress + 4.8F * progress;
/*     */       
/* 166 */       transform = TransformUtils.tween(identity, transform2, progress, true);
/*     */     } else {
/* 168 */       AffineTransform identity = new AffineTransform();
/* 169 */       progress = (progress - cut3) / (1.0F - cut3);
/*     */       
/* 171 */       progress = -4.8F * progress * progress + 4.8F * progress;
/*     */       
/* 173 */       transform = TransformUtils.tween(identity, transform3, progress, true);
/*     */     } 
/*     */     
/* 176 */     Transition2DInstruction[] instr = { (Transition2DInstruction)new ImageInstruction(true), (Transition2DInstruction)new ImageInstruction(false, transform, null) };
/*     */ 
/*     */ 
/*     */     
/* 180 */     return instr;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 185 */     if (this.type == 1) {
/* 186 */       return "Toss Right";
/*     */     }
/* 188 */     return "Toss Left";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\TossTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */