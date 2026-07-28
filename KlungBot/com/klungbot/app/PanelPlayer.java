/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.AudioEngineer;
/*     */ import com.klungbot.Device;
/*     */ import com.klungbot.Instrument;
/*     */ import com.klungbot.Maestro;
/*     */ import com.klungbot.Player;
/*     */ import com.klungbot.doremi.Doremi;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Vector;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.DefaultTableModel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PanelPlayer
/*     */   extends JPanel
/*     */   implements ListSelectionListener
/*     */ {
/*     */   Main parent;
/*     */   PlayerModel model;
/*     */   Player player;
/*     */   private JComboBox cbDevice;
/*     */   private JLabel jLabel2;
/*     */   private JPanel jPanel1;
/*     */   private JPanel jPanel2;
/*     */   private JPanel jPanel3;
/*     */   private JScrollPane jScrollPane1;
/*     */   private JLabel lbDevice;
/*     */   private JLabel lbName;
/*     */   private JSpinner sLatency;
/*     */   private JTable tbPlayers;
/*     */   
/*     */   class PlayerModel
/*     */     extends AbstractTableModel
/*     */   {
/*  61 */     String header = "Instrument";
/*     */     Vector<Player> players;
/*     */     Maestro m;
/*     */     
/*     */     public PlayerModel() {
/*  66 */       this.m = PanelPlayer.this.parent.maestro;
/*  67 */       this.players = AudioEngineer.getPlayers();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getColumnName(int ci) {
/*  72 */       if (ci == 0) return this.header; 
/*  73 */       return Doremi.getTrackName(ci - 1);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRowCount() {
/*  78 */       return this.players.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getColumnCount() {
/*  83 */       return 11;
/*     */     }
/*     */ 
/*     */     
/*     */     public Class getColumnClass(int ci) {
/*  88 */       if (ci == 0) return String.class; 
/*  89 */       return Boolean.class;
/*     */     }
/*     */     
/*     */     public boolean isCellEditable(int row, int col) {
/*  93 */       return (col >= 1);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getValueAt(int rowIndex, int columnIndex) {
/*  98 */       if (columnIndex == 0) {
/*  99 */         return ((Player)this.players.get(rowIndex)).toString();
/*     */       }
/* 101 */       return new Boolean((this.players.get(rowIndex) == this.m.getPlayer(columnIndex - 1)));
/*     */     }
/*     */ 
/*     */     
/*     */     public void setValueAt(Object obj, int row, int col) {
/* 106 */       if (obj instanceof Boolean) {
/* 107 */         Boolean b = (Boolean)obj;
/* 108 */         if (!b.booleanValue()) {
/* 109 */           this.m.setPlayer(col - 1, null);
/*     */         } else {
/*     */           
/* 112 */           Player old = this.m.setPlayer(col - 1, this.players.get(row));
/* 113 */           if (old != null) {
/* 114 */             int idx = this.players.indexOf(old);
/* 115 */             fireTableCellUpdated(idx, col);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     void refresh() {
/* 122 */       this.m.getAudioEngineer(); Vector<Player> players = AudioEngineer.getPlayers();
/* 123 */       fireTableDataChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PanelPlayer(Main parent) {
/* 133 */     initComponents();
/* 134 */     this.parent = parent;
/* 135 */     this.model = new PlayerModel();
/*     */     
/* 137 */     this.tbPlayers.setModel(this.model);
/* 138 */     Integer latency = Integer.valueOf((int)Device.getDefaultLatency());
/* 139 */     this.sLatency.setValue(latency);
/*     */ 
/*     */     
/* 142 */     ListSelectionModel selectionModel = this.tbPlayers.getSelectionModel();
/* 143 */     selectionModel.setSelectionMode(0);
/* 144 */     selectionModel.addListSelectionListener(this);
/* 145 */     this.player = this.model.players.firstElement();
/*     */   }
/*     */   
/*     */   private void initDevices() {
/* 149 */     if (this.cbDevice.getItemCount() > 0)
/* 150 */       this.cbDevice.removeAllItems(); 
/* 151 */     this.cbDevice.addItem("None");
/* 152 */     ArrayList<String> ls = Device.listDevicePorts();
/* 153 */     if (ls != null && ls.size() > 0) {
/* 154 */       for (String s : ls) {
/* 155 */         this.cbDevice.addItem(s);
/*     */       }
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
/*     */   private void initComponents() {
/* 169 */     this.jScrollPane1 = new JScrollPane();
/* 170 */     this.tbPlayers = new JTable();
/* 171 */     this.jPanel1 = new JPanel();
/* 172 */     this.lbName = new JLabel();
/* 173 */     this.jPanel2 = new JPanel();
/* 174 */     this.lbDevice = new JLabel();
/* 175 */     this.cbDevice = new JComboBox();
/* 176 */     this.jPanel3 = new JPanel();
/* 177 */     this.jLabel2 = new JLabel();
/* 178 */     this.sLatency = new JSpinner();
/*     */     
/* 180 */     addComponentListener(new ComponentAdapter() {
/*     */           public void componentShown(ComponentEvent evt) {
/* 182 */             PanelPlayer.this.formComponentShown(evt);
/*     */           }
/*     */         });
/* 185 */     setLayout(new BorderLayout());
/*     */     
/* 187 */     this.jScrollPane1.setName("jScrollPane1");
/*     */     
/* 189 */     this.tbPlayers.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null }, , { null, null, null, null }, , { null, null, null, null }, , { null, null, null, null },  }, (Object[])new String[] { "Title 1", "Title 2", "Title 3", "Title 4" })
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
/* 200 */           boolean[] canEdit = new boolean[] { false, true, true, true };
/*     */ 
/*     */ 
/*     */           
/*     */           public boolean isCellEditable(int rowIndex, int columnIndex) {
/* 205 */             return this.canEdit[columnIndex];
/*     */           }
/*     */         });
/* 208 */     this.tbPlayers.setName("tbPlayers");
/* 209 */     this.tbPlayers.getTableHeader().setReorderingAllowed(false);
/* 210 */     this.jScrollPane1.setViewportView(this.tbPlayers);
/* 211 */     if (this.tbPlayers.getColumnModel().getColumnCount() > 0) {
/* 212 */       this.tbPlayers.getColumnModel().getColumn(0).setResizable(false);
/* 213 */       this.tbPlayers.getColumnModel().getColumn(1).setResizable(false);
/*     */     } 
/*     */     
/* 216 */     add(this.jScrollPane1, "Center");
/*     */     
/* 218 */     this.jPanel1.setName("jPanel1");
/*     */     
/* 220 */     this.lbName.setText("Instrument name");
/* 221 */     this.lbName.setName("lbName");
/* 222 */     this.jPanel1.add(this.lbName);
/*     */     
/* 224 */     this.jPanel2.setName("jPanel2");
/*     */     
/* 226 */     this.lbDevice.setText("Device : ");
/* 227 */     this.lbDevice.setName("lbDevice");
/* 228 */     this.jPanel2.add(this.lbDevice);
/*     */     
/* 230 */     this.cbDevice.setModel(new DefaultComboBoxModel<>(new String[] { "/dev/ACM0" }));
/* 231 */     this.cbDevice.setName("cbDevice");
/* 232 */     this.cbDevice.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 234 */             PanelPlayer.this.cbDeviceActionPerformed(evt);
/*     */           }
/*     */         });
/* 237 */     this.jPanel2.add(this.cbDevice);
/*     */     
/* 239 */     this.jPanel1.add(this.jPanel2);
/*     */     
/* 241 */     this.jPanel3.setName("jPanel3");
/*     */     
/* 243 */     this.jLabel2.setText("Latency(ms)");
/* 244 */     this.jLabel2.setName("jLabel2");
/* 245 */     this.jPanel3.add(this.jLabel2);
/*     */     
/* 247 */     this.sLatency.setName("sLatency");
/* 248 */     this.sLatency.setValue(Integer.valueOf(1000));
/* 249 */     this.sLatency.addChangeListener(new ChangeListener() {
/*     */           public void stateChanged(ChangeEvent evt) {
/* 251 */             PanelPlayer.this.sLatencyStateChanged(evt);
/*     */           }
/*     */         });
/* 254 */     this.jPanel3.add(this.sLatency);
/*     */     
/* 256 */     this.jPanel1.add(this.jPanel3);
/*     */     
/* 258 */     add(this.jPanel1, "South");
/*     */   }
/*     */   
/*     */   private void formComponentShown(ComponentEvent evt) {
/* 262 */     this.parent.setMessage("Set Instruments mapping", Color.GREEN);
/* 263 */     initDevices();
/* 264 */     setStatus();
/* 265 */     this.model.refresh();
/*     */   }
/*     */   
/*     */   private void sLatencyStateChanged(ChangeEvent evt) {
/* 269 */     if (this.player == null)
/* 270 */       return;  int latency = ((Integer)this.sLatency.getValue()).intValue();
/* 271 */     if (latency >= 0) {
/* 272 */       this.player.setLatency(latency);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void cbDeviceActionPerformed(ActionEvent evt) {
/* 278 */     if (this.player == null)
/* 279 */       return;  if (!(this.player instanceof Instrument))
/* 280 */       return;  Device dev = ((Instrument)this.player).getDevice();
/* 281 */     String ob = (String)this.cbDevice.getSelectedItem();
/* 282 */     if (ob == null)
/* 283 */       return;  if (ob.equals("None"))
/* 284 */       return;  dev.setPort(ob);
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueChanged(ListSelectionEvent e) {
/* 289 */     if (e.getValueIsAdjusting())
/* 290 */       return;  if (this.tbPlayers.getSelectedColumn() != 0)
/* 291 */       return;  int row = this.tbPlayers.getSelectedRow();
/* 292 */     if (row < 0)
/* 293 */       return;  this.player = this.model.players.get(row);
/* 294 */     setStatus();
/*     */   }
/*     */   
/*     */   private void setStatus() {
/* 298 */     if (this.player instanceof Instrument) {
/* 299 */       Device dev = ((Instrument)this.player).getDevice();
/* 300 */       this.lbName.setText(this.player.getId());
/* 301 */       this.lbDevice.setText("Device:");
/* 302 */       this.cbDevice.setVisible(true);
/* 303 */       String devName = dev.getPort();
/* 304 */       for (int i = 0; i < this.cbDevice.getItemCount(); i++) {
/* 305 */         Object it = this.cbDevice.getItemAt(i);
/* 306 */         if (devName.equals(it)) {
/* 307 */           this.cbDevice.setSelectedItem(Integer.valueOf(i));
/*     */         }
/*     */       } 
/* 310 */       this.sLatency.setValue(Integer.valueOf(dev.getLatency()));
/*     */     }
/* 312 */     else if (this.player instanceof com.klungbot.Synthesizer) {
/* 313 */       this.lbName.setText(this.player.getId());
/* 314 */       this.lbDevice.setText("Synthesizer");
/* 315 */       this.cbDevice.setVisible(false);
/* 316 */       this.sLatency.setValue(Integer.valueOf(this.player.getLatency()));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */