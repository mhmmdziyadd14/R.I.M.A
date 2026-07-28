/*     */ package com.bric.image.transition.app;
/*     */ 
/*     */ import com.bric.animation.BufferedAnimationPanel;
/*     */ import com.bric.image.transition.Transition2D;
/*     */ import com.bric.image.transition.spunk.CollapseTransition2D;
/*     */ import com.bric.image.transition.spunk.DotsTransition2D;
/*     */ import com.bric.image.transition.spunk.FlurryTransition2D;
/*     */ import com.bric.image.transition.spunk.FunkyWipeTransition2D;
/*     */ import com.bric.image.transition.spunk.GooTransition2D;
/*     */ import com.bric.image.transition.spunk.HalftoneTransition2D;
/*     */ import com.bric.image.transition.spunk.LevitateTransition2D;
/*     */ import com.bric.image.transition.spunk.MeshShuffleTransition2D;
/*     */ import com.bric.image.transition.spunk.ScribbleTransition2D;
/*     */ import com.bric.image.transition.spunk.SpiralTransition2D;
/*     */ import com.bric.image.transition.spunk.SquareRainTransition2D;
/*     */ import com.bric.image.transition.spunk.SquaresTransition2D;
/*     */ import com.bric.image.transition.spunk.StarsTransition2D;
/*     */ import com.bric.image.transition.spunk.SwivelTransition2D;
/*     */ import com.bric.image.transition.spunk.TossTransition2D;
/*     */ import com.bric.image.transition.spunk.WaveTransition2D;
/*     */ import com.bric.image.transition.spunk.WeaveTransition2D;
/*     */ import com.bric.image.transition.spunk.ZoomTransition2D;
/*     */ import com.bric.image.transition.vanilla.BarsTransition2D;
/*     */ import com.bric.image.transition.vanilla.BatTransition2D;
/*     */ import com.bric.image.transition.vanilla.BlendTransition2D;
/*     */ import com.bric.image.transition.vanilla.BlindsTransition2D;
/*     */ import com.bric.image.transition.vanilla.BoxTransition2D;
/*     */ import com.bric.image.transition.vanilla.CheckerboardTransition2D;
/*     */ import com.bric.image.transition.vanilla.CircleTransition2D;
/*     */ import com.bric.image.transition.vanilla.CurtainTransition2D;
/*     */ import com.bric.image.transition.vanilla.DiamondsTransition2D;
/*     */ import com.bric.image.transition.vanilla.DocumentaryTransition2D;
/*     */ import com.bric.image.transition.vanilla.DropTransition2D;
/*     */ import com.bric.image.transition.vanilla.MotionBlendTransition2D;
/*     */ import com.bric.image.transition.vanilla.PivotTransition2D;
/*     */ import com.bric.image.transition.vanilla.PushTransition2D;
/*     */ import com.bric.image.transition.vanilla.RadialWipeTransition2D;
/*     */ import com.bric.image.transition.vanilla.RevealTransition2D;
/*     */ import com.bric.image.transition.vanilla.RotateTransition2D;
/*     */ import com.bric.image.transition.vanilla.ScaleTransition2D;
/*     */ import com.bric.image.transition.vanilla.SlideTransition2D;
/*     */ import com.bric.image.transition.vanilla.SplitTransition2D;
/*     */ import com.bric.image.transition.vanilla.StarTransition2D;
/*     */ import com.bric.image.transition.vanilla.WipeTransition2D;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GradientPaint;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Shape;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.font.TextLayout;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import javax.swing.JApplet;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.UIManager;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Transition2DDemo
/*     */   extends JApplet
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   BufferedImage img1;
/*     */   BufferedImage img2;
/*     */   
/*     */   public static BufferedImage createBlurbGraphic(Dimension preferredSize) {
/* 110 */     BufferedImage frameA = SimplestTransitionDemo.createImage(400, "A", true, true);
/* 111 */     BufferedImage frameB = SimplestTransitionDemo.createImage(400, "B", false, true);
/* 112 */     BufferedImage finalImage = new BufferedImage(500, 500, 2);
/* 113 */     SwivelTransition2D swivelTransition2D = new SwivelTransition2D(Color.white, 5);
/* 114 */     float fraction = 0.25F;
/* 115 */     Graphics2D g = finalImage.createGraphics();
/* 116 */     g.setRenderingHints(getQualityHints());
/* 117 */     swivelTransition2D.paint(g, frameA, frameB, fraction);
/* 118 */     g.setPaint(new GradientPaint(0.0F, 400.0F, new Color(255, 255, 255, 0), 0.0F, 500.0F, Color.white));
/* 119 */     g.fillRect(0, 400, finalImage.getWidth(), 100);
/* 120 */     g.dispose();
/* 121 */     return finalImage;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 127 */     Transition2DDemo w = new Transition2DDemo(SimplestTransitionDemo.createImage("A", true), SimplestTransitionDemo.createImage("B", false));
/* 128 */     JFrame frame = new JFrame("Transition2DDemo");
/*     */     
/* 130 */     frame.setDefaultCloseOperation(3);
/* 131 */     frame.getContentPane().add(w);
/* 132 */     frame.pack();
/* 133 */     frame.setVisible(true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 138 */   JComboBox options = new JComboBox();
/* 139 */   AnimationController controller = new AnimationController();
/* 140 */   JSpinner duration = new JSpinner(new SpinnerNumberModel(2.0D, 0.1D, 100.0D, 0.1D));
/*     */   
/* 142 */   Transition2D[] transitions = new Transition2D[] { (Transition2D)new BarsTransition2D(9, true), (Transition2D)new BarsTransition2D(9, false), (Transition2D)new BarsTransition2D(10, true), (Transition2D)new BarsTransition2D(10, false), (Transition2D)new BatTransition2D(7), (Transition2D)new BatTransition2D(8), (Transition2D)new BlendTransition2D(), (Transition2D)new BlindsTransition2D(2), (Transition2D)new BlindsTransition2D(1), (Transition2D)new BlindsTransition2D(3), (Transition2D)new BlindsTransition2D(4), (Transition2D)new BoxTransition2D(7), (Transition2D)new BoxTransition2D(8), (Transition2D)new CheckerboardTransition2D(2), (Transition2D)new CheckerboardTransition2D(1), (Transition2D)new CheckerboardTransition2D(3), (Transition2D)new CheckerboardTransition2D(4), (Transition2D)new CircleTransition2D(7), (Transition2D)new CircleTransition2D(8), (Transition2D)new CollapseTransition2D(), (Transition2D)new CurtainTransition2D(), (Transition2D)new DiamondsTransition2D(55), (Transition2D)new DiamondsTransition2D(90), (Transition2D)new DiamondsTransition2D(120), (Transition2D)new DocumentaryTransition2D(2), (Transition2D)new DocumentaryTransition2D(1), (Transition2D)new DocumentaryTransition2D(3), (Transition2D)new DocumentaryTransition2D(4), (Transition2D)new DotsTransition2D(), (Transition2D)new DropTransition2D(), (Transition2D)new FlurryTransition2D(7), (Transition2D)new FlurryTransition2D(8), (Transition2D)new FunkyWipeTransition2D(true), (Transition2D)new FunkyWipeTransition2D(false), (Transition2D)new GooTransition2D(), (Transition2D)new HalftoneTransition2D(7), (Transition2D)new HalftoneTransition2D(8), (Transition2D)new LevitateTransition2D(), (Transition2D)new MeshShuffleTransition2D(), (Transition2D)new MotionBlendTransition2D(), (Transition2D)new PivotTransition2D(14, true), (Transition2D)new PivotTransition2D(15, true), (Transition2D)new PivotTransition2D(16, true), (Transition2D)new PivotTransition2D(17, true), (Transition2D)new PivotTransition2D(14, false), (Transition2D)new PivotTransition2D(15, false), (Transition2D)new PivotTransition2D(16, false), (Transition2D)new PivotTransition2D(17, false), (Transition2D)new PushTransition2D(2), (Transition2D)new PushTransition2D(1), (Transition2D)new PushTransition2D(3), (Transition2D)new PushTransition2D(4), (Transition2D)new RadialWipeTransition2D(6), (Transition2D)new RadialWipeTransition2D(5), (Transition2D)new RevealTransition2D(2), (Transition2D)new RevealTransition2D(1), (Transition2D)new RevealTransition2D(3), (Transition2D)new RevealTransition2D(4), (Transition2D)new RotateTransition2D(7), (Transition2D)new RotateTransition2D(8), (Transition2D)new ScaleTransition2D(7), (Transition2D)new ScaleTransition2D(8), (Transition2D)new ScribbleTransition2D(false), (Transition2D)new ScribbleTransition2D(true), (Transition2D)new SlideTransition2D(2), (Transition2D)new SlideTransition2D(1), (Transition2D)new SlideTransition2D(3), (Transition2D)new SlideTransition2D(4), (Transition2D)new SpiralTransition2D(false), (Transition2D)new SpiralTransition2D(true), (Transition2D)new SplitTransition2D(9, false), (Transition2D)new SplitTransition2D(10, false), (Transition2D)new SplitTransition2D(9, true), (Transition2D)new SplitTransition2D(10, true), (Transition2D)new SquareRainTransition2D(), (Transition2D)new SquaresTransition2D(), (Transition2D)new StarTransition2D(7), (Transition2D)new StarTransition2D(8), (Transition2D)new StarsTransition2D(2), (Transition2D)new StarsTransition2D(1), (Transition2D)new SwivelTransition2D(6), (Transition2D)new SwivelTransition2D(5), (Transition2D)new TossTransition2D(2), (Transition2D)new TossTransition2D(1), (Transition2D)new WaveTransition2D(3), (Transition2D)new WaveTransition2D(1), (Transition2D)new WeaveTransition2D(), (Transition2D)new WipeTransition2D(2), (Transition2D)new WipeTransition2D(1), (Transition2D)new WipeTransition2D(3), (Transition2D)new WipeTransition2D(4), (Transition2D)new ZoomTransition2D(2), (Transition2D)new ZoomTransition2D(1) };
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Transition2DDemo() {
/* 243 */     this(SimplestTransitionDemo.createImage("A", true), 
/* 244 */         SimplestTransitionDemo.createImage("B", false));
/*     */   }
/*     */   
/*     */   public Transition2DDemo(BufferedImage bi1, BufferedImage bi2) {
/*     */     try {
/* 249 */       String lf = UIManager.getSystemLookAndFeelClassName();
/* 250 */       UIManager.setLookAndFeel(lf);
/* 251 */     } catch (Throwable e) {
/* 252 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 255 */     this.img1 = bi1;
/* 256 */     this.img2 = bi2;
/*     */     
/* 258 */     getContentPane().setLayout(new GridBagLayout());
/* 259 */     GridBagConstraints c = new GridBagConstraints();
/* 260 */     c.gridx = 0; c.gridy = 0; c.weightx = 1.0D; c.weighty = 0.0D;
/* 261 */     c.fill = 0; c.anchor = 16;
/* 262 */     JPanel optionsPanel = new JPanel(new GridBagLayout());
/* 263 */     getContentPane().add(optionsPanel, c);
/* 264 */     final TransitionPanel panel = new TransitionPanel(this.options.getItemAt(0));
/* 265 */     c.weighty = 1.0D; c.gridy++; c.fill = 0;
/* 266 */     getContentPane().add((Component)panel, c);
/* 267 */     c.weightx = 0.0D;
/* 268 */     c.gridy++; c.anchor = 18;
/* 269 */     getContentPane().add(this.controller, c);
/*     */     
/* 271 */     Dimension d = this.controller.getPreferredSize();
/* 272 */     d.width = (panel.getPreferredSize()).width;
/* 273 */     this.controller.setPreferredSize(d);
/*     */     
/* 275 */     c.gridy++;
/*     */     
/* 277 */     c.gridx = 0; c.gridy = 0; c.weightx = 0.0D; c.weighty = 0.0D;
/* 278 */     c.anchor = 13; c.insets = new Insets(3, 3, 3, 3);
/* 279 */     optionsPanel.add(new JLabel("Transition:"), c);
/* 280 */     c.gridy++;
/* 281 */     optionsPanel.add(new JLabel("Duration (s):"), c);
/* 282 */     c.gridx++; c.gridy = 0; c.anchor = 17;
/* 283 */     optionsPanel.add(this.options, c);
/* 284 */     c.gridy++;
/* 285 */     optionsPanel.add(this.duration, c);
/*     */ 
/*     */     
/* 288 */     optionsPanel.setOpaque(false);
/* 289 */     this.options.setOpaque(false);
/* 290 */     this.duration.setOpaque(false);
/*     */     
/* 292 */     this.controller.addPropertyChangeListener(AnimationController.TIME_PROPERTY, new PropertyChangeListener()
/*     */         {
/*     */           public void propertyChange(PropertyChangeEvent e) {
/* 295 */             panel.refresh();
/*     */           }
/*     */         });
/*     */     
/* 299 */     this.controller.setLooping(true);
/* 300 */     this.controller.play();
/* 301 */     ChangeListener durationListener = new ChangeListener() {
/*     */         public void stateChanged(ChangeEvent e) {
/* 303 */           float d = ((Number)Transition2DDemo.this.duration.getValue()).floatValue();
/* 304 */           Transition2DDemo.this.controller.setDuration(2.0F * d);
/*     */         }
/*     */       };
/* 307 */     this.duration.addChangeListener(durationListener);
/* 308 */     durationListener.stateChanged(null);
/*     */     
/* 310 */     this.options.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent e) {
/* 312 */             panel.refresh();
/*     */           }
/*     */         });
/*     */     
/* 316 */     this.options.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent e) {
/* 318 */             Transition2D t = (Transition2D)Transition2DDemo.this.options.getSelectedItem();
/* 319 */             if (t == null)
/*     */               return; 
/* 321 */             panel.setTransition(t);
/*     */           }
/*     */         });
/*     */     
/* 325 */     this.options.removeAllItems();
/* 326 */     for (int a = 0; a < this.transitions.length; a++) {
/* 327 */       this.options.addItem(this.transitions[a]);
/*     */       
/* 329 */       if (this.transitions[a].toString().indexOf("Scribble") != -1) {
/* 330 */         this.options.setSelectedIndex(a);
/*     */       }
/*     */     } 
/* 333 */     getContentPane().setBackground(Color.white);
/* 334 */     if (getContentPane() instanceof JComponent)
/* 335 */       ((JComponent)getContentPane()).setOpaque(true); 
/*     */   }
/*     */   
/*     */   public static RenderingHints getQualityHints() {
/* 339 */     RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 340 */     hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
/* 341 */     hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
/*     */     
/* 343 */     hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/* 344 */     hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/* 345 */     hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */     
/* 347 */     return hints;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   class TransitionPanel
/*     */     extends BufferedAnimationPanel
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */     
/*     */     Transition2D transition;
/*     */     
/*     */     Font font;
/*     */ 
/*     */     
/*     */     public TransitionPanel(Transition2D transition) {
/* 364 */       this.font = new Font("Mono", 0, 12);
/*     */       setTransition(transition);
/*     */       setPreferredSize(new Dimension(Transition2DDemo.this.img1.getWidth(), Transition2DDemo.this.img1.getHeight())); } protected void paintAnimation(Graphics2D g, int width, int height) {
/*     */       BufferedImage frameA, frameB;
/* 368 */       g.setColor(Color.black);
/* 369 */       g.fillRect(0, 0, width, height);
/* 370 */       float t = Transition2DDemo.this.controller.getTime() / Transition2DDemo.this.controller.getDuration() * 2.0F;
/*     */       
/* 372 */       if (t >= 2.0F) {
/* 373 */         t = 0.0F;
/* 374 */         frameA = Transition2DDemo.this.img1;
/* 375 */         frameB = Transition2DDemo.this.img2;
/* 376 */       } else if (t >= 1.0F) {
/* 377 */         t %= 1.0F;
/* 378 */         frameA = Transition2DDemo.this.img2;
/* 379 */         frameB = Transition2DDemo.this.img1;
/*     */       } else {
/* 381 */         frameA = Transition2DDemo.this.img1;
/* 382 */         frameB = Transition2DDemo.this.img2;
/*     */       } 
/* 384 */       g.setRenderingHints(Transition2DDemo.getQualityHints());
/* 385 */       this.transition.paint(g, frameA, frameB, t);
/* 386 */       Graphics2D g2 = g;
/* 387 */       TextLayout tl = new TextLayout((t * 100.0F) + "%", this.font, g2.getFontRenderContext());
/* 388 */       Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(5.0D, 18.0D));
/* 389 */       g2.setColor(Color.black);
/* 390 */       g2.setStroke(new BasicStroke(2.0F));
/* 391 */       g2.draw(outline);
/* 392 */       g2.setColor(Color.white);
/* 393 */       g2.fill(outline);
/*     */     }
/*     */     
/*     */     public void setTransition(Transition2D transition) {
/*     */       this.transition = transition;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\app\Transition2DDemo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */