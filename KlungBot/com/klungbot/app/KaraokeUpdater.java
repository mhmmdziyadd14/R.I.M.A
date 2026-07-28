/*    */ package com.klungbot.app;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.WindowEvent;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.ProgressMonitor;
/*    */ import javax.swing.SwingUtilities;
/*    */ import javax.swing.Timer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KaraokeUpdater
/*    */   extends JFrame
/*    */   implements ActionListener
/*    */ {
/*    */   static ProgressMonitor pbar;
/* 26 */   static int counter = 0;
/*    */   
/*    */   Timer timer;
/*    */   
/*    */   public KaraokeUpdater() {
/* 31 */     super("Karaoke Updater");
/* 32 */     setSize(250, 100);
/* 33 */     setDefaultCloseOperation(2);
/*    */     
/* 35 */     pbar = new ProgressMonitor(null, "Monitoring Progress", "Initializing . . .", 0, 100);
/*    */ 
/*    */ 
/*    */     
/* 39 */     this.timer = new Timer(500, this);
/* 40 */     this.timer.start();
/* 41 */     setVisible(true);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void actionPerformed(ActionEvent e) {
/* 47 */     SwingUtilities.invokeLater(new Updates(this.timer, this));
/*    */   }
/*    */   
/*    */   class Updates implements Runnable { Timer ti;
/*    */     
/*    */     public Updates(Timer t, KaraokeUpdater k) {
/* 53 */       this.ti = t; this.ku = k;
/*    */     } KaraokeUpdater ku;
/*    */     public void run() {
/* 56 */       if (KaraokeUpdater.pbar.isCanceled()) {
/*    */         try {
/* 58 */           finalize();
/* 59 */           this.ti.stop(); KaraokeUpdater.pbar.close();
/* 60 */           this.ku.dispatchEvent(new WindowEvent(this.ku, 201));
/* 61 */           wait();
/* 62 */         } catch (Throwable ex) {
/* 63 */           Logger.getLogger(KaraokeUpdater.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */         } 
/*    */       }
/* 66 */       KaraokeUpdater.pbar.setProgress(KaraokeUpdater.counter);
/* 67 */       KaraokeUpdater.pbar.setNote("Operation is " + KaraokeUpdater.counter + "% complete");
/* 68 */       KaraokeUpdater.counter += 2;
/*    */     } }
/*    */ 
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\KaraokeUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */