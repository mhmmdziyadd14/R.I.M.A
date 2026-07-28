/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.Device;
/*     */ import com.klungbot.DoremiReader;
/*     */ import com.klungbot.Maestro;
/*     */ import com.klungbot.MaestroListener;
/*     */ import com.klungbot.MidiInfo;
/*     */ import com.klungbot.ParserException;
/*     */ import com.klungbot.Sequence;
/*     */ import com.klungbot.ServerListener;
/*     */ import com.klungbot.Synthesizer;
/*     */ import com.klungbot.doremi.ArrayRythm;
/*     */ import com.klungbot.doremi.Effect;
/*     */ import com.klungbot.util.FileTreeNode;
/*     */ import com.klungbot.util.Options;
/*     */ import java.awt.Color;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
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
/*     */ public class MainConsole
/*     */   extends JFrame
/*     */   implements MaestroListener, ServerListener
/*     */ {
/*     */   private int statusAngklung;
/*     */   Maestro maestro;
/*     */   Effect effect;
/*     */   FileTreeNode albumSelected;
/*     */   FileTreeNode midiSelected;
/*     */   FileTreeNode draftSelected;
/*     */   PanelMelody melodyPanel;
/*     */   PanelEdit editPanel;
/*     */   PanelTopHits hitsPanel;
/*     */   PanelVisual visualPanel;
/*     */   PanelPlayer playerPanel;
/*     */   PanelMidi midiPanel;
/*     */   PanelAnimation animPanel;
/*     */   PanelAccomp accompPanel;
/*     */   PanelOrchestra orchestraPanel;
/*     */   PanelKaraoke karaokePanel;
/*     */   AnimationFrame animFrame;
/*     */   boolean changed = false;
/*     */   DoremiReader doremi;
/*     */   String baseFolder;
/*     */   String draftFolder;
/*     */   String albumFolder;
/*     */   String midiFolder;
/*     */   String listFolder;
/*     */   String soundFolder;
/*     */   Synthesizer midi;
/*     */   MidiInfo midiInfo;
/*  76 */   ArrayList<MainListener> listeners = new ArrayList<>();
/*     */ 
/*     */   
/*     */   public MainConsole(String bf) {
/*  80 */     this.baseFolder = bf;
/*  81 */     this.statusAngklung = 0;
/*     */     
/*  83 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*  84 */     initOptions();
/*     */     
/*  86 */     this.doremi = new DoremiReader();
/*  87 */     this.maestro = new Maestro(this.baseFolder, this);
/*  88 */     this.effect = this.maestro.getEffect();
/*     */     
/*  90 */     initMaestro();
/*  91 */     initMidi();
/*  92 */     initAlbum();
/*  93 */     initDraft();
/*  94 */     this.albumSelected = null;
/*  95 */     this.midiSelected = null;
/*  96 */     this.draftSelected = null;
/*     */     
/*  98 */     playDoremi("./album/PRD/02 - Que_Sera_Sera (kurulung-centok).123");
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
/* 110 */     setDefaultCloseOperation(3);
/* 111 */     setTitle("Klungbot Maestro");
/* 112 */     addWindowListener(new WindowAdapter() {
/*     */           public void windowOpened(WindowEvent evt) {
/* 114 */             MainConsole.this.formWindowOpened(evt);
/*     */           }
/*     */           public void windowClosing(WindowEvent evt) {
/* 117 */             MainConsole.this.formWindowClosing(evt);
/*     */           }
/*     */         });
/*     */     
/* 121 */     pack();
/*     */   }
/*     */   
/*     */   void addMainListener(MainListener ml) {
/* 125 */     this.listeners.add(ml);
/*     */   }
/*     */   
/*     */   void removeMainListener(MainListener ml) {
/* 129 */     this.listeners.remove(ml);
/*     */   }
/*     */   
/*     */   boolean isListenerVisible() {
/* 133 */     for (MainListener l : this.listeners) {
/* 134 */       if (l.isReady()) return true; 
/*     */     } 
/* 136 */     return false;
/*     */   }
/*     */   
/*     */   void initOptions() {
/* 140 */     if (!Options.load(this.baseFolder)) {
/* 141 */       Options.initDefault();
/*     */     }
/* 143 */     this.draftFolder = Options.get("folder.draft") + File.separator;
/* 144 */     this.albumFolder = Options.get("folder.album") + File.separator;
/* 145 */     this.midiFolder = Options.get("folder.midi") + File.separator;
/* 146 */     this.listFolder = Options.get("folder.list") + File.separator;
/*     */   }
/*     */   
/*     */   void initMidi() {
/* 150 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.midiFolder);
/* 151 */     root.expandAll(".mid");
/* 152 */     DefaultTreeModel model = new DefaultTreeModel((TreeNode)root);
/* 153 */     this.midiSelected = null;
/*     */   }
/*     */   
/*     */   public void initAlbum() {
/* 157 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.albumFolder);
/* 158 */     root.expandAll(".123");
/* 159 */     DefaultTreeModel model = new DefaultTreeModel((TreeNode)root);
/* 160 */     this.albumSelected = null;
/*     */   }
/*     */   
/*     */   public void initDraft() {
/* 164 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.draftFolder);
/* 165 */     root.expandAll(".123");
/* 166 */     this.draftSelected = null;
/*     */   }
/*     */   
/*     */   public void initList() {
/* 170 */     initDraft();
/* 171 */     initAlbum();
/* 172 */     initMidi();
/*     */   }
/*     */   
/*     */   public String getSelectedFile() {
/* 176 */     if (this.draftSelected != null) {
/* 177 */       return this.draftSelected.getFile().getName();
/*     */     }
/* 179 */     if (this.albumSelected != null) {
/* 180 */       return this.albumSelected.getFile().getName();
/*     */     }
/* 182 */     if (this.midiSelected != null) {
/* 183 */       return this.midiSelected.getFile().getName();
/*     */     }
/* 185 */     return null;
/*     */   }
/*     */   
/*     */   public File getSelectedDraft() {
/* 189 */     if (this.draftSelected != null && 
/* 190 */       this.draftSelected.isLeaf()) {
/* 191 */       return this.draftSelected.getFile();
/*     */     }
/*     */     
/* 194 */     return null;
/*     */   }
/*     */   
/*     */   public File getSelectedDraftFolder() {
/* 198 */     if (this.draftSelected != null) {
/* 199 */       if (this.draftSelected.isLeaf()) {
/* 200 */         FileTreeNode p = (FileTreeNode)this.draftSelected.getParent();
/* 201 */         return p.getFile();
/*     */       } 
/* 203 */       return this.draftSelected.getFile();
/*     */     } 
/* 205 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getSelectedMidi() {
/* 210 */     if (this.midiSelected != null) {
/* 211 */       return this.midiSelected.getFile();
/*     */     }
/* 213 */     return null;
/*     */   }
/*     */   
/*     */   public List getSelectedAlbums() {
/* 217 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void initMaestro() {
/* 223 */     this.maestro.initPlayers();
/*     */   }
/*     */   
/*     */   public void playMidi(File file, MidiInfo info) {
/*     */     try {
/* 228 */       Sequence seq = MidiSystem.getSequence(file);
/* 229 */       if (this.midiPanel.isVisible() && info != null) {
/* 230 */         info.autoSaveMap();
/* 231 */         this.maestro.setMidiMap(info);
/*     */       } else {
/*     */         
/* 234 */         MidiInfo mi = new MidiInfo(this.midiSelected.getFile(), this.maestro);
/* 235 */         mi.openMap();
/* 236 */         this.maestro.setMidiMap(mi);
/*     */       } 
/* 238 */       this.maestro.queue(seq);
/*     */     }
/* 240 */     catch (Exception ex) {
/* 241 */       ex.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void analyzeDoremi(File file) {
/*     */     try {
/* 247 */       Sequence sequence = this.doremi.read(file);
/* 248 */       this.orchestraPanel.changeSequence(sequence);
/*     */     }
/* 250 */     catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public void selectDoremi(File file) {
/*     */     try {
/* 256 */       if (this.orchestraPanel.isVisible()) {
/* 257 */         Sequence sequence = this.doremi.read(file);
/* 258 */         this.orchestraPanel.changeSequence(sequence);
/*     */       }
/*     */     
/* 261 */     } catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public void playDoremi(String fname) {
/*     */     try {
/* 267 */       File file = new File(fname);
/* 268 */       Sequence sequence = this.doremi.read(file);
/* 269 */       this.maestro.queue(sequence);
/*     */     }
/* 271 */     catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public void playDoremi(File file) {
/*     */     try {
/* 277 */       Sequence sequence = this.doremi.read(file);
/* 278 */       this.maestro.queue(sequence);
/*     */     }
/* 280 */     catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private void playEditor() {
/*     */     try {
/* 286 */       String buffer = this.editPanel.getSelectedSong();
/* 287 */       Sequence sequence = this.doremi.read(buffer);
/* 288 */       this.maestro.queue(sequence);
/*     */     }
/* 290 */     catch (ParserException ex) {
/* 291 */       this.editPanel.setCaretPosition(ex.getRow(), ex.getCol());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void playKaraoke() {
/* 296 */     playDoremi(this.karaokePanel.getSelectedFile());
/*     */   }
/*     */   
/*     */   public void play() {
/* 300 */     if (this.albumSelected != null) {
/* 301 */       if (this.albumSelected.isLeaf()) {
/* 302 */         playDoremi(this.albumSelected.getFile());
/*     */       }
/* 304 */     } else if (this.draftSelected != null) {
/* 305 */       if (this.draftSelected.isLeaf()) {
/* 306 */         playDoremi(this.draftSelected.getFile());
/*     */       }
/* 308 */     } else if (this.midiSelected != null) {
/* 309 */       playMidi(this.midiSelected.getFile(), this.midiInfo);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void finish() {
/* 314 */     this.maestro.finish();
/*     */   }
/*     */ 
/*     */   
/*     */   private void formWindowOpened(WindowEvent evt) {
/* 319 */     System.out.println("Starting ...");
/*     */   }
/*     */ 
/*     */   
/*     */   private void formWindowClosing(WindowEvent evt) {
/* 324 */     System.out.println("Closing");
/*     */     
/*     */     try {
/* 327 */       this.maestro.finish();
/* 328 */       this.maestro.playOff(0L);
/*     */     }
/* 330 */     catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/*     */     final String baseFolder;
/* 338 */     if (args.length < 1) {
/* 339 */       baseFolder = "." + File.separator;
/* 340 */     } else if (args[0].endsWith(File.separator)) {
/* 341 */       baseFolder = args[0];
/*     */     } else {
/*     */       
/* 344 */       baseFolder = args[0] + File.separator;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 352 */     ArrayRythm.initDefault();
/* 353 */     EventQueue.invokeLater(new Runnable() {
/*     */           public void run() {
/* 355 */             MainConsole.blueStart();
/* 356 */             new MainConsole(baseFolder);
/* 357 */             MainConsole.blueStop();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void updatePlayerStatus(String name, String status, Color color) {
/* 367 */     JLabel lb = new JLabel();
/* 368 */     lb.setForeground(color);
/* 369 */     lb.setHorizontalAlignment(0);
/* 370 */     lb.setText(name);
/* 371 */     lb.setToolTipText(status);
/* 372 */     lb.setForeground(color);
/*     */   }
/*     */ 
/*     */   
/*     */   public void connected(Device dev) {
/* 377 */     final String name = dev.getName();
/* 378 */     final String status = "Connected to " + dev.getPort();
/* 379 */     SwingUtilities.invokeLater(new Runnable() {
/*     */           public void run() {
/* 381 */             MainConsole.this.updatePlayerStatus(name, status, Color.GREEN);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void disconnected(Device dev) {
/* 389 */     final String name = dev.getName();
/* 390 */     final String status = "Cannot connect to " + dev.getPort();
/* 391 */     SwingUtilities.invokeLater(new Runnable() {
/*     */           public void run() {
/* 393 */             MainConsole.this.updatePlayerStatus(name, status, Color.RED);
/*     */           }
/*     */         });
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
/*     */   public void changeForte(int forte) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void changeTempo(int tempo) {
/* 417 */     int value = tempo;
/* 418 */     SwingUtilities.invokeLater(new Runnable() {
/*     */           public void run() {
/* 420 */             MainConsole.this.changed = true;
/*     */           }
/*     */         });
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
/*     */   public void changeKey(int value) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void changeTick(long tick, long nextOn) {
/* 443 */     long value = tick;
/* 444 */     long no = nextOn;
/* 445 */     SwingUtilities.invokeLater(new Runnable() {
/*     */           public void run() {
/* 447 */             MainConsole.this.changed = true;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void started(Sequence seq) {
/* 454 */     if (seq == null)
/* 455 */       return;  StringBuilder s = new StringBuilder();
/* 456 */     s.append(" ");
/* 457 */     s.append(seq.titles.get(0));
/* 458 */     if (seq.composers != null) {
/* 459 */       s.append(", Composer: ");
/* 460 */       s.append(seq.composers.get(0));
/*     */     } 
/* 462 */     if (seq.arrangers != null) {
/* 463 */       s.append(", Arranger: ");
/* 464 */       s.append(seq.arrangers.get(0));
/*     */     } 
/* 466 */     if (seq.editor != null) {
/* 467 */       s.append(", Editor: ");
/* 468 */       s.append(seq.editor);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void finished(Sequence seq) {
/* 474 */     if (seq == null)
/* 475 */       return;  this.hitsPanel.finished(seq);
/*     */ 
/*     */     
/* 478 */     if (this.karaokePanel.isVisible() && this.karaokePanel.isPlaylistFilled()) {
/* 479 */       playDoremi(this.karaokePanel.getSelectedFile());
/*     */     }
/*     */     
/* 482 */     if (this.visualPanel.isReady()) {
/* 483 */       this.visualPanel.finished(seq);
/*     */     }
/* 485 */     if (this.orchestraPanel.isReady()) {
/* 486 */       this.orchestraPanel.finished(seq);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void waiting(long waited) {
/* 492 */     if (this.visualPanel.isReady()) {
/* 493 */       this.visualPanel.waiting(waited);
/*     */     }
/* 495 */     if (this.orchestraPanel.isReady()) {
/* 496 */       this.orchestraPanel.waiting(waited);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 502 */     this.maestro.finish();
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOn(byte data1, byte data2, byte data3) {
/* 507 */     this.maestro.midiOn(data1, data2, data3);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff(byte data1, byte data2) {
/* 512 */     this.maestro.midiOff(data1, data2);
/*     */   }
/*     */ 
/*     */   
/*     */   public Maestro getMaestro() {
/* 517 */     return this.maestro;
/*     */   }
/*     */   
/*     */   public String getAlbumFolder() {
/* 521 */     return this.albumFolder;
/*     */   }
/*     */   
/*     */   static void blueStart() {}
/*     */   
/*     */   static void blueStop() {}
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\MainConsole.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */