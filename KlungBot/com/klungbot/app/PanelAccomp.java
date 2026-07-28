/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.Maestro;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JToggleButton;
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
/*     */ public class PanelAccomp
/*     */   extends JPanel
/*     */   implements PianoListener
/*     */ {
/*     */   Piano piano;
/*     */   Main parent;
/*     */   Maestro maestro;
/*  37 */   String[] styles = new String[] { "Swing", "Jazz", "Calung" };
/*     */   JComboBox[] cbStyles;
/*     */   
/*     */   public PanelAccomp(Main parent) {
/*  41 */     initComponents();
/*  42 */     initPanel();
/*  43 */     this.piano = new Piano(this.panelKeyboard, this);
/*  44 */     this.parent = parent;
/*  45 */     this.maestro = parent.maestro;
/*     */   }
/*     */ 
/*     */   
/*     */   JToggleButton[] tbStyles;
/*     */   
/*     */   int selected;
/*     */   
/*     */   private JPanel jPanel2;
/*     */   private JPanel panelKeyboard;
/*     */   
/*     */   private void initComponents() {
/*  57 */     this.panelKeyboard = new JPanel();
/*  58 */     this.jPanel2 = new JPanel();
/*     */     
/*  60 */     setLayout(new BorderLayout());
/*     */     
/*  62 */     this.panelKeyboard.setName("panelKeyboard");
/*  63 */     this.panelKeyboard.setLayout(new BorderLayout());
/*  64 */     add(this.panelKeyboard, "Center");
/*     */     
/*  66 */     this.jPanel2.setName("jPanel2");
/*  67 */     this.jPanel2.setLayout(new GridLayout(2, 8));
/*  68 */     add(this.jPanel2, "First");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void cbStyleActionPerformed(ActionEvent evt) {
/*  74 */     JComboBox cb = (JComboBox)evt.getSource();
/*  75 */     System.out.println("CB " + cb.getName() + " = " + cb.getSelectedIndex());
/*     */   }
/*     */   
/*     */   private void tbStyleActionPerformed(ActionEvent evt) {
/*  79 */     JToggleButton tb = (JToggleButton)evt.getSource();
/*  80 */     int s = Integer.parseInt(tb.getName());
/*  81 */     if (s != this.selected) {
/*  82 */       this.tbStyles[this.selected].setSelected(false);
/*  83 */       this.selected = s;
/*  84 */       this.tbStyles[this.selected].setSelected(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initPanel() {
/*  94 */     this.cbStyles = new JComboBox[8];
/*  95 */     this.tbStyles = new JToggleButton[8];
/*     */     int i;
/*  97 */     for (i = 0; i < 8; i++) {
/*  98 */       this.cbStyles[i] = new JComboBox();
/*  99 */       this.cbStyles[i].setModel(new DefaultComboBoxModel((Object[])this.styles));
/* 100 */       this.cbStyles[i].setName(String.valueOf(i));
/* 101 */       this.cbStyles[i].addActionListener(new ActionListener() {
/*     */             public void actionPerformed(ActionEvent evt) {
/* 103 */               PanelAccomp.this.cbStyleActionPerformed(evt);
/*     */             }
/*     */           });
/* 106 */       this.jPanel2.add(this.cbStyles[i]);
/*     */     } 
/* 108 */     for (i = 0; i < 8; i++) {
/* 109 */       this.tbStyles[i] = new JToggleButton();
/* 110 */       this.tbStyles[i].setName(String.valueOf(i));
/* 111 */       this.tbStyles[i].addActionListener(new ActionListener() {
/*     */             public void actionPerformed(ActionEvent evt) {
/* 113 */               PanelAccomp.this.tbStyleActionPerformed(evt);
/*     */             }
/*     */           });
/* 116 */       this.jPanel2.add(this.tbStyles[i]);
/*     */     } 
/* 118 */     this.selected = 0;
/* 119 */     this.tbStyles[0].setSelected(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void midiOn(byte note, byte forte) {
/* 129 */     this.maestro.midiOn(note);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff(byte note) {
/* 134 */     this.maestro.midiOff(note);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelAccomp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */