/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.bric.image.transition.Transition;
/*     */ import com.bric.image.transition.vanilla.CurtainTransition2D;
/*     */ import com.bric.image.transition.vanilla.SplitTransition2D;
/*     */ import java.awt.DisplayMode;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.KeyEventDispatcher;
/*     */ import java.awt.KeyboardFocusManager;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.image.BufferStrategy;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.JFrame;
/*     */ import wayang.Adegan;
/*     */ import wayang.Lakon;
/*     */ import wayang.TransitionAdegan;
/*     */ 
/*     */ public class AnimationFrame
/*     */   extends JFrame
/*     */ {
/*     */   Main parent;
/*     */   ArrayList<Lakon> lakons;
/*     */   Lakon lakon;
/*     */   Adegan current;
/*     */   Adegan prev;
/*     */   Adegan next;
/*     */   long tick;
/*     */   long noteOn;
/*     */   GraphicsDevice device;
/*     */   DisplayMode originalDM;
/*     */   DisplayMode preferredDM;
/*  40 */   int width = 800;
/*  41 */   int height = 600;
/*     */   BufferStrategy buffer;
/*     */   
/*     */   private class MyDispatcher
/*     */     implements KeyEventDispatcher {
/*     */     public boolean dispatchKeyEvent(KeyEvent e) {
/*  47 */       if (e.getID() == 401)
/*     */       {
/*  49 */         switch (e.getKeyCode()) { case 123:
/*  50 */             AnimationFrame.this.parent.stop(); return true;
/*  51 */           case 122: AnimationFrame.this.parent.play(); return true;
/*  52 */           case 27: AnimationFrame.this.finish(); return true; }
/*     */       
/*     */       }
/*  55 */       return false;
/*     */     }
/*     */     
/*     */     private MyDispatcher() {} }
/*     */   
/*     */   public static AnimationFrame create(Main parent) {
/*  61 */     GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*     */     
/*  63 */     GraphicsDevice dev = env.getDefaultScreenDevice();
/*  64 */     AnimationFrame frame = new AnimationFrame(parent, dev);
/*  65 */     frame.initLakons();
/*  66 */     return frame;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AnimationFrame(Main parent, GraphicsDevice device) {
/*  72 */     super(device.getDefaultConfiguration());
/*  73 */     this.device = device;
/*  74 */     initFullScreen();
/*  75 */     initComponents();
/*     */     try {
/*  77 */       Image im = ImageIO.read(getClass().getResource("/resources/icon/klung.png"));
/*  78 */       setIconImage(im);
/*  79 */     } catch (IOException ex) {}
/*  80 */     this.parent = parent;
/*  81 */     KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
/*  82 */     manager.addKeyEventDispatcher(new MyDispatcher());
/*     */   }
/*     */   
/*     */   private void initFullScreen() {
/*  86 */     boolean isFullScreen = this.device.isFullScreenSupported();
/*  87 */     setUndecorated(isFullScreen);
/*  88 */     setResizable(!isFullScreen);
/*  89 */     if (isFullScreen) {
/*  90 */       setIgnoreRepaint(true);
/*  91 */       this.originalDM = this.device.getDisplayMode();
/*  92 */       for (DisplayMode dm : this.device.getDisplayModes()) {
/*  93 */         if (dm.getWidth() >= this.width && dm
/*  94 */           .getHeight() >= this.height && dm
/*  95 */           .getBitDepth() >= 24) {
/*  96 */           this.preferredDM = dm;
/*  97 */           this.width = dm.getWidth();
/*  98 */           this.height = dm.getHeight();
/*     */           break;
/*     */         } 
/*     */       } 
/* 102 */       setSize(this.width, this.height);
/*     */     } else {
/* 104 */       this.preferredDM = this.originalDM = null;
/* 105 */       Insets insets = getInsets();
/* 106 */       setSize(this.width + insets.left + insets.right, this.height + insets.top + insets.bottom);
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
/*     */   private void initComponents() {
/* 120 */     setTitle("Klungbot Visualization");
/*     */     
/* 122 */     GroupLayout layout = new GroupLayout(getContentPane());
/* 123 */     getContentPane().setLayout(layout);
/* 124 */     layout.setHorizontalGroup(layout
/* 125 */         .createParallelGroup(GroupLayout.Alignment.LEADING)
/* 126 */         .addGap(0, 400, 32767));
/*     */     
/* 128 */     layout.setVerticalGroup(layout
/* 129 */         .createParallelGroup(GroupLayout.Alignment.LEADING)
/* 130 */         .addGap(0, 300, 32767));
/*     */ 
/*     */     
/* 133 */     pack();
/*     */   }
/*     */ 
/*     */   
/*     */   public void start(ArrayList<Lakon> lakons) {
/* 138 */     Adegan.setResolution(6);
/* 139 */     this.lakon = this.lakons.get(0);
/* 140 */     this.prev = null;
/* 141 */     this.current = (Adegan)this.lakon.getFirstChild();
/* 142 */     this.current.start(-1L, getWidth(), getHeight());
/* 143 */     if (this.preferredDM != null) {
/* 144 */       this.device.setFullScreenWindow(this);
/* 145 */       this.device.setDisplayMode(this.preferredDM);
/* 146 */       validate();
/* 147 */       createBufferStrategy(3);
/* 148 */       this.buffer = getBufferStrategy();
/*     */     } else {
/*     */       
/* 151 */       setVisible(true);
/* 152 */       this.buffer = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void finish() {
/* 157 */     setVisible(false);
/* 158 */     if (this.originalDM != null) {
/* 159 */       this.device.setDisplayMode(this.originalDM);
/* 160 */       this.device.setFullScreenWindow(null);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void changeTick(long tick, long note) {
/* 165 */     if (tick % Adegan.getResolution() == 0L) {
/* 166 */       this.tick = tick;
/* 167 */       this.noteOn = note;
/* 168 */       render();
/*     */     } 
/*     */   }
/*     */   
/*     */   void render() {
/* 173 */     if (this.buffer == null) {
/* 174 */       repaint();
/*     */     } else {
/*     */       
/* 177 */       Graphics g = this.buffer.getDrawGraphics();
/*     */       try {
/* 179 */         paint(g);
/*     */       } finally {
/* 181 */         g.dispose();
/*     */       } 
/* 183 */       this.buffer.show();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void paint(Graphics g) {
/* 189 */     Graphics2D g2 = (Graphics2D)g;
/* 190 */     if (this.noteOn == -1L) {
/* 191 */       this.current.display(g2);
/*     */       return;
/*     */     } 
/* 194 */     this.noteOn = -1L;
/* 195 */     if (this.current.isExitTime(this.tick)) {
/* 196 */       Adegan next = (Adegan)this.current.getNextSibling();
/* 197 */       if (next != null) {
/* 198 */         if (this.prev != null) this.prev.finish(); 
/* 199 */         this.prev = this.current;
/* 200 */         this.current = next;
/* 201 */         this.current.start(this.tick, getWidth(), getHeight());
/* 202 */         this.current.enter(g2, this.tick, this.noteOn, this.prev);
/*     */         return;
/*     */       } 
/*     */     } 
/* 206 */     if (this.current.isActionTime(this.tick)) {
/* 207 */       this.current.action(g2, this.tick, this.noteOn);
/*     */       return;
/*     */     } 
/* 210 */     if (this.prev != null) {
/* 211 */       this.current.enter(g2, this.tick, this.noteOn, this.prev);
/*     */       return;
/*     */     } 
/* 214 */     this.current.display(g2);
/*     */   }
/*     */ 
/*     */   
/*     */   void initLakons() {
/* 219 */     this.lakons = new ArrayList<>();
/* 220 */     Lakon l = new Lakon("Peresmian APCO-Incubation", 4, 4);
/* 221 */     l.setInfo("Manuk Dadali");
/* 222 */     TransitionAdegan a = new TransitionAdegan(0, 24, (Transition)new CurtainTransition2D(), "slides/aijb/slide1.jpg");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 227 */     l.add((Adegan)a);
/* 228 */     a = new TransitionAdegan(384, 768, (Transition)new CurtainTransition2D(), "slides/aijb/slide2.jpg");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 233 */     l.add((Adegan)a);
/* 234 */     a = new TransitionAdegan(384, 384, (Transition)new SplitTransition2D(), "slides/aijb/slide3.jpg");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 239 */     l.add((Adegan)a);
/* 240 */     this.lakons.add(l);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\AnimationFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */