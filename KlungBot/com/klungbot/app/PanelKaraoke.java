/*     */ package com.klungbot.app;
/*     */ import com.klungbot.Sequence;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.table.DefaultTableModel;
/*     */ import javax.swing.table.TableColumn;
/*     */ 
/*     */ public class PanelKaraoke extends JPanel {
/*     */   private JButton jButton1;
/*     */   private JButton jButton2;
/*     */   private JButton jButton3;
/*     */   private JButton jButton4;
/*     */   private JButton jButton5;
/*     */   
/*     */   public void updateSongList() {
/*  39 */     Statement stmt = null;
/*  40 */     Connection conn = null;
/*  41 */     ResultSet rs = null, rs2 = null;
/*     */     try {
/*  43 */       conn = DriverManager.getConnection("jdbc:derby:karaoke_db;create=true");
/*  44 */       DatabaseMetaData dbmd = conn.getMetaData();
/*  45 */       rs2 = dbmd.getTables(null, "APP", "SONGS2", null);
/*  46 */       if (rs2.next()) {
/*  47 */         DefaultTableModel model = (DefaultTableModel)this.jTable1.getModel();
/*  48 */         model.setRowCount(0);
/*     */         
/*  50 */         stmt = conn.createStatement();
/*  51 */         rs = stmt.executeQuery("select * from songs2 where title!='' order by title ");
/*  52 */         while (rs.next()) {
/*  53 */           model.addRow(new Object[] { rs.getString(3), rs.getString(4), rs.getString(2) });
/*     */         } 
/*     */       } 
/*  56 */     } catch (SQLException ex) {
/*  57 */       Logger.getLogger(KaraokeUpdater2.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */     } 
/*     */   }
/*     */   private JButton jButton6; private JButton jButton7; private JComboBox<String> jComboBox1; private JPanel jPanel1; private JPanel jPanel2; private JPanel jPanel3; private JPanel jPanel4; private JPanel jPanel5; private JPanel jPanel6; private JPanel jPanel7;
/*     */   private JPanel jPanel8;
/*     */   
/*     */   public PanelKaraoke() {
/*  64 */     initComponents();
/*  65 */     this.jTable1.setRowHeight(30);
/*  66 */     this.jTable2.setRowHeight(30);
/*     */     
/*  68 */     TableColumn column = this.jTable1.getColumnModel().getColumn(2);
/*  69 */     column.setMinWidth(0); column.setMaxWidth(0); column.setWidth(0);
/*  70 */     column.setPreferredWidth(0); doLayout();
/*     */     
/*  72 */     column = this.jTable2.getColumnModel().getColumn(1);
/*  73 */     column.setMinWidth(0); column.setMaxWidth(0); column.setWidth(0);
/*  74 */     column.setPreferredWidth(0); doLayout();
/*     */     
/*  76 */     updateSongList();
/*     */   }
/*     */ 
/*     */   
/*     */   private JScrollPane jScrollPane1;
/*     */   private JScrollPane jScrollPane2;
/*     */   private JSplitPane jSplitPane1;
/*     */   private JTable jTable1;
/*     */   private JTable jTable2;
/*     */   private JTextField jTextField1;
/*     */   
/*     */   private void initComponents() {
/*  88 */     this.jSplitPane1 = new JSplitPane();
/*  89 */     this.jPanel1 = new JPanel();
/*  90 */     this.jPanel3 = new JPanel();
/*  91 */     this.jPanel4 = new JPanel();
/*  92 */     this.jComboBox1 = new JComboBox<>();
/*  93 */     this.jTextField1 = new JTextField();
/*  94 */     this.jButton1 = new JButton();
/*  95 */     this.jButton3 = new JButton();
/*  96 */     this.jPanel5 = new JPanel();
/*  97 */     this.jButton2 = new JButton();
/*  98 */     this.jPanel2 = new JPanel();
/*  99 */     this.jScrollPane1 = new JScrollPane();
/* 100 */     this.jTable1 = new JTable();
/* 101 */     this.jPanel6 = new JPanel();
/* 102 */     this.jPanel8 = new JPanel();
/* 103 */     this.jButton4 = new JButton();
/* 104 */     this.jButton5 = new JButton();
/* 105 */     this.jButton6 = new JButton();
/* 106 */     this.jButton7 = new JButton();
/* 107 */     this.jPanel7 = new JPanel();
/* 108 */     this.jScrollPane2 = new JScrollPane();
/* 109 */     this.jTable2 = new JTable();
/*     */     
/* 111 */     setLayout(new BorderLayout());
/*     */     
/* 113 */     this.jSplitPane1.setMinimumSize(new Dimension(300, 30));
/* 114 */     this.jSplitPane1.setPreferredSize(new Dimension(300, 30));
/*     */     
/* 116 */     this.jPanel1.setLayout(new BorderLayout());
/*     */     
/* 118 */     this.jPanel4.setLayout(new FlowLayout(0));
/*     */     
/* 120 */     this.jComboBox1.setModel(new DefaultComboBoxModel<>(new String[] { "View By Song Title", "View By Composer", "View By Arranger", "View By Editor", "View By Origin" }));
/* 121 */     this.jPanel4.add(this.jComboBox1);
/*     */     
/* 123 */     this.jTextField1.setMinimumSize(new Dimension(200, 28));
/* 124 */     this.jTextField1.setPreferredSize(new Dimension(200, 28));
/* 125 */     this.jPanel4.add(this.jTextField1);
/*     */     
/* 127 */     this.jButton1.setText("Search");
/* 128 */     this.jButton1.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 130 */             PanelKaraoke.this.jButton1ActionPerformed(evt);
/*     */           }
/*     */         });
/* 133 */     this.jPanel4.add(this.jButton1);
/*     */     
/* 135 */     this.jButton3.setText("Add");
/* 136 */     this.jButton3.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 138 */             PanelKaraoke.this.jButton3ActionPerformed(evt);
/*     */           }
/*     */         });
/* 141 */     this.jPanel4.add(this.jButton3);
/*     */     
/* 143 */     this.jPanel3.add(this.jPanel4);
/*     */     
/* 145 */     this.jPanel5.setLayout(new FlowLayout(2));
/*     */     
/* 147 */     this.jButton2.setText("Refresh DB");
/* 148 */     this.jButton2.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 150 */             PanelKaraoke.this.jButton2ActionPerformed(evt);
/*     */           }
/*     */         });
/* 153 */     this.jPanel5.add(this.jButton2);
/*     */     
/* 155 */     this.jPanel3.add(this.jPanel5);
/*     */     
/* 157 */     this.jPanel1.add(this.jPanel3, "First");
/*     */     
/* 159 */     this.jPanel2.setLayout(new BorderLayout());
/*     */     
/* 161 */     this.jTable1.setFont(new Font("Ubuntu", 0, 24));
/* 162 */     this.jTable1.setModel(new DefaultTableModel(new Object[0][], (Object[])new String[] { "Song Title", "Composer", "Filepath" })
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 170 */           boolean[] canEdit = new boolean[] { false, false, false };
/*     */ 
/*     */ 
/*     */           
/*     */           public boolean isCellEditable(int rowIndex, int columnIndex) {
/* 175 */             return this.canEdit[columnIndex];
/*     */           }
/*     */         });
/* 178 */     this.jTable1.setGridColor(new Color(254, 254, 254));
/* 179 */     this.jScrollPane1.setViewportView(this.jTable1);
/*     */     
/* 181 */     this.jPanel2.add(this.jScrollPane1, "Center");
/*     */     
/* 183 */     this.jPanel1.add(this.jPanel2, "Center");
/*     */     
/* 185 */     this.jSplitPane1.setLeftComponent(this.jPanel1);
/*     */     
/* 187 */     this.jPanel6.setLayout(new BorderLayout());
/*     */     
/* 189 */     this.jPanel8.setLayout(new FlowLayout(2));
/*     */     
/* 191 */     this.jButton4.setText("Up");
/* 192 */     this.jButton4.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 194 */             PanelKaraoke.this.jButton4ActionPerformed(evt);
/*     */           }
/*     */         });
/* 197 */     this.jPanel8.add(this.jButton4);
/*     */     
/* 199 */     this.jButton5.setText("Down");
/* 200 */     this.jButton5.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 202 */             PanelKaraoke.this.jButton5ActionPerformed(evt);
/*     */           }
/*     */         });
/* 205 */     this.jPanel8.add(this.jButton5);
/*     */     
/* 207 */     this.jButton6.setText("Delete");
/* 208 */     this.jButton6.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 210 */             PanelKaraoke.this.jButton6ActionPerformed(evt);
/*     */           }
/*     */         });
/* 213 */     this.jPanel8.add(this.jButton6);
/*     */     
/* 215 */     this.jButton7.setText("Clear");
/* 216 */     this.jButton7.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 218 */             PanelKaraoke.this.jButton7ActionPerformed(evt);
/*     */           }
/*     */         });
/* 221 */     this.jPanel8.add(this.jButton7);
/*     */     
/* 223 */     this.jPanel6.add(this.jPanel8, "First");
/*     */     
/* 225 */     this.jPanel7.setLayout(new BorderLayout());
/*     */     
/* 227 */     this.jTable2.setModel(new DefaultTableModel(new Object[0][], (Object[])new String[] { "Playlist", "Filepath" })
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 235 */           boolean[] canEdit = new boolean[] { false, false };
/*     */ 
/*     */ 
/*     */           
/*     */           public boolean isCellEditable(int rowIndex, int columnIndex) {
/* 240 */             return this.canEdit[columnIndex];
/*     */           }
/*     */         });
/* 243 */     this.jScrollPane2.setViewportView(this.jTable2);
/*     */     
/* 245 */     this.jPanel7.add(this.jScrollPane2, "Center");
/*     */     
/* 247 */     this.jPanel6.add(this.jPanel7, "Center");
/*     */     
/* 249 */     this.jSplitPane1.setRightComponent(this.jPanel6);
/*     */     
/* 251 */     add(this.jSplitPane1, "Center");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void jButton1ActionPerformed(ActionEvent evt) {}
/*     */ 
/*     */   
/*     */   private void jButton2ActionPerformed(ActionEvent evt) {
/* 260 */     String[] ObjButtons = { "Yes", "No" };
/* 261 */     int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to update database?", "Database Updater", -1, 2, null, (Object[])ObjButtons, ObjButtons[1]);
/*     */ 
/*     */ 
/*     */     
/* 265 */     if (PromptResult == 0)
/*     */     {
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
/* 300 */       SwingUtilities.invokeLater(new RunUpdate(this));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void jButton3ActionPerformed(ActionEvent evt) {
/* 307 */     String title = this.jTable1.getValueAt(this.jTable1.getSelectedRow(), 0).toString();
/* 308 */     String composer = this.jTable1.getValueAt(this.jTable1.getSelectedRow(), 1).toString();
/* 309 */     String filepath = this.jTable1.getValueAt(this.jTable1.getSelectedRow(), 2).toString();
/* 310 */     String playlist = title + " (" + composer + ")";
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 315 */     DefaultTableModel model = (DefaultTableModel)this.jTable2.getModel();
/* 316 */     model.addRow(new Object[] { playlist, filepath });
/*     */   }
/*     */ 
/*     */   
/*     */   private void jButton6ActionPerformed(ActionEvent evt) {
/* 321 */     DefaultTableModel model = (DefaultTableModel)this.jTable2.getModel();
/* 322 */     model.removeRow(this.jTable2.getSelectedRow());
/*     */   }
/*     */ 
/*     */   
/*     */   private void jButton7ActionPerformed(ActionEvent evt) {
/* 327 */     DefaultTableModel model = (DefaultTableModel)this.jTable2.getModel();
/* 328 */     model.setRowCount(0);
/*     */   }
/*     */ 
/*     */   
/*     */   private void jButton4ActionPerformed(ActionEvent evt) {
/* 333 */     DefaultTableModel model = (DefaultTableModel)this.jTable2.getModel();
/* 334 */     int i = this.jTable2.getSelectedRow();
/* 335 */     if (i > 0) {
/* 336 */       model.moveRow(i, i, i - 1);
/* 337 */       this.jTable2.setRowSelectionInterval(i - 1, i - 1);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void jButton5ActionPerformed(ActionEvent evt) {
/* 343 */     DefaultTableModel model = (DefaultTableModel)this.jTable2.getModel();
/* 344 */     int i = this.jTable2.getSelectedRow();
/* 345 */     if (i < this.jTable2.getRowCount() - 1) {
/* 346 */       model.moveRow(i, i, i + 1);
/* 347 */       this.jTable2.setRowSelectionInterval(i + 1, i + 1);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void finished(Sequence seq) {
/* 352 */     System.out.println("Lose it");
/* 353 */     if (isPlaylistFilled());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPlaylistFilled() {
/* 358 */     if (this.jTable2.getRowCount() > 0) return true; 
/* 359 */     return false;
/*     */   }
/*     */   
/*     */   public File getSelectedFile() {
/* 363 */     DefaultTableModel model = (DefaultTableModel)this.jTable2.getModel();
/*     */     
/* 365 */     int i = this.jTable2.getSelectedRow();
/* 366 */     if (i == -1) i = 0;
/*     */     
/* 368 */     String filepath = this.jTable2.getValueAt(i, 1).toString();
/* 369 */     model.removeRow(i);
/*     */     
/* 371 */     return new File(filepath);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelKaraoke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */