/*     */ package com.bric.image.transition.app;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.InputMap;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSlider;
/*     */ import javax.swing.KeyStroke;
/*     */ import javax.swing.Timer;
/*     */ import javax.swing.border.LineBorder;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
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
/*     */ public class AnimationController
/*     */   extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  54 */   static Image playImage = Toolkit.getDefaultToolkit().createImage(AnimationController.class.getResource("resources/playImage.png"));
/*  55 */   static Image pauseImage = Toolkit.getDefaultToolkit().createImage(AnimationController.class.getResource("resources/pauseImage.png"));
/*  56 */   static ImageIcon playIcon = new ImageIcon(playImage);
/*  57 */   static ImageIcon pauseIcon = new ImageIcon(pauseImage);
/*  58 */   JButton playButton = new JButton(playIcon);
/*  59 */   private static int SLIDER_MAXIMUM = 1000;
/*  60 */   JSlider slider = new JSlider(0, SLIDER_MAXIMUM);
/*  61 */   float time = 0.0F;
/*     */   boolean loops = false;
/*  63 */   float duration = 5.0F;
/*     */   
/*  65 */   public static String LOOP_PROPERTY = "loop key";
/*  66 */   public static String TIME_PROPERTY = "time key";
/*  67 */   public static String DURATION_PROPERTY = "property key";
/*  68 */   public static String PLAYING_PROPERTY = "playing key";
/*     */   
/*  70 */   ActionListener buttonListener = new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  72 */         if (AnimationController.this.playButton.getIcon() == AnimationController.playIcon) {
/*  73 */           AnimationController.this.play();
/*     */         } else {
/*  75 */           AnimationController.this.pause();
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*  80 */   static Timer timer = new Timer(40, null);
/*     */   static {
/*  82 */     timer.start();
/*     */   }
/*  84 */   int adjustingSlider = 0;
/*  85 */   ChangeListener sliderListener = new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/*  87 */         if (AnimationController.this.adjustingSlider > 0)
/*  88 */           return;  float f = AnimationController.this.slider.getValue();
/*  89 */         f /= AnimationController.SLIDER_MAXIMUM;
/*  90 */         f *= AnimationController.this.duration;
/*  91 */         AnimationController.this.setTime(f);
/*     */       }
/*     */     };
/*     */   long lastStartTime; ActionListener actionListener; boolean playing;
/*     */   public AnimationController() {
/*  96 */     this(new JButton[0]);
/*     */   }
/*     */   
/*  99 */   public AnimationController(JButton[] buttons) { super(new GridBagLayout());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 146 */     this.lastStartTime = 0L;
/* 147 */     this.actionListener = new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 149 */           if (!AnimationController.this.playing)
/*     */             return; 
/* 151 */           float duration = AnimationController.this.getDuration();
/* 152 */           if (duration == 0.0F) throw new RuntimeException("Can't play an animation with a duration of 0 s."); 
/* 153 */           long period = (long)(1000.0F * duration);
/* 154 */           long t = System.currentTimeMillis() - AnimationController.this.lastStartTime;
/* 155 */           if (AnimationController.this.loops) {
/* 156 */             long k = t % period;
/* 157 */             float f = (float)k;
/* 158 */             f /= 1000.0F;
/* 159 */             AnimationController.this.setTime(f);
/*     */           }
/* 161 */           else if (t < period) {
/* 162 */             float f = (float)t;
/* 163 */             f /= 1000.0F;
/* 164 */             AnimationController.this.setTime(f);
/*     */           } else {
/* 166 */             AnimationController.this.setTime(duration);
/* 167 */             AnimationController.this.pause();
/*     */           } 
/*     */         }
/*     */       };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 178 */     this.playing = false; GridBagConstraints c = new GridBagConstraints(); c.gridx = 0; c.gridy = 0; c.weightx = 0.0D; c.weighty = 1.0D; c.fill = 3; add(this.playButton, c); for (int a = 0; a < buttons.length; a++) { c.gridx++; add(buttons[a], c); buttons[a].setOpaque(false); buttons[a].setRolloverIcon(new DarkenedIcon(buttons[a], 0.5F)); buttons[a].setPressedIcon(new DarkenedIcon(buttons[a], 0.75F)); buttons[a].setBorder(new PartialLineBorder(Color.gray, new Insets(1, 0, 1, 1))); }
/*     */      c.weightx = 1.0D; c.gridx++; c.fill = 1; add(this.slider, c); this.playButton.setOpaque(false); this.playButton.setBorder(new LineBorder(Color.gray)); this.playButton.setRolloverIcon(new DarkenedIcon(this.playButton, 0.5F)); this.playButton.setPressedIcon(new DarkenedIcon(this.playButton, 0.75F)); this.slider.setOpaque(false); this.slider.setBorder(new PartialLineBorder(Color.gray, new Insets(1, 0, 1, 1))); Dimension d = this.slider.getPreferredSize(); d.width = 60; d.height = 25; this.slider.setPreferredSize((Dimension)d.clone()); d.width = d.height; this.playButton.setPreferredSize(d); for (int i = 0; i < buttons.length; i++)
/*     */       buttons[i].setPreferredSize(d);  this.playButton.addActionListener(this.buttonListener); this.slider.setValue(0); setTime(0.0F); this.slider.addChangeListener(this.sliderListener); InputMap inputMap = this.slider.getInputMap(0); inputMap.put(KeyStroke.getKeyStroke(' '), "togglePlay"); this.slider.getActionMap().put("togglePlay", new AbstractAction() { private static final long serialVersionUID = 1L; public void actionPerformed(ActionEvent e) { AnimationController.this.playButton.doClick(); } }
/* 181 */       ); } public void play() { if (this.playing)
/* 182 */       return;  if (Math.abs(this.time - this.duration) < 0.001D) {
/* 183 */       setTime(0.0F);
/*     */     }
/*     */     
/* 186 */     this.lastStartTime = System.currentTimeMillis() - (long)(this.time * 1000.0F);
/* 187 */     this.playing = true;
/* 188 */     timer.addActionListener(this.actionListener);
/* 189 */     this.playButton.setIcon(pauseIcon);
/* 190 */     firePropertyChange(PLAYING_PROPERTY, false, true); }
/*     */    public float getDuration() {
/*     */     return this.duration;
/*     */   } public boolean isPlaying() {
/* 194 */     return this.playing;
/*     */   }
/*     */   
/*     */   public void pause() {
/* 198 */     if (!this.playing)
/*     */       return; 
/* 200 */     this.playing = false;
/* 201 */     timer.removeActionListener(this.actionListener);
/* 202 */     this.playButton.setIcon(playIcon);
/* 203 */     firePropertyChange(PLAYING_PROPERTY, true, false);
/*     */   }
/*     */   
/*     */   public boolean isLooping() {
/* 207 */     return this.loops;
/*     */   }
/*     */   
/*     */   public void setLooping(boolean b) {
/* 211 */     if (this.loops == b)
/* 212 */       return;  this.loops = b;
/* 213 */     firePropertyChange(LOOP_PROPERTY, !this.loops, this.loops);
/*     */   }
/*     */   
/*     */   public void setDuration(float f) {
/* 217 */     if (this.duration == f)
/*     */       return; 
/* 219 */     float oldDuration = this.duration;
/* 220 */     this.duration = f;
/* 221 */     firePropertyChange(DURATION_PROPERTY, oldDuration, this.duration);
/*     */     
/* 223 */     float percent = this.time / this.duration;
/* 224 */     int v = (int)(percent * SLIDER_MAXIMUM);
/* 225 */     if (v > SLIDER_MAXIMUM) v = SLIDER_MAXIMUM; 
/* 226 */     this.adjustingSlider++;
/* 227 */     this.slider.setValue(v);
/* 228 */     this.adjustingSlider--;
/*     */   }
/*     */   
/*     */   public float getTime() {
/* 232 */     return this.time;
/*     */   }
/*     */   
/*     */   public void setTime(float f) {
/* 236 */     if (this.time == f)
/*     */       return; 
/* 238 */     if (this.playing) {
/* 239 */       this.lastStartTime = System.currentTimeMillis() - (long)(f * 1000.0F);
/*     */     }
/*     */     
/* 242 */     float oldTime = this.time;
/* 243 */     this.time = f;
/* 244 */     float percent = f / this.duration;
/* 245 */     if (percent > 1.0F) {
/* 246 */       this.adjustingSlider++;
/* 247 */       this.slider.setValue(SLIDER_MAXIMUM);
/* 248 */       this.adjustingSlider--;
/*     */     } else {
/* 250 */       int v = (int)(percent * SLIDER_MAXIMUM);
/* 251 */       this.adjustingSlider++;
/* 252 */       this.slider.setValue(v);
/* 253 */       this.adjustingSlider--;
/*     */     } 
/* 255 */     firePropertyChange(TIME_PROPERTY, oldTime, this.time);
/*     */   }
/*     */ 
/*     */   
/*     */   public void paintComponent(Graphics g) {
/* 260 */     super.paintComponent(g);
/* 261 */     Graphics2D g2 = (Graphics2D)g;
/* 262 */     g2.setPaint(new GradientPaint(0.0F, 0.0F, Color.lightGray, 0.0F, getHeight(), Color.white));
/* 263 */     g2.fillRect(0, 0, getWidth(), getHeight());
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\app\AnimationController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */