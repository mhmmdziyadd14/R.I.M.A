/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.Clipper;
/*     */ import com.bric.geom.MeasuredShape;
/*     */ import com.bric.geom.RectangularTransform;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Point2D;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FunkyWipeTransition2D
/*     */   extends Transition2D
/*     */ {
/*  46 */   private static final GeneralPath pathCyclic = createPathCyclic();
/*  47 */   private static final MeasuredShape measuredPathCyclic = new MeasuredShape(pathCyclic);
/*  48 */   private static final GeneralPath pathAcross = createPathAcross(); boolean circular;
/*  49 */   private static final MeasuredShape measuredPathAcross = new MeasuredShape(pathAcross);
/*     */   
/*     */   private static GeneralPath createPathCyclic() {
/*  52 */     GeneralPath p = new GeneralPath();
/*  53 */     p.moveTo(99.936F, 51.019F);
/*  54 */     p.curveTo(99.936F, 51.019F, 78.316F, 86.931F, 51.019F, 89.745F);
/*  55 */     p.curveTo(23.721F, 92.559F, -2.012F, 75.843F, 11.082F, 61.21F);
/*  56 */     p.curveTo(4.178F, 46.576F, 34.931F, 39.565F, 62.229F, 36.751F);
/*  57 */     p.curveTo(89.526F, 33.937F, 99.936F, 51.019F, 99.936F, 51.019F);
/*  58 */     return p;
/*     */   }
/*     */   private static GeneralPath createPathAcross() {
/*  61 */     GeneralPath p = new GeneralPath();
/*  62 */     p.moveTo(99.936F, 21.019F);
/*  63 */     p.curveTo(99.936F, 51.019F, 78.316F, 86.931F, 51.019F, 89.745F);
/*  64 */     p.curveTo(23.721F, 92.559F, -2.012F, 75.843F, 0.0F, 61.21F);
/*  65 */     return p;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public FunkyWipeTransition2D(boolean fullCircle) {
/*  71 */     this.circular = fullCircle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*  78 */     Rectangle2D.Float frameRect = new Rectangle2D.Float(0.0F, 0.0F, size.width, size.height);
/*  79 */     Point2D p = new Point2D.Double();
/*  80 */     MeasuredShape path = this.circular ? measuredPathCyclic : measuredPathAcross;
/*     */     
/*  82 */     path.getPoint(progress * path.getOriginalDistance(), p);
/*     */     
/*  84 */     int m = this.circular ? 1 : 2;
/*  85 */     double angle = 1.5707963267948966D + m * Math.PI * progress;
/*     */     
/*  87 */     float k = 10000.0F;
/*  88 */     GeneralPath clip = new GeneralPath();
/*  89 */     clip.moveTo((float)p.getX(), (float)p.getY());
/*  90 */     clip.lineTo((float)(p.getX() + k * Math.cos(angle)), (float)(p.getY() + k * Math.sin(angle)));
/*  91 */     clip.lineTo((float)(p.getX() + k * Math.cos(angle) + k * Math.cos(angle - 1.5707963267948966D)), 
/*  92 */         (float)(p.getY() + k * Math.sin(angle) + k * Math.sin(angle - 1.5707963267948966D)));
/*  93 */     clip.lineTo((float)(p.getX() - 100.0D * Math.cos(angle) + k * Math.cos(angle - 1.5707963267948966D)), 
/*  94 */         (float)(p.getY() - k * Math.sin(angle) + k * Math.sin(angle - 1.5707963267948966D)));
/*  95 */     clip.lineTo((float)(p.getX() - k * Math.cos(angle)), 
/*  96 */         (float)(p.getY() - k * Math.sin(angle)));
/*  97 */     clip.closePath();
/*     */     
/*  99 */     AffineTransform map = RectangularTransform.create(new Rectangle2D.Float(0.0F, 0.0F, 100.0F, 100.0F), frameRect);
/*     */ 
/*     */ 
/*     */     
/* 103 */     clip.transform(map);
/*     */     
/* 105 */     clip = Clipper.clipToRect(clip, frameRect);
/*     */     
/* 107 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction(true, 1.0F, frameRect, size, null), (Transition2DInstruction)new ImageInstruction(false, 1.0F, frameRect, size, clip) };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 115 */     String s = this.circular ? " Circular" : " Across";
/* 116 */     return "Funky Wipe " + s;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\FunkyWipeTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */