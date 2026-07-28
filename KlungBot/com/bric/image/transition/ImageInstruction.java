/*     */ package com.bric.image.transition;
/*     */ 
/*     */ import com.bric.geom.Clipper;
/*     */ import com.bric.geom.RectangularTransform;
/*     */ import com.bric.geom.ShapeStringUtils;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.Composite;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ImageObserver;
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
/*     */ public class ImageInstruction
/*     */   extends Transition2DInstruction
/*     */ {
/*     */   public boolean isFirstFrame = true;
/*  44 */   public Shape clipping = null;
/*     */ 
/*     */   
/*  47 */   public AffineTransform transform = null;
/*     */ 
/*     */   
/*  50 */   public float opacity = 1.0F;
/*     */   
/*     */   public ImageInstruction(boolean isFirstFrame, float opacity, AffineTransform transform, Shape clipping) {
/*  53 */     this(isFirstFrame, transform, clipping);
/*  54 */     this.opacity = opacity;
/*     */   }
/*     */   
/*     */   public ImageInstruction(boolean isFirstFrame, float opacity) {
/*  58 */     this(isFirstFrame);
/*  59 */     this.opacity = opacity;
/*     */   }
/*     */   
/*     */   public ImageInstruction(boolean isFirstFrame, float opacity, Rectangle2D dest, Dimension frameSize, Shape clipping) {
/*  63 */     this(isFirstFrame, opacity, RectangularTransform.create(new Rectangle2D.Double(0.0D, 0.0D, frameSize.width, frameSize.height), dest), clipping);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ImageInstruction(boolean isFirstFrame) {
/*  72 */     this(isFirstFrame, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  77 */     String clippingString = (this.clipping == null) ? "null" : ShapeStringUtils.toString(this.clipping);
/*  78 */     return "ImageInstruction[ isFirstFrame = " + this.isFirstFrame + ", transform = " + this.transform + ", clipping = " + clippingString + " opacity=" + this.opacity + "]";
/*     */   }
/*     */ 
/*     */   
/*     */   public ImageInstruction(ImageInstruction i) {
/*  83 */     this.clipping = i.clipping;
/*  84 */     this.isFirstFrame = i.isFirstFrame;
/*  85 */     this.transform = i.transform;
/*  86 */     this.opacity = i.opacity;
/*     */   }
/*     */   
/*     */   public ImageInstruction(boolean isFirstFrame, AffineTransform transform, Shape clipping) {
/*  90 */     this.isFirstFrame = isFirstFrame;
/*  91 */     if (transform != null)
/*  92 */       this.transform = new AffineTransform(transform); 
/*  93 */     if (clipping != null) {
/*  94 */       if (clipping instanceof Rectangle) {
/*  95 */         this.clipping = new Rectangle((Rectangle)clipping);
/*  96 */       } else if (clipping instanceof Rectangle2D) {
/*  97 */         Rectangle2D r = new Rectangle2D.Float();
/*  98 */         r.setFrame((Rectangle2D)clipping);
/*  99 */         this.clipping = r;
/*     */       } else {
/* 101 */         this.clipping = new GeneralPath(clipping);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public ImageInstruction(boolean isFirstFrame, Rectangle2D dest, Dimension frameSize, Shape clipping) {
/* 107 */     this(isFirstFrame, RectangularTransform.create(new Rectangle2D.Double(0.0D, 0.0D, frameSize.width, frameSize.height), dest), clipping);
/*     */   }
/*     */ 
/*     */   
/*     */   public void paint(Graphics2D g, BufferedImage frameA, BufferedImage frameB) {
/* 112 */     BufferedImage img = this.isFirstFrame ? frameA : frameB;
/*     */     
/* 114 */     Composite oldComposite = null;
/* 115 */     if (this.opacity != 1.0F) {
/* 116 */       oldComposite = g.getComposite();
/* 117 */       g.setComposite(AlphaComposite.getInstance(3, this.opacity));
/*     */     } 
/*     */     
/* 120 */     Shape oldClipping = null;
/* 121 */     if (this.clipping != null) {
/* 122 */       oldClipping = g.getClip();
/* 123 */       Clipper.clip(g, this.clipping);
/*     */     } 
/*     */     
/* 126 */     g.drawImage(img, this.transform, (ImageObserver)null);
/*     */     
/* 128 */     if (this.clipping != null) {
/* 129 */       g.setClip(oldClipping);
/*     */     }
/* 131 */     if (this.opacity != 1.0F)
/* 132 */       g.setComposite(oldComposite); 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\ImageInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */