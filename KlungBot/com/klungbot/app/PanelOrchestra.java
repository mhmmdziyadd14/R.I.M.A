/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.Sequence;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ 
/*     */ public class PanelOrchestra
/*     */   extends JPanel
/*     */   implements MainListener {
/*     */   Main parent;
/*     */   PanelAnalysis pAnalysis;
/*     */   int channel;
/*     */   Sequence sequence;
/*  24 */   ArrayList<FormOrchestra> forms = new ArrayList<>(); private ButtonGroup buttonGroup1; private JButton jButton1;
/*     */   private JLabel jLabel1;
/*     */   private JPanel jPanel1;
/*     */   private JPanel jPanel2;
/*     */   
/*     */   public PanelOrchestra(Main p) {
/*  30 */     this.parent = p;
/*  31 */     initComponents();
/*  32 */     this.pAnalysis = new PanelAnalysis(null);
/*  33 */     add(this.pAnalysis, "Center");
/*  34 */     this.channel = 0;
/*     */   }
/*     */   private JPanel jPanel4; private JRadioButton jRadioButton1; private JRadioButton jRadioButton2; private JRadioButton jRadioButton3; private JRadioButton jRadioButton4;
/*     */   void addForm(FormOrchestra item) {
/*  38 */     this.forms.add(item);
/*     */   }
/*     */   
/*     */   void removeForm(FormOrchestra item) {
/*  42 */     this.forms.remove(item);
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
/*     */   private void initComponents() {
/*  54 */     this.buttonGroup1 = new ButtonGroup();
/*  55 */     this.jPanel4 = new JPanel();
/*  56 */     this.jPanel1 = new JPanel();
/*  57 */     this.jPanel2 = new JPanel();
/*  58 */     this.jRadioButton1 = new JRadioButton();
/*  59 */     this.jRadioButton2 = new JRadioButton();
/*  60 */     this.jRadioButton3 = new JRadioButton();
/*  61 */     this.jRadioButton4 = new JRadioButton();
/*  62 */     this.jLabel1 = new JLabel();
/*  63 */     this.jButton1 = new JButton();
/*     */     
/*  65 */     setLayout(new BorderLayout());
/*     */     
/*  67 */     this.jPanel1.setLayout(new BorderLayout());
/*     */     
/*  69 */     this.jPanel2.setLayout(new GridLayout(1, 0));
/*     */     
/*  71 */     this.buttonGroup1.add(this.jRadioButton1);
/*  72 */     this.jRadioButton1.setText("V");
/*  73 */     this.jRadioButton1.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*  75 */             PanelOrchestra.this.jRadioButton1ActionPerformed(evt);
/*     */           }
/*     */         });
/*  78 */     this.jPanel2.add(this.jRadioButton1);
/*     */     
/*  80 */     this.buttonGroup1.add(this.jRadioButton2);
/*  81 */     this.jRadioButton2.setText("VA");
/*  82 */     this.jRadioButton2.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*  84 */             PanelOrchestra.this.jRadioButton2ActionPerformed(evt);
/*     */           }
/*     */         });
/*  87 */     this.jPanel2.add(this.jRadioButton2);
/*     */     
/*  89 */     this.buttonGroup1.add(this.jRadioButton3);
/*  90 */     this.jRadioButton3.setText("VB");
/*  91 */     this.jRadioButton3.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*  93 */             PanelOrchestra.this.jRadioButton3ActionPerformed(evt);
/*     */           }
/*     */         });
/*  96 */     this.jPanel2.add(this.jRadioButton3);
/*     */     
/*  98 */     this.buttonGroup1.add(this.jRadioButton4);
/*  99 */     this.jRadioButton4.setText("VC");
/* 100 */     this.jRadioButton4.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 102 */             PanelOrchestra.this.jRadioButton4ActionPerformed(evt);
/*     */           }
/*     */         });
/* 105 */     this.jPanel2.add(this.jRadioButton4);
/*     */     
/* 107 */     this.jPanel1.add(this.jPanel2, "Center");
/*     */     
/* 109 */     this.jLabel1.setText("  Track:  ");
/* 110 */     this.jPanel1.add(this.jLabel1, "Before");
/*     */     
/* 112 */     this.jButton1.setText("Orchestrate");
/* 113 */     this.jButton1.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 115 */             PanelOrchestra.this.jButton1ActionPerformed(evt);
/*     */           }
/*     */         });
/* 118 */     this.jPanel1.add(this.jButton1, "East");
/*     */     
/* 120 */     this.jPanel4.add(this.jPanel1);
/*     */     
/* 122 */     add(this.jPanel4, "North");
/*     */   }
/*     */   
/*     */   private void jRadioButton1ActionPerformed(ActionEvent evt) {
/* 126 */     this.channel = 0;
/* 127 */     setSequence();
/*     */   }
/*     */   
/*     */   private void jRadioButton3ActionPerformed(ActionEvent evt) {
/* 131 */     this.channel = 2;
/* 132 */     setSequence();
/*     */   }
/*     */   
/*     */   private void jRadioButton2ActionPerformed(ActionEvent evt) {
/* 136 */     this.channel = 1;
/* 137 */     setSequence();
/*     */   }
/*     */   
/*     */   private void jRadioButton4ActionPerformed(ActionEvent evt) {
/* 141 */     this.channel = 3;
/* 142 */     setSequence();
/*     */   }
/*     */   
/*     */   private void jButton1ActionPerformed(ActionEvent evt) {
/* 146 */     FormOrchestra form = new FormOrchestra(this, this.channel);
/* 147 */     form.setVisible(true);
/* 148 */     if (this.sequence != null) {
/* 149 */       form.changeSequence(this.sequence);
/*     */     }
/*     */   }
/*     */   
/*     */   private void formComponentShown(ComponentEvent evt) {
/* 154 */     if (this.parent != null)
/* 155 */       this.parent.setMessage("Orchestrating a concert. Pick a song from the album/draft, examine the note distribution", Color.GREEN); 
/*     */   }
/*     */   
/*     */   void setSequence() {
/* 159 */     if (this.sequence != null) {
/* 160 */       this.pAnalysis.analyze(this.sequence, this.channel);
/*     */     }
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
/*     */   
/*     */   public void changeSequence(Sequence seq) {
/* 178 */     this.sequence = seq;
/* 179 */     setSequence();
/* 180 */     for (FormOrchestra f : this.forms) {
/* 181 */       f.changeSequence(seq);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(Sequence seq) {
/* 187 */     for (FormOrchestra f : this.forms) {
/* 188 */       f.start(seq);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void changeTick(int value, long nextOn) {
/* 194 */     for (FormOrchestra f : this.forms) {
/* 195 */       f.changeTick(value, nextOn);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void waiting(long wait) {
/* 201 */     for (FormOrchestra f : this.forms) {
/* 202 */       f.waiting(wait);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void finished(Sequence seq) {
/* 208 */     for (FormOrchestra f : this.forms) {
/* 209 */       f.finished(seq);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReady() {
/* 215 */     for (FormOrchestra f : this.forms) {
/* 216 */       if (f.isReady()) return true; 
/*     */     } 
/* 218 */     return false;
/*     */   }
/*     */   
/*     */   public void play(boolean state) {
/* 222 */     if (state) {
/* 223 */       this.parent.play();
/*     */     } else {
/*     */       
/* 226 */       this.parent.finish();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelOrchestra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */