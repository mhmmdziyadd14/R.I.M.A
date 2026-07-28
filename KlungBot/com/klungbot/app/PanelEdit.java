/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.MidiConverter;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.StringReader;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.JToggleButton;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.KeyStroke;
/*     */ import javax.swing.event.CaretEvent;
/*     */ import javax.swing.event.CaretListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.event.UndoableEditEvent;
/*     */ import javax.swing.event.UndoableEditListener;
/*     */ import javax.swing.text.Caret;
/*     */ import javax.swing.text.Utilities;
/*     */ import javax.swing.undo.UndoManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PanelEdit
/*     */   extends JPanel
/*     */   implements DocumentListener, CaretListener
/*     */ {
/*  43 */   static String newFileName = "noname.123";
/*     */   
/*     */   File fileName;
/*     */   boolean modified;
/*     */   Main frame;
/*  48 */   String doremiInfo = "$ Doremi is a simple text notation for writing a music\n$ It starts with the header part like these lines\nT: The title\nC: The composer name\nA: The arranger name\nE: The editor\nR: Rythim name (jazz, swing, etc.)\nM: Meter (e.g: 2/4, 3/4. 4/4, 6/8, 8/8)\nQ: Beats per minute (e.g: 80)\nK: The key (e.g: C, C#, D)\n\n$ Followed by the music parts.\n$ It may have multiple voices,\nV1: 1 2 3 4 | 5 6 7 0 |\nV2: 3 0 0 0 | 7 0 0 0 |\n$ an accompaniment chord,\nVA: @1 0 0 0 | @3m 0 0 0|\n$ then followed by the words line\nW: Do Re Mi Fa Sol-La-Si-Do\n\n$The voice part contains sequence of numerical notes\n$that must be a digit between 1 to 7, or 0 for silence.\n$The notes can be lowered or raised using these modifiers:\n$ / : up 1/2 pitch\n$ \\ : down 1/2 pitch\n$ ' : up 1 octave\n$ , : down 1 octave\nV1: 1/ 2\\ 3, 4'\n\n$The notes length is a quarter by default. It can be changed\n$ - : note is 1/2 length\n$ = : note is 1/4 length\n$ + : note is 2/3 length\n$ . : add the length by 1\n$ ^ : note is a stacato\n$ | : the bar line\nV1: 1- 2= 3= . | 4^ 5-^ 6=^ 7^=\n\n$ Meanwhile the accompaniment lines may use chords symbols\n$ @1 @1# .. @7 : Major chords\n$ @1m @1#m .. @7m : Minor chords\n$ @17 @1#7 .. @77 : Major chords with septim\n";
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   String doremiNew = "# Doremi file\nT: title \nC: composer\nA: arranger\nE: editor\nG: genre\nM: 4/4\nQ: 80\nK: C\n\n# Music part\nV1: \n"; DialogSave dlgSave; JTextPane editor; DoremiDocument docDoremi; PlainDocument docPlain; FindAndReplace finder; private JButton btAlbum;
/*     */   private JButton btCopy;
/*     */   private JButton btCut;
/*     */   private JButton btDelete;
/*     */   private JButton btEdit;
/*     */   private JButton btInfo;
/*     */   private JButton btNew;
/*     */   private JButton btPaste;
/*     */   private JButton btRedo;
/*     */   private JButton btReplace;
/*     */   private JButton btReplaceAll;
/*     */   private JButton btSave;
/*     */   private JButton btSaveAs;
/*     */   private JButton btSearch;
/*     */   private JButton btUndo;
/*     */   private JToggleButton btView;
/*     */   private JPanel editorPanel;
/*     */   private JScrollPane jScrollPane1;
/* 106 */   protected UndoManager undo = new UndoManager(); private JToolBar.Separator jSeparator1; private JToolBar.Separator jSeparator2;
/*     */   private JToolBar.Separator jSeparator3;
/*     */   private JTextField tfReplace;
/*     */   private JTextField tfSearch;
/*     */   private JToolBar toolBar;
/*     */   
/*     */   protected class MyUndoableEditListener implements UndoableEditListener { public void undoableEditHappened(UndoableEditEvent e) {
/* 113 */       if (!e.getEdit().isSignificant())
/* 114 */         return;  String name = e.getEdit().getPresentationName();
/* 115 */       if (name.equals("style change"))
/* 116 */         return;  PanelEdit.this.undo.addEdit(e.getEdit());
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PanelEdit(Main frame) {
/* 124 */     initComponents();
/* 125 */     this.fileName = null;
/* 126 */     this.modified = false;
/* 127 */     this.frame = frame;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     this.editor = new JTextPane();
/* 134 */     this.editorPanel.add(this.editor, "Center");
/* 135 */     this.docDoremi = new DoremiDocument();
/* 136 */     this.docPlain = null;
/* 137 */     this.editor.setDocument(this.docDoremi);
/* 138 */     this.editor.addCaretListener(this);
/*     */ 
/*     */     
/* 141 */     this.docDoremi.addUndoableEditListener(new MyUndoableEditListener());
/*     */     
/* 143 */     this.finder = new FindAndReplace(this.editor);
/* 144 */     initShortcuts();
/*     */   }
/*     */ 
/*     */   
/*     */   private void initShortcuts() {
/* 149 */     this.editor.getActionMap().put("Undo", new AbstractAction("Undo")
/*     */         {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 152 */             PanelEdit.this.btUndoActionPerformed(evt);
/*     */           }
/*     */         });
/* 155 */     this.editor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
/*     */ 
/*     */     
/* 158 */     this.editor.getActionMap().put("Redo", new AbstractAction("Redo")
/*     */         {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 161 */             PanelEdit.this.btRedoActionPerformed(evt);
/*     */           }
/*     */         });
/* 164 */     this.editor.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
/*     */ 
/*     */     
/* 167 */     this.editor.getActionMap().put("Save", new AbstractAction("Save")
/*     */         {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 170 */             PanelEdit.this.btSaveActionPerformed(evt);
/*     */           }
/*     */         });
/* 173 */     this.editor.getInputMap().put(KeyStroke.getKeyStroke("control S"), "Save");
/*     */ 
/*     */     
/* 176 */     this.editor.getActionMap().put("Publish", new AbstractAction("Publish")
/*     */         {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 179 */             PanelEdit.this.btAlbumActionPerformed(evt);
/*     */           }
/*     */         });
/* 182 */     this.editor.getInputMap().put(KeyStroke.getKeyStroke("control P"), "Publish");
/*     */ 
/*     */     
/* 185 */     this.editor.getActionMap().put("TextMode", new AbstractAction("TextMode")
/*     */         {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 188 */             PanelEdit.this.btViewActionPerformed(evt);
/*     */           }
/*     */         });
/* 191 */     this.editor.getInputMap().put(KeyStroke.getKeyStroke("F2"), "TextMode");
/*     */ 
/*     */     
/* 194 */     this.editor.getActionMap().put("Help", new AbstractAction("Help")
/*     */         {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 197 */             PanelEdit.this.btInfoActionPerformed(evt);
/*     */           }
/*     */         });
/* 200 */     this.editor.getInputMap().put(KeyStroke.getKeyStroke("F1"), "Help");
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
/*     */   private void initComponents() {
/* 213 */     this.toolBar = new JToolBar();
/* 214 */     this.btView = new JToggleButton();
/* 215 */     this.btNew = new JButton();
/* 216 */     this.btEdit = new JButton();
/* 217 */     this.btSave = new JButton();
/* 218 */     this.btSaveAs = new JButton();
/* 219 */     this.btAlbum = new JButton();
/* 220 */     this.btDelete = new JButton();
/* 221 */     this.jSeparator1 = new JToolBar.Separator();
/* 222 */     this.btCut = new JButton();
/* 223 */     this.btCopy = new JButton();
/* 224 */     this.btPaste = new JButton();
/* 225 */     this.btUndo = new JButton();
/* 226 */     this.btRedo = new JButton();
/* 227 */     this.jSeparator2 = new JToolBar.Separator();
/* 228 */     this.tfSearch = new JTextField();
/* 229 */     this.btSearch = new JButton();
/* 230 */     this.tfReplace = new JTextField();
/* 231 */     this.btReplace = new JButton();
/* 232 */     this.btReplaceAll = new JButton();
/* 233 */     this.jSeparator3 = new JToolBar.Separator();
/* 234 */     this.btInfo = new JButton();
/* 235 */     this.jScrollPane1 = new JScrollPane();
/* 236 */     this.editorPanel = new JPanel();
/*     */     
/* 238 */     addComponentListener(new ComponentAdapter() {
/*     */           public void componentHidden(ComponentEvent evt) {
/* 240 */             PanelEdit.this.formComponentHidden(evt);
/*     */           }
/*     */           public void componentShown(ComponentEvent evt) {
/* 243 */             PanelEdit.this.formComponentShown(evt);
/*     */           }
/*     */         });
/* 246 */     setLayout(new BorderLayout());
/*     */     
/* 248 */     this.toolBar.setFloatable(false);
/* 249 */     this.toolBar.setRollover(true);
/* 250 */     this.toolBar.setName("toolBar");
/*     */     
/* 252 */     this.btView.setIcon(new ImageIcon(getClass().getResource("/resources/icon/view.png")));
/* 253 */     this.btView.setToolTipText("Toggle view mode");
/* 254 */     this.btView.setFocusable(false);
/* 255 */     this.btView.setHorizontalTextPosition(0);
/* 256 */     this.btView.setName("btView");
/* 257 */     this.btView.setVerticalTextPosition(3);
/* 258 */     this.btView.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 260 */             PanelEdit.this.btViewActionPerformed(evt);
/*     */           }
/*     */         });
/* 263 */     this.toolBar.add(this.btView);
/*     */     
/* 265 */     this.btNew.setIcon(new ImageIcon(getClass().getResource("/resources/icon/filenew.png")));
/* 266 */     this.btNew.setToolTipText("Edit a new file");
/* 267 */     this.btNew.setContentAreaFilled(false);
/* 268 */     this.btNew.setFocusable(false);
/* 269 */     this.btNew.setHorizontalTextPosition(0);
/* 270 */     this.btNew.setName("btNew");
/* 271 */     this.btNew.setVerticalTextPosition(3);
/* 272 */     this.btNew.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 274 */             PanelEdit.this.btNewActionPerformed(evt);
/*     */           }
/*     */         });
/* 277 */     this.toolBar.add(this.btNew);
/*     */     
/* 279 */     this.btEdit.setIcon(new ImageIcon(getClass().getResource("/resources/icon/edit.png")));
/* 280 */     this.btEdit.setToolTipText("Edit the selected file");
/* 281 */     this.btEdit.setContentAreaFilled(false);
/* 282 */     this.btEdit.setFocusable(false);
/* 283 */     this.btEdit.setHorizontalTextPosition(0);
/* 284 */     this.btEdit.setName("btEdit");
/* 285 */     this.btEdit.setVerticalTextPosition(3);
/* 286 */     this.btEdit.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 288 */             PanelEdit.this.btEditActionPerformed(evt);
/*     */           }
/*     */         });
/* 291 */     this.toolBar.add(this.btEdit);
/*     */     
/* 293 */     this.btSave.setIcon(new ImageIcon(getClass().getResource("/resources/icon/filesave.png")));
/* 294 */     this.btSave.setToolTipText("Save file");
/* 295 */     this.btSave.setContentAreaFilled(false);
/* 296 */     this.btSave.setFocusable(false);
/* 297 */     this.btSave.setHideActionText(true);
/* 298 */     this.btSave.setHorizontalTextPosition(0);
/* 299 */     this.btSave.setName("btSave");
/* 300 */     this.btSave.setVerticalTextPosition(3);
/* 301 */     this.btSave.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 303 */             PanelEdit.this.btSaveActionPerformed(evt);
/*     */           }
/*     */         });
/* 306 */     this.toolBar.add(this.btSave);
/*     */     
/* 308 */     this.btSaveAs.setIcon(new ImageIcon(getClass().getResource("/resources/icon/filesaveas.png")));
/* 309 */     this.btSaveAs.setToolTipText("Save as ...");
/* 310 */     this.btSaveAs.setContentAreaFilled(false);
/* 311 */     this.btSaveAs.setFocusable(false);
/* 312 */     this.btSaveAs.setHorizontalTextPosition(0);
/* 313 */     this.btSaveAs.setName("btSaveAs");
/* 314 */     this.btSaveAs.setVerticalTextPosition(3);
/* 315 */     this.btSaveAs.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 317 */             PanelEdit.this.btSaveAsActionPerformed(evt);
/*     */           }
/*     */         });
/* 320 */     this.toolBar.add(this.btSaveAs);
/*     */     
/* 322 */     this.btAlbum.setIcon(new ImageIcon(getClass().getResource("/resources/icon/playsound.png")));
/* 323 */     this.btAlbum.setToolTipText("Publish file to album");
/* 324 */     this.btAlbum.setFocusable(false);
/* 325 */     this.btAlbum.setHorizontalTextPosition(0);
/* 326 */     this.btAlbum.setName("btAlbum");
/* 327 */     this.btAlbum.setVerticalTextPosition(3);
/* 328 */     this.btAlbum.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 330 */             PanelEdit.this.btAlbumActionPerformed(evt);
/*     */           }
/*     */         });
/* 333 */     this.toolBar.add(this.btAlbum);
/*     */     
/* 335 */     this.btDelete.setIcon(new ImageIcon(getClass().getResource("/resources/icon/cancel.png")));
/* 336 */     this.btDelete.setToolTipText("Delete file !");
/* 337 */     this.btDelete.setFocusable(false);
/* 338 */     this.btDelete.setHorizontalTextPosition(0);
/* 339 */     this.btDelete.setName("btDelete");
/* 340 */     this.btDelete.setVerticalTextPosition(3);
/* 341 */     this.btDelete.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 343 */             PanelEdit.this.btDeleteActionPerformed(evt);
/*     */           }
/*     */         });
/* 346 */     this.toolBar.add(this.btDelete);
/*     */     
/* 348 */     this.jSeparator1.setName("jSeparator1");
/* 349 */     this.toolBar.add(this.jSeparator1);
/*     */     
/* 351 */     this.btCut.setIcon(new ImageIcon(getClass().getResource("/resources/icon/cut.png")));
/* 352 */     this.btCut.setToolTipText("Cut text");
/* 353 */     this.btCut.setContentAreaFilled(false);
/* 354 */     this.btCut.setFocusable(false);
/* 355 */     this.btCut.setHorizontalTextPosition(0);
/* 356 */     this.btCut.setName("btCut");
/* 357 */     this.btCut.setVerticalTextPosition(3);
/* 358 */     this.btCut.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 360 */             PanelEdit.this.btCutActionPerformed(evt);
/*     */           }
/*     */         });
/* 363 */     this.toolBar.add(this.btCut);
/*     */     
/* 365 */     this.btCopy.setIcon(new ImageIcon(getClass().getResource("/resources/icon/copy.png")));
/* 366 */     this.btCopy.setToolTipText("Copy text");
/* 367 */     this.btCopy.setContentAreaFilled(false);
/* 368 */     this.btCopy.setFocusable(false);
/* 369 */     this.btCopy.setHorizontalTextPosition(0);
/* 370 */     this.btCopy.setName("btCopy");
/* 371 */     this.btCopy.setVerticalTextPosition(3);
/* 372 */     this.btCopy.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 374 */             PanelEdit.this.btCopyActionPerformed(evt);
/*     */           }
/*     */         });
/* 377 */     this.toolBar.add(this.btCopy);
/*     */     
/* 379 */     this.btPaste.setIcon(new ImageIcon(getClass().getResource("/resources/icon/paste.png")));
/* 380 */     this.btPaste.setToolTipText("Paste text");
/* 381 */     this.btPaste.setContentAreaFilled(false);
/* 382 */     this.btPaste.setFocusable(false);
/* 383 */     this.btPaste.setHorizontalTextPosition(0);
/* 384 */     this.btPaste.setName("btPaste");
/* 385 */     this.btPaste.setVerticalTextPosition(3);
/* 386 */     this.btPaste.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 388 */             PanelEdit.this.btPasteActionPerformed(evt);
/*     */           }
/*     */         });
/* 391 */     this.toolBar.add(this.btPaste);
/*     */     
/* 393 */     this.btUndo.setIcon(new ImageIcon(getClass().getResource("/resources/icon/undo.png")));
/* 394 */     this.btUndo.setToolTipText("Undo");
/* 395 */     this.btUndo.setContentAreaFilled(false);
/* 396 */     this.btUndo.setFocusable(false);
/* 397 */     this.btUndo.setHorizontalTextPosition(0);
/* 398 */     this.btUndo.setName("btUndo");
/* 399 */     this.btUndo.setVerticalTextPosition(3);
/* 400 */     this.btUndo.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 402 */             PanelEdit.this.btUndoActionPerformed(evt);
/*     */           }
/*     */         });
/* 405 */     this.toolBar.add(this.btUndo);
/*     */     
/* 407 */     this.btRedo.setIcon(new ImageIcon(getClass().getResource("/resources/icon/redo.png")));
/* 408 */     this.btRedo.setToolTipText("Redo");
/* 409 */     this.btRedo.setContentAreaFilled(false);
/* 410 */     this.btRedo.setFocusable(false);
/* 411 */     this.btRedo.setHorizontalTextPosition(0);
/* 412 */     this.btRedo.setName("btRedo");
/* 413 */     this.btRedo.setVerticalTextPosition(3);
/* 414 */     this.btRedo.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 416 */             PanelEdit.this.btRedoActionPerformed(evt);
/*     */           }
/*     */         });
/* 419 */     this.toolBar.add(this.btRedo);
/*     */     
/* 421 */     this.jSeparator2.setName("jSeparator2");
/* 422 */     this.toolBar.add(this.jSeparator2);
/*     */     
/* 424 */     this.tfSearch.setToolTipText("Searched text");
/* 425 */     this.tfSearch.setMinimumSize(new Dimension(100, 25));
/* 426 */     this.tfSearch.setName("tfSearch");
/* 427 */     this.tfSearch.setPreferredSize(new Dimension(100, 25));
/* 428 */     this.toolBar.add(this.tfSearch);
/*     */     
/* 430 */     this.btSearch.setIcon(new ImageIcon(getClass().getResource("/resources/icon/search.png")));
/* 431 */     this.btSearch.setToolTipText("Find");
/* 432 */     this.btSearch.setFocusable(false);
/* 433 */     this.btSearch.setHorizontalTextPosition(0);
/* 434 */     this.btSearch.setName("btSearch");
/* 435 */     this.btSearch.setVerticalTextPosition(3);
/* 436 */     this.btSearch.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 438 */             PanelEdit.this.btSearchActionPerformed(evt);
/*     */           }
/*     */         });
/* 441 */     this.toolBar.add(this.btSearch);
/*     */     
/* 443 */     this.tfReplace.setToolTipText("Replacement text");
/* 444 */     this.tfReplace.setMinimumSize(new Dimension(100, 25));
/* 445 */     this.tfReplace.setName("tfReplace");
/* 446 */     this.tfReplace.setPreferredSize(new Dimension(100, 25));
/* 447 */     this.toolBar.add(this.tfReplace);
/*     */     
/* 449 */     this.btReplace.setIcon(new ImageIcon(getClass().getResource("/resources/icon/replace.png")));
/* 450 */     this.btReplace.setToolTipText("Search and replace");
/* 451 */     this.btReplace.setFocusable(false);
/* 452 */     this.btReplace.setHorizontalTextPosition(0);
/* 453 */     this.btReplace.setName("btReplace");
/* 454 */     this.btReplace.setVerticalTextPosition(3);
/* 455 */     this.btReplace.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 457 */             PanelEdit.this.btReplaceActionPerformed(evt);
/*     */           }
/*     */         });
/* 460 */     this.toolBar.add(this.btReplace);
/*     */     
/* 462 */     this.btReplaceAll.setIcon(new ImageIcon(getClass().getResource("/resources/icon/replace_all.png")));
/* 463 */     this.btReplaceAll.setToolTipText("Replace all");
/* 464 */     this.btReplaceAll.setFocusable(false);
/* 465 */     this.btReplaceAll.setHorizontalTextPosition(0);
/* 466 */     this.btReplaceAll.setName("btReplaceAll");
/* 467 */     this.btReplaceAll.setVerticalTextPosition(3);
/* 468 */     this.btReplaceAll.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 470 */             PanelEdit.this.btReplaceAllActionPerformed(evt);
/*     */           }
/*     */         });
/* 473 */     this.toolBar.add(this.btReplaceAll);
/*     */     
/* 475 */     this.jSeparator3.setName("jSeparator3");
/* 476 */     this.toolBar.add(this.jSeparator3);
/*     */     
/* 478 */     this.btInfo.setIcon(new ImageIcon(getClass().getResource("/resources/icon/info.png")));
/* 479 */     this.btInfo.setToolTipText("Paste text");
/* 480 */     this.btInfo.setContentAreaFilled(false);
/* 481 */     this.btInfo.setFocusable(false);
/* 482 */     this.btInfo.setHorizontalTextPosition(0);
/* 483 */     this.btInfo.setName("btInfo");
/* 484 */     this.btInfo.setVerticalTextPosition(3);
/* 485 */     this.btInfo.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 487 */             PanelEdit.this.btInfoActionPerformed(evt);
/*     */           }
/*     */         });
/* 490 */     this.toolBar.add(this.btInfo);
/*     */     
/* 492 */     add(this.toolBar, "First");
/*     */     
/* 494 */     this.jScrollPane1.setName("jScrollPane1");
/*     */     
/* 496 */     this.editorPanel.setName("editorPanel");
/* 497 */     this.editorPanel.setLayout(new BorderLayout());
/* 498 */     this.jScrollPane1.setViewportView(this.editorPanel);
/*     */     
/* 500 */     add(this.jScrollPane1, "Center");
/*     */   }
/*     */   
/*     */   private void btCutActionPerformed(ActionEvent evt) {
/* 504 */     this.editor.cut();
/*     */   }
/*     */ 
/*     */   
/*     */   private void btCopyActionPerformed(ActionEvent evt) {
/* 509 */     this.editor.copy();
/*     */   }
/*     */ 
/*     */   
/*     */   private void btPasteActionPerformed(ActionEvent evt) {
/* 514 */     this.editor.paste();
/*     */   }
/*     */   
/*     */   private void btSaveActionPerformed(ActionEvent evt) {
/* 518 */     if (this.fileName == null) {
/* 519 */       btSaveAsActionPerformed(evt);
/*     */     } else {
/*     */       
/* 522 */       save();
/* 523 */       this.frame.setMessage("Saved " + this.fileName, Color.GREEN);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void btNewActionPerformed(ActionEvent evt) {
/* 528 */     if (this.modified) {
/* 529 */       btSaveActionPerformed(evt);
/*     */     }
/* 531 */     this.editor.setText(this.doremiNew);
/* 532 */     this.fileName = null;
/* 533 */     this.modified = false;
/* 534 */     this.frame.setMessage("Editing a new file", Color.GREEN);
/*     */   }
/*     */   
/*     */   private void btSaveAsActionPerformed(ActionEvent evt) {
/* 538 */     if (this.dlgSave == null) {
/* 539 */       this.dlgSave = new DialogSave(this.frame, this.frame.draftFolder);
/*     */     }
/* 541 */     if (this.fileName == null) {
/* 542 */       this.fileName = new File(newFileName);
/*     */     }
/* 544 */     if (this.dlgSave.showDialog(this.fileName) != 0) {
/* 545 */       String fpath = this.dlgSave.getFilePath();
/* 546 */       if (!fpath.endsWith(".123")) {
/* 547 */         fpath = fpath + ".123";
/*     */       }
/* 549 */       this.fileName = new File(fpath);
/* 550 */       if (save()) {
/* 551 */         this.frame.initDraft();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void btEditActionPerformed(ActionEvent evt) {
/* 558 */     File fname = this.frame.getSelectedDraft();
/* 559 */     if (fname != null) {
/* 560 */       if (this.modified) save(); 
/* 561 */       open(fname);
/* 562 */       this.frame.setMessage("Editing " + fname, Color.GREEN);
/*     */     } else {
/*     */       
/* 565 */       this.frame.setMessage("Please choose a file from the draft list", Color.YELLOW);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void btInfoActionPerformed(ActionEvent evt) {
/* 571 */     if (this.modified) {
/* 572 */       btSaveActionPerformed(evt);
/*     */     }
/* 574 */     this.editor.setText(this.doremiInfo);
/* 575 */     this.fileName = null;
/* 576 */     this.modified = false;
/*     */   }
/*     */   
/*     */   private void btUndoActionPerformed(ActionEvent evt) {
/* 580 */     if (this.undo.canUndo())
/* 581 */       this.undo.undo(); 
/*     */   }
/*     */   
/*     */   private void btRedoActionPerformed(ActionEvent evt) {
/* 585 */     if (this.undo.canRedo())
/* 586 */       this.undo.redo(); 
/*     */   }
/*     */   
/*     */   private void btSearchActionPerformed(ActionEvent evt) {
/* 590 */     String text = this.tfSearch.getText();
/* 591 */     if (text.length() == 0) {
/* 592 */       this.frame.setMessage("Search text is still empty", Color.YELLOW);
/*     */       return;
/*     */     } 
/* 595 */     if (!this.finder.find(text)) {
/* 596 */       this.frame.setMessage("Search term not found. Repeat again to search from the start");
/*     */     } else {
/*     */       
/* 599 */       this.editor.requestFocus();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void btReplaceActionPerformed(ActionEvent evt) {
/* 605 */     String text = this.tfSearch.getText();
/* 606 */     if (text.length() == 0) {
/* 607 */       this.frame.setMessage("Search text is still empty", Color.YELLOW);
/*     */       return;
/*     */     } 
/* 610 */     String replace = this.tfReplace.getText();
/* 611 */     if (!this.finder.replace(text, replace)) {
/* 612 */       this.frame.setMessage("End of document. Press the button again to replace from the start");
/*     */     } else {
/*     */       
/* 615 */       this.editor.requestFocus();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void btReplaceAllActionPerformed(ActionEvent evt) {
/* 622 */     String text = this.tfSearch.getText();
/* 623 */     if (text.length() == 0) {
/* 624 */       this.frame.setMessage("Search text is still empty", Color.YELLOW);
/*     */       return;
/*     */     } 
/* 627 */     String replace = this.tfReplace.getText();
/* 628 */     int i = 0;
/* 629 */     while (this.finder.replace(text, replace)) {
/* 630 */       i++;
/*     */     }
/* 632 */     this.frame.setMessage("Replaced " + i + " occurances");
/* 633 */     this.editor.requestFocus();
/*     */   }
/*     */   
/*     */   private void btAlbumActionPerformed(ActionEvent evt) {
/* 637 */     publish();
/*     */   }
/*     */   
/*     */   private void btDeleteActionPerformed(ActionEvent evt) {
/* 641 */     String fname = this.frame.getSelectedFile();
/* 642 */     delete(fname);
/*     */   }
/*     */ 
/*     */   
/*     */   private void formComponentShown(ComponentEvent evt) {
/* 647 */     this.frame.setMessage("Editing doremi songs. Select a file from the draft, then click the edit button.", Color.GREEN);
/*     */   }
/*     */ 
/*     */   
/*     */   private void btViewActionPerformed(ActionEvent evt) {
/* 652 */     Caret caret = this.editor.getCaret();
/* 653 */     String s = this.editor.getText();
/* 654 */     if (this.btView.isSelected()) {
/* 655 */       if (this.docPlain == null) this.docPlain = new PlainDocument(); 
/* 656 */       this.editor.setDocument(this.docPlain);
/*     */     } else {
/*     */       
/* 659 */       this.editor.setDocument(this.docDoremi);
/*     */     } 
/* 661 */     this.editor.setText(s);
/* 662 */     this.editor.setCaret(caret);
/* 663 */     this.undo.discardAllEdits();
/*     */   }
/*     */ 
/*     */   
/*     */   private void formComponentHidden(ComponentEvent evt) {
/* 668 */     this.frame.setInfo("");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void importMidi(String fname) {
/*     */     try {
/* 675 */       MidiConverter converter = new MidiConverter();
/* 676 */       StringBuilder text = converter.read(fname);
/*     */     }
/* 678 */     catch (Exception ex) {
/* 679 */       System.err.println("Open file: " + ex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void open(File fname) {
/* 685 */     StringBuilder s = new StringBuilder();
/*     */     try {
/* 687 */       BufferedReader reader = new BufferedReader(new FileReader(fname));
/* 688 */       while (reader.ready()) {
/* 689 */         s.append(reader.readLine());
/* 690 */         s.append("\n");
/*     */       } 
/* 692 */       reader.close();
/* 693 */       this.fileName = fname;
/* 694 */       this.modified = false;
/* 695 */       this.editor.setText(s.toString());
/* 696 */       this.undo.discardAllEdits();
/*     */     }
/* 698 */     catch (Exception ex) {
/* 699 */       this.frame.setMessage("Error opening " + fname + " : " + ex.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean save() {
/*     */     try {
/* 706 */       File fd = this.fileName.getParentFile();
/* 707 */       if (!fd.exists()) fd.mkdirs(); 
/* 708 */       BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName));
/* 709 */       writer.write(this.editor.getText());
/* 710 */       writer.close();
/*     */       
/* 712 */       this.frame.setMessage(" Saved " + this.fileName, Color.GREEN);
/* 713 */       return true;
/*     */     }
/* 715 */     catch (Exception ex) {
/* 716 */       this.frame.setMessage(" Error saving " + this.fileName + ": " + ex.getMessage(), Color.RED);
/*     */       
/* 718 */       return false;
/*     */     } 
/*     */   }
/*     */   String stripBase(String ffull, String base) {
/* 722 */     return ffull.substring(base.length());
/*     */   }
/*     */ 
/*     */   
/*     */   public void publish() {
/*     */     try {
/* 728 */       String fname = stripBase(this.fileName.getPath(), this.frame.baseFolder + this.frame.draftFolder);
/* 729 */       File f1 = new File(this.frame.baseFolder + this.frame.albumFolder + fname);
/* 730 */       File fd = f1.getParentFile();
/* 731 */       if (!fd.exists()) fd.mkdirs(); 
/* 732 */       BufferedWriter writer = new BufferedWriter(new FileWriter(f1));
/* 733 */       writer.write(this.editor.getText());
/* 734 */       writer.close();
/* 735 */       this.frame.initAlbum();
/* 736 */       this.frame.setMessage("Published to " + f1.getPath(), Color.green);
/*     */     }
/* 738 */     catch (Exception ex) {
/* 739 */       this.frame.setMessage("Error publishing " + this.fileName + ": " + ex.getMessage(), Color.red);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void delete(String fname) {
/* 744 */     File file = new File(fname);
/* 745 */     file.delete();
/* 746 */     this.frame.setMessage("Deleted " + fname, Color.GREEN);
/* 747 */     this.frame.initList();
/*     */   }
/*     */   
/*     */   public String getSelectedSong() {
/* 751 */     if (this.editor.getSelectionStart() == this.editor.getSelectionEnd()) {
/* 752 */       return this.editor.getText();
/*     */     }
/*     */     
/* 755 */     BufferedReader reader = new BufferedReader(new StringReader(this.editor.getText()));
/* 756 */     StringBuilder buffer = new StringBuilder();
/*     */     try {
/*     */       String line;
/*     */       do {
/* 760 */         line = reader.readLine();
/* 761 */         buffer.append(line);
/* 762 */         buffer.append("\n");
/* 763 */       } while (!line.startsWith("K:"));
/* 764 */     } catch (Exception ex) {}
/* 765 */     buffer.append("\n\n");
/* 766 */     buffer.append(this.editor.getSelectedText());
/* 767 */     buffer.append("\n");
/* 768 */     return buffer.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void insertUpdate(DocumentEvent e) {
/* 800 */     this.modified = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeUpdate(DocumentEvent e) {
/* 805 */     this.modified = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void changedUpdate(DocumentEvent e) {
/* 810 */     this.modified = true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void caretUpdate(CaretEvent e) {
/* 816 */     JTextPane ed = (JTextPane)e.getSource();
/* 817 */     int linenum = 1;
/* 818 */     int columnnum = 1;
/*     */     try {
/* 820 */       int pos = ed.getCaretPosition();
/* 821 */       int offs = Utilities.getRowStart(ed, pos) - 1;
/* 822 */       columnnum = pos - offs;
/* 823 */       while (offs > 0) {
/* 824 */         offs = Utilities.getRowStart(ed, offs) - 1;
/* 825 */         linenum++;
/*     */       }
/*     */     
/* 828 */     } catch (Exception ex) {}
/* 829 */     this.frame.setInfo("Cursor = [" + linenum + ", " + columnnum + "]");
/*     */   }
/*     */   
/*     */   public void setCaretPosition(int row, int col) {
/* 833 */     int ofs = 0;
/*     */     try {
/* 835 */       while (row > 1) {
/* 836 */         ofs = Utilities.getPositionBelow(this.editor, ofs, 0);
/* 837 */         row--;
/*     */       }
/*     */     
/* 840 */     } catch (Exception ex) {}
/* 841 */     this.editor.setCaretPosition(ofs + col);
/* 842 */     this.editor.requestFocus();
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelEdit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */