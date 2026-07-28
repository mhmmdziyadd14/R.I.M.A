/*     */ package wayang;
/*     */ 
/*     */ import com.bric.image.transition.Transition;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.BufferedImageOp;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import javax.imageio.ImageIO;
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
/*     */ public class TransitionAdegan
/*     */   extends Adegan
/*     */ {
/*     */   File imgFile;
/*     */   BufferedImage img;
/*     */   Transition transition;
/*     */   
/*     */   public TransitionAdegan(int alength, int tlength, Transition t, String fname) {
/*  37 */     super(alength, tlength);
/*  38 */     this.imgFile = new File(fname);
/*  39 */     this.transition = t;
/*     */   }
/*     */   
/*     */   public TransitionAdegan(int alength, int tlength, Transition t, String fname, Adegan parent) {
/*  43 */     super(alength, tlength, parent);
/*  44 */     this.imgFile = new File(fname);
/*  45 */     this.transition = t;
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
/*     */   public static BufferedImage createImage(int width, int height, String text, boolean light, boolean useGradients) {
/*  58 */     BufferedImage bi = new BufferedImage(width, height, 1);
/*  59 */     Font font = new Font("Default", 0, height / 4);
/*  60 */     FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
/*     */     
/*  62 */     Graphics2D g = bi.createGraphics();
/*  63 */     if (useGradients) {
/*  64 */       if (light) {
/*  65 */         g.setPaint(new GradientPaint(0.0F, bi.getHeight(), Color.red, bi.getWidth(), 0.0F, Color.yellow, true));
/*     */       } else {
/*  67 */         g.setPaint(new GradientPaint(0.0F, 0.0F, Color.blue, bi.getWidth(), bi.getHeight(), Color.green, true));
/*     */       }
/*     */     
/*  70 */     } else if (light) {
/*  71 */       g.setPaint(new Color(14784569));
/*     */     } else {
/*  73 */       g.setPaint(new Color(3886738));
/*     */     } 
/*     */     
/*  76 */     g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
/*  77 */     g.setColor(Color.black);
/*  78 */     g.setFont(font);
/*  79 */     float tw = (float)font.getStringBounds(text, frc).getWidth();
/*  80 */     g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  81 */     g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*  82 */     g.drawString(text, (bi.getWidth() / 2) - tw / 2.0F, (height * 160 / 200));
/*  83 */     g.dispose();
/*     */     
/*  85 */     return bi;
/*     */   }
/*     */ 
/*     */   
/*     */   public BufferedImage getImage() {
/*  90 */     return this.img;
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(long tick, int width, int height) {
/*  95 */     super.start(tick, width, height);
/*  96 */     if (this.img == null) {
/*     */       try {
/*  98 */         this.img = ImageIO.read(this.imgFile);
/*  99 */       } catch (IOException e) {
/* 100 */         System.err.println("Cannot read from file " + this.imgFile.getPath());
/*     */       } 
/*     */     }
/* 103 */     if (this.img == null) {
/* 104 */       this.img = createImage(width, height, this.imgFile.getPath(), true, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void enter(Graphics2D g, long tick, long note, Adegan prev) {
/* 110 */     float progress = (float)(tick - this.tick + resolution);
/* 111 */     progress /= this.transition_length;
/* 112 */     this.transition.paint(g, prev.getImage(), this.img, progress);
/* 113 */     System.out.println("ENTER " + this.name + " " + progress);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void action(Graphics2D g, long tick, long note) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void display(Graphics2D g) {
/* 124 */     g.drawImage(this.img, (BufferedImageOp)null, 0, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void finish() {
/* 129 */     this.img = null;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\wayang\TransitionAdegan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */