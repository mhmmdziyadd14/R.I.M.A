/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.Sequence;
/*     */ import com.klungbot.doremi.Doremi;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JToggleButton;
/*     */ 
/*     */ public class FormOrchestra
/*     */   extends JFrame
/*     */   implements MainListener {
/*     */   PanelVisual pVisual;
/*     */   int channel;
/*     */   PanelOrchestra parent;
/*     */   
/*     */   public FormOrchestra(PanelOrchestra parent, int channel) {
/*  30 */     this.channel = channel;
/*  31 */     this.parent = parent;
/*  32 */     initComponents();
/*  33 */     this.pVisual = new PanelVisual(null);
/*  34 */     this.pVisual.setTrack(channel);
/*  35 */     getContentPane().add(this.pVisual, "Center");
/*  36 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*  37 */     setMinimumSize(new Dimension(800, 500));
/*  38 */     setMaximumSize(new Dimension(1600, 1200));
/*  39 */     setSize((tk.getScreenSize()).width, (tk.getScreenSize()).height - 40);
/*  40 */     setDefaultCloseOperation(2);
/*  41 */     setTitle("Klung Along: " + Doremi.getTrackName(channel));
/*  42 */     parent.addForm(this);
/*     */   }
/*     */ 
/*     */   
/*     */   private JLabel jLabel2;
/*     */   
/*     */   private JPanel jPanel1;
/*     */   
/*     */   private JToggleButton jToggleButton1;
/*     */   private JLabel lbTitle;
/*     */   
/*     */   private void initComponents() {
/*  54 */     this.jPanel1 = new JPanel();
/*  55 */     this.lbTitle = new JLabel();
/*  56 */     this.jLabel2 = new JLabel();
/*  57 */     this.jToggleButton1 = new JToggleButton();
/*     */     
/*  59 */     setDefaultCloseOperation(3);
/*  60 */     setTitle("KlungMaestro");
/*  61 */     addWindowListener(new WindowAdapter() {
/*     */           public void windowClosed(WindowEvent evt) {
/*  63 */             FormOrchestra.this.formWindowClosed(evt);
/*     */           }
/*     */         });
/*     */     
/*  67 */     this.jPanel1.setLayout(new BorderLayout());
/*     */     
/*  69 */     this.lbTitle.setFont(new Font("Tahoma", 1, 36));
/*  70 */     this.lbTitle.setText(" Orchestrating");
/*  71 */     this.jPanel1.add(this.lbTitle, "Center");
/*     */     
/*  73 */     this.jLabel2.setHorizontalAlignment(0);
/*  74 */     this.jLabel2.setIcon(new ImageIcon(getClass().getResource("/resources/header48.png")));
/*  75 */     this.jLabel2.setMinimumSize(new Dimension(240, 48));
/*  76 */     this.jLabel2.setPreferredSize(new Dimension(240, 48));
/*  77 */     this.jLabel2.addMouseListener(new MouseAdapter() {
/*     */           public void mouseClicked(MouseEvent evt) {
/*  79 */             FormOrchestra.this.jLabel2MouseClicked(evt);
/*     */           }
/*     */         });
/*  82 */     this.jPanel1.add(this.jLabel2, "West");
/*     */     
/*  84 */     this.jToggleButton1.setIcon(new ImageIcon(getClass().getResource("/resources/icon/play.png")));
/*  85 */     this.jToggleButton1.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*  87 */             FormOrchestra.this.jToggleButton1ActionPerformed(evt);
/*     */           }
/*     */         });
/*  90 */     this.jPanel1.add(this.jToggleButton1, "East");
/*     */     
/*  92 */     getContentPane().add(this.jPanel1, "First");
/*     */     
/*  94 */     pack();
/*     */   }
/*     */   
/*     */   private void jLabel2MouseClicked(MouseEvent evt) {
/*  98 */     JDialog dlg = new AboutBox(this, true);
/*  99 */     dlg.setVisible(true);
/*     */   }
/*     */ 
/*     */   
/*     */   private void formWindowClosed(WindowEvent evt) {
/* 104 */     this.parent.removeForm(this);
/*     */   }
/*     */ 
/*     */   
/*     */   private void jToggleButton1ActionPerformed(ActionEvent evt) {
/* 109 */     this.parent.play(this.jToggleButton1.isSelected());
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
/*     */   public void changeSequence(Sequence seq) {
/* 122 */     String t = " Track " + Doremi.getTrackName(this.channel) + ": " + (String)seq.titles.get(0);
/* 123 */     this.lbTitle.setText(t);
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(Sequence seq) {
/* 128 */     changeSequence(seq);
/* 129 */     this.pVisual.start(seq);
/*     */   }
/*     */ 
/*     */   
/*     */   public void changeTick(int value, long nextOn) {
/* 134 */     this.pVisual.changeTick(value, nextOn);
/*     */   }
/*     */ 
/*     */   
/*     */   public void waiting(long wait) {
/* 139 */     this.pVisual.waiting(wait);
/*     */   }
/*     */ 
/*     */   
/*     */   public void finished(Sequence seq) {
/* 144 */     this.pVisual.finished(seq);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReady() {
/* 149 */     return isVisible();
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\FormOrchestra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */