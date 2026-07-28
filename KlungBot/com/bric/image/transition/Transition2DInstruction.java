/*     */ package com.bric.image.transition;
/*     */ 
/*     */ import com.bric.geom.EmptyPathException;
/*     */ import com.bric.geom.ShapeBounds;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.Area;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
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
/*     */ public abstract class Transition2DInstruction
/*     */ {
/*     */   public abstract void paint(Graphics2D paramGraphics2D, BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2);
/*     */   
/*     */   public static Transition2DInstruction[] filterVisibleInstructions(Transition2DInstruction[] instr, Dimension frameSize) {
/*  59 */     Vector<Transition2DInstruction> v = new Vector<>();
/*     */     
/*  61 */     Rectangle2D movieRect = new Rectangle(0, 0, frameSize.width, frameSize.height);
/*  62 */     Area movieArea = new Area(movieRect);
/*  63 */     for (int a = 0; a < instr.length; a++) {
/*  64 */       Transition2DInstruction i = instr[a];
/*     */ 
/*     */       
/*  67 */       if (i instanceof ShapeInstruction) {
/*  68 */         ShapeInstruction i2 = (ShapeInstruction)i;
/*  69 */         if ((i2.fillColor == null || i2.fillColor.getAlpha() < 5) && (i2.strokeColor == null || i2.strokeWidth == 0.0F || i2.strokeColor
/*  70 */           .getAlpha() < 5))
/*     */         {
/*  72 */           i = null;
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*  77 */       if (i instanceof ImageInstruction) {
/*  78 */         ImageInstruction i2 = (ImageInstruction)i;
/*  79 */         if (i2.opacity < 0.05F)
/*     */         {
/*  81 */           i = null;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  90 */       Shape instructionShape = null;
/*  91 */       if (i instanceof ImageInstruction) {
/*  92 */         ImageInstruction i2 = (ImageInstruction)i;
/*  93 */         instructionShape = i2.clipping;
/*     */ 
/*     */ 
/*     */         
/*  97 */         if (instructionShape == null) {
/*  98 */           v.add(i);
/*     */           break;
/*     */         } 
/* 101 */       } else if (i instanceof ShapeInstruction) {
/* 102 */         ShapeInstruction i2 = (ShapeInstruction)i;
/* 103 */         instructionShape = i2.shape;
/*     */       } 
/* 105 */       if (instructionShape != null) {
/*     */         try {
/* 107 */           Rectangle2D instructionRect = ShapeBounds.getBounds(instructionShape);
/* 108 */           if (movieRect.contains(instructionRect)) {
/*     */             
/* 110 */             v.add(i);
/* 111 */           } else if (movieRect.intersects(instructionRect)) {
/*     */ 
/*     */ 
/*     */             
/* 115 */             Area instructionArea = new Area(instructionShape);
/* 116 */             instructionArea.intersect(movieArea);
/* 117 */             if (!instructionArea.isEmpty()) {
/* 118 */               v.add(i);
/*     */             }
/*     */           } 
/* 121 */         } catch (EmptyPathException e) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 127 */     return v.<Transition2DInstruction>toArray(new Transition2DInstruction[v.size()]);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\Transition2DInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */