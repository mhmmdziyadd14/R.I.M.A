/*    */ package com.klungbot.app;
/*    */ 
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Image;
/*    */ import java.awt.Toolkit;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import javax.imageio.ImageIO;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JScrollPane;
/*    */ import javax.swing.JTextPane;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HelpForm
/*    */   extends JFrame
/*    */ {
/*    */   String page;
/*    */   private JScrollPane jScrollPane1;
/*    */   private JTextPane tpBrowser;
/*    */   
/*    */   public HelpForm(String helpFolder) {
/* 31 */     initComponents();
/* 32 */     Toolkit tk = Toolkit.getDefaultToolkit();
/* 33 */     setMinimumSize(new Dimension(600, 400));
/* 34 */     setSize((tk.getScreenSize()).width / 2, (tk.getScreenSize()).height - 100);
/*    */     try {
/* 36 */       Image im = ImageIO.read(getClass().getResource("/resources/icon/klung.png"));
/* 37 */       setIconImage(im);
/* 38 */     } catch (IOException ex) {}
/* 39 */     File localFile = new File(helpFolder + "index.html");
/* 40 */     this.page = "file:///" + localFile.getAbsolutePath();
/* 41 */     this.tpBrowser.setEditable(false);
/* 42 */     reload();
/*    */   }
/*    */ 
/*    */   
/*    */   public void reload() {
/*    */     try {
/* 48 */       this.tpBrowser.setPage(this.page);
/*    */     }
/* 50 */     catch (Exception e1) {
/* 51 */       this.tpBrowser.setText("Could not load page:" + this.page + "\n" + "Error:" + e1
/* 52 */           .getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void initComponents() {
/* 64 */     this.jScrollPane1 = new JScrollPane();
/* 65 */     this.tpBrowser = new JTextPane();
/*    */     
/* 67 */     setDefaultCloseOperation(2);
/* 68 */     setTitle("Klungbot Help");
/*    */     
/* 70 */     this.jScrollPane1.setViewportView(this.tpBrowser);
/*    */     
/* 72 */     getContentPane().add(this.jScrollPane1, "Center");
/*    */     
/* 74 */     pack();
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\HelpForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */