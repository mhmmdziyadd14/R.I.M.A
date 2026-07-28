/*     */ package com.bric.image.transition.app;
/*     */ 
/*     */ import com.bric.image.transition.Transition;
/*     */ import com.bric.image.transition.spunk.GooTransition2D;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.Timer;
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
/*     */ public class SimplestTransitionDemo
/*     */   extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  52 */   public static final BufferedImage bi1 = createImage("A", true);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   public static final BufferedImage bi2 = createImage("B", false);
/*     */ 
/*     */   
/*     */   public static final float DURATION = 2000.0F;
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/*  64 */     JFrame frame = new JFrame("SimplestDemo");
/*  65 */     SimplestTransitionDemo d = new SimplestTransitionDemo();
/*  66 */     frame.setDefaultCloseOperation(3);
/*  67 */     frame.getContentPane().add(d);
/*  68 */     frame.pack();
/*  69 */     frame.setVisible(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage createImage(String text, boolean light) {
/*  80 */     return createImage(400, text, light, true);
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
/*     */   public static BufferedImage createImage(int size, String text, boolean light, boolean useGradients) {
/*  92 */     BufferedImage bi = new BufferedImage(size, size, 1);
/*  93 */     Font font = new Font("Default", 0, size * 150 / 200);
/*  94 */     FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
/*     */     
/*  96 */     Graphics2D g = bi.createGraphics();
/*  97 */     if (useGradients) {
/*  98 */       if (light) {
/*  99 */         g.setPaint(new GradientPaint(0.0F, bi.getHeight(), Color.red, bi.getWidth(), 0.0F, Color.yellow, true));
/*     */       } else {
/* 101 */         g.setPaint(new GradientPaint(0.0F, 0.0F, Color.blue, bi.getWidth(), bi.getHeight(), Color.green, true));
/*     */       }
/*     */     
/* 104 */     } else if (light) {
/* 105 */       g.setPaint(new Color(14784569));
/*     */     } else {
/* 107 */       g.setPaint(new Color(3886738));
/*     */     } 
/*     */     
/* 110 */     g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
/* 111 */     g.setColor(Color.black);
/* 112 */     g.setFont(font);
/* 113 */     float width = (float)font.getStringBounds(text, frc).getWidth();
/* 114 */     g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 115 */     g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/* 116 */     g.drawString(text, (bi.getWidth() / 2) - width / 2.0F, (size * 160 / 200));
/* 117 */     g.dispose();
/*     */     
/* 119 */     return bi;
/*     */   }
/*     */   
/* 122 */   Transition transition = (Transition)new GooTransition2D();
/* 123 */   ActionListener repainter = new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 125 */         SimplestTransitionDemo.this.repaint();
/*     */       }
/*     */     };
/*     */   
/*     */   public SimplestTransitionDemo() {
/* 130 */     setPreferredSize(new Dimension(bi1.getWidth(), bi1.getHeight()));
/* 131 */     Timer timer = new Timer(50, this.repainter);
/* 132 */     timer.start();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void paintComponent(Graphics g) {
/* 137 */     super.paintComponent(g);
/* 138 */     long t = System.currentTimeMillis();
/* 139 */     float progress = (float)(t % 4000L);
/* 140 */     if (progress > 2000.0F) {
/* 141 */       progress = (progress - 2000.0F) / 2000.0F;
/* 142 */       this.transition.paint((Graphics2D)g, bi2, bi1, progress);
/*     */     } else {
/* 144 */       progress /= 2000.0F;
/* 145 */       this.transition.paint((Graphics2D)g, bi1, bi2, progress);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\app\SimplestTransitionDemo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */