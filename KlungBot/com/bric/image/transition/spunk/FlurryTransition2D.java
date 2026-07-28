/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.ShapeBounds;
/*     */ import com.bric.geom.TransformUtils;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
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
/*     */ public class FlurryTransition2D
/*     */   extends Transition2D
/*     */ {
/*  46 */   int type = 8;
/*     */   
/*  48 */   Comparator<ImageInstruction> comparator = new Comparator<ImageInstruction>()
/*     */     {
/*     */       public int compare(ImageInstruction i1, ImageInstruction i2) {
/*  51 */         if (i1.isFirstFrame && !i2.isFirstFrame)
/*  52 */           return 1; 
/*  53 */         if (i2.isFirstFrame && !i1.isFirstFrame) {
/*  54 */           return -1;
/*     */         }
/*  56 */         Rectangle2D r1 = ShapeBounds.getBounds(i1.clipping);
/*  57 */         Rectangle2D r2 = ShapeBounds.getBounds(i2.clipping);
/*  58 */         double area1 = r1.getWidth() * r1.getHeight();
/*  59 */         double area2 = r2.getWidth() * r2.getHeight();
/*  60 */         if (area1 < area2) {
/*  61 */           return -1;
/*     */         }
/*  63 */         return 1;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FlurryTransition2D() {
/*  71 */     this(8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FlurryTransition2D(int type) {
/*  79 */     if (type != 8 && type != 7) {
/*  80 */       throw new IllegalArgumentException("This transition must use OUT or IN.");
/*     */     }
/*  82 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/*  88 */     if (this.type == 7) {
/*  89 */       progress = 1.0F - progress;
/*     */     }
/*     */     
/*  92 */     progress *= 0.78F;
/*     */     
/*  94 */     Vector<Rectangle2D> v1 = new Vector<>();
/*     */     
/*  96 */     float yHeight = 20.0F;
/*  97 */     float xWidth = 20.0F; float y;
/*  98 */     for (y = 0.0F; y < 200.0F; y += yHeight) {
/*  99 */       float x; for (x = 0.0F; x < 200.0F; x += xWidth) {
/* 100 */         Rectangle2D r = new Rectangle2D.Double(x, y, xWidth, yHeight);
/* 101 */         v1.add(r);
/*     */       } 
/*     */     } 
/*     */     
/* 105 */     progress = (float)Math.pow(progress, 1.0D);
/* 106 */     ImageInstruction[] instr = new ImageInstruction[v1.size() + 1];
/* 107 */     instr[0] = new ImageInstruction(false);
/* 108 */     Random random = new Random(); int a;
/* 109 */     for (a = 0; a < v1.size(); a++) {
/* 110 */       Rectangle2D r = v1.get(a);
/* 111 */       random.setSeed(a);
/* 112 */       Shape clipping = r;
/* 113 */       Point2D center = new Point2D.Double(r.getCenterX() - 100.0D, r.getCenterY() - 100.0D);
/* 114 */       float k = (float)(Math.sqrt(center.getX() * center.getX() + center.getY() * center.getY()) / Math.sqrt(20000.0D));
/* 115 */       k = (1.0F - progress) * k + progress;
/* 116 */       float scaleProgress = (float)Math.pow((2.0F * progress * k), 0.02D + (4.0F * random.nextFloat()));
/* 117 */       AffineTransform transform = new AffineTransform();
/* 118 */       transform.translate(100.0D, 100.0D);
/* 119 */       transform.scale((1.0F + 2.0F * scaleProgress), (1.0F + 2.0F * scaleProgress));
/* 120 */       transform.rotate(progress);
/* 121 */       transform.translate(-100.0D, -100.0D);
/* 122 */       Point2D p1 = new Point2D.Double(r.getCenterX(), r.getCenterY());
/* 123 */       Point2D p2 = new Point2D.Double();
/* 124 */       Point2D p3 = new Point2D.Double();
/* 125 */       transform.transform(p1, p2);
/*     */       
/* 127 */       double dx = -(p1.getX() - p2.getX());
/* 128 */       double dy = -(p1.getY() - p2.getY());
/* 129 */       transform.setToIdentity();
/* 130 */       transform.concatenate(TransformUtils.createAffineTransform(0.0D, 0.0D, 0.0D, 200.0D, 200.0D, 0.0D, 0.0D, 0.0D, 0.0D, size.height, size.width, 0.0D));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 138 */       transform.scale(1.0D + Math.abs(dx) / 15.0D, 1.0D + Math.abs(dy) / 15.0D);
/* 139 */       transform.rotate(progress);
/* 140 */       transform.translate(dx, dy);
/*     */       
/* 142 */       clipping = transform.createTransformedShape(clipping);
/*     */       
/* 144 */       p1.setLocation(r.getX(), r.getY());
/* 145 */       p2.setLocation(r.getX() + r.getWidth(), r.getY());
/* 146 */       p3.setLocation(r.getX(), r.getY() + r.getHeight());
/* 147 */       transform.transform(p1, p1);
/* 148 */       transform.transform(p2, p2);
/* 149 */       transform.transform(p3, p3);
/*     */       
/* 151 */       transform = TransformUtils.createAffineTransform(r
/* 152 */           .getX() * size.width / 200.0D, r.getY() * size.height / 200.0D, (r
/* 153 */           .getX() + r.getWidth()) * size.width / 200.0D, r.getY() * size.height / 200.0D, r
/* 154 */           .getX() * size.width / 200.0D, (r.getY() + r.getHeight()) * size.height / 200.0D, p1
/*     */           
/* 156 */           .getX(), p1.getY(), p2
/* 157 */           .getX(), p2.getY(), p3
/* 158 */           .getX(), p3.getY());
/*     */ 
/*     */       
/* 161 */       instr[a + 1] = new ImageInstruction(true, transform, clipping);
/*     */     } 
/* 163 */     Arrays.sort(instr, this.comparator);
/* 164 */     if (this.type == 7) {
/* 165 */       for (a = 0; a < instr.length; a++) {
/* 166 */         (instr[a]).isFirstFrame = !(instr[a]).isFirstFrame;
/*     */       }
/*     */     }
/* 169 */     return (Transition2DInstruction[])instr;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 174 */     if (this.type == 8) {
/* 175 */       return "Flurry Out";
/*     */     }
/* 177 */     return "Flurry In";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\FlurryTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */