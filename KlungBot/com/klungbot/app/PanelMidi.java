/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.MidiConverter;
/*     */ import com.klungbot.MidiInfo;
/*     */ import com.klungbot.doremi.Doremi;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.DefaultTableModel;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PanelMidi
/*     */   extends JPanel
/*     */ {
/*     */   Main parent;
/*     */   PlayerModel model;
/*     */   MidiConverter converter;
/*     */   private JButton btSave;
/*     */   private JScrollPane jScrollPane1;
/*     */   private JTable tbPlayers;
/*     */   
/*     */   class PlayerModel
/*     */     extends AbstractTableModel
/*     */   {
/*  34 */     String[] headers = new String[] { "Channel", "Instrument", "Notes", "Lowest", "Highest", "Melody", "Accomp", "MIDI" };
/*     */     
/*     */     MidiInfo minfo;
/*     */     
/*     */     String yes;
/*     */     String no;
/*     */     
/*     */     public String getColumnName(int ci) {
/*  42 */       if (ci < this.headers.length) return this.headers[ci]; 
/*  43 */       return Doremi.getTrackName(ci - this.headers.length);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getColumnCount() {
/*  48 */       return 10 + this.headers.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRowCount() {
/*  53 */       if (this.minfo == null) {
/*  54 */         PanelMidi.this.setMessage("No midi file is selected yet");
/*  55 */         return 0;
/*     */       } 
/*  57 */       return this.minfo.getLength();
/*     */     }
/*     */ 
/*     */     
/*     */     public Class getColumnClass(int ci) {
/*  62 */       if (ci == 0) return Integer.class; 
/*  63 */       if (ci > 0 && ci < this.headers.length - 1) return String.class; 
/*  64 */       return Boolean.class;
/*     */     }
/*     */     
/*     */     public boolean isCellEditable(int row, int col) {
/*  68 */       return (col >= 7);
/*     */     }
/*     */     public PlayerModel() {
/*  71 */       this.yes = "*";
/*  72 */       this.no = "";
/*     */       this.minfo = null;
/*     */     }
/*     */     public Object getValueAt(int row, int col) {
/*  76 */       if (this.minfo == null) {
/*  77 */         PanelMidi.this.parent.setMessage("Please select a midi file");
/*  78 */         return null;
/*     */       } 
/*  80 */       switch (col) { case 0:
/*  81 */           return new Integer(this.minfo.getChannel(row));
/*  82 */         case 1: return this.minfo.getInstruments(row);
/*  83 */         case 2: return Integer.valueOf(this.minfo.getCount(row));
/*  84 */         case 3: return this.minfo.getLowestNote(row);
/*  85 */         case 4: return this.minfo.getHighestNote(row);
/*  86 */         case 5: return this.minfo.getMelodyRequirement(row);
/*  87 */         case 6: return this.minfo.getAccompRequirement(row); }
/*     */       
/*  89 */       return new Boolean(this.minfo.isMapped(row, col - 8));
/*     */     }
/*     */ 
/*     */     
/*     */     public void setValueAt(Object obj, int row, int col) {
/*  94 */       if (obj instanceof Boolean) {
/*  95 */         Boolean b = (Boolean)obj;
/*  96 */         if (b.booleanValue()) {
/*  97 */           this.minfo.setMap(row, col - 8);
/*     */         } else {
/*     */           
/* 100 */           this.minfo.setMap(row, -2);
/*     */         } 
/* 102 */         PanelMidi.this.parent.maestro.setMidiMap(this.minfo, row);
/* 103 */         fireTableRowsUpdated(row, row);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void setInfo(MidiInfo info) {
/* 108 */       if (this.minfo != null) {
/* 109 */         this.minfo.autoSaveMap();
/*     */       }
/* 111 */       this.minfo = info;
/* 112 */       fireTableDataChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public PanelMidi(Main parent) {
/* 118 */     initComponents();
/* 119 */     this.parent = parent;
/* 120 */     this.model = new PlayerModel();
/* 121 */     this.tbPlayers.setModel(this.model);
/*     */   }
/*     */   
/*     */   void setMessage(String s) {
/* 125 */     if (isVisible())
/* 126 */       this.parent.setMessage(s, Color.yellow); 
/*     */   }
/*     */   
/*     */   public void setInfo(MidiInfo info) {
/* 130 */     info.openInfo();
/*     */     
/* 132 */     this.model.setInfo(info);
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
/* 144 */     this.jScrollPane1 = new JScrollPane();
/* 145 */     this.tbPlayers = new JTable();
/* 146 */     this.btSave = new JButton();
/*     */     
/* 148 */     addComponentListener(new ComponentAdapter() {
/*     */           public void componentShown(ComponentEvent evt) {
/* 150 */             PanelMidi.this.formComponentShown(evt);
/*     */           }
/*     */         });
/* 153 */     setLayout(new BorderLayout());
/*     */     
/* 155 */     this.jScrollPane1.setName("jScrollPane1");
/*     */     
/* 157 */     this.tbPlayers.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null }, , { null, null, null, null }, , { null, null, null, null }, , { null, null, null, null },  }, (Object[])new String[] { "Title 1", "Title 2", "Title 3", "Title 4" })
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 168 */           boolean[] canEdit = new boolean[] { false, true, true, true };
/*     */ 
/*     */ 
/*     */           
/*     */           public boolean isCellEditable(int rowIndex, int columnIndex) {
/* 173 */             return this.canEdit[columnIndex];
/*     */           }
/*     */         });
/* 176 */     this.tbPlayers.setName("tbPlayers");
/* 177 */     this.tbPlayers.getTableHeader().setReorderingAllowed(false);
/* 178 */     this.jScrollPane1.setViewportView(this.tbPlayers);
/* 179 */     if (this.tbPlayers.getColumnModel().getColumnCount() > 0) {
/* 180 */       this.tbPlayers.getColumnModel().getColumn(0).setResizable(false);
/* 181 */       this.tbPlayers.getColumnModel().getColumn(1).setResizable(false);
/*     */     } 
/*     */     
/* 184 */     add(this.jScrollPane1, "Center");
/*     */     
/* 186 */     this.btSave.setText("Save mapping");
/* 187 */     this.btSave.setName("Save mapping");
/* 188 */     this.btSave.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 190 */             PanelMidi.this.btSaveActionPerformed(evt);
/*     */           }
/*     */         });
/* 193 */     add(this.btSave, "South");
/*     */   }
/*     */ 
/*     */   
/*     */   private void formComponentShown(ComponentEvent evt) {
/* 198 */     this.parent.setMessage("Set MIDI channels mapping", Color.GREEN);
/*     */   }
/*     */ 
/*     */   
/*     */   private void btSaveActionPerformed(ActionEvent evt) {
/* 203 */     if (this.model.minfo != null)
/* 204 */       this.model.minfo.saveMap(); 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelMidi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */