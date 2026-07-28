/*      */ package com.klungbot.app;
/*      */ import com.klungbot.MidiInfo;
/*      */ import com.klungbot.Sequence;
/*      */ import com.klungbot.util.FileTreeNode;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.GridLayout;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.File;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JTree;
/*      */ import javax.swing.border.TitledBorder;
/*      */ import javax.swing.event.ChangeEvent;
/*      */ import javax.swing.event.ChangeListener;
/*      */ import javax.swing.event.TreeSelectionEvent;
/*      */ import javax.swing.event.TreeSelectionListener;
/*      */ 
/*      */ public class Main extends JFrame implements MaestroListener, ServerListener {
/*      */   private int statusAngklung;
/*      */   Maestro maestro;
/*      */   Effect effect;
/*      */   FileTreeNode albumSelected;
/*      */   FileTreeNode midiSelected;
/*      */   FileTreeNode draftSelected;
/*      */   PanelMelody melodyPanel;
/*      */   PanelEdit editPanel;
/*      */   PanelTopHits hitsPanel;
/*      */   PanelVisual visualPanel;
/*      */   PanelPlayer playerPanel;
/*      */   PanelMidi midiPanel;
/*      */   PanelAnimation animPanel;
/*      */   PanelAccomp accompPanel;
/*      */   PanelOrchestra orchestraPanel;
/*      */   PanelKaraoke karaokePanel;
/*      */   AnimationFrame animFrame;
/*      */   boolean changed = false;
/*      */   DoremiReader doremi;
/*      */   String baseFolder;
/*      */   String draftFolder;
/*      */   String albumFolder;
/*      */   String midiFolder;
/*      */   String listFolder;
/*      */   String soundFolder;
/*      */   Synthesizer midi;
/*      */   MidiInfo midiInfo;
/*      */   private JButton btFinish;
/*      */   private JButton btPause;
/*      */   private JButton btPlay;
/*      */   private JButton btStart;
/*      */   public JCheckBox cbMultitones;
/*      */   private JCheckBox cbSustain;
/*      */   public JCheckBox cbTrack;
/*      */   private JCheckBox cbTrackA;
/*      */   private JCheckBox cbTrackB;
/*      */   private JCheckBox cbTrackC;
/*      */   private JCheckBox cbTrackD;
/*      */   private JCheckBox cbTrackE;
/*      */   private JCheckBox cbTrackF;
/*      */   private JCheckBox cbTrackG;
/*      */   public JCheckBox cbVoice1;
/*      */   private JCheckBox cbVoice2;
/*   72 */   ArrayList<MainListener> listeners = new ArrayList<>(); private JCheckBox cbVoice3; private JCheckBox cbVoice4; private JCheckBox cbVoice5; private JCheckBox cbVoice6; private JCheckBox cbVoice7; private JCheckBox cbVoice8; private JButton jButton1; private JButton jButton2; private JButton jButton3; private JLabel jLabel1; private JPanel jPanel1; private JPanel jPanel10; private JPanel jPanel11; private JPanel jPanel12; private JPanel jPanel13;
/*      */   private JPanel jPanel14;
/*      */   
/*      */   public Main(String bf) {
/*   76 */     this.baseFolder = bf;
/*   77 */     this.statusAngklung = 0;
/*   78 */     initComponents();
/*   79 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*   80 */     setMinimumSize(new Dimension(800, 500));
/*   81 */     setMaximumSize(new Dimension(1600, 1200));
/*   82 */     setSize((tk.getScreenSize()).width, (tk.getScreenSize()).height - 40);
/*      */     
/*      */     try {
/*   85 */       Image im = ImageIO.read(getClass().getResource("/resources/icon/klung.png"));
/*   86 */       setIconImage(im);
/*   87 */     } catch (IOException ex) {}
/*   88 */     initOptions();
/*      */     
/*   90 */     this.doremi = new DoremiReader();
/*   91 */     this.maestro = new Maestro(this.baseFolder, this);
/*   92 */     this.effect = this.maestro.getEffect();
/*      */     
/*   94 */     initMaestro();
/*   95 */     initMidi();
/*   96 */     initAlbum();
/*   97 */     initDraft();
/*   98 */     this.albumSelected = null;
/*   99 */     this.midiSelected = null;
/*  100 */     this.draftSelected = null;
/*      */     
/*  102 */     this.melodyPanel = new PanelMelody(this);
/*  103 */     this.editPanel = new PanelEdit(this);
/*  104 */     this.hitsPanel = new PanelTopHits(this);
/*  105 */     this.visualPanel = new PanelVisual(this);
/*  106 */     this.playerPanel = new PanelPlayer(this);
/*  107 */     this.midiPanel = new PanelMidi(this);
/*  108 */     this.accompPanel = new PanelAccomp(this);
/*  109 */     this.animPanel = new PanelAnimation(this);
/*  110 */     this.orchestraPanel = new PanelOrchestra(this);
/*  111 */     this.karaokePanel = new PanelKaraoke();
/*      */     
/*  113 */     this.tabPanel.addTab("", new ImageIcon(getClass().getResource("/resources/icon24/player.png")), this.karaokePanel);
/*  114 */     this.tabPanel.addTab("", new ImageIcon(getClass().getResource("/resources/icon24/player.png")), this.visualPanel);
/*  115 */     this.tabPanel.addTab("", new ImageIcon(getClass().getResource("/resources/icon24/keyboard.png")), this.melodyPanel);
/*  116 */     this.tabPanel.addTab("", new ImageIcon(getClass().getResource("/resources/icon24/tophits.png")), this.hitsPanel);
/*  117 */     this.tabPanel.addTab("", new ImageIcon(getClass().getResource("/resources/icon24/analysis.png")), this.orchestraPanel);
/*  118 */     this.tabPanel.addTab("", new ImageIcon(getClass().getResource("/resources/icon24/editor.png")), this.editPanel);
/*  119 */     this.tabPanel.addTab("", new ImageIcon(getClass().getResource("/resources/icon24/midi.png")), this.midiPanel);
/*  120 */     this.tabPanel.addTab("", new ImageIcon(getClass().getResource("/resources/icon24/instrument.png")), this.playerPanel);
/*      */     
/*  122 */     this.cbSustain.setSelected((this.maestro.getEffect()).sustain);
/*  123 */     this.cbMultitones.setSelected((this.maestro.getEffect()).multinote);
/*      */   }
/*      */   private JPanel jPanel15; private JPanel jPanel2; private JPanel jPanel3; private JPanel jPanel4; private JPanel jPanel5; private JPanel jPanel6; private JPanel jPanel7; private JPanel jPanel8; private JPanel jPanel9; private JScrollPane jScrollPane1; private JScrollPane jScrollPane2; private JScrollPane jScrollPane3; private JScrollPane jScrollPane4; private JLabel lbInfo; private JLabel lbMessage; private JPanel pPlayers; private JPanel pPosition; private JPanel pTempo; private JPanel pVibrate; private JPanel panelLeft; private JPanel panelRight; private JPanel panelSouth; private JSlider sPosition; private JSlider sTempo; private JSlider sVibrate;
/*      */   private JSplitPane splitCenter;
/*      */   private JTabbedPane splitLeft;
/*      */   private JTabbedPane tabPanel;
/*      */   private JTree treeAlbum;
/*      */   private JTree treeDraft;
/*      */   private JTree treeMidi;
/*      */   private JTree treePlaylist;
/*      */   
/*      */   private void initComponents() {
/*  135 */     this.jPanel6 = new JPanel();
/*  136 */     this.jPanel2 = new JPanel();
/*  137 */     this.jPanel14 = new JPanel();
/*  138 */     this.jPanel5 = new JPanel();
/*  139 */     this.jLabel1 = new JLabel();
/*  140 */     this.jPanel9 = new JPanel();
/*  141 */     this.btPlay = new JButton();
/*  142 */     this.btStart = new JButton();
/*  143 */     this.btPause = new JButton();
/*  144 */     this.btFinish = new JButton();
/*  145 */     this.jPanel10 = new JPanel();
/*  146 */     this.pPosition = new JPanel();
/*  147 */     this.sPosition = new JSlider();
/*  148 */     this.pTempo = new JPanel();
/*  149 */     this.sTempo = new JSlider();
/*  150 */     this.pVibrate = new JPanel();
/*  151 */     this.sVibrate = new JSlider();
/*  152 */     this.jPanel7 = new JPanel();
/*  153 */     this.jPanel13 = new JPanel();
/*  154 */     this.jPanel12 = new JPanel();
/*  155 */     this.cbTrack = new JCheckBox();
/*  156 */     this.cbTrackA = new JCheckBox();
/*  157 */     this.cbTrackB = new JCheckBox();
/*  158 */     this.cbTrackC = new JCheckBox();
/*  159 */     this.cbTrackD = new JCheckBox();
/*  160 */     this.cbTrackE = new JCheckBox();
/*  161 */     this.cbTrackF = new JCheckBox();
/*  162 */     this.cbTrackG = new JCheckBox();
/*  163 */     this.jPanel11 = new JPanel();
/*  164 */     this.cbVoice1 = new JCheckBox();
/*  165 */     this.cbVoice2 = new JCheckBox();
/*  166 */     this.cbVoice3 = new JCheckBox();
/*  167 */     this.cbVoice4 = new JCheckBox();
/*  168 */     this.cbVoice5 = new JCheckBox();
/*  169 */     this.cbVoice6 = new JCheckBox();
/*  170 */     this.cbVoice7 = new JCheckBox();
/*  171 */     this.cbVoice8 = new JCheckBox();
/*  172 */     this.jPanel8 = new JPanel();
/*  173 */     this.cbSustain = new JCheckBox();
/*  174 */     this.cbMultitones = new JCheckBox();
/*  175 */     this.splitCenter = new JSplitPane();
/*  176 */     this.panelRight = new JPanel();
/*  177 */     this.tabPanel = new JTabbedPane();
/*  178 */     this.panelLeft = new JPanel();
/*  179 */     this.splitLeft = new JTabbedPane();
/*  180 */     this.jPanel15 = new JPanel();
/*  181 */     this.jPanel1 = new JPanel();
/*  182 */     this.jButton1 = new JButton();
/*  183 */     this.jButton2 = new JButton();
/*  184 */     this.jButton3 = new JButton();
/*  185 */     this.jPanel3 = new JPanel();
/*  186 */     this.jScrollPane4 = new JScrollPane();
/*  187 */     this.treePlaylist = new JTree();
/*  188 */     this.jScrollPane2 = new JScrollPane();
/*  189 */     this.treeAlbum = new JTree();
/*  190 */     this.jScrollPane3 = new JScrollPane();
/*  191 */     this.treeDraft = new JTree();
/*  192 */     this.jScrollPane1 = new JScrollPane();
/*  193 */     this.treeMidi = new JTree();
/*  194 */     this.panelSouth = new JPanel();
/*  195 */     this.pPlayers = new JPanel();
/*  196 */     this.jPanel4 = new JPanel();
/*  197 */     this.lbMessage = new JLabel();
/*  198 */     this.lbInfo = new JLabel();
/*      */     
/*  200 */     setDefaultCloseOperation(3);
/*  201 */     setTitle("Klungbot Maestro");
/*  202 */     addWindowListener(new WindowAdapter() {
/*      */           public void windowOpened(WindowEvent evt) {
/*  204 */             Main.this.formWindowOpened(evt);
/*      */           }
/*      */           public void windowClosing(WindowEvent evt) {
/*  207 */             Main.this.formWindowClosing(evt);
/*      */           }
/*      */         });
/*      */     
/*  211 */     this.jPanel6.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
/*  212 */     this.jPanel6.setName("jPanel6");
/*  213 */     this.jPanel6.setLayout(new BorderLayout());
/*      */     
/*  215 */     this.jPanel2.setMinimumSize(new Dimension(600, 45));
/*  216 */     this.jPanel2.setName("jPanel2");
/*  217 */     this.jPanel2.setLayout(new GridLayout(2, 0));
/*      */     
/*  219 */     this.jPanel14.setName("jPanel14");
/*  220 */     this.jPanel14.setLayout(new BorderLayout());
/*      */     
/*  222 */     this.jPanel5.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
/*  223 */     this.jPanel5.setName("jPanel5");
/*      */     
/*  225 */     this.jLabel1.setHorizontalAlignment(0);
/*  226 */     this.jLabel1.setIcon(new ImageIcon(getClass().getResource("/resources/header48.png")));
/*  227 */     this.jLabel1.setMinimumSize(new Dimension(240, 48));
/*  228 */     this.jLabel1.setName("jLabel1");
/*  229 */     this.jLabel1.setPreferredSize(new Dimension(240, 48));
/*  230 */     this.jLabel1.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  232 */             Main.this.jLabel1MouseClicked(evt);
/*      */           }
/*      */         });
/*  235 */     this.jPanel5.add(this.jLabel1);
/*      */     
/*  237 */     this.jPanel9.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
/*  238 */     this.jPanel9.setName("jPanel9");
/*  239 */     this.jPanel9.setLayout(new FlowLayout(1, 0, 2));
/*      */     
/*  241 */     this.btPlay.setIcon(new ImageIcon(getClass().getResource("/resources/icon/play.png")));
/*  242 */     this.btPlay.setToolTipText("Play the selected tune");
/*  243 */     this.btPlay.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
/*  244 */     this.btPlay.setFocusable(false);
/*  245 */     this.btPlay.setHorizontalTextPosition(0);
/*  246 */     this.btPlay.setName("btPlay");
/*  247 */     this.btPlay.setVerticalTextPosition(3);
/*  248 */     this.btPlay.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent evt) {
/*  250 */             Main.this.btPlayActionPerformed(evt);
/*      */           }
/*      */         });
/*  253 */     this.jPanel9.add(this.btPlay);
/*      */     
/*  255 */     this.btStart.setIcon(new ImageIcon(getClass().getResource("/resources/icon/restart.png")));
/*  256 */     this.btStart.setToolTipText("Restart the playing tune");
/*  257 */     this.btStart.setBorder((Border)null);
/*  258 */     this.btStart.setFocusable(false);
/*  259 */     this.btStart.setHorizontalTextPosition(0);
/*  260 */     this.btStart.setName("btStart");
/*  261 */     this.btStart.setVerticalTextPosition(3);
/*  262 */     this.btStart.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent evt) {
/*  264 */             Main.this.btStartActionPerformed(evt);
/*      */           }
/*      */         });
/*  267 */     this.jPanel9.add(this.btStart);
/*      */     
/*  269 */     this.btPause.setIcon(new ImageIcon(getClass().getResource("/resources/icon/pause.png")));
/*  270 */     this.btPause.setToolTipText("Pause/continue playing");
/*  271 */     this.btPause.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
/*  272 */     this.btPause.setFocusable(false);
/*  273 */     this.btPause.setHorizontalTextPosition(0);
/*  274 */     this.btPause.setName("btPause");
/*  275 */     this.btPause.setVerticalTextPosition(3);
/*  276 */     this.btPause.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent evt) {
/*  278 */             Main.this.btPauseActionPerformed(evt);
/*      */           }
/*      */         });
/*  281 */     this.jPanel9.add(this.btPause);
/*      */     
/*  283 */     this.btFinish.setIcon(new ImageIcon(getClass().getResource("/resources/icon/finish.png")));
/*  284 */     this.btFinish.setToolTipText("End the playing tune");
/*  285 */     this.btFinish.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
/*  286 */     this.btFinish.setFocusable(false);
/*  287 */     this.btFinish.setHorizontalTextPosition(0);
/*  288 */     this.btFinish.setName("btFinish");
/*  289 */     this.btFinish.setVerticalTextPosition(3);
/*  290 */     this.btFinish.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent evt) {
/*  292 */             Main.this.btFinishActionPerformed(evt);
/*      */           }
/*      */         });
/*  295 */     this.jPanel9.add(this.btFinish);
/*      */     
/*  297 */     this.jPanel5.add(this.jPanel9);
/*      */     
/*  299 */     this.jPanel14.add(this.jPanel5, "West");
/*      */     
/*  301 */     this.jPanel10.setName("jPanel10");
/*  302 */     this.jPanel10.setLayout(new GridLayout(1, 0));
/*      */     
/*  304 */     this.pPosition.setBorder(BorderFactory.createTitledBorder("Position"));
/*  305 */     this.pPosition.setMinimumSize(new Dimension(100, 46));
/*  306 */     this.pPosition.setName("pPosition");
/*  307 */     this.pPosition.setPreferredSize(new Dimension(200, 46));
/*  308 */     this.pPosition.setLayout(new BorderLayout());
/*      */     
/*  310 */     this.sPosition.setMajorTickSpacing(10);
/*  311 */     this.sPosition.setPaintTicks(true);
/*  312 */     this.sPosition.setName("sPosition");
/*  313 */     this.sPosition.addChangeListener(new ChangeListener() {
/*      */           public void stateChanged(ChangeEvent evt) {
/*  315 */             Main.this.sPositionStateChanged(evt);
/*      */           }
/*      */         });
/*  318 */     this.pPosition.add(this.sPosition, "Center");
/*      */     
/*  320 */     this.jPanel10.add(this.pPosition);
/*      */     
/*  322 */     this.pTempo.setBorder(BorderFactory.createTitledBorder("Tempo"));
/*  323 */     this.pTempo.setMinimumSize(new Dimension(100, 46));
/*  324 */     this.pTempo.setName("pTempo");
/*  325 */     this.pTempo.setPreferredSize(new Dimension(200, 46));
/*  326 */     this.pTempo.setLayout(new BorderLayout());
/*      */     
/*  328 */     this.sTempo.setMajorTickSpacing(20);
/*  329 */     this.sTempo.setMaximum(200);
/*  330 */     this.sTempo.setMinimum(20);
/*  331 */     this.sTempo.setPaintTicks(true);
/*  332 */     this.sTempo.setName("sTempo");
/*  333 */     this.sTempo.addChangeListener(new ChangeListener() {
/*      */           public void stateChanged(ChangeEvent evt) {
/*  335 */             Main.this.sTempoStateChanged(evt);
/*      */           }
/*      */         });
/*  338 */     this.pTempo.add(this.sTempo, "Center");
/*      */     
/*  340 */     this.jPanel10.add(this.pTempo);
/*      */     
/*  342 */     this.pVibrate.setBorder(BorderFactory.createTitledBorder("Volume"));
/*  343 */     this.pVibrate.setMinimumSize(new Dimension(100, 46));
/*  344 */     this.pVibrate.setName("pVibrate");
/*  345 */     this.pVibrate.setPreferredSize(new Dimension(200, 46));
/*  346 */     this.pVibrate.setLayout(new BorderLayout());
/*      */     
/*  348 */     this.sVibrate.setMajorTickSpacing(10);
/*  349 */     this.sVibrate.setPaintTicks(true);
/*  350 */     this.sVibrate.setName("sVibrate");
/*  351 */     this.sVibrate.addChangeListener(new ChangeListener() {
/*      */           public void stateChanged(ChangeEvent evt) {
/*  353 */             Main.this.sVibrateStateChanged(evt);
/*      */           }
/*      */         });
/*  356 */     this.pVibrate.add(this.sVibrate, "Center");
/*      */     
/*  358 */     this.jPanel10.add(this.pVibrate);
/*      */     
/*  360 */     this.jPanel14.add(this.jPanel10, "Center");
/*      */     
/*  362 */     this.jPanel2.add(this.jPanel14);
/*      */     
/*  364 */     this.jPanel7.setName("jPanel7");
/*  365 */     this.jPanel7.setLayout(new BorderLayout());
/*      */     
/*  367 */     this.jPanel13.setName("jPanel13");
/*  368 */     this.jPanel13.setLayout(new BoxLayout(this.jPanel13, 2));
/*      */     
/*  370 */     this.panelLeft.setBorder(BorderFactory.createTitledBorder(null, "Harmony", 2, 5));
/*  371 */     this.jPanel12.setBorder(BorderFactory.createTitledBorder("Tracks"));
/*  372 */     this.jPanel12.setMinimumSize(new Dimension(484, 10));
/*  373 */     this.jPanel12.setName("jPanel12");
/*  374 */     this.jPanel12.setPreferredSize(new Dimension(150, 40));
/*  375 */     this.panelLeft.setLayout(new GridLayout(1, 0));
/*  376 */     this.jPanel12.setLayout(new GridLayout(1, 0));
/*      */     
/*  378 */     this.cbTrack.setSelected(true);
/*  379 */     this.cbTrack.setText("V");
/*  380 */     this.cbTrack.setName("cbTrack");
/*  381 */     this.cbTrack.addChangeListener(new ChangeListener() {
/*      */           public void stateChanged(ChangeEvent evt) {
/*  383 */             Main.this.cbTrackStateChanged(evt);
/*      */           }
/*      */         });
/*  386 */     this.jPanel12.add(this.cbTrack);
/*      */     
/*  388 */     this.cbTrackA.setSelected(true);
/*  389 */     this.cbTrackA.setText("A");
/*  390 */     this.cbTrackA.setName("cbTrackA");
/*  391 */     this.cbTrackA.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  393 */             Main.this.cbTrackAMouseClicked(evt);
/*      */           }
/*      */         });
/*  396 */     this.jPanel12.add(this.cbTrackA);
/*      */     
/*  398 */     this.cbTrackB.setSelected(true);
/*  399 */     this.cbTrackB.setText("B");
/*  400 */     this.cbTrackB.setName("cbTrackB");
/*  401 */     this.cbTrackB.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  403 */             Main.this.cbTrackBMouseClicked(evt);
/*      */           }
/*      */         });
/*  406 */     this.jPanel12.add(this.cbTrackB);
/*      */     
/*  408 */     this.cbTrackC.setSelected(true);
/*  409 */     this.cbTrackC.setText("C");
/*  410 */     this.cbTrackC.setName("cbTrackC");
/*  411 */     this.cbTrackC.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  413 */             Main.this.cbTrackCMouseClicked(evt);
/*      */           }
/*      */         });
/*  416 */     this.jPanel12.add(this.cbTrackC);
/*      */     
/*  418 */     this.cbTrackD.setSelected(true);
/*  419 */     this.cbTrackD.setText("D");
/*  420 */     this.cbTrackD.setName("cbTrackD");
/*  421 */     this.cbTrackD.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  423 */             Main.this.cbTrackDMouseClicked(evt);
/*      */           }
/*      */         });
/*  426 */     this.jPanel12.add(this.cbTrackD);
/*      */     
/*  428 */     this.cbTrackE.setSelected(true);
/*  429 */     this.cbTrackE.setText("E");
/*  430 */     this.cbTrackE.setName("cbTrackE");
/*  431 */     this.cbTrackE.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  433 */             Main.this.cbTrackEMouseClicked(evt);
/*      */           }
/*      */         });
/*  436 */     this.jPanel12.add(this.cbTrackE);
/*      */     
/*  438 */     this.cbTrackF.setSelected(true);
/*  439 */     this.cbTrackF.setText("F");
/*  440 */     this.cbTrackF.setName("cbTrackF");
/*  441 */     this.cbTrackF.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  443 */             Main.this.cbTrackFMouseClicked(evt);
/*      */           }
/*      */         });
/*  446 */     this.jPanel12.add(this.cbTrackF);
/*      */     
/*  448 */     this.cbTrackG.setSelected(true);
/*  449 */     this.cbTrackG.setText("G");
/*  450 */     this.cbTrackG.setName("cbTrackG");
/*  451 */     this.cbTrackG.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  453 */             Main.this.cbTrackGMouseClicked(evt);
/*      */           }
/*      */         });
/*  456 */     this.jPanel12.add(this.cbTrackG);
/*      */     
/*  458 */     this.jPanel13.add(this.jPanel12);
/*      */     
/*  460 */     this.panelLeft.setBorder(BorderFactory.createTitledBorder(null, "Harmony", 2, 5));
/*  461 */     this.jPanel11.setBorder(BorderFactory.createTitledBorder("Voices"));
/*  462 */     this.jPanel11.setMinimumSize(new Dimension(484, 10));
/*  463 */     this.jPanel11.setName("jPanel11");
/*  464 */     this.jPanel11.setPreferredSize(new Dimension(150, 40));
/*  465 */     this.panelLeft.setLayout(new GridLayout(1, 0));
/*  466 */     this.jPanel11.setLayout(new GridLayout(1, 0));
/*      */     
/*  468 */     this.cbVoice1.setSelected(true);
/*  469 */     this.cbVoice1.setText("1");
/*  470 */     this.cbVoice1.setName("cbVoice1");
/*  471 */     this.cbVoice1.addChangeListener(new ChangeListener() {
/*      */           public void stateChanged(ChangeEvent evt) {
/*  473 */             Main.this.cbVoice1StateChanged(evt);
/*      */           }
/*      */         });
/*  476 */     this.jPanel11.add(this.cbVoice1);
/*      */     
/*  478 */     this.cbVoice2.setSelected(true);
/*  479 */     this.cbVoice2.setText("2");
/*  480 */     this.cbVoice2.setName("cbVoice2");
/*  481 */     this.cbVoice2.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  483 */             Main.this.cbVoice2MouseClicked(evt);
/*      */           }
/*      */         });
/*  486 */     this.jPanel11.add(this.cbVoice2);
/*      */     
/*  488 */     this.cbVoice3.setSelected(true);
/*  489 */     this.cbVoice3.setText("3");
/*  490 */     this.cbVoice3.setName("cbVoice3");
/*  491 */     this.cbVoice3.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  493 */             Main.this.cbVoice3MouseClicked(evt);
/*      */           }
/*      */         });
/*  496 */     this.jPanel11.add(this.cbVoice3);
/*      */     
/*  498 */     this.cbVoice4.setSelected(true);
/*  499 */     this.cbVoice4.setText("4");
/*  500 */     this.cbVoice4.setName("cbVoice4");
/*  501 */     this.cbVoice4.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  503 */             Main.this.cbVoice4MouseClicked(evt);
/*      */           }
/*      */         });
/*  506 */     this.jPanel11.add(this.cbVoice4);
/*      */     
/*  508 */     this.cbVoice5.setSelected(true);
/*  509 */     this.cbVoice5.setText("5");
/*  510 */     this.cbVoice5.setName("cbVoice5");
/*  511 */     this.cbVoice5.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  513 */             Main.this.cbVoice5MouseClicked(evt);
/*      */           }
/*      */         });
/*  516 */     this.jPanel11.add(this.cbVoice5);
/*      */     
/*  518 */     this.cbVoice6.setSelected(true);
/*  519 */     this.cbVoice6.setText("6");
/*  520 */     this.cbVoice6.setName("cbVoice6");
/*  521 */     this.cbVoice6.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  523 */             Main.this.cbVoice6MouseClicked(evt);
/*      */           }
/*      */         });
/*  526 */     this.jPanel11.add(this.cbVoice6);
/*      */     
/*  528 */     this.cbVoice7.setSelected(true);
/*  529 */     this.cbVoice7.setText("7");
/*  530 */     this.cbVoice7.setName("cbVoice7");
/*  531 */     this.cbVoice7.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  533 */             Main.this.cbVoice7MouseClicked(evt);
/*      */           }
/*      */         });
/*  536 */     this.jPanel11.add(this.cbVoice7);
/*      */     
/*  538 */     this.cbVoice8.setSelected(true);
/*  539 */     this.cbVoice8.setText("8<");
/*  540 */     this.cbVoice8.setName("cbVoice8");
/*  541 */     this.cbVoice8.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  543 */             Main.this.cbVoice8MouseClicked(evt);
/*      */           }
/*      */         });
/*  546 */     this.jPanel11.add(this.cbVoice8);
/*      */     
/*  548 */     this.jPanel13.add(this.jPanel11);
/*      */     
/*  550 */     this.jPanel7.add(this.jPanel13, "Center");
/*      */     
/*  552 */     this.panelLeft.setBorder(BorderFactory.createTitledBorder(null, "Harmony", 2, 5));
/*  553 */     this.jPanel8.setBorder(BorderFactory.createTitledBorder("Effects"));
/*  554 */     this.jPanel8.setMinimumSize(new Dimension(200, 10));
/*  555 */     this.jPanel8.setName("jPanel8");
/*  556 */     this.jPanel8.setPreferredSize(new Dimension(200, 40));
/*  557 */     this.panelLeft.setLayout(new GridLayout(1, 0));
/*  558 */     this.jPanel8.setLayout(new GridLayout(1, 0));
/*      */     
/*  560 */     this.cbSustain.setText("Overlap");
/*  561 */     this.cbSustain.setName("cbSustain");
/*  562 */     this.cbSustain.addMouseListener(new MouseAdapter() {
/*      */           public void mouseClicked(MouseEvent evt) {
/*  564 */             Main.this.cbSustainMouseClicked(evt);
/*      */           }
/*      */         });
/*  567 */     this.jPanel8.add(this.cbSustain);
/*      */     
/*  569 */     this.cbMultitones.setText("Multi");
/*  570 */     this.cbMultitones.setName("cbMultitones");
/*  571 */     this.cbMultitones.addChangeListener(new ChangeListener() {
/*      */           public void stateChanged(ChangeEvent evt) {
/*  573 */             Main.this.cbMultitonesStateChanged(evt);
/*      */           }
/*      */         });
/*  576 */     this.jPanel8.add(this.cbMultitones);
/*      */     
/*  578 */     this.jPanel7.add(this.jPanel8, "East");
/*  579 */     this.jPanel8.getAccessibleContext().setAccessibleName("Multi-pitch");
/*      */     
/*  581 */     this.jPanel2.add(this.jPanel7);
/*      */     
/*  583 */     this.jPanel6.add(this.jPanel2, "Center");
/*      */     
/*  585 */     getContentPane().add(this.jPanel6, "North");
/*      */     
/*  587 */     this.splitCenter.setDividerLocation(0);
/*  588 */     this.splitCenter.setMinimumSize(new Dimension(0, 170));
/*  589 */     this.splitCenter.setName("splitCenter");
/*  590 */     this.splitCenter.setPreferredSize(new Dimension(0, 894));
/*      */     
/*  592 */     this.panelRight.setName("panelRight");
/*  593 */     this.panelRight.setLayout(new BorderLayout());
/*      */     
/*  595 */     this.tabPanel.setTabPlacement(3);
/*  596 */     this.tabPanel.setName("tabPanels");
/*  597 */     this.panelRight.add(this.tabPanel, "Center");
/*      */     
/*  599 */     this.splitCenter.setRightComponent(this.panelRight);
/*      */     
/*  601 */     this.panelLeft.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
/*  602 */     this.panelLeft.setMinimumSize(new Dimension(0, 122));
/*  603 */     this.panelLeft.setName("panelLeft");
/*  604 */     this.panelLeft.setPreferredSize(new Dimension(0, 510));
/*  605 */     this.panelLeft.setLayout(new BorderLayout());
/*      */     
/*  607 */     this.splitLeft.setTabPlacement(3);
/*  608 */     this.splitLeft.setMaximumSize(new Dimension(400, 32767));
/*  609 */     this.splitLeft.setMinimumSize(new Dimension(240, 120));
/*  610 */     this.splitLeft.setName("tabList");
/*  611 */     this.splitLeft.setOpaque(true);
/*      */     
/*  613 */     this.jPanel15.setName("jPanel15");
/*  614 */     this.jPanel15.setLayout(new BorderLayout());
/*      */     
/*  616 */     this.jPanel1.setName("jPanel1");
/*      */     
/*  618 */     this.jButton1.setText("Delete");
/*  619 */     this.jButton1.setName("jButton1");
/*  620 */     this.jPanel1.add(this.jButton1);
/*      */     
/*  622 */     this.jButton2.setText("Up");
/*  623 */     this.jButton2.setName("jButton2");
/*  624 */     this.jPanel1.add(this.jButton2);
/*      */     
/*  626 */     this.jButton3.setText("Down");
/*  627 */     this.jButton3.setName("jButton3");
/*  628 */     this.jPanel1.add(this.jButton3);
/*      */     
/*  630 */     this.jPanel15.add(this.jPanel1, "First");
/*      */     
/*  632 */     this.jPanel3.setName("jPanel3");
/*  633 */     this.jPanel3.setLayout(new BorderLayout());
/*      */     
/*  635 */     this.jScrollPane4.setName("jScrollPane4");
/*      */     
/*  637 */     this.treePlaylist.setName("treePlaylist");
/*  638 */     this.treePlaylist.addTreeSelectionListener(new TreeSelectionListener() {
/*      */           public void valueChanged(TreeSelectionEvent evt) {
/*  640 */             Main.this.treePlaylistValueChanged(evt);
/*      */           }
/*      */         });
/*  643 */     this.treePlaylist.addKeyListener(new KeyAdapter() {
/*      */           public void keyPressed(KeyEvent evt) {
/*  645 */             Main.this.treePlaylistKeyPressed(evt);
/*      */           }
/*      */         });
/*  648 */     this.jScrollPane4.setViewportView(this.treePlaylist);
/*      */     
/*  650 */     this.jPanel3.add(this.jScrollPane4, "First");
/*      */     
/*  652 */     this.jPanel15.add(this.jPanel3, "Center");
/*      */     
/*  654 */     this.splitLeft.addTab("Playlist", this.jPanel15);
/*      */     
/*  656 */     this.jScrollPane2.setName("jScrollPane2");
/*      */     
/*  658 */     this.treeAlbum.setName("treeAlbum");
/*  659 */     this.treeAlbum.addTreeSelectionListener(new TreeSelectionListener() {
/*      */           public void valueChanged(TreeSelectionEvent evt) {
/*  661 */             Main.this.treeAlbumValueChanged(evt);
/*      */           }
/*      */         });
/*  664 */     this.treeAlbum.addKeyListener(new KeyAdapter() {
/*      */           public void keyPressed(KeyEvent evt) {
/*  666 */             Main.this.treeAlbumKeyPressed(evt);
/*      */           }
/*      */         });
/*  669 */     this.jScrollPane2.setViewportView(this.treeAlbum);
/*      */     
/*  671 */     this.splitLeft.addTab("Album", this.jScrollPane2);
/*      */     
/*  673 */     this.jScrollPane3.setName("jScrollPane3");
/*      */     
/*  675 */     this.treeDraft.setName("treeDraft");
/*  676 */     this.treeDraft.addTreeSelectionListener(new TreeSelectionListener() {
/*      */           public void valueChanged(TreeSelectionEvent evt) {
/*  678 */             Main.this.treeDraftValueChanged(evt);
/*      */           }
/*      */         });
/*  681 */     this.treeDraft.addKeyListener(new KeyAdapter() {
/*      */           public void keyPressed(KeyEvent evt) {
/*  683 */             Main.this.treeDraftKeyPressed(evt);
/*      */           }
/*      */         });
/*  686 */     this.jScrollPane3.setViewportView(this.treeDraft);
/*      */     
/*  688 */     this.splitLeft.addTab("Draft", this.jScrollPane3);
/*      */     
/*  690 */     this.jScrollPane1.setName("jScrollPane1");
/*      */     
/*  692 */     this.treeMidi.setName("treeMidi");
/*  693 */     this.treeMidi.addTreeSelectionListener(new TreeSelectionListener() {
/*      */           public void valueChanged(TreeSelectionEvent evt) {
/*  695 */             Main.this.treeMidiValueChanged(evt);
/*      */           }
/*      */         });
/*  698 */     this.treeMidi.addKeyListener(new KeyAdapter() {
/*      */           public void keyPressed(KeyEvent evt) {
/*  700 */             Main.this.treeMidiKeyPressed(evt);
/*      */           }
/*      */         });
/*  703 */     this.jScrollPane1.setViewportView(this.treeMidi);
/*      */     
/*  705 */     this.splitLeft.addTab("Midi", this.jScrollPane1);
/*      */     
/*  707 */     this.panelLeft.add(this.splitLeft, "Center");
/*      */     
/*  709 */     this.splitCenter.setLeftComponent(this.panelLeft);
/*      */     
/*  711 */     getContentPane().add(this.splitCenter, "Center");
/*      */     
/*  713 */     this.panelSouth.setBackground(new Color(62, 61, 57));
/*  714 */     this.panelSouth.setName("panelSouth");
/*  715 */     this.panelSouth.setPreferredSize(new Dimension(459, 30));
/*  716 */     this.panelSouth.setLayout(new BorderLayout());
/*      */     
/*  718 */     this.pPlayers.setBackground(new Color(1, 1, 0));
/*  719 */     this.pPlayers.setName("pPlayers");
/*  720 */     this.panelSouth.add(this.pPlayers, "East");
/*      */     
/*  722 */     this.jPanel4.setBackground(new Color(14, 11, 7));
/*  723 */     this.jPanel4.setName("jPanel4");
/*  724 */     this.jPanel4.setLayout(new BorderLayout());
/*      */     
/*  726 */     this.lbMessage.setFont(new Font("DejaVu Sans", 1, 14));
/*  727 */     this.lbMessage.setForeground(Color.green);
/*  728 */     this.lbMessage.setText("Welcome");
/*  729 */     this.lbMessage.setName("lbMessage");
/*  730 */     this.jPanel4.add(this.lbMessage, "Center");
/*      */     
/*  732 */     this.lbInfo.setForeground(new Color(255, 255, 51));
/*  733 */     this.lbInfo.setHorizontalAlignment(11);
/*  734 */     this.lbInfo.setText("jLabel2");
/*  735 */     this.lbInfo.setName("lbInfo");
/*  736 */     this.jPanel4.add(this.lbInfo, "East");
/*      */     
/*  738 */     this.panelSouth.add(this.jPanel4, "Center");
/*      */     
/*  740 */     getContentPane().add(this.panelSouth, "Last");
/*      */     
/*  742 */     pack();
/*      */   }
/*      */   
/*      */   void addMainListener(MainListener ml) {
/*  746 */     this.listeners.add(ml);
/*      */   }
/*      */   
/*      */   void removeMainListener(MainListener ml) {
/*  750 */     this.listeners.remove(ml);
/*      */   }
/*      */   
/*      */   boolean isListenerVisible() {
/*  754 */     for (MainListener l : this.listeners) {
/*  755 */       if (l.isReady()) return true; 
/*      */     } 
/*  757 */     return false;
/*      */   }
/*      */   
/*      */   void initOptions() {
/*  761 */     if (!Options.load(this.baseFolder)) {
/*  762 */       Options.initDefault();
/*      */     }
/*  764 */     this.draftFolder = Options.get("folder.draft") + File.separator;
/*  765 */     this.albumFolder = Options.get("folder.album") + File.separator;
/*  766 */     this.midiFolder = Options.get("folder.midi") + File.separator;
/*  767 */     this.listFolder = Options.get("folder.list") + File.separator;
/*      */   }
/*      */   
/*      */   void initMidi() {
/*  771 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.midiFolder);
/*  772 */     root.expandAll(".mid");
/*  773 */     DefaultTreeModel model = new DefaultTreeModel((TreeNode)root);
/*  774 */     this.treeMidi.setModel(model);
/*  775 */     this.midiSelected = null;
/*      */   }
/*      */   
/*      */   public void initAlbum() {
/*  779 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.albumFolder);
/*  780 */     root.expandAll(".123");
/*  781 */     DefaultTreeModel model = new DefaultTreeModel((TreeNode)root);
/*  782 */     this.treeAlbum.setModel(model);
/*  783 */     this.albumSelected = null;
/*      */   }
/*      */   
/*      */   public void initDraft() {
/*  787 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.draftFolder);
/*  788 */     root.expandAll(".123");
/*  789 */     this.treeDraft.setModel(new DefaultTreeModel((TreeNode)root));
/*  790 */     this.draftSelected = null;
/*      */   }
/*      */   
/*      */   public void initList() {
/*  794 */     initDraft();
/*  795 */     initAlbum();
/*  796 */     initMidi();
/*      */   }
/*      */   
/*      */   public String getSelectedFile() {
/*  800 */     if (this.draftSelected != null) {
/*  801 */       return this.draftSelected.getFile().getName();
/*      */     }
/*  803 */     if (this.albumSelected != null) {
/*  804 */       return this.albumSelected.getFile().getName();
/*      */     }
/*  806 */     if (this.midiSelected != null) {
/*  807 */       return this.midiSelected.getFile().getName();
/*      */     }
/*  809 */     return null;
/*      */   }
/*      */   
/*      */   public File getSelectedDraft() {
/*  813 */     if (this.draftSelected != null && 
/*  814 */       this.draftSelected.isLeaf()) {
/*  815 */       return this.draftSelected.getFile();
/*      */     }
/*      */     
/*  818 */     return null;
/*      */   }
/*      */   
/*      */   public File getSelectedDraftFolder() {
/*  822 */     if (this.draftSelected != null) {
/*  823 */       if (this.draftSelected.isLeaf()) {
/*  824 */         FileTreeNode fileTreeNode = (FileTreeNode)this.draftSelected.getParent();
/*  825 */         return fileTreeNode.getFile();
/*      */       } 
/*  827 */       return this.draftSelected.getFile();
/*      */     } 
/*  829 */     FileTreeNode p = (FileTreeNode)this.treeDraft.getModel().getRoot();
/*  830 */     return p.getFile();
/*      */   }
/*      */ 
/*      */   
/*      */   public File getSelectedMidi() {
/*  835 */     if (this.midiSelected != null) {
/*  836 */       return this.midiSelected.getFile();
/*      */     }
/*  838 */     return null;
/*      */   }
/*      */   
/*      */   public List getSelectedAlbums() {
/*  842 */     if (this.treeAlbum.getSelectionCount() > 0) {
/*  843 */       List<File> ss = new ArrayList<>();
/*  844 */       for (TreePath sp : this.treeAlbum.getSelectionPaths()) {
/*  845 */         FileTreeNode tn = (FileTreeNode)sp.getLastPathComponent();
/*  846 */         if (tn.isLeaf()) {
/*  847 */           ss.add(tn.getFile());
/*      */         }
/*      */       } 
/*  850 */       return ss;
/*      */     } 
/*  852 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void initMaestro() {
/*  858 */     this.maestro.initPlayers();
/*  859 */     this.sTempo.setValue(50);
/*  860 */     this.sVibrate.setValue(this.maestro.getVolume());
/*  861 */     this.sTempo.setValue(this.maestro.getSpeed());
/*      */   }
/*      */ 
/*      */   
/*      */   private void sTempoStateChanged(ChangeEvent evt) {
/*  866 */     if (this.sTempo.getValueIsAdjusting())
/*  867 */       return;  int valSlider = this.sTempo.getValue();
/*  868 */     TitledBorder b = (TitledBorder)this.pTempo.getBorder();
/*  869 */     b.setTitle("Speed: " + valSlider);
/*  870 */     this.pTempo.repaint();
/*  871 */     this.maestro.setSpeed(valSlider);
/*      */   }
/*      */   
/*      */   public void playMidi(File file, MidiInfo info) {
/*      */     try {
/*  876 */       setMessage("Start playing midi " + file.getName(), Color.YELLOW);
/*  877 */       Sequence seq = MidiSystem.getSequence(file);
/*  878 */       if (this.midiPanel.isVisible() && info != null) {
/*  879 */         info.autoSaveMap();
/*  880 */         this.maestro.setMidiMap(info);
/*      */       } else {
/*      */         
/*  883 */         MidiInfo mi = new MidiInfo(this.midiSelected.getFile(), this.maestro);
/*  884 */         mi.openMap();
/*  885 */         this.maestro.setMidiMap(mi);
/*      */       } 
/*  887 */       setMessage("Playing midi " + file.getName(), Color.GREEN);
/*  888 */       this.maestro.queue(seq);
/*      */     }
/*  890 */     catch (Exception ex) {
/*  891 */       ex.printStackTrace();
/*  892 */       setMessage(ex.getMessage(), Color.RED);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void analyzeDoremi(File file) {
/*      */     try {
/*  898 */       Sequence sequence = this.doremi.read(file);
/*  899 */       this.orchestraPanel.changeSequence(sequence);
/*  900 */       setMessage("Analyze " + file.getName(), Color.GREEN);
/*      */     }
/*  902 */     catch (Exception ex) {
/*  903 */       setMessage(ex.getMessage(), Color.RED);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void selectDoremi(File file) {
/*      */     try {
/*  909 */       if (this.orchestraPanel.isVisible()) {
/*  910 */         Sequence sequence = this.doremi.read(file);
/*  911 */         this.orchestraPanel.changeSequence(sequence);
/*      */       }
/*      */     
/*  914 */     } catch (Exception ex) {
/*  915 */       setMessage(ex.getMessage(), Color.RED);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void playDoremi(String fname) {
/*      */     try {
/*  921 */       File file = new File(fname);
/*  922 */       Sequence sequence = this.doremi.read(file);
/*  923 */       this.maestro.queue(sequence);
/*      */     }
/*  925 */     catch (Exception ex) {
/*  926 */       setMessage(ex.getMessage(), Color.RED);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void playDoremi(File file) {
/*      */     try {
/*  932 */       Sequence sequence = this.doremi.read(file);
/*  933 */       this.maestro.queue(sequence);
/*      */     }
/*  935 */     catch (Exception ex) {
/*  936 */       setMessage(ex.getMessage(), Color.RED);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void playEditor() {
/*      */     try {
/*  942 */       String buffer = this.editPanel.getSelectedSong();
/*  943 */       Sequence sequence = this.doremi.read(buffer);
/*  944 */       this.maestro.queue(sequence);
/*      */     }
/*  946 */     catch (ParserException ex) {
/*  947 */       setMessage(ex.getMessage(), Color.RED);
/*  948 */       this.editPanel.setCaretPosition(ex.getRow(), ex.getCol());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void playKaraoke() {
/*  953 */     playDoremi(this.karaokePanel.getSelectedFile());
/*      */   }
/*      */   
/*      */   public void play() {
/*  957 */     if (this.albumSelected != null) {
/*  958 */       if (this.albumSelected.isLeaf())
/*  959 */         System.out.println(this.albumSelected.getFile()); 
/*  960 */       playDoremi(this.albumSelected.getFile());
/*      */     }
/*  962 */     else if (this.draftSelected != null) {
/*  963 */       if (this.draftSelected.isLeaf()) {
/*  964 */         playDoremi(this.draftSelected.getFile());
/*      */       }
/*  966 */     } else if (this.midiSelected != null) {
/*  967 */       playMidi(this.midiSelected.getFile(), this.midiInfo);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void finish() {
/*  972 */     this.maestro.finish();
/*      */   }
/*      */ 
/*      */   
/*      */   private void formWindowOpened(WindowEvent evt) {
/*  977 */     System.out.println("Starting ...");
/*  978 */     setMessage("Welcome to the Indonesian musical heritages", Color.GREEN);
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbVoice1StateChanged(ChangeEvent evt) {
/*  983 */     this.effect.setMute(1, !this.cbVoice1.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbVoice2MouseClicked(MouseEvent evt) {
/*  988 */     this.effect.setMute(2, !this.cbVoice2.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbVoice3MouseClicked(MouseEvent evt) {
/*  993 */     this.effect.setMute(3, !this.cbVoice3.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbVoice4MouseClicked(MouseEvent evt) {
/*  998 */     this.effect.setMute(4, !this.cbVoice4.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbVoice5MouseClicked(MouseEvent evt) {
/* 1003 */     this.effect.setMute(5, !this.cbVoice5.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbVoice6MouseClicked(MouseEvent evt) {
/* 1008 */     this.effect.setMute(6, !this.cbVoice6.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbVoice7MouseClicked(MouseEvent evt) {
/* 1013 */     this.effect.setMute(7, !this.cbVoice7.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbVoice8MouseClicked(MouseEvent evt) {
/* 1018 */     this.effect.setMute(8, !this.cbVoice8.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void sVibrateStateChanged(ChangeEvent evt) {
/* 1023 */     if (this.sVibrate.getValueIsAdjusting())
/* 1024 */       return;  int v = this.sVibrate.getValue();
/* 1025 */     TitledBorder b = (TitledBorder)this.pVibrate.getBorder();
/* 1026 */     b.setTitle("Volume: " + v);
/* 1027 */     this.pVibrate.repaint();
/* 1028 */     this.maestro.setVolume(v);
/*      */   }
/*      */ 
/*      */   
/*      */   private void sPositionStateChanged(ChangeEvent evt) {
/* 1033 */     if (this.sPosition.getValueIsAdjusting())
/* 1034 */       return;  int v = this.sPosition.getValue();
/* 1035 */     int m = this.sPosition.getMaximum();
/* 1036 */     TitledBorder b = (TitledBorder)this.pPosition.getBorder();
/* 1037 */     b.setTitle("Position: " + v + "/" + m);
/* 1038 */     this.pPosition.repaint();
/* 1039 */     if (this.changed) {
/* 1040 */       this.changed = false;
/*      */       return;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void jLabel1MouseClicked(MouseEvent evt) {
/* 1047 */     JDialog dlg = new AboutBox(this, true);
/* 1048 */     dlg.setVisible(true);
/*      */   }
/*      */ 
/*      */   
/*      */   private void formWindowClosing(WindowEvent evt) {
/* 1053 */     System.out.println("Closing");
/*      */     
/*      */     try {
/* 1056 */       this.maestro.finish();
/* 1057 */       this.maestro.playOff(0L);
/*      */     }
/* 1059 */     catch (Exception ex) {}
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbTrackStateChanged(ChangeEvent evt) {
/* 1064 */     this.effect.setTrackMute(0, !this.cbTrack.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbTrackAMouseClicked(MouseEvent evt) {
/* 1069 */     this.effect.setTrackMute(1, !this.cbTrackA.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbTrackBMouseClicked(MouseEvent evt) {
/* 1074 */     this.effect.setTrackMute(2, !this.cbTrackB.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbTrackCMouseClicked(MouseEvent evt) {
/* 1079 */     this.effect.setTrackMute(3, !this.cbTrackC.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbTrackDMouseClicked(MouseEvent evt) {
/* 1084 */     this.effect.setTrackMute(4, !this.cbTrackD.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbTrackEMouseClicked(MouseEvent evt) {
/* 1089 */     this.effect.setTrackMute(5, !this.cbTrackE.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbTrackFMouseClicked(MouseEvent evt) {
/* 1094 */     this.effect.setTrackMute(6, !this.cbTrackF.isSelected());
/*      */   }
/*      */ 
/*      */   
/*      */   private void cbTrackGMouseClicked(MouseEvent evt) {
/* 1099 */     this.effect.setTrackMute(7, !this.cbTrackG.isSelected());
/*      */   }
/*      */   
/*      */   private void cbMultitonesStateChanged(ChangeEvent evt) {
/* 1103 */     this.effect.setMultinote(this.cbMultitones.isSelected());
/*      */   }
/*      */   
/*      */   private void cbSustainMouseClicked(MouseEvent evt) {
/* 1107 */     this.effect.setSustain(this.cbSustain.isSelected());
/*      */   }
/*      */   
/*      */   private void btFinishActionPerformed(ActionEvent evt) {
/* 1111 */     this.maestro.finish();
/*      */   }
/*      */ 
/*      */   
/*      */   private void btPauseActionPerformed(ActionEvent evt) {
/* 1116 */     if (this.maestro.pause()) {
/* 1117 */       this.btPause.setIcon(new ImageIcon(getClass().getResource("/resources/icon/pause.png")));
/*      */     } else {
/*      */       
/* 1120 */       this.btPause.setIcon(new ImageIcon(getClass().getResource("/resources/icon/continue.png")));
/*      */     } 
/*      */   }
/*      */   
/*      */   private void btStartActionPerformed(ActionEvent evt) {
/* 1125 */     this.maestro.start();
/*      */   }
/*      */   
/*      */   private void btPlayActionPerformed(ActionEvent evt) {
/* 1129 */     if (this.karaokePanel.isVisible()) {
/* 1130 */       playKaraoke();
/*      */     }
/* 1132 */     else if (this.editPanel.isVisible()) {
/* 1133 */       playEditor();
/*      */     } else {
/*      */       
/* 1136 */       play();
/*      */     } 
/* 1138 */     this.btPause.setIcon(new ImageIcon(getClass().getResource("/resources/icon/pause.png")));
/*      */   }
/*      */ 
/*      */   
/*      */   private void treeMidiKeyPressed(KeyEvent evt) {
/* 1143 */     switch (evt.getKeyCode()) {
/*      */       case 10:
/* 1145 */         if (this.midiSelected != null)
/* 1146 */           playMidi(this.midiSelected.getFile(), this.midiInfo); 
/*      */         return;
/*      */       case 27:
/* 1149 */         this.maestro.finish();
/*      */         return;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void treeMidiValueChanged(TreeSelectionEvent evt) {
/* 1158 */     this.draftSelected = this.albumSelected = null;
/* 1159 */     this.midiSelected = (FileTreeNode)evt.getPath().getLastPathComponent();
/* 1160 */     if (this.midiSelected.isLeaf()) {
/* 1161 */       this.midiInfo = null;
/* 1162 */       if (this.midiPanel.isVisible()) {
/* 1163 */         this.midiInfo = new MidiInfo(this.midiSelected.getFile(), this.maestro);
/* 1164 */         this.midiPanel.setInfo(this.midiInfo);
/*      */       } 
/*      */     } else {
/* 1167 */       this.midiSelected = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void treeDraftKeyPressed(KeyEvent evt) {}
/*      */ 
/*      */   
/*      */   private void treeDraftValueChanged(TreeSelectionEvent evt) {
/* 1176 */     this.albumSelected = this.midiSelected = null;
/* 1177 */     this.draftSelected = (FileTreeNode)evt.getPath().getLastPathComponent();
/* 1178 */     if (this.draftSelected.isLeaf()) {
/* 1179 */       selectDoremi(this.draftSelected.getFile());
/*      */     }
/*      */   }
/*      */   
/*      */   private void treeAlbumKeyPressed(KeyEvent evt) {
/* 1184 */     switch (evt.getKeyCode()) {
/*      */       case 10:
/* 1186 */         if (this.midiSelected != null)
/* 1187 */           playMidi(this.midiSelected.getFile(), this.midiInfo); 
/*      */         return;
/*      */       case 27:
/* 1190 */         this.maestro.finish();
/*      */         return;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void treeAlbumValueChanged(TreeSelectionEvent evt) {
/* 1198 */     this.draftSelected = this.midiSelected = null;
/* 1199 */     this.albumSelected = (FileTreeNode)evt.getPath().getLastPathComponent();
/* 1200 */     if (this.albumSelected.isLeaf()) {
/* 1201 */       selectDoremi(this.albumSelected.getFile());
/*      */     } else {
/*      */       
/* 1204 */       this.albumSelected = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void treePlaylistValueChanged(TreeSelectionEvent evt) {}
/*      */ 
/*      */ 
/*      */   
/*      */   private void treePlaylistKeyPressed(KeyEvent evt) {}
/*      */ 
/*      */ 
/*      */   
/*      */   public static void main(String[] args) {
/*      */     final String baseFolder;
/* 1221 */     if (args.length < 1) {
/* 1222 */       baseFolder = "." + File.separator;
/* 1223 */     } else if (args[0].endsWith(File.separator)) {
/* 1224 */       baseFolder = args[0];
/*      */     } else {
/*      */       
/* 1227 */       baseFolder = args[0] + File.separator;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1235 */     ArrayRythm.initDefault();
/* 1236 */     EventQueue.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1238 */             Main.blueStart();
/* 1239 */             (new Main(baseFolder)).setVisible(true);
/* 1240 */             Main.blueStop();
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void updatePlayerStatus(String name, String status, Color color) {
/* 1314 */     for (int i = 0; i < this.pPlayers.getComponentCount(); i++) {
/* 1315 */       JLabel jLabel = (JLabel)this.pPlayers.getComponent(i);
/* 1316 */       if (name.equals(jLabel.getText())) {
/* 1317 */         jLabel.setToolTipText(status);
/* 1318 */         jLabel.setForeground(color);
/*      */         return;
/*      */       } 
/*      */     } 
/* 1322 */     JLabel lb = new JLabel();
/* 1323 */     lb.setForeground(color);
/* 1324 */     lb.setHorizontalAlignment(0);
/* 1325 */     lb.setText(name);
/* 1326 */     lb.setToolTipText(status);
/* 1327 */     lb.setForeground(color);
/* 1328 */     this.pPlayers.add(lb);
/*      */   }
/*      */ 
/*      */   
/*      */   public void connected(Device dev) {
/* 1333 */     final String name = dev.getName();
/* 1334 */     final String status = "Connected to " + dev.getPort();
/* 1335 */     SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1337 */             Main.this.updatePlayerStatus(name, status, Color.GREEN);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void disconnected(Device dev) {
/* 1345 */     final String name = dev.getName();
/* 1346 */     final String status = "Cannot connect to " + dev.getPort();
/* 1347 */     SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1349 */             Main.this.updatePlayerStatus(name, status, Color.RED);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void changeForte(int forte) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void changeTempo(int tempo) {
/* 1373 */     final int value = tempo;
/* 1374 */     SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1376 */             Main.this.changed = true;
/* 1377 */             Main.this.sTempo.setValue(value);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void changeKey(int value) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void changeTick(long tick, long nextOn) {
/* 1403 */     final long value = tick;
/* 1404 */     final long no = nextOn;
/* 1405 */     SwingUtilities.invokeLater(new Runnable() {
/*      */           public void run() {
/* 1407 */             Main.this.changed = true;
/* 1408 */             if (Main.this.visualPanel.isReady()) {
/* 1409 */               Main.this.visualPanel.changeTick((int)value, no);
/*      */             }
/* 1411 */             if (Main.this.orchestraPanel.isReady()) {
/* 1412 */               Main.this.orchestraPanel.changeTick((int)value, no);
/*      */             }
/* 1414 */             if (Main.this.animFrame != null && Main.this.animFrame.isVisible()) {
/* 1415 */               Main.this.animFrame.changeTick((int)value, no);
/*      */             }
/* 1417 */             if (value % 24L == 0L) {
/* 1418 */               Main.this.sPosition.setValue((int)(value / 24L));
/*      */             }
/*      */           }
/*      */         });
/*      */   }
/*      */   
/*      */   public void started(Sequence seq) {
/* 1425 */     if (seq == null)
/* 1426 */       return;  this.sPosition.setMaximum(seq.max_tick / 24);
/* 1427 */     StringBuilder s = new StringBuilder();
/* 1428 */     s.append(" ");
/* 1429 */     s.append(seq.titles.get(0));
/* 1430 */     if (seq.composers != null) {
/* 1431 */       s.append(", Composer: ");
/* 1432 */       s.append(seq.composers.get(0));
/*      */     } 
/* 1434 */     if (seq.arrangers != null) {
/* 1435 */       s.append(", Arranger: ");
/* 1436 */       s.append(seq.arrangers.get(0));
/*      */     } 
/* 1438 */     if (seq.editor != null) {
/* 1439 */       s.append(", Editor: ");
/* 1440 */       s.append(seq.editor);
/*      */     } 
/* 1442 */     setMessage(s.toString(), Color.GREEN);
/* 1443 */     if (this.visualPanel.isReady()) {
/* 1444 */       this.visualPanel.start(seq);
/*      */     }
/* 1446 */     if (this.orchestraPanel.isReady()) {
/* 1447 */       this.orchestraPanel.start(seq);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void finished(Sequence seq) {
/* 1453 */     setMessage("Thank you .... ", Color.GREEN);
/* 1454 */     if (seq == null)
/* 1455 */       return;  this.hitsPanel.finished(seq);
/*      */ 
/*      */     
/* 1458 */     if (this.karaokePanel.isVisible() && this.karaokePanel.isPlaylistFilled()) {
/* 1459 */       playDoremi(this.karaokePanel.getSelectedFile());
/*      */     }
/*      */     
/* 1462 */     if (this.visualPanel.isReady()) {
/* 1463 */       this.visualPanel.finished(seq);
/*      */     }
/* 1465 */     if (this.orchestraPanel.isReady()) {
/* 1466 */       this.orchestraPanel.finished(seq);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public void waiting(long waited) {
/* 1472 */     if (this.visualPanel.isReady()) {
/* 1473 */       this.visualPanel.waiting(waited);
/*      */     }
/* 1475 */     if (this.orchestraPanel.isReady()) {
/* 1476 */       this.orchestraPanel.waiting(waited);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setMessage(String str) {
/* 1481 */     setMessage(str, Color.WHITE);
/*      */   }
/*      */   
/*      */   public void setMessage(String str, Color c) {
/* 1485 */     this.lbMessage.setForeground(c);
/* 1486 */     this.lbMessage.setText(str);
/*      */   }
/*      */   
/*      */   public void setInfo(String str) {
/* 1490 */     this.lbInfo.setText(str);
/*      */   }
/*      */   
/*      */   public void startVisualization(ArrayList<Lakon> lakons) {
/* 1494 */     if (this.animFrame == null) {
/* 1495 */       this.animFrame = AnimationFrame.create(this);
/*      */     }
/* 1497 */     this.animFrame.start(lakons);
/*      */   }
/*      */   
/*      */   public void stopVisualization() {
/* 1501 */     this.animFrame.setVisible(false);
/*      */   }
/*      */ 
/*      */   
/*      */   public void stop() {
/* 1506 */     this.maestro.finish();
/*      */   }
/*      */ 
/*      */   
/*      */   public void midiOn(byte data1, byte data2, byte data3) {
/* 1511 */     this.maestro.midiOn(data1, data2, data3);
/*      */   }
/*      */ 
/*      */   
/*      */   public void midiOff(byte data1, byte data2) {
/* 1516 */     this.maestro.midiOff(data1, data2);
/*      */   }
/*      */ 
/*      */   
/*      */   public Maestro getMaestro() {
/* 1521 */     return this.maestro;
/*      */   }
/*      */   
/*      */   public String getAlbumFolder() {
/* 1525 */     return this.albumFolder;
/*      */   }
/*      */   
/*      */   static void blueStart() {}
/*      */   
/*      */   static void blueStop() {}
/*      */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */