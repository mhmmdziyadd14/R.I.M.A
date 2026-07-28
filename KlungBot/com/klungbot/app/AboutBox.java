/*     */ package com.klungbot.app;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Frame;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.ActionMap;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.InputMap;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.KeyStroke;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.UnsupportedLookAndFeelException;
/*     */ import javax.swing.border.SoftBevelBorder;
/*     */ 
/*     */ public class AboutBox extends JDialog {
/*     */   public static final int RET_CANCEL = 0;
/*     */   
/*     */   public AboutBox(Frame parent, boolean modal) {
/*  36 */     super(parent, modal);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 197 */     this.returnStatus = 0;
/*     */     initComponents();
/*     */     Toolkit toolkit = Toolkit.getDefaultToolkit();
/*     */     Dimension screenSize = toolkit.getScreenSize();
/*     */     int x = (screenSize.width - getWidth()) / 2;
/*     */     int y = (screenSize.height - getHeight()) / 2;
/*     */     setLocation(x, y);
/*     */     String cancelName = "cancel";
/*     */     InputMap inputMap = getRootPane().getInputMap(1);
/*     */     inputMap.put(KeyStroke.getKeyStroke(27, 0), cancelName);
/*     */     ActionMap actionMap = getRootPane().getActionMap();
/*     */     actionMap.put(cancelName, new AbstractAction() {
/*     */           public void actionPerformed(ActionEvent e) {
/*     */             AboutBox.this.doClose(0);
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public static final int RET_OK = 1;
/*     */   private JEditorPane jEditorPane1;
/*     */   private JLabel jLabel1;
/*     */   private JPanel jPanel1;
/*     */   private JPanel jPanel2;
/*     */   private JScrollPane jScrollPane1;
/*     */   private JButton okButton;
/*     */   private int returnStatus;
/*     */   
/*     */   public int getReturnStatus() {
/*     */     return this.returnStatus;
/*     */   }
/*     */   
/*     */   private void initComponents() {
/*     */     this.jPanel1 = new JPanel();
/*     */     this.okButton = new JButton();
/*     */     this.jLabel1 = new JLabel();
/*     */     this.jPanel2 = new JPanel();
/*     */     this.jScrollPane1 = new JScrollPane();
/*     */     this.jEditorPane1 = new JEditorPane();
/*     */     setTitle("About");
/*     */     addWindowListener(new WindowAdapter() {
/*     */           public void windowClosing(WindowEvent evt) {
/*     */             AboutBox.this.closeDialog(evt);
/*     */           }
/*     */         });
/*     */     this.okButton.setText("Close");
/*     */     this.okButton.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*     */             AboutBox.this.okButtonActionPerformed(evt);
/*     */           }
/*     */         });
/*     */     this.jPanel1.add(this.okButton);
/*     */     getRootPane().setDefaultButton(this.okButton);
/*     */     getContentPane().add(this.jPanel1, "Last");
/*     */     this.jLabel1.setHorizontalAlignment(0);
/*     */     this.jLabel1.setIcon(new ImageIcon(getClass().getResource("/resources/header48.png")));
/*     */     getContentPane().add(this.jLabel1, "North");
/*     */     this.jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/*     */     this.jPanel2.setLayout(new BorderLayout());
/*     */     this.jScrollPane1.setBorder(new SoftBevelBorder(1));
/*     */     this.jScrollPane1.setHorizontalScrollBarPolicy(31);
/*     */     this.jEditorPane1.setEditable(false);
/*     */     this.jEditorPane1.setBackground(SystemColor.control);
/*     */     this.jEditorPane1.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
/*     */     this.jEditorPane1.setContentType("text/html");
/*     */     this.jEditorPane1.setText("<html>\n  <head>\n  </head>\n  <body>\n    <p style=\"margin-top: 0\"/>\n<center>\n<b>Klungbot Maestro v 1.2 Gubernur Jabar</b><br>\nIntegrated angklung robot controller and midi synthesizer<br> \nto play, practice, and compose music with doremi music notation.\n<p>\n(c) Eko Mursito Budi, 2012<br>\nAll rights reserved.\n<p>\n<b>Passionatelly developed by :</b><br>\nEko Mursito Budi<br>\nKarismanto Rahmadika, Krisna Diastama<br>\nFariza D. Prasetya, Alvin Nurhadi, Ari A. Rochim,<br>\nNugroho H. Wibowo, Sigit Yudanto.\n<p>\n<b>Inspired by the angklung mastery of :</b><br>\nAsep Suhada, Sunata, Yayan Udjo, Handiman<br>\n\n<p>\nPlease visit our web site at:<br>\n<b>www.klungbot.com</b>\n<p>\n\n</center>\n</body>\n</html>\n");
/*     */     this.jScrollPane1.setViewportView(this.jEditorPane1);
/*     */     this.jPanel2.add(this.jScrollPane1, "Center");
/*     */     getContentPane().add(this.jPanel2, "Center");
/*     */     pack();
/*     */   }
/*     */   
/*     */   private void okButtonActionPerformed(ActionEvent evt) {
/*     */     doClose(1);
/*     */   }
/*     */   
/*     */   private void closeDialog(WindowEvent evt) {
/*     */     doClose(0);
/*     */   }
/*     */   
/*     */   private void doClose(int retStatus) {
/*     */     this.returnStatus = retStatus;
/*     */     setVisible(false);
/*     */     dispose();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/*     */       for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
/*     */         if ("Nimbus".equals(info.getName())) {
/*     */           UIManager.setLookAndFeel(info.getClassName());
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } catch (ClassNotFoundException ex) {
/*     */       Logger.getLogger(AboutBox.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } catch (InstantiationException ex) {
/*     */       Logger.getLogger(AboutBox.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } catch (IllegalAccessException ex) {
/*     */       Logger.getLogger(AboutBox.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } catch (UnsupportedLookAndFeelException ex) {
/*     */       Logger.getLogger(AboutBox.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */     EventQueue.invokeLater(new Runnable() {
/*     */           public void run() {
/*     */             AboutBox dialog = new AboutBox(new JFrame(), true);
/*     */             dialog.addWindowListener(new WindowAdapter() {
/*     */                   public void windowClosing(WindowEvent e) {
/*     */                     System.exit(0);
/*     */                   }
/*     */                 });
/*     */             dialog.setVisible(true);
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\AboutBox.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */