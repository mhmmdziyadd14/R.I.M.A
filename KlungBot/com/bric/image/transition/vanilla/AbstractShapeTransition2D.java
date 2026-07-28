/*     */ package com.bric.image.transition.vanilla;
/*     */ 
/*     */ import com.bric.geom.ShapeBounds;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Area;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.util.Hashtable;
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
/*     */ public abstract class AbstractShapeTransition2D
/*     */   extends Transition2D
/*     */ {
/*     */   int type;
/*     */   
/*     */   public AbstractShapeTransition2D() {
/*  50 */     this(8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractShapeTransition2D(int type) {
/*  60 */     if (type != 7 && type != 8)
/*  61 */       throw new IllegalArgumentException("Type must be IN or OUT."); 
/*  62 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  67 */   Hashtable<Dimension, Number> multipliers = new Hashtable<>();
/*     */ 
/*     */   
/*     */   protected float calculateMultiplier(Dimension size) {
/*  71 */     Shape shape = getShape();
/*  72 */     Area base = new Area(shape);
/*  73 */     AffineTransform transform = new AffineTransform();
/*  74 */     Rectangle2D r = ShapeBounds.getBounds(base);
/*  75 */     transform.translate((size.width / 2.0F) - r.getCenterX(), (size.height / 2.0F) - r.getCenterY());
/*  76 */     base.transform(transform);
/*  77 */     r = ShapeBounds.getBounds(base, r);
/*  78 */     float min = 0.0F;
/*  79 */     float max = 1.0F;
/*  80 */     Rectangle2D boundsRect = new Rectangle2D.Float(0.0F, 0.0F, size.width, size.height);
/*  81 */     while (!isOK(base, r, boundsRect, max)) {
/*  82 */       min = max;
/*  83 */       max = (float)(max * 1.2D);
/*     */     } 
/*  85 */     float f = calculateMultiplier(base, r, boundsRect, min, max);
/*  86 */     isOK(base, r, boundsRect, f);
/*  87 */     return f;
/*     */   }
/*     */   public abstract Shape getShape();
/*     */   
/*     */   private float calculateMultiplier(Area shape, Rectangle2D shapeBounds, Rectangle2D bounds, float min, float max) {
/*  92 */     if ((max - min) < 0.5D) {
/*  93 */       return max;
/*     */     }
/*  95 */     float middle = (min + max) / 2.0F;
/*  96 */     if (isOK(shape, shapeBounds, bounds, middle)) {
/*  97 */       return calculateMultiplier(shape, shapeBounds, bounds, min, middle);
/*     */     }
/*  99 */     return calculateMultiplier(shape, shapeBounds, bounds, middle, max);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isOK(Area shape, Rectangle2D shapeBounds, Rectangle2D bounds, float ratio) {
/* 105 */     Area area = new Area(shape);
/* 106 */     area.transform(AffineTransform.getScaleInstance(ratio, ratio));
/* 107 */     Rectangle2D r = ShapeBounds.getBounds(area);
/* 108 */     area.transform(AffineTransform.getTranslateInstance(-r.getCenterX() + bounds.getCenterX(), 
/* 109 */           -r.getCenterY() + bounds.getCenterY()));
/*     */     
/* 111 */     Area boundsArea = new Area(bounds);
/* 112 */     boundsArea.subtract(area);
/* 113 */     return boundsArea.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 118 */     Number multiplier = this.multipliers.get(size);
/* 119 */     if (multiplier == null) {
/* 120 */       multiplier = new Float(calculateMultiplier(size));
/* 121 */       this.multipliers.put(size, multiplier);
/*     */     } 
/*     */     
/* 124 */     if (this.type == 7) {
/* 125 */       progress = 1.0F - progress;
/*     */     }
/*     */     
/* 128 */     Shape clipping = getShape();
/* 129 */     Rectangle2D r = ShapeBounds.getBounds(clipping);
/*     */     
/* 131 */     AffineTransform transform = new AffineTransform();
/*     */     
/* 133 */     transform.setToIdentity();
/*     */ 
/*     */     
/* 136 */     transform.translate((size.width / 2), (size.height / 2));
/* 137 */     transform.scale((progress * multiplier.floatValue()), (progress * multiplier.floatValue()));
/* 138 */     transform.translate((-size.width / 2), (-size.height / 2));
/*     */     
/* 140 */     transform.translate(-r.getCenterX() + (size.width / 2.0F), -r.getCenterY() + (size.height / 2.0F));
/*     */     
/* 142 */     clipping = transform.createTransformedShape(clipping);
/*     */     
/* 144 */     return new Transition2DInstruction[] { (Transition2DInstruction)new ImageInstruction((this.type == 8)), (Transition2DInstruction)new ImageInstruction((this.type != 8), null, clipping) };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String getShapeName();
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 154 */     if (this.type == 7) {
/* 155 */       return getShapeName() + " In";
/*     */     }
/* 157 */     return getShapeName() + " Out";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\vanilla\AbstractShapeTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */