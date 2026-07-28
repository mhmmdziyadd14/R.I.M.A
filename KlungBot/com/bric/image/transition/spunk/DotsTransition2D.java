/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.Ellipse2D;
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
/*     */ public class DotsTransition2D
/*     */   extends AbstractClippedTransition2D
/*     */ {
/*  34 */   Ellipse2D[] bubbles = new Ellipse2D[] { 
/*  35 */       make(0.680542285974303D, 0.18889704343632807D, 0.19951594932207756D), 
/*  36 */       make(0.026909280976121663D, 0.6901511430360959D, 0.1906895473555708D), 
/*  37 */       make(0.995D, 0.44D, 0.03D), 
/*  38 */       make(0.29737678359980557D, 0.3283705267534811D, 0.0912077460942279D), 
/*  39 */       make(0.4940590401790048D, 0.02553734599175539D, 0.16311957162512475D), 
/*  40 */       make(0.4349481618226607D, 0.25633754562508043D, 0.1165154509890553D), 
/*  41 */       make(0.9809361717371541D, 0.30209191539494906D, 0.1333244867709235D), 
/*  42 */       make(0.94840475449474D, 0.9709666965127903D, 0.1916617398526077D), 
/*  43 */       make(0.20824174526117323D, 0.6000480763996424D, 0.16450145156027104D), 
/*  44 */       make(0.245D, 0.03D, 0.1264350652695335D), 
/*  45 */       make(0.63129647161756D, 0.7748508554123787D, 0.13777349084037474D), 
/*  46 */       make(0.12D, 0.115D, 0.05665589165107964D), 
/*  47 */       make(0.04541801270979995D, 0.31964490650135213D, 0.19585902380194273D), 
/*  48 */       make(0.568725862339568D, 0.5659780448945103D, 0.10445627774737297D), 
/*  49 */       make(0.565D, 0.38D, 0.10292414822016213D), 
/*  50 */       make(0.9407853703745508D, 0.7099945865685147D, 0.14021507084018198D), 
/*  51 */       make(0.8430323086248085D, 0.5875060184753639D, 0.19975669996812462D), 
/*  52 */       make(0.7908761878447338D, 0.9138289915660384D, 0.14630462931216134D), 
/*  53 */       make(0.7885669941774602D, 0.45314728864907283D, 0.19542608515347965D), 
/*  54 */       make(0.3908460558351622D, 0.4880566885333508D, 0.19958992150674115D), 
/*  55 */       make(0.2543162939332376D, 0.16250491418006419D, 0.13575009482043435D), 
/*  56 */       make(0.3009613498958983D, 0.7949264449270182D, 0.16819513825380095D), 
/*  57 */       make(0.007611521495801465D, 0.015461107683142572D, 0.13790660268586205D), 
/*  58 */       make(0.18134036468339323D, 0.9533112934957934D, 0.19875969340753744D), 
/*  59 */       make(0.43773209673997937D, 0.6987587742834488D, 0.1450244188942148D), 
/*  60 */       make(0.5586433953881341D, 0.9556322415516078D, 0.18462455447167833D), 
/*  61 */       make(0.9464285339584122D, 0.034394073749900445D, 0.21788008758584235D) };
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
/*     */   protected Ellipse2D.Double make(double x, double y, double r) {
/* 105 */     return new Ellipse2D.Double(x - r, y - r, r * 2.0D, r * 2.0D);
/*     */   }
/*     */ 
/*     */   
/*     */   public Shape[] getShapes(float progress, Dimension size) {
/* 110 */     Vector<Shape> v = new Vector<>();
/* 111 */     float domain = 0.9F;
/* 112 */     float span = 1.0F - domain;
/* 113 */     Random random = new Random();
/*     */     
/* 115 */     for (int a = 0; a < this.bubbles.length; a++) {
/* 116 */       float k = a / (this.bubbles.length - 1.0F);
/* 117 */       float base = (float)(Math.sqrt(k) * 0.25D + k * 0.75D) * domain;
/*     */       
/* 119 */       float p = (progress - base) / span;
/* 120 */       if (p > 0.01D) {
/* 121 */         if (p > 1.0F) p = 1.0F;
/*     */ 
/*     */         
/* 124 */         p = (float)(-1.6666666666666186D * p * p + 2.6666666666666203D * p);
/*     */         
/* 126 */         float r = (float)Math.max(this.bubbles[a].getWidth() * size.width / 2.0D, this.bubbles[a]
/* 127 */             .getHeight() * size.height / 2.0D);
/* 128 */         r *= p;
/*     */         
/* 130 */         random.setSeed((10 * a));
/* 131 */         float dx = (1.0F - p) * (2.0F * random.nextFloat() - 1.0F);
/* 132 */         float dy = (1.0F - p) * (2.0F * random.nextFloat() - 1.0F);
/* 133 */         dx = (1.0F - p) * (float)(r * Math.cos((random.nextFloat() * 10.0F + 8.0F * (1.0F - p))));
/* 134 */         dy = (1.0F - p) * (float)(r * Math.sin((random.nextFloat() * 10.0F + 8.0F * (1.0F - p))));
/*     */         
/* 136 */         v.add(new Ellipse2D.Double(this.bubbles[a]
/* 137 */               .getCenterX() * size.width - r + dx, this.bubbles[a]
/* 138 */               .getCenterY() * size.height - r + dy, (2.0F * r), (2.0F * r)));
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     return v.<Shape>toArray(new Shape[v.size()]);
/*     */   }
/*     */ 
/*     */   
/*     */   public float getStrokeWidth(float progress) {
/* 150 */     return (float)(10.0D * (1.0D - Math.pow(progress, 5.0D)));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 155 */     return "Dots";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\DotsTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */