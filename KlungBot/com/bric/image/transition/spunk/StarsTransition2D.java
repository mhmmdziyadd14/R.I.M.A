/*     */ package com.bric.image.transition.spunk;
/*     */ 
/*     */ import com.bric.geom.RectangularTransform;
/*     */ import com.bric.geom.ShapeBounds;
/*     */ import com.bric.geom.ShapeStringUtils;
/*     */ import com.bric.geom.ShapeUtils;
/*     */ import com.bric.geom.TransformUtils;
/*     */ import com.bric.image.transition.ImageInstruction;
/*     */ import com.bric.image.transition.Transition2DInstruction;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Shape;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Point2D;
/*     */ import java.awt.geom.Rectangle2D;
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
/*     */ public class StarsTransition2D
/*     */   extends AbstractClippedTransition2D
/*     */ {
/*  45 */   static GeneralPath[] star = new GeneralPath[] {
/*  46 */       createStar(1.5F), 
/*  47 */       createStar(1.6F), 
/*  48 */       createStar(1.7F), 
/*  49 */       createStar(1.8F), 
/*  50 */       createStar(1.9F), 
/*  51 */       createStar(2.0F), 
/*  52 */       createStar(2.1F), 
/*  53 */       createStar(2.2F), 
/*  54 */       createStar(2.3F)
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static GeneralPath createStar(float r2) {
/*  65 */     GeneralPath p = new GeneralPath();
/*  66 */     double angle = 0.0D;
/*  67 */     double k = 0.6283185307179586D;
/*  68 */     p.moveTo((float)Math.cos(angle), (float)Math.sin(angle));
/*  69 */     for (int a = 0; a < 5; a++) {
/*  70 */       p.lineTo((float)(r2 * Math.cos(angle + k)), (float)(r2 * Math.sin(angle + k)));
/*  71 */       angle += 1.2566370614359172D;
/*  72 */       p.lineTo((float)Math.cos(angle), (float)Math.sin(angle));
/*     */     } 
/*  74 */     p.closePath();
/*  75 */     return p;
/*     */   }
/*     */   
/*  78 */   int type = 1;
/*     */   
/*     */   GeneralPath[] paths;
/*     */ 
/*     */   
/*     */   public StarsTransition2D() {
/*  84 */     this(1);
/*     */   }
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
/*     */   public String toString() {
/* 100 */     if (this.type == 1) {
/* 101 */       return "Stars Right";
/*     */     }
/* 103 */     return "Stars Left";
/*     */   }
/*     */ 
/*     */   
/*     */   protected void fit(GeneralPath p, float length, float centerX, float centerY, GeneralPath path, Dimension size, float progress) {
/* 108 */     Rectangle2D r = p.getBounds2D();
/* 109 */     AffineTransform t = new AffineTransform();
/* 110 */     t.translate(-r.getX() - r.getWidth() / 2.0D, -r.getY() - r.getHeight() / 2.0D);
/* 111 */     t.rotate(((1.0F - progress) * 1.0F));
/* 112 */     double scaleProgress = Math.pow(progress, 3.0D) * 0.75D;
/* 113 */     t.scale(length / r.getWidth() * (0.02D + 1.8D * scaleProgress), length / r.getWidth() * (0.02D + 1.8D * scaleProgress));
/* 114 */     p.transform(t);
/*     */     
/* 116 */     if (progress > 1.0F) progress = 1.0F; 
/* 117 */     if (progress < 0.0F) progress = 0.0F; 
/* 118 */     Point2D endPoint = ShapeUtils.getPoint(path, 1.0F);
/* 119 */     Point2D startPoint = ShapeUtils.getPoint(path, progress);
/* 120 */     Rectangle2D pathBounds = ShapeBounds.getBounds(path);
/* 121 */     AffineTransform pathTransform = RectangularTransform.create(pathBounds, new Rectangle2D.Float(0.0F, 0.0F, (size.width + 100), size.height));
/*     */ 
/*     */ 
/*     */     
/* 125 */     pathTransform.transform(endPoint, endPoint);
/* 126 */     pathTransform.transform(startPoint, startPoint);
/* 127 */     r = p.getBounds();
/* 128 */     t.setToTranslation(-r.getCenterX() + centerX - endPoint.getX() + startPoint.getX(), 
/* 129 */         -r.getCenterY() + centerY - endPoint.getY() + startPoint.getY());
/*     */     
/* 131 */     p.transform(t);
/*     */   }
/*     */   public StarsTransition2D(int type) {
/* 134 */     this
/*     */ 
/*     */       
/* 137 */       .paths = new GeneralPath[] { ShapeStringUtils.createGeneralPath("m 82.604 6.405 c 81.496 6.405 58.748 5.967 57.234 5.937 c 48.657 5.767 39.783 5.605 30.4 11.819 c 19.367 19.125 9.915 39.783 23.713 50.988 c 35.754 60.766 50.748 54.184 53.807 47.734 c 56.105 42.887 49.464 38.223 45.159 38.223"), ShapeStringUtils.createGeneralPath("m 130.936 47.089 c 130.636 46.6 113.679 45.149 103.386 45.364 c 94.251 45.555 88.013 49.832 82.977 54.875 c 75.353 62.51 70.458 72.743 73.292 82.281 c 76.126 91.818 93.239 89.414 93.239 89.414 c 93.239 89.414 101.796 85.728 100.734 78.276 c 99.683 70.903 96.561 71.393 93.124 71.393 c 84.366 71.393 85.661 83.327 94.277 78.651"), ShapeStringUtils.createGeneralPath("m 124.379 23.124 c 124.044 24.216 107.26 20.206 97.997 21.225 c 88.734 22.245 74.072 27.614 64.329 38.119 c 54.586 48.624 52.078 53.184 52.683 61.27 c 53.288 69.356 71.622 78.91 77.935 66.901") };
/*     */     if (type != 2 && type != 1)
/*     */       throw new IllegalArgumentException("This transition must use type RIGHT or LEFT"); 
/*     */     this.type = type;
/*     */   } public Shape[] getShapes(float progress, Dimension size) {
/* 142 */     progress = 1.0F - progress;
/*     */     
/* 144 */     GeneralPath star1 = new GeneralPath(star[8]);
/* 145 */     GeneralPath star2 = new GeneralPath(star[5]);
/* 146 */     GeneralPath star3 = new GeneralPath(star[8]);
/* 147 */     GeneralPath star4 = new GeneralPath(star[5]);
/* 148 */     GeneralPath star5 = new GeneralPath(star[7]);
/* 149 */     GeneralPath star6 = new GeneralPath(star[5]);
/* 150 */     GeneralPath star7 = new GeneralPath(star[8]);
/* 151 */     GeneralPath star8 = new GeneralPath(star[6]);
/*     */     
/* 153 */     Random random = new Random(2L);
/*     */     
/* 155 */     star1.transform(AffineTransform.getRotateInstance(random.nextDouble()));
/* 156 */     star2.transform(AffineTransform.getRotateInstance(random.nextDouble()));
/* 157 */     star3.transform(AffineTransform.getRotateInstance(random.nextDouble()));
/* 158 */     star4.transform(AffineTransform.getRotateInstance(random.nextDouble()));
/* 159 */     star5.transform(AffineTransform.getRotateInstance(random.nextDouble()));
/* 160 */     star6.transform(AffineTransform.getRotateInstance(random.nextDouble()));
/* 161 */     star7.transform(AffineTransform.getRotateInstance(random.nextDouble()));
/* 162 */     star8.transform(AffineTransform.getRotateInstance(random.nextDouble()));
/*     */     
/* 164 */     float big = Math.min(size.width, size.height) * 0.7F;
/* 165 */     float base1 = (float)(Math.pow(progress, 2.2D) * 0.5D + 0.0D);
/* 166 */     float base2 = (float)(Math.pow(progress, 2.2D) * 0.5D + 0.03750000149011612D);
/* 167 */     float base3 = (float)(Math.pow(progress, 2.2D) * 0.5D + 0.07500000298023224D);
/* 168 */     float base4 = (float)(Math.pow(progress, 2.2D) * 0.5D + 0.11250000447034836D);
/* 169 */     float base5 = (float)(Math.pow(progress, 2.2D) * 0.5D + 0.15000000596046448D);
/* 170 */     float base6 = (float)(Math.pow(progress, 2.2D) * 0.5D + 0.1875D);
/* 171 */     float base7 = (float)(Math.pow(progress, 2.2D) * 0.5D + 0.22500000894069672D);
/* 172 */     float base8 = (float)(Math.pow(progress, 2.2D) * 0.5D + 0.26250001788139343D);
/* 173 */     float progress1 = (progress - base1) / (1.0F - base1);
/* 174 */     float progress2 = (progress - base2) / (1.0F - base2);
/* 175 */     float progress3 = (progress - base3) / (1.0F - base3);
/* 176 */     float progress4 = (progress - base4) / (1.0F - base4);
/* 177 */     float progress5 = (progress - base5) / (1.0F - base5);
/* 178 */     float progress6 = (progress - base6) / (1.0F - base6);
/* 179 */     float progress7 = (progress - base7) / (1.0F - base7);
/* 180 */     float progress8 = (progress - base8) / (1.0F - base8);
/* 181 */     Vector<GeneralPath> v = new Vector<>();
/*     */     
/* 183 */     if (progress1 > 0.0F) {
/* 184 */       fit(star1, big, size.width * 2.0F / 3.0F, size.height * 3.0F / 4.0F, this.paths[0], size, progress1 * 2.0F);
/* 185 */       v.add(star1);
/*     */     } 
/* 187 */     if (progress2 > 0.0F) {
/* 188 */       fit(star2, big, size.width * 7.0F / 8.0F, size.height * 1.0F / 5.0F, this.paths[1], size, progress2 * 2.0F);
/* 189 */       v.add(star2);
/*     */     } 
/* 191 */     if (progress3 > 0.0F) {
/* 192 */       fit(star3, big, size.width * 1.0F / 6.0F, size.height * 2.2F / 5.0F, this.paths[2], size, progress3 * 2.0F);
/* 193 */       v.add(star3);
/*     */     } 
/* 195 */     if (progress4 > 0.0F) {
/* 196 */       fit(star4, big, size.width * 3.1F / 6.0F, size.height * 1.2F / 5.0F, this.paths[0], size, progress4 * 2.0F);
/* 197 */       v.add(star4);
/*     */     } 
/* 199 */     if (progress5 > 0.0F) {
/* 200 */       fit(star5, big, size.width * 1.9F / 6.0F, size.height * 4.2F / 5.0F, this.paths[1], size, progress5 * 2.0F);
/* 201 */       v.add(star5);
/*     */     } 
/* 203 */     if (progress6 > 0.0F) {
/* 204 */       fit(star6, big, size.width * 13.0F / 15.0F, size.height * 4.3F / 5.0F, this.paths[2], size, progress6 * 2.0F);
/* 205 */       v.add(star6);
/*     */     } 
/* 207 */     if (progress7 > 0.0F) {
/* 208 */       fit(star7, big, size.width * 2.0F / 5.0F, size.height * 2.4F / 5.0F, this.paths[0], size, progress7 * 2.0F);
/* 209 */       v.add(star7);
/*     */     } 
/* 211 */     if (progress8 > 0.0F) {
/* 212 */       fit(star8, big, size.width * 3.0F / 6.0F, size.height * 2.0F / 5.0F, this.paths[2], size, progress8 * 2.0F);
/* 213 */       v.add(star8);
/*     */     } 
/*     */     
/* 216 */     Shape[] shapes = v.<Shape>toArray(new Shape[v.size()]);
/* 217 */     if (this.type == 2) {
/* 218 */       AffineTransform flipHorizontal = TransformUtils.createAffineTransform(0.0D, 0.0D, 0.0D, size.height, size.width, 0.0D, size.width, 0.0D, size.width, size.height, 0.0D, 0.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 224 */       for (int a = 0; a < shapes.length; a++) {
/* 225 */         if (shapes[a] instanceof GeneralPath) {
/* 226 */           ((GeneralPath)shapes[a]).transform(flipHorizontal);
/*     */         } else {
/* 228 */           shapes[a] = flipHorizontal.createTransformedShape(shapes[a]);
/*     */         } 
/*     */       } 
/*     */     } 
/* 232 */     return shapes;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getStrokeWidth(float progress) {
/* 237 */     return 2.0F + 7.0F * (1.0F - progress);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DInstruction[] getInstructions(float progress, Dimension size) {
/* 244 */     Transition2DInstruction[] instr = super.getInstructions(progress, size);
/* 245 */     for (int a = 0; a < instr.length; a++) {
/* 246 */       if (instr[a] instanceof ImageInstruction) {
/* 247 */         ImageInstruction i = (ImageInstruction)instr[a];
/* 248 */         i.isFirstFrame = !i.isFirstFrame;
/*     */       } 
/*     */     } 
/* 251 */     return instr;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\spunk\StarsTransition2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */