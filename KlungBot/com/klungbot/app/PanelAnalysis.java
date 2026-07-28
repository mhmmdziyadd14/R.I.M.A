/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.DoremiAnalyzer;
/*     */ import com.klungbot.Sequence;
/*     */ import com.klungbot.doremi.Diatonic;
/*     */ import com.klungbot.doremi.Scale;
/*     */ import com.klungbot.util.ColorTable;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.font.TextLayout;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import javax.swing.GroupLayout;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
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
/*     */ public class PanelAnalysis
/*     */   extends JPanel
/*     */ {
/*     */   Main parent;
/*     */   Font font;
/*     */   Stroke stroke1;
/*     */   DoremiAnalyzer analyzer;
/*     */   long maxLength;
/*     */   Scale scale;
/*     */   Color[] colors;
/*     */   int[] positions;
/*     */   int[] fulls;
/*     */   int[] whites;
/*     */   int[] blacks;
/*     */   int[] intervals;
/*     */   long angklung16;
/*     */   long angklung18;
/*     */   
/*     */   public PanelAnalysis(Main p) {
/* 118 */     this.colors = ColorTable.colors;
/* 119 */     this.positions = new int[] { 0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12 };
/* 120 */     this.fulls = new int[] { 0, 2, 4, 5, 7, 9, 11 };
/* 121 */     this.whites = new int[] { 2, 2, 1, 2, 2, 2, 1 };
/* 122 */     this.blacks = new int[] { 2, 3, 2, 2, 3 };
/* 123 */     this.intervals = new int[] { 2, 2, 2, 3, 1, 2, 2, 2, 2, 2, 3, 1 };
/* 124 */     this.angklung16 = 1507364175872L;
/* 125 */     this.angklung18 = 3706387562496L; this.parent = p; initComponents(); this.font = new Font("verdana", 1, 14); float[] dash0 = { 2.0F, 2.0F, 2.0F, 2.0F }; this.stroke1 = new BasicStroke(1.0F, 2, 1, 10.0F, dash0, 0.0F);
/*     */     this.analyzer = new DoremiAnalyzer();
/*     */     this.maxLength = 0L;
/* 128 */     this.scale = Diatonic.createScale(); } public void paintComponent(Graphics g) { super.paintComponent(g);
/* 129 */     Graphics2D g2 = (Graphics2D)g;
/* 130 */     int w = getWidth() - 16;
/* 131 */     int h = getHeight() - 100;
/* 132 */     int w1 = w / 44;
/* 133 */     int h1 = h / 48;
/* 134 */     int x0 = (w - w1 * 43) / 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     g2.setFont(this.font);
/* 141 */     FontRenderContext frc = g2.getFontRenderContext();
/*     */     
/* 143 */     int note = 12; int j;
/* 144 */     for (j = 0; j < 3; j++) {
/* 145 */       for (int i = 0; i < 7; i++) {
/* 146 */         int k = x0 + j * 14 * w1 + this.positions[note % 12] * w1;
/* 147 */         long mask = 1L << note;
/* 148 */         if ((mask & this.angklung16) == 0L) {
/* 149 */           g2.setPaint(Color.WHITE);
/*     */         } else {
/*     */           
/* 152 */           g2.setPaint(Color.LIGHT_GRAY);
/*     */         } 
/* 154 */         g2.fillRect(k + 2, h + h1, w1 * 2 - 4, 90);
/* 155 */         g2.setPaint(Color.BLACK);
/* 156 */         g2.drawRect(k + 2, h + h1, w1 * 2 - 4, 90);
/* 157 */         note += this.whites[i];
/*     */         
/* 159 */         TextLayout textLayout = new TextLayout(this.scale.symbolOfIndex(1 + this.fulls[i]).toUpperCase(), this.font, frc);
/*     */         
/* 161 */         Rectangle2D rectangle2D = textLayout.getBounds();
/* 162 */         textLayout.draw(g2, (float)((k + w1) - rectangle2D.getCenterX()), (h + h1 + 80));
/*     */       } 
/*     */     } 
/*     */     
/* 166 */     int x1 = x0 + j * 14 * w1 + this.positions[note % 12] * w1;
/* 167 */     g2.setPaint(Color.WHITE);
/* 168 */     g2.fillRect(x1 + 2, h + h1, w1 * 2 - 4, 90);
/* 169 */     g2.setPaint(Color.BLACK);
/* 170 */     g2.drawRect(x1 + 2, h + h1, w1 * 2 - 4, 90);
/*     */     
/* 172 */     TextLayout tl = new TextLayout(this.scale.symbolOfIndex(1).toUpperCase(), this.font, frc);
/* 173 */     Rectangle2D b = tl.getBounds();
/* 174 */     tl.draw(g2, (float)((x1 + w1) - b.getCenterX()), (h + h1 + 80));
/*     */ 
/*     */     
/* 177 */     note = 13;
/* 178 */     for (j = 0; j < 3; j++) {
/* 179 */       for (int i = 0; i < 5; i++) {
/* 180 */         x1 = x0 + j * 14 * w1 + this.positions[note % 12] * w1;
/* 181 */         long mask = 1L << note;
/* 182 */         if ((mask & this.angklung16) == 0L) {
/* 183 */           g2.setPaint(Color.WHITE);
/*     */         } else {
/*     */           
/* 186 */           g2.setPaint(Color.GRAY);
/*     */         } 
/* 188 */         g2.fillRect(x1 + 2, h + h1, w1 * 2 - 4, 40);
/* 189 */         g2.setPaint(Color.BLACK);
/* 190 */         g2.drawRect(x1 + 2, h + h1, w1 * 2 - 4, 40);
/* 191 */         note += this.blacks[i];
/*     */       } 
/*     */     } 
/* 194 */     if (this.maxLength == 0L)
/* 195 */       return;  long[] noteLength = this.analyzer.getNoteLength();
/* 196 */     float dh2 = (h - h1) / (float)this.maxLength;
/* 197 */     for (note = 0; note < noteLength.length; ) {
/* 198 */       int i = note % 12;
/* 199 */       j = note / 12;
/* 200 */       x1 = x0 + j * 14 * w1 + this.positions[i] * w1;
/* 201 */       int h2 = Math.round(dh2 * (float)noteLength[note]);
/* 202 */       g2.setPaint(this.colors[i]);
/* 203 */       g2.fillRect(x1 + 2, h + h1 - h2, w1 * 2 - 4, h2);
/* 204 */       note += this.intervals[i];
/*     */     } 
/*     */     
/* 207 */     for (note = 1; note < noteLength.length; ) {
/* 208 */       int i = note % 12;
/* 209 */       j = note / 12;
/* 210 */       x1 = x0 + j * 14 * w1 + this.positions[i] * w1;
/* 211 */       int h2 = Math.round(dh2 * (float)noteLength[note]);
/* 212 */       g2.setPaint(this.colors[i]);
/* 213 */       g2.fillRect(x1 + 2, h + h1 - h2, w1 * 2 - 4, h2);
/* 214 */       note += this.intervals[i];
/*     */     }  }
/*     */ 
/*     */   
/*     */   private void initComponents() {
/*     */     addComponentListener(new ComponentAdapter() {
/*     */           public void componentShown(ComponentEvent evt) {
/*     */             PanelAnalysis.this.formComponentShown(evt);
/*     */           }
/*     */         });
/*     */     GroupLayout layout = new GroupLayout(this);
/*     */     setLayout(layout);
/*     */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 669, 32767));
/*     */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 300, 32767));
/*     */   }
/*     */   
/*     */   private void formComponentShown(ComponentEvent evt) {
/*     */     if (this.parent != null)
/*     */       this.parent.setMessage("Analyze a song. Pick a song from the album/draft", Color.GREEN); 
/*     */   }
/*     */   
/*     */   public void analyze(Sequence seq, int channel) {
/*     */     this.analyzer.analyze(seq, channel);
/*     */     long[] noteLength = this.analyzer.getNoteLength();
/*     */     this.maxLength = 0L;
/*     */     for (int i = 0; i < noteLength.length; i++) {
/*     */       if (noteLength[i] > this.maxLength)
/*     */         this.maxLength = noteLength[i]; 
/*     */     } 
/*     */     aRepaint();
/*     */   }
/*     */   
/*     */   private void aRepaint() {
/*     */     SwingUtilities.invokeLater(new Runnable() {
/*     */           public void run() {
/*     */             PanelAnalysis.this.repaint();
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelAnalysis.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */