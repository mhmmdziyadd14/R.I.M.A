/*     */ package com.bric.animation;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Executors;
/*     */ import javax.swing.JPanel;
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
/*     */ public abstract class BufferedAnimationPanel
/*     */   extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   BufferedImage buffer;
/*     */   
/*     */   class RefreshRunnable
/*     */     implements Runnable
/*     */   {
/*     */     int w;
/*     */     int h;
/*     */     
/*     */     RefreshRunnable(int width, int height) {
/*  41 */       this.w = Math.max(1, width);
/*  42 */       this.h = Math.max(1, height);
/*     */     }
/*     */     
/*     */     public void run() {
/*  46 */       synchronized (BufferedAnimationPanel.this.synchronizationLock) {
/*     */         
/*  48 */         if (BufferedAnimationPanel.this.currentRunnable != this) {
/*     */           return;
/*     */         }
/*     */       } 
/*  52 */       BufferedImage refreshedBuffer = BufferedAnimationPanel.this.getBuffer(this.w, this.h);
/*  53 */       Graphics2D g = refreshedBuffer.createGraphics();
/*  54 */       g.clipRect(0, 0, this.w, this.h);
/*  55 */       BufferedAnimationPanel.this.paintAnimation(g, this.w, this.h);
/*  56 */       g.dispose();
/*     */       
/*  58 */       synchronized (BufferedAnimationPanel.this.synchronizationLock) {
/*  59 */         BufferedImage prevBuffer = BufferedAnimationPanel.this.buffer;
/*  60 */         BufferedAnimationPanel.this.buffer = refreshedBuffer;
/*  61 */         BufferedAnimationPanel.this.storeBuffer(prevBuffer);
/*     */       } 
/*     */       
/*  64 */       BufferedAnimationPanel.this.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*  69 */   Dimension size = new Dimension(0, 0);
/*     */   Runnable currentRunnable;
/*  71 */   Executor executor = Executors.newSingleThreadExecutor();
/*  72 */   Object synchronizationLock = new Object();
/*     */   
/*     */   public BufferedAnimationPanel() {
/*  75 */     addComponentListener(new ComponentAdapter()
/*     */         {
/*     */           public void componentResized(ComponentEvent e) {
/*  78 */             BufferedAnimationPanel.this.setBufferSize(BufferedAnimationPanel.this.getWidth(), BufferedAnimationPanel.this.getHeight());
/*     */           }
/*     */         });
/*  81 */     setDoubleBuffered(false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private BufferedImage spareImage;
/*     */ 
/*     */   
/*     */   private void setBufferSize(int w, int h) {
/*  90 */     synchronized (this.synchronizationLock) {
/*  91 */       if (this.size == null || this.size.width != w || this.size.height != h) {
/*  92 */         this.size = new Dimension(w, h);
/*  93 */         refresh();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private BufferedImage getBuffer(int w, int h) {
/*     */     BufferedImage returnValue;
/* 101 */     if (this.spareImage == null || this.spareImage.getWidth() != w || this.spareImage.getHeight() != h) {
/* 102 */       returnValue = new BufferedImage(w, h, 2);
/*     */     } else {
/* 104 */       returnValue = this.spareImage;
/*     */     } 
/* 106 */     this.spareImage = null;
/* 107 */     return returnValue;
/*     */   }
/*     */   
/*     */   private void storeBuffer(BufferedImage bi) {
/* 111 */     this.spareImage = bi;
/*     */   }
/*     */   
/*     */   protected void paintComponent(Graphics g) {
/* 115 */     super.paintComponent(g);
/* 116 */     BufferedImage currentBuffer = this.buffer;
/* 117 */     if (currentBuffer != null) {
/* 118 */       synchronized (currentBuffer) {
/* 119 */         g.drawImage(currentBuffer, 0, 0, null);
/*     */       } 
/*     */     } else {
/* 122 */       refresh();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void refresh() {
/* 127 */     synchronized (this.synchronizationLock) {
/* 128 */       RefreshRunnable refreshRunnable = new RefreshRunnable(this.size.width, this.size.height);
/* 129 */       this.currentRunnable = refreshRunnable;
/* 130 */       this.executor.execute(refreshRunnable);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract void paintAnimation(Graphics2D paramGraphics2D, int paramInt1, int paramInt2);
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\animation\BufferedAnimationPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */