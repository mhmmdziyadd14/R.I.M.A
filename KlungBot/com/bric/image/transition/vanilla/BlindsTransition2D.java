/*     */ package com.bric.image.transition.vanilla;
/*     */ 
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
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
/*     */ 
/*     */ 
/*     */ public class BlindsTransition2D
/*     */   extends Transition2D
/*     */ {
/*     */   int type;
/*     */   int blinds;
/*     */   
/*     */   public BlindsTransition2D() {
/*  44 */     this(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlindsTransition2D(int type) {
/*  52 */     this(type, 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlindsTransition2D(int type, int numberOfBlinds) {
/*  61 */     if (type != 2 && type != 1 && type != 3 && type != 4) {
/*  62 */       throw new IllegalArgumentException("The type must be LEFT, RIGHT, UP or DOWN");
/*     */     }
/*  64 */     if (numberOfBlinds < 4)
/*  65 */       throw new IllegalArgumentException("The number of blinds (" + numberOfBlinds + ") must be greater than 3."); 
/*  66 */     this.type = type;
/*  67 */     this.blinds = numberOfBlinds;
/*     */   }
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*     */     float k;
/*  73 */     Vector<Transition2DInstruction> v = new Vector<>();
/*  74 */     v.add(new ImageInstruction((this.type == 1 || this.type == 4)));
/*     */     
/*  76 */     if (this.type == 2 || this.type == 1) {
/*  77 */       k = size.width / this.blinds;
/*     */     } else {
/*  79 */       k = size.height / this.blinds;
/*     */     } 
/*  81 */     for (int a = 0; a < this.blinds; a++) {
/*     */       Rectangle2D r;
/*  83 */       if (this.type == 4) {
/*  84 */         r = new Rectangle2D.Float(0.0F, a * k, size.width, progress * k);
/*  85 */       } else if (this.type == 3) {
/*  86 */         r = new Rectangle2D.Float(0.0F, a * k, size.width, k - progress * k);
/*  87 */       } else if (this.type == 1) {
/*  88 */         r = new Rectangle2D.Float(a * k, 0.0F, progress * k, size.height);
/*     */       } else {
/*  90 */         r = new Rectangle2D.Float(a * k, 0.0F, k - progress * k, size.height);
/*     */       } 
/*  92 */       v.add(new ImageInstruction((this.type == 3 || this.type == 2), null, r));
/*     */     } 
/*  94 */     return v.<Transition2DInstruction>toArray(new Transition2DInstruction[v.size()]);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  99 */     if (this.type == 2)
/* 100 */       return "Blinds Left"; 
/* 101 */     if (this.type == 1)
/* 102 */       return "Blinds Right"; 
/* 103 */     if (this.type == 3) {
/* 104 */       return "Blinds Up";
/*     */     }
/* 106 */     return "Blinds Down";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\BlindsTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */