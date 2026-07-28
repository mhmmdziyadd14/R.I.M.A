/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.KlungbotHttpServer;
/*     */ import com.klungbot.KlungbotServerListener;
/*     */ import com.klungbot.KlungbotUDPServer;
/*     */ import com.klungbot.Maestro;
/*     */ import com.klungbot.Sequence;
/*     */ import com.klungbot.Timer;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.table.DefaultTableModel;
/*     */ import javax.swing.table.TableColumn;
/*     */ 
/*     */ 
/*     */ public class PanelTopHits
/*     */   extends JPanel
/*     */   implements KlungbotServerListener
/*     */ {
/*     */   Main parent;
/*     */   TopHits topHits;
/*     */   Maestro maestro;
/*     */   Timer timer;
/*  42 */   String newFileName = "default.12p"; boolean playing = false; KlungbotHttpServer httpServer; KlungbotUDPServer udpServer; private JLabel appDescLabel; private JButton btAdd;
/*  43 */   String fileName = this.newFileName;
/*     */   
/*     */   private JButton btOpen;
/*     */   
/*     */   private JToggleButton btPlay;
/*     */   private JButton btRemove;
/*     */   private JButton btSave;
/*     */   
/*     */   public PanelTopHits(Main p) {
/*  52 */     this.parent = p;
/*  53 */     this.maestro = p.getMaestro();
/*  54 */     initComponents();
/*  55 */     this.topHits = new TopHits();
/*  56 */     this.tTopHits.setModel(this.topHits);
/*  57 */     TableColumn c = this.tTopHits.getColumnModel().getColumn(1);
/*  58 */     c.setMaxWidth(100);
/*  59 */     c.setMinWidth(20);
/*  60 */     c.setPreferredWidth(50);
/*     */   }
/*     */   private JToggleButton btServer; private JButton btVisual1;
/*     */   private JButton btVote;
/*     */   private JPanel jPanel1;
/*     */   private JPanel jPanel2;
/*     */   private JScrollPane jScrollPane1;
/*     */   private JScrollPane jScrollPane2;
/*     */   private JTable tTopHits;
/*     */   private JTextArea taServer;
/*     */   
/*     */   private void initComponents() {
/*  72 */     this.jPanel1 = new JPanel();
/*  73 */     this.btOpen = new JButton();
/*  74 */     this.btSave = new JButton();
/*  75 */     this.btAdd = new JButton();
/*  76 */     this.btRemove = new JButton();
/*  77 */     this.btVote = new JButton();
/*  78 */     this.btPlay = new JToggleButton();
/*  79 */     this.btServer = new JToggleButton();
/*  80 */     this.btVisual1 = new JButton();
/*  81 */     this.jScrollPane1 = new JScrollPane();
/*  82 */     this.tTopHits = new JTable();
/*  83 */     this.jPanel2 = new JPanel();
/*  84 */     this.appDescLabel = new JLabel();
/*  85 */     this.jScrollPane2 = new JScrollPane();
/*  86 */     this.taServer = new JTextArea();
/*     */     
/*  88 */     addComponentListener(new ComponentAdapter() {
/*     */           public void componentShown(ComponentEvent evt) {
/*  90 */             PanelTopHits.this.formComponentShown(evt);
/*     */           }
/*     */         });
/*  93 */     setLayout(new BorderLayout());
/*     */     
/*  95 */     this.jPanel1.setName("jPanel1");
/*  96 */     this.jPanel1.setLayout(new GridLayout(1, 0));
/*     */     
/*  98 */     this.btOpen.setText("Open");
/*  99 */     this.btOpen.setName("btOpen");
/* 100 */     this.btOpen.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 102 */             PanelTopHits.this.btOpenActionPerformed(evt);
/*     */           }
/*     */         });
/* 105 */     this.jPanel1.add(this.btOpen);
/*     */     
/* 107 */     this.btSave.setText("Save");
/* 108 */     this.btSave.setName("btSave");
/* 109 */     this.btSave.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 111 */             PanelTopHits.this.btSaveActionPerformed(evt);
/*     */           }
/*     */         });
/* 114 */     this.jPanel1.add(this.btSave);
/*     */     
/* 116 */     this.btAdd.setText("Add");
/* 117 */     this.btAdd.setName("btAdd");
/* 118 */     this.btAdd.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 120 */             PanelTopHits.this.btAddActionPerformed(evt);
/*     */           }
/*     */         });
/* 123 */     this.jPanel1.add(this.btAdd);
/*     */     
/* 125 */     this.btRemove.setText("Remove");
/* 126 */     this.btRemove.setName("btRemove");
/* 127 */     this.btRemove.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 129 */             PanelTopHits.this.btRemoveActionPerformed(evt);
/*     */           }
/*     */         });
/* 132 */     this.jPanel1.add(this.btRemove);
/*     */     
/* 134 */     this.btVote.setText("Vote");
/* 135 */     this.btVote.setName("btVote");
/* 136 */     this.btVote.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 138 */             PanelTopHits.this.btVoteActionPerformed(evt);
/*     */           }
/*     */         });
/* 141 */     this.jPanel1.add(this.btVote);
/*     */     
/* 143 */     this.btPlay.setText("Play");
/* 144 */     this.btPlay.setName("btPlay");
/* 145 */     this.btPlay.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 147 */             PanelTopHits.this.btPlayActionPerformed(evt);
/*     */           }
/*     */         });
/* 150 */     this.jPanel1.add(this.btPlay);
/*     */     
/* 152 */     this.btServer.setText("Server");
/* 153 */     this.btServer.setName("btServer");
/* 154 */     this.btServer.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 156 */             PanelTopHits.this.btServerActionPerformed(evt);
/*     */           }
/*     */         });
/* 159 */     this.jPanel1.add(this.btServer);
/*     */     
/* 161 */     this.btVisual1.setText("Visualization");
/* 162 */     this.btVisual1.setName("btVisual1");
/* 163 */     this.btVisual1.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 165 */             PanelTopHits.this.btVisual1ActionPerformed(evt);
/*     */           }
/*     */         });
/* 168 */     this.jPanel1.add(this.btVisual1);
/*     */     
/* 170 */     add(this.jPanel1, "South");
/*     */     
/* 172 */     this.jScrollPane1.setName("jScrollPane1");
/*     */     
/* 174 */     this.tTopHits.setFont(new Font("DejaVu Sans", 0, 18));
/* 175 */     this.tTopHits.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null }, , { null, null, null, null }, , { null, null, null, null }, , { null, null, null, null },  }, (Object[])new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
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
/* 186 */     this.tTopHits.setName("tTopHits");
/* 187 */     this.tTopHits.setRowHeight(24);
/* 188 */     this.jScrollPane1.setViewportView(this.tTopHits);
/*     */     
/* 190 */     add(this.jScrollPane1, "Center");
/*     */     
/* 192 */     this.jPanel2.setBackground(Color.green);
/* 193 */     this.jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/* 194 */     this.jPanel2.setName("jPanel2");
/* 195 */     this.jPanel2.setLayout(new BorderLayout());
/*     */     
/* 197 */     this.appDescLabel.setFont(new Font("DejaVu Sans", 0, 18));
/* 198 */     this.appDescLabel.setText("<html><b>Playlist Server</b><p>Playlist can be voted remotelly using a laptop, tablet, or phone.<ol><li>Connect to the \"klungbot\" hot-spot<li>Browse to this server (see the Web Adress below)<li>Choose your favourite song</li>");
/* 199 */     this.appDescLabel.setName("appDescLabel");
/* 200 */     this.jPanel2.add(this.appDescLabel, "North");
/*     */     
/* 202 */     this.jScrollPane2.setAutoscrolls(true);
/* 203 */     this.jScrollPane2.setName("jScrollPane2");
/*     */     
/* 205 */     this.taServer.setColumns(20);
/* 206 */     this.taServer.setEditable(false);
/* 207 */     this.taServer.setRows(5);
/* 208 */     this.taServer.setText("Click the [Server] button to run the server.");
/* 209 */     this.taServer.setName("taServer");
/* 210 */     this.jScrollPane2.setViewportView(this.taServer);
/*     */     
/* 212 */     this.jPanel2.add(this.jScrollPane2, "Center");
/*     */     
/* 214 */     add(this.jPanel2, "East");
/*     */   }
/*     */   
/*     */   private void btVoteActionPerformed(ActionEvent evt) {
/* 218 */     int[] rows = this.tTopHits.getSelectedRows();
/* 219 */     for (int i = rows.length; i > 0; ) {
/* 220 */       i--;
/* 221 */       this.topHits.revote(rows[i]);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void btAddActionPerformed(ActionEvent evt) {
/* 226 */     List list = this.parent.getSelectedAlbums();
/* 227 */     if (list == null) {
/* 228 */       this.parent.setMessage("Cannot add. Please, select songs from the album first.", Color.YELLOW);
/*     */       return;
/*     */     } 
/* 231 */     for (Object j : list) {
/* 232 */       String fname = j.toString();
/* 233 */       this.topHits.vote(fname);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void btRemoveActionPerformed(ActionEvent evt) {
/* 238 */     int[] rows = this.tTopHits.getSelectedRows();
/* 239 */     for (int i = rows.length; i > 0; ) {
/* 240 */       i--;
/* 241 */       this.topHits.remove(rows[i]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void playNext() {
/* 247 */     Object fname = this.topHits.remove(0);
/* 248 */     if (fname != null) {
/* 249 */       this.parent.playDoremi(this.parent.baseFolder + (String)fname);
/*     */     } else {
/*     */       
/* 252 */       this.playing = false;
/* 253 */       this.btPlay.setSelected(false);
/* 254 */       this.btPlay.setText("Start");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void btPlayActionPerformed(ActionEvent evt) {
/* 259 */     this.playing = this.btPlay.isSelected();
/* 260 */     if (this.playing) {
/* 261 */       this.btPlay.setText("Stop");
/* 262 */       playNext();
/*     */     } else {
/*     */       
/* 265 */       this.btPlay.setText("Start");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void btOpenActionPerformed(ActionEvent evt) {
/* 271 */     FilenameFilter filter = new FilenameFilter()
/*     */       {
/*     */         public boolean accept(File d, String f) {
/* 274 */           return f.endsWith(".12p");
/*     */         }
/*     */       };
/* 277 */     String[] dir = (new File(this.parent.baseFolder + this.parent.listFolder)).list(filter);
/* 278 */     if (dir == null || dir.length == 0) {
/* 279 */       this.parent.setMessage("Sorry, no playlist is available yet", Color.RED);
/*     */       return;
/*     */     } 
/* 282 */     Arrays.sort((Object[])dir);
/* 283 */     Object input = JOptionPane.showInputDialog(this.parent, "Select the playlist", "Open playlist", 3, null, (Object[])dir, dir[0]);
/*     */ 
/*     */     
/* 286 */     if (input == null)
/*     */       return;  try {
/* 288 */       this.topHits.open(this.parent.baseFolder + this.parent.listFolder + input.toString());
/* 289 */       this.fileName = input.toString();
/* 290 */       this.parent.setMessage("Open playlist " + this.fileName, Color.GREEN);
/*     */     }
/* 292 */     catch (Exception ex) {
/* 293 */       this.parent.setMessage("Error opening " + input.toString() + ": " + ex.getMessage(), Color.RED);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void btSaveActionPerformed(ActionEvent evt) {
/* 299 */     String input = (String)JOptionPane.showInputDialog(null, "Please enter the new file name", "Save as", 3, null, null, this.fileName);
/*     */ 
/*     */     
/* 302 */     if (input == null)
/* 303 */       return;  if (!input.endsWith(".12p")) {
/* 304 */       this.fileName = input + ".12p";
/*     */     } else {
/* 306 */       this.fileName = input;
/*     */     }  try {
/* 308 */       this.topHits.save(this.parent.baseFolder + this.parent.listFolder + this.fileName);
/* 309 */       this.parent.setMessage("Saved as " + this.fileName, Color.GREEN);
/*     */     }
/* 311 */     catch (Exception ex) {
/* 312 */       this.parent.setMessage("Error saving " + this.fileName + ": " + ex.getMessage(), Color.RED);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void formComponentShown(ComponentEvent evt) {
/* 319 */     this.parent.setMessage("Playlist panel. Pick songs from the album, add them here, then play them all.", Color.GREEN);
/*     */   }
/*     */ 
/*     */   
/*     */   private void btServerActionPerformed(ActionEvent evt) {
/* 324 */     if (this.httpServer == null) {
/* 325 */       this.httpServer = new KlungbotHttpServer(this);
/*     */     }
/* 327 */     if (this.udpServer == null) {
/* 328 */       this.udpServer = new KlungbotUDPServer(this);
/*     */     }
/* 330 */     if (this.btServer.isSelected()) {
/* 331 */       this.taServer.append("\nStarting the server .... ");
/* 332 */       this.httpServer.begin();
/* 333 */       this.udpServer.begin();
/*     */     } else {
/* 335 */       this.httpServer.finish();
/* 336 */       this.udpServer.finish();
/* 337 */       this.maestro.delay(500L);
/* 338 */       this.taServer.append("\nServer stopped.\n=================================");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void btVisual1ActionPerformed(ActionEvent evt) {
/* 343 */     this.parent.startVisualization(null);
/*     */   }
/*     */   
/*     */   public void finished(Sequence seq) {
/* 347 */     if (this.playing) {
/* 348 */       playNext();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void voteSong(String name) {
/* 372 */     String s = this.topHits.vote(name);
/* 373 */     if (s != null) {
/* 374 */       this.parent.playDoremi(this.parent.baseFolder + this.parent.albumFolder + s);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void play(String fname) {
/* 380 */     this.parent.playDoremi(fname);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void play() {
/* 386 */     Object fname = this.topHits.remove(0);
/* 387 */     if (fname != null) {
/* 388 */       this.parent.playDoremi((String)fname);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void finish() {
/* 394 */     this.parent.stop();
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOn(byte note, byte forte, byte channel) {
/* 399 */     this.parent.midiOn(note, forte, channel);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff(byte note, byte channel) {
/* 404 */     this.parent.midiOff(note, channel);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlbumFolder() {
/* 409 */     return this.parent.albumFolder;
/*     */   }
/*     */ 
/*     */   
/*     */   public void log(String label, String msg) {
/* 414 */     if (msg != null) {
/* 415 */       this.taServer.append(String.format("\n%-20s: %s", new Object[] { label, msg }));
/*     */     } else {
/*     */       
/* 418 */       this.taServer.append(String.format("\n%s", new Object[] { label }));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelTopHits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */