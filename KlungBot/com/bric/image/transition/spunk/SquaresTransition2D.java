/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.RectangularTransform;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Random;
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
/*     */ public class SquaresTransition2D
/*     */   extends Transition2D
/*     */ {
/*  40 */   Comparator<ImageInstruction> comparator = new Comparator<ImageInstruction>()
/*     */     {
/*     */       public int compare(ImageInstruction i1, ImageInstruction i2) {
/*  43 */         if (i1.isFirstFrame && !i2.isFirstFrame)
/*  44 */           return 1; 
/*  45 */         if (i2.isFirstFrame && !i1.isFirstFrame) {
/*  46 */           return -1;
/*     */         }
/*  48 */         double d1 = i1.transform.getDeterminant();
/*  49 */         double d2 = i2.transform.getDeterminant();
/*  50 */         if (d1 < d2) {
/*  51 */           return -1;
/*     */         }
/*  53 */         return 1;
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   float[][] accels;
/*     */   float[][] delays;
/*  60 */   float progressMax = 1.0F;
/*     */   
/*     */   public SquaresTransition2D() {
/*  63 */     this(10, 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SquaresTransition2D(int columns, int rows) {
/*  72 */     this.delays = new float[columns][rows];
/*  73 */     this.accels = new float[columns][rows];
/*  74 */     Random random = new Random();
/*  75 */     for (int x = 0; x < columns; x++) {
/*  76 */       for (int y = 0; y < rows; y++) {
/*  77 */         float offset = ((y - rows / 2) * (y - rows / 2) + (x - columns / 2) * (x - columns / 2));
/*  78 */         offset /= (rows * rows / 4 + columns * columns / 4);
/*     */         
/*  80 */         this.delays[x][y] = 0.3F * offset + 0.1F * random.nextFloat();
/*  81 */         this.accels[x][y] = 0.5F + 0.8F * random.nextFloat();
/*     */       } 
/*     */     } 
/*  84 */     this.progressMax = findMax(0.0F, 2.0F);
/*     */   }
/*     */   
/*     */   protected float findMax(float t0, float t1) {
/*  88 */     if ((t1 - t0) < 1.0E-4D) return Math.max(t0, t1);
/*     */     
/*  90 */     Rectangle2D r = new Rectangle2D.Float(0.0F, 0.0F, 100.0F, 100.0F);
/*  91 */     float mid = t0 / 2.0F + t1 / 2.0F;
/*  92 */     Transition2DInstruction[] instrA = getInstructions(t0, new Dimension(100, 100));
/*  93 */     Transition2DInstruction[] instrB = getInstructions(mid, new Dimension(100, 100));
/*  94 */     Transition2DInstruction[] instrC = getInstructions(t1, new Dimension(100, 100));
/*  95 */     boolean validA = false;
/*  96 */     boolean validB = false;
/*  97 */     boolean validC = false;
/*  98 */     for (int a = 1; a < instrA.length; a++) {
/*  99 */       if (r.intersects((Rectangle2D)((ImageInstruction)instrA[a]).clipping)) {
/* 100 */         validA = true;
/*     */       }
/* 102 */       if (r.intersects((Rectangle2D)((ImageInstruction)instrB[a]).clipping)) {
/* 103 */         validB = true;
/*     */       }
/* 105 */       if (r.intersects((Rectangle2D)((ImageInstruction)instrC[a]).clipping)) {
/* 106 */         validC = true;
/*     */       }
/*     */     } 
/* 109 */     if (validA && validC)
/* 110 */       return Math.max(t0, t1); 
/* 111 */     if (validA) {
/* 112 */       if (validB) {
/* 113 */         return findMax(mid, t1);
/*     */       }
/* 115 */       return findMax(t0, mid);
/*     */     } 
/*     */     
/* 118 */     throw new RuntimeException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 125 */     progress *= this.progressMax;
/*     */     
/* 127 */     int columns = this.accels.length;
/* 128 */     int rows = (this.accels[0]).length;
/* 129 */     ImageInstruction[] instr = new ImageInstruction[columns * rows + 1];
/* 130 */     instr[0] = new ImageInstruction(false);
/* 131 */     float columnWidth = size.width / columns;
/* 132 */     float rowHeight = size.height / rows;
/* 133 */     int ctr = 0;
/* 134 */     for (int x = 0; x < columns; x++) {
/* 135 */       for (int y = 0; y < rows; y++) {
/* 136 */         float delay = this.delays[x][y];
/* 137 */         float accel = this.accels[x][y];
/* 138 */         float t = progress - delay;
/* 139 */         if (t < 0.0F)
/* 140 */           t = 0.0F; 
/* 141 */         float z = 1.0F + 120.0F * accel * t * t;
/* 142 */         Rectangle2D r = new Rectangle2D.Float(x * columnWidth, y * rowHeight, columnWidth, rowHeight);
/* 143 */         RectangularTransform transform = new RectangularTransform();
/*     */         
/* 145 */         float centerX = (size.width / 2);
/* 146 */         float centerY = (size.height / 2);
/*     */         
/* 148 */         transform.translate(centerX, centerY);
/* 149 */         transform.scale(z, z);
/* 150 */         double dx = centerX - r.getCenterX();
/* 151 */         double dy = centerY - r.getCenterY();
/* 152 */         transform.translate(-centerX - (10.0F * t) * dx * progress, -centerY - (10.0F * t) * dy * progress);
/*     */         
/* 154 */         Rectangle2D clip = transform.transform(r);
/* 155 */         instr[1 + ctr++] = new ImageInstruction(true, transform.createAffineTransform(), clip);
/*     */       } 
/*     */     } 
/* 158 */     Arrays.sort(instr, this.comparator);
/* 159 */     return (Transition2DInstruction[])instr;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 164 */     return "Squares";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\SquaresTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */