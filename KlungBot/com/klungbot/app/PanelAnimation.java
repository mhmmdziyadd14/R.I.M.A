/*     */ package com.klungbot.app;
/*     */ import com.bric.image.transition.Transition;
/*     */ import com.bric.image.transition.spunk.GooTransition2D;
/*     */ import com.klungbot.MidiInfo;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.DefaultTableModel;
/*     */ import wayang.Adegan;
/*     */ import wayang.Lakon;
/*     */ import wayang.TransitionAdegan;
/*     */ 
/*     */ public class PanelAnimation extends JPanel {
/*     */   Main parent;
/*     */   MidiInfo minfo;
/*     */   TableModel model;
/*     */   ArrayList<Lakon> lakons;
/*     */   private JToggleButton btStart;
/*     */   private JScrollPane jScrollPane1;
/*     */   private JTable tbAnimation;
/*     */   
/*     */   class TableModel extends AbstractTableModel {
/*  29 */     String[] headers = new String[] { "Name", "Meter", "Songs" };
/*     */ 
/*     */     
/*     */     public String getColumnName(int ci) {
/*  33 */       if (ci < this.headers.length) return this.headers[ci]; 
/*  34 */       return Doremi.getTrackName(ci - this.headers.length);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getColumnCount() {
/*  39 */       return this.headers.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRowCount() {
/*  44 */       return PanelAnimation.this.lakons.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public Class getColumnClass(int ci) {
/*  49 */       return String.class;
/*     */     }
/*     */     
/*     */     public boolean isCellEditable(int row, int col) {
/*  53 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getValueAt(int row, int col) {
/*  58 */       Lakon l = PanelAnimation.this.lakons.get(row);
/*  59 */       switch (col) { case 0:
/*  60 */           return l.toString();
/*  61 */         case 1: return l.getMeterStr();
/*  62 */         case 2: return l.getInfo(); }
/*     */       
/*  64 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setValueAt(Object obj, int row, int col) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PanelAnimation(Main parent) {
/*  76 */     initComponents();
/*  77 */     this.lakons = new ArrayList<>();
/*  78 */     initLakons();
/*  79 */     this.parent = parent;
/*  80 */     this.model = new TableModel();
/*  81 */     this.tbAnimation.setModel(this.model);
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
/*  93 */     this.jScrollPane1 = new JScrollPane();
/*  94 */     this.tbAnimation = new JTable();
/*  95 */     this.btStart = new JToggleButton();
/*     */     
/*  97 */     setLayout(new BorderLayout());
/*     */     
/*  99 */     this.tbAnimation.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null }, , { null, null, null, null }, , { null, null, null, null }, , { null, null, null, null },  }, (Object[])new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
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
/* 110 */     this.jScrollPane1.setViewportView(this.tbAnimation);
/*     */     
/* 112 */     add(this.jScrollPane1, "Center");
/*     */     
/* 114 */     this.btStart.setText("Start Visualisation");
/* 115 */     this.btStart.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 117 */             PanelAnimation.this.btStartActionPerformed(evt);
/*     */           }
/*     */         });
/* 120 */     add(this.btStart, "Last");
/*     */   }
/*     */ 
/*     */   
/*     */   private void btStartActionPerformed(ActionEvent evt) {
/* 125 */     if (this.btStart.isSelected()) {
/* 126 */       this.parent.startVisualization(this.lakons);
/*     */     } else {
/*     */       
/* 129 */       this.parent.stopVisualization();
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
/*     */   void initLakons() {
/* 141 */     Lakon l = new Lakon("Peresmian Pusat Kreatif", 4, 4);
/* 142 */     l.setInfo("Bangun Pemudi Pemuda");
/* 143 */     TransitionAdegan a = new TransitionAdegan(240, 720, (Transition)new GooTransition2D(), "Pembukaan");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 148 */     l.add((Adegan)a);
/* 149 */     a = new TransitionAdegan(240, 720, (Transition)new GooTransition2D(), "Presentasi");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 154 */     l.add((Adegan)a);
/* 155 */     a = new TransitionAdegan(240, 720, (Transition)new GooTransition2D(), "Penutup");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 160 */     l.add((Adegan)a);
/* 161 */     this.lakons.add(l);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelAnimation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */