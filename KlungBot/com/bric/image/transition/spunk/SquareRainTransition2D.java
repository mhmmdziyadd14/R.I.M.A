/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.TransformUtils;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.RoundRectangle2D;
/*     */ import java.util.Random;
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
/*     */ public class SquareRainTransition2D
/*     */   extends AbstractClippedTransition2D
/*     */ {
/*     */   float[] offset;
/*     */   float[] accel;
/*     */   
/*     */   public SquareRainTransition2D() {
/*  46 */     this(12, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SquareRainTransition2D(int columns, boolean randomize) {
/*  56 */     Random r = new Random();
/*  57 */     this.offset = new float[columns];
/*  58 */     this.accel = new float[columns];
/*  59 */     boolean ok = false;
/*     */     
/*  61 */     long seed = 1196622174915L;
/*  62 */     if (randomize) {
/*  63 */       seed = System.currentTimeMillis();
/*     */     }
/*  65 */     while (!ok) {
/*  66 */       seed++;
/*  67 */       r.setSeed(seed);
/*  68 */       ok = true;
/*  69 */       for (int a = 0; a < columns && ok; a++) {
/*  70 */         float m = a / (columns - 1.0F);
/*  71 */         if (m < 0.5F) {
/*  72 */           m /= 0.5F;
/*     */         } else {
/*  74 */           m = (1.0F - m) / 0.5F;
/*     */         } 
/*  76 */         this.offset[a] = -m;
/*  77 */         this.accel[a] = 10.0F * r.nextFloat();
/*  78 */         if ((this.accel[a] + 1.0F + this.offset[a]) < 1.2D)
/*  79 */           ok = false; 
/*     */       } 
/*  81 */       if (ok) {
/*     */         
/*  83 */         boolean atLeastOneSlowOne = false;
/*  84 */         for (int i = 0; i < columns && !atLeastOneSlowOne; i++) {
/*  85 */           atLeastOneSlowOne = ((this.accel[i] + 1.0F + this.offset[i]) < 1.3D);
/*     */         }
/*  87 */         ok = atLeastOneSlowOne;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Shape[] getShapes(float progress, Dimension size) {
/*  95 */     Vector<Shape> v = new Vector<>();
/*     */     
/*  97 */     float columnWidth = size.width / this.offset.length;
/*  98 */     int rows = (int)((size.height / columnWidth) + 0.5D);
/*  99 */     float rowHeight = size.height / rows;
/* 100 */     for (int a = 0; a < this.offset.length; a++) {
/* 101 */       float x = a * columnWidth;
/* 102 */       float centerX = x + columnWidth / 2.0F;
/* 103 */       float w = (size.width / this.offset.length);
/* 104 */       float y = size.height * (this.offset[a] + progress + progress * progress * this.accel[a]);
/*     */       
/* 106 */       int row = (int)((y - 2.0F * rowHeight) / rowHeight);
/*     */ 
/*     */       
/* 109 */       Rectangle2D rect = new Rectangle2D.Float(x - 1.0F, 0.0F, w + 2.0F, row * rowHeight);
/* 110 */       v.add(rect);
/* 111 */       float centerY = row * rowHeight + rowHeight / 2.0F;
/* 112 */       float f1 = (y - rowHeight * row) / rowHeight;
/*     */       
/* 114 */       float k1 = f1 / 3.0F;
/* 115 */       float k2 = (f1 - 1.0F) / 3.0F;
/* 116 */       float k3 = (f1 - 2.0F) / 3.0F;
/* 117 */       if (k1 < 0.0F) k1 = 0.0F; 
/* 118 */       if (k2 < 0.0F) k2 = 0.0F; 
/* 119 */       if (k3 < 0.0F) k3 = 0.0F; 
/* 120 */       if (k1 > 1.0F) k1 = 1.0F; 
/* 121 */       if (k2 > 1.0F) k2 = 1.0F; 
/* 122 */       if (k3 > 1.0F) k3 = 1.0F;
/*     */ 
/*     */       
/* 125 */       if (k1 > 0.0F) {
/* 126 */         Shape shape = new RoundRectangle2D.Float(centerX - k1 * columnWidth / 2.0F, centerY - k1 * rowHeight / 2.0F, k1 * columnWidth, k1 * rowHeight, columnWidth / 4.0F * (1.0F - k1), rowHeight / 4.0F * (1.0F - k1));
/* 127 */         v.add(shape);
/*     */       } 
/* 129 */       if (k2 > 0.0F) {
/* 130 */         Shape shape = new RoundRectangle2D.Float(centerX - k2 * columnWidth / 2.0F, centerY - k2 * rowHeight / 2.0F + rowHeight, k2 * columnWidth, k2 * rowHeight, columnWidth * (1.0F - k2), rowHeight * (1.0F - k2));
/* 131 */         v.add(shape);
/*     */       } 
/* 133 */       if (k3 > 0.0F) {
/* 134 */         Shape shape = new RoundRectangle2D.Float(centerX - k3 * columnWidth / 2.0F, centerY - k3 * rowHeight / 2.0F + 2.0F * rowHeight, k3 * columnWidth, k3 * rowHeight, columnWidth * (1.0F - k3), rowHeight * (1.0F - k3));
/* 135 */         v.add(shape);
/*     */       } 
/*     */     } 
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
/*     */ 
/*     */     
/* 168 */     Shape[] shapes = v.<Shape>toArray(new Shape[v.size()]);
/*     */ 
/*     */     
/* 171 */     float k = getStrokeWidth(progress) + 1.0F;
/*     */     
/* 173 */     AffineTransform fit = TransformUtils.createAffineTransform(0.0D, 0.0D, size.width, 0.0D, 0.0D, size.height, -k, -k, (size.width + k), -k, -k, (size.height + k));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 181 */     for (int i = 0; i < shapes.length; i++) {
/* 182 */       if (shapes[i] instanceof GeneralPath) {
/* 183 */         ((GeneralPath)shapes[i]).transform(fit);
/*     */       } else {
/* 185 */         shapes[i] = fit.createTransformedShape(shapes[i]);
/*     */       } 
/*     */     } 
/* 188 */     return shapes;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getStrokeWidth(float progress) {
/* 193 */     return 5.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 198 */     return "Square Rain";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\SquareRainTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */