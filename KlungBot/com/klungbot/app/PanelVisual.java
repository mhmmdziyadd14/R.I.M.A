/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.Converter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PanelVisual
/*     */   extends JPanel
/*     */   implements MainListener
/*     */ {
/*  43 */   long[] logo = new long[] { 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 21557132558336L, 21696760938496L, 20524214943744L, 20524214878208L, 20515624943616L, 20515624878080L, 20446905401344L, 20447173902336L, 20524483313664L, 20524214943744L, 65677206126592L, 65529029754880L };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long[] notes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long waitedNotes;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   int tick = 0;
/*     */   boolean running;
/*     */   Main parent;
/*     */   Font font;
/*     */   Stroke stroke1;
/*     */   Stroke stroke0;
/*     */   Converter converter;
/*     */   Scale scale;
/*  80 */   int channel = 0;
/*  81 */   int meter = 4;
/*     */ 
/*     */ 
/*     */   
/*     */   Color[] colors;
/*     */ 
/*     */   
/*     */   int[] positions;
/*     */ 
/*     */   
/*     */   int[] increments;
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrack(int channel) {
/*  96 */     this.channel = channel;
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
/* 108 */     addComponentListener(new ComponentAdapter() {
/*     */           public void componentShown(ComponentEvent evt) {
/* 110 */             PanelVisual.this.formComponentShown(evt);
/*     */           }
/*     */         });
/*     */     
/* 114 */     GroupLayout layout = new GroupLayout(this);
/* 115 */     setLayout(layout);
/* 116 */     layout.setHorizontalGroup(layout
/* 117 */         .createParallelGroup(GroupLayout.Alignment.LEADING)
/* 118 */         .addGap(0, 669, 32767));
/*     */     
/* 120 */     layout.setVerticalGroup(layout
/* 121 */         .createParallelGroup(GroupLayout.Alignment.LEADING)
/* 122 */         .addGap(0, 300, 32767));
/*     */   }
/*     */ 
/*     */   
/*     */   private void formComponentShown(ComponentEvent evt) {
/* 127 */     if (this.parent != null) {
/* 128 */       this.parent.setMessage("Music visualization. Pick a song from the album, then click the play button", Color.GREEN);
/*     */     }
/*     */   }
/*     */   
/*     */   public void start(Sequence seq) {
/* 133 */     this.tick = 0;
/* 134 */     this.waitedNotes = 0L;
/* 135 */     this.notes = this.converter.convert(seq, this.channel);
/* 136 */     this.scale = seq.scale;
/* 137 */     this.meter = seq.meter;
/* 138 */     arepaint();
/*     */   }
/*     */   
/*     */   public void finished(Sequence seq) {
/* 142 */     this.tick = 0;
/* 143 */     this.notes = this.logo;
/*     */     
/* 145 */     arepaint();
/*     */   }
/*     */   
/*     */   public void waiting(long waited) {
/* 149 */     this.waitedNotes = waited;
/* 150 */     arepaint();
/*     */   }
/*     */   
/*     */   private void arepaint() {
/* 154 */     repaint();
/*     */   }
/*     */   
/*     */   public void changeTick(int tick, long nextOn) {
/* 158 */     if (this.notes == this.logo) {
/* 159 */       this.tick = 0;
/*     */       return;
/*     */     } 
/* 162 */     this.tick = tick;
/* 163 */     this.waitedNotes = 0L;
/* 164 */     if (!this.running) {
/* 165 */       this.running = true;
/* 166 */       arepaint();
/*     */     } 
/*     */   }
/*     */   
/* 170 */   public PanelVisual(Main p) { this.colors = ColorTable.colors;
/*     */     
/* 172 */     this.positions = new int[] { 0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12 };
/* 173 */     this.increments = new int[] { 2, 2, 2, 3, 1, 2, 2, 2, 2, 2, 3, 1 }; this.parent = p; initComponents(); this.font = new Font("verdana", 1, 14); float[] dash0 = { 2.0F, 2.0F, 2.0F, 2.0F }; this.stroke1 = new BasicStroke(1.0F, 2, 1, 10.0F, dash0, 0.0F);
/*     */     this.converter = new Converter();
/*     */     this.notes = this.logo;
/* 176 */     this.scale = (Scale)new Diatonic(); } public void paintComponent(Graphics g) { super.paintComponent(g);
/* 177 */     int current = this.tick;
/* 178 */     Graphics2D g2 = (Graphics2D)g;
/* 179 */     int w = getWidth() - 20;
/* 180 */     int h = getHeight() - 100;
/* 181 */     int nnotes = 37;
/* 182 */     int wnotes = 22;
/*     */     
/* 184 */     int nbeat = 8;
/* 185 */     int hbeat = 12;
/* 186 */     float w1 = w / (wnotes * 2);
/* 187 */     float h1 = h / (nbeat * hbeat);
/* 188 */     int x0 = 10;
/*     */ 
/*     */ 
/*     */     
/* 192 */     if (current >= this.notes.length) {
/*     */       return;
/*     */     }
/* 195 */     this.running = true;
/* 196 */     g2.setFont(this.font);
/* 197 */     FontRenderContext frc = g2.getFontRenderContext();
/*     */ 
/*     */     
/* 200 */     g2.setPaint(Color.WHITE); int n;
/* 201 */     for (n = 0; n < nnotes + 2; ) {
/* 202 */       int j = n % 12;
/* 203 */       float x1 = x0 + (n / 12 * 14) * w1 + this.positions[j] * w1;
/* 204 */       g2.drawLine((int)x1, 20, (int)x1, h);
/* 205 */       n += this.increments[j];
/*     */     } 
/*     */ 
/*     */     
/* 209 */     this.stroke0 = g2.getStroke();
/* 210 */     g2.setStroke(this.stroke1);
/* 211 */     for (int i = 0; i < nbeat * hbeat - 2 && 
/* 212 */       i + current < this.notes.length; i++) {
/*     */       
/* 214 */       if ((i + current) % 12 == 0) {
/* 215 */         g2.setPaint(Color.BLACK);
/* 216 */         int y1 = h - (int)(i * h1);
/* 217 */         g2.drawLine(10, y1, w, y1);
/*     */       } 
/*     */       
/* 220 */       long mask = 4096L;
/* 221 */       for (int j = 0; j < nnotes; j++) {
/* 222 */         int jn = j % 12;
/* 223 */         int jo = j / 12;
/* 224 */         float x1 = x0 + (jo * 14) * w1 + this.positions[jn] * w1;
/* 225 */         if ((this.notes[i + current] & mask) != 0L) {
/* 226 */           g2.setPaint(ColorTable.paints[jn]);
/* 227 */           g2.fillRect((int)x1 + 2, h - (int)(i * h1), (int)(w1 * 2.0F) - 4, (int)h1 + 1);
/*     */         } 
/* 229 */         mask <<= 1L;
/*     */       } 
/*     */     } 
/* 232 */     g2.setStroke(this.stroke0);
/*     */ 
/*     */     
/* 235 */     int note = 12;
/* 236 */     while (note < 12 + nnotes) {
/* 237 */       int j = note / 12 - 1;
/* 238 */       int k = note % 12;
/* 239 */       long mask = 1L << note;
/* 240 */       if ((this.notes[current] & mask) != 0L) {
/* 241 */         g2.setPaint(Color.GRAY);
/*     */       
/*     */       }
/* 244 */       else if ((this.waitedNotes & mask) != 0L) {
/* 245 */         g2.setPaint(Color.PINK);
/*     */       }
/* 247 */       else if (this.notes == this.logo) {
/* 248 */         g2.setPaint(ColorTable.paints[k]);
/*     */       } else {
/*     */         
/* 251 */         g2.setPaint(Color.WHITE);
/*     */       } 
/* 253 */       float x1 = x0 + (j * 14) * w1 + this.positions[k] * w1;
/* 254 */       int x2 = (int)x1 + 2;
/* 255 */       int h2 = (int)h1 + h;
/* 256 */       int l2 = (int)(w1 * 2.0F) - 4;
/* 257 */       g2.fillRect(x2, h2, l2, 90);
/* 258 */       g2.setPaint(Color.BLACK);
/* 259 */       g2.drawRect(x2, h2, l2, 90);
/*     */       
/* 261 */       TextLayout tl = new TextLayout(this.scale.symbolOfIndex(k + 25 - 12).toUpperCase(), this.font, frc);
/*     */       
/* 263 */       Rectangle2D b = tl.getBounds();
/* 264 */       tl.draw(g2, (float)((x1 + w1) - b.getCenterX()), (h2 + 80));
/*     */ 
/*     */       
/* 267 */       note += this.increments[k];
/*     */     } 
/*     */     
/* 270 */     note = 13;
/* 271 */     g2.setPaint(Color.BLACK);
/* 272 */     while (note < 12 + nnotes) {
/* 273 */       int j = note / 12 - 1;
/* 274 */       int k = note % 12;
/* 275 */       float x1 = x0 + (j * 14) * w1 + this.positions[note % 12] * w1;
/* 276 */       long mask = 1L << note;
/* 277 */       if ((this.notes[current] & mask) != 0L) {
/* 278 */         g2.setPaint(Color.GRAY);
/*     */       
/*     */       }
/* 281 */       else if ((this.waitedNotes & mask) != 0L) {
/* 282 */         g2.setPaint(Color.PINK);
/*     */       } 
/* 284 */       int x2 = (int)x1 + 2;
/* 285 */       int h2 = (int)h1 + h;
/* 286 */       int l2 = (int)(w1 * 2.0F) - 4;
/* 287 */       g2.fillRect(x2, h2, l2, 40);
/* 288 */       g2.setPaint(Color.BLACK);
/* 289 */       g2.drawRect(x2, h2, l2, 40);
/* 290 */       note += this.increments[k];
/*     */     } 
/* 292 */     this.running = false; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void changeSequence(Sequence seq) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReady() {
/* 304 */     return isVisible();
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelVisual.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */