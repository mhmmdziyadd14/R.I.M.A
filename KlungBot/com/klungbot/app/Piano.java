/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.doremi.Scale;
/*     */ import java.awt.Color;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import javax.sound.midi.MidiMessage;
/*     */ import javax.sound.midi.Receiver;
/*     */ import javax.sound.midi.ShortMessage;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JLayeredPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JToggleButton;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Piano
/*     */   implements MouseListener, Receiver
/*     */ {
/*     */   JPanel container;
/*     */   PianoListener listener;
/*     */   JLabel[] labels;
/*     */   JToggleButton[] buttons;
/*     */   JToggleButton btLast;
/*     */   int tootLast;
/*     */   
/*     */   private void initKeyboard() {
/*     */     String system = System.getProperty("os.name");
/*     */     boolean macos = system.toLowerCase().startsWith("mac");
/*     */     JLayeredPane panel = new JLayeredPane();
/*     */     this.container.add(panel);
/*     */     int sx = 45;
/*     */     int sy = 40;
/*     */     int[] white = { 
/*     */         13, 15, 17, 18, 20, 22, 24, 25, 27, 29, 
/*     */         30, 32, 34, 36, 37, 39, 41, 42, 44, 46, 
/*     */         48, 49 };
/*     */     int[][] black = { { 14, 16, 0, 19, 21, 23 }, { 26, 28, 0, 31, 33, 35 }, { 38, 40, 0, 43, 45, 47 } };
/*     */     this.labels = new JLabel[white.length];
/*     */     this.buttons = new JToggleButton[white.length + 15];
/*     */     for (int i = 0; i < white.length; i++) {
/*     */       JToggleButton b = new JToggleButton();
/*     */       b.setBackground(Color.WHITE);
/*     */       b.setLocation(i * sx + 17, 30);
/*     */       b.setSize(sx, sy * 7);
/*     */       b.addMouseListener(this);
/*     */       b.setName("" + white[i]);
/*     */       this.buttons[white[i] - 13] = b;
/*     */       panel.add(b, Integer.valueOf(0), -1);
/*     */       this.labels[i] = new JLabel(Integer.toString(i % 7 + 1));
/*     */       this.labels[i].setHorizontalAlignment(0);
/*     */       this.labels[i].setLocation(i * sx + 17, 5);
/*     */       this.labels[i].setSize(sx, 20);
/*     */       panel.add(this.labels[i], Integer.valueOf(0), -1);
/*     */     } 
/*     */     for (int z = 0; z < 3; z++) {
/*     */       for (int j = 0; j < 6; j++) {
/*     */         if (j != 2) {
/*     */           JToggleButton b = new JToggleButton();
/*     */           b.setBackground(Color.BLACK);
/*     */           if (macos)
/*     */             b.putClientProperty("JButton.buttonType", "toggled"); 
/*     */           b.setLocation(z * sx * 7 + sx * 2 / 3 + j * sx + 17, 30);
/*     */           b.setSize(sx * 2 / 3, sy * 4);
/*     */           b.addMouseListener(this);
/*     */           b.setName("" + black[z][j]);
/*     */           this.buttons[black[z][j] - 13] = b;
/*     */           panel.add(b, Integer.valueOf(1), -1);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public Piano(JPanel parent, PianoListener l) {
/* 104 */     this.btLast = null;
/* 105 */     this.tootLast = 0;
/*     */     this.container = parent;
/*     */     this.listener = l;
/*     */     initKeyboard();
/*     */   } public void mousePressed(MouseEvent e) {
/* 110 */     JToggleButton s = (JToggleButton)e.getSource();
/*     */     
/* 112 */     int toot = Integer.parseInt(s.getName());
/* 113 */     this.listener.midiOn(Scale.indexToMidi(toot), 127);
/* 114 */     this.tootLast = toot;
/* 115 */     this.btLast = s;
/*     */   }
/*     */   public void mouseClicked(MouseEvent e) {}
/*     */   
/*     */   public void mouseReleased(MouseEvent e) {
/* 120 */     if (this.btLast != null) {
/* 121 */       if (this.tootLast != 0) {
/* 122 */         this.listener.midiOff(Scale.indexToMidi(this.tootLast));
/* 123 */         this.tootLast = 0;
/* 124 */         this.btLast.setSelected(false);
/*     */       } 
/* 126 */       this.btLast = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void mouseEntered(MouseEvent e) {
/* 132 */     if (this.btLast != null) {
/* 133 */       JToggleButton s = (JToggleButton)e.getSource();
/*     */       
/* 135 */       int toot = Integer.parseInt(s.getName());
/* 136 */       this.btLast.setSelected(false);
/* 137 */       s.setSelected(true);
/* 138 */       this.listener.midiOn(Scale.indexToMidi(toot), 127);
/* 139 */       if (this.tootLast != 0) {
/* 140 */         this.listener.midiOff(Scale.indexToMidi(this.tootLast));
/*     */       }
/* 142 */       this.tootLast = toot;
/* 143 */       this.btLast = s;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mouseExited(MouseEvent e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(MidiMessage message, long timeStamp) {
/* 163 */     if (message instanceof ShortMessage)
/*     */     {
/* 165 */       decodeMessage((ShortMessage)message); } 
/*     */   }
/*     */   
/*     */   public void decodeMessage(ShortMessage message) {
/*     */     byte note, velocity;
/* 170 */     String strMessage = null;
/* 171 */     int nChannel = message.getChannel();
/*     */     
/* 173 */     int cmd = message.getCommand();
/* 174 */     switch (cmd) {
/*     */       case 128:
/* 176 */         note = (byte)message.getData1();
/* 177 */         this.listener.midiOff(note);
/* 178 */         buttonOff(note);
/*     */         break;
/*     */       
/*     */       case 144:
/* 182 */         note = (byte)message.getData1();
/* 183 */         velocity = (byte)message.getData2();
/* 184 */         if (velocity <= 0) {
/* 185 */           this.listener.midiOff(note);
/* 186 */           buttonOff(note); break;
/*     */         } 
/* 188 */         this.listener.midiOn(note, (byte)(velocity / 2 + 64));
/* 189 */         buttonOn(note);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void buttonOn(byte n) {
/* 197 */     byte i = (byte)(n - 48);
/*     */     
/* 199 */     if (i >= 0 && i < 37)
/* 200 */       this.buttons[i].setSelected(true); 
/*     */   }
/*     */   
/*     */   void buttonOff(byte n) {
/* 204 */     byte i = (byte)(n - 48);
/*     */     
/* 206 */     if (i >= 0 && i < 37)
/* 207 */       this.buttons[i].setSelected(false); 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\Piano.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */