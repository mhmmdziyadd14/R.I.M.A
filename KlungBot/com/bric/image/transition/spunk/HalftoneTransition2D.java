/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.RectangularShape;
/*     */ import java.awt.geom.RoundRectangle2D;
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
/*     */ public class HalftoneTransition2D
/*     */   extends Transition2D
/*     */ {
/*  41 */   int type = 8;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HalftoneTransition2D() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public HalftoneTransition2D(int type) {
/*  51 */     if (type == 7 || type == 8) {
/*  52 */       this.type = type;
/*     */     } else {
/*  54 */       throw new IllegalArgumentException("The type must be IN or OUT");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*  61 */     if (this.type == 8) {
/*  62 */       progress = 1.0F - progress;
/*     */     }
/*  64 */     progress = (float)Math.pow(progress, 0.5D);
/*     */     
/*  66 */     float ySize = size.height * 0.05F;
/*  67 */     float xSize = size.width * 0.05F;
/*  68 */     Vector<RectangularShape> v = new Vector<>();
/*  69 */     float angleProgress = (float)Math.pow((1.0F - Math.min(progress, 1.0F)), 0.5D);
/*     */     
/*  71 */     float progressZ = 1.3F * progress;
/*  72 */     double w = (xSize * progressZ);
/*  73 */     double h = (ySize * progressZ);
/*  74 */     float min = (float)Math.min(w, h); float y;
/*  75 */     for (y = 0.0F; y < size.height; y += ySize) {
/*  76 */       float x; for (x = 0.0F; x < size.width; x += xSize) {
/*  77 */         v.add(new RoundRectangle2D.Double((x + xSize / 2.0F) - w / 2.0D, (y + ySize / 2.0F) - h / 2.0D, w * progress + ((1.0F - progress) * min), h * progress + ((1.0F - progress) * min), w * angleProgress * progress + ((1.0F - progress) * min * angleProgress), h * angleProgress * progress + ((1.0F - progress) * min * angleProgress)));
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     ImageInstruction[] instr = new ImageInstruction[v.size() + 1];
/*  85 */     instr[0] = new ImageInstruction(false); int a;
/*  86 */     for (a = 0; a < v.size(); a++) {
/*  87 */       float progress2 = progress;
/*  88 */       RectangularShape r = v.get(a);
/*  89 */       Point2D p1 = new Point2D.Double(r.getCenterX(), r.getCenterY());
/*  90 */       Point2D p2 = new Point2D.Double(r.getCenterX(), r.getCenterY());
/*  91 */       AffineTransform transform = new AffineTransform();
/*  92 */       transform.translate(r.getCenterX(), r.getCenterY());
/*  93 */       transform.scale((30.0F * (1.0F - progress) + 1.0F), (30.0F * (1.0F - progress) + 1.0F));
/*  94 */       transform.translate(-r.getCenterX(), -r.getCenterY());
/*     */       
/*  96 */       transform.rotate(0.3D * (1.0F - progress2), (size.width / 3), (size.height / 2));
/*     */       
/*  98 */       transform.transform(p1, p2);
/*  99 */       transform.setToTranslation(p1.getX() - p2.getX(), p1.getY() - p2.getY());
/* 100 */       Shape shape = transform.createTransformedShape(r);
/* 101 */       instr[a + 1] = new ImageInstruction(true, transform, shape);
/*     */     } 
/* 103 */     if (this.type == 7) {
/* 104 */       for (a = 0; a < instr.length; a++) {
/* 105 */         (instr[a]).isFirstFrame = !(instr[a]).isFirstFrame;
/*     */       }
/*     */     }
/*     */     
/* 109 */     return (Transition2DInstruction[])instr;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 114 */     if (this.type == 7) {
/* 115 */       return "Halftone In";
/*     */     }
/* 117 */     return "Halftone Out";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\HalftoneTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */