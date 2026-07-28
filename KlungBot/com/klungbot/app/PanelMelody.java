/*     */ package com.klungbot.app;
/*     */ import com.klungbot.AudioEngineer;
/*     */ import com.klungbot.KlungbotUDPServer;
/*     */ import com.klungbot.Maestro;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.util.ArrayList;
/*     */ import javax.sound.midi.MidiDevice;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ public class PanelMelody extends JPanel implements PianoListener {
/*     */   Piano piano;
/*     */   Main parent;
/*     */   Maestro maestro;
/*     */   KlungbotUDPServer udpServer;
/*     */   int track;
/*     */   int last_track;
/*     */   ArrayList<MidiDevice.Info> midiDevices;
/*     */   MidiDevice.Info midiSelected;
/*     */   private JComboBox cbInput;
/*     */   
/*     */   public PanelMelody(Main parent) {
/*  33 */     initComponents();
/*  34 */     this.piano = new Piano(this.panelKeyboard, this);
/*  35 */     this.parent = parent;
/*  36 */     this.maestro = parent.maestro;
/*  37 */     this.track = this.last_track = 0;
/*  38 */     this.midiSelected = null;
/*     */   }
/*     */ 
/*     */   
/*     */   private JComboBox cbInteractive;
/*     */   
/*     */   private JComboBox cbTrack;
/*     */   private JLabel jLabel1;
/*     */   private JLabel jLabel2;
/*     */   private JLabel jLabel3;
/*     */   
/*     */   private void initComponents() {
/*  50 */     this.panelKeyboard = new JPanel();
/*  51 */     this.jPanel2 = new JPanel();
/*  52 */     this.jPanel1 = new JPanel();
/*  53 */     this.jLabel1 = new JLabel();
/*  54 */     this.cbInput = new JComboBox();
/*  55 */     this.jPanel3 = new JPanel();
/*  56 */     this.jLabel2 = new JLabel();
/*  57 */     this.cbTrack = new JComboBox();
/*  58 */     this.jPanel5 = new JPanel();
/*  59 */     this.jLabel3 = new JLabel();
/*  60 */     this.cbInteractive = new JComboBox();
/*     */     
/*  62 */     addComponentListener(new ComponentAdapter() {
/*     */           public void componentShown(ComponentEvent evt) {
/*  64 */             PanelMelody.this.formComponentShown(evt);
/*     */           }
/*     */           public void componentHidden(ComponentEvent evt) {
/*  67 */             PanelMelody.this.formComponentHidden(evt);
/*     */           }
/*     */         });
/*  70 */     setLayout(new BorderLayout());
/*     */     
/*  72 */     this.panelKeyboard.setName("panelKeyboard");
/*  73 */     this.panelKeyboard.setLayout(new BorderLayout());
/*  74 */     add(this.panelKeyboard, "Center");
/*     */     
/*  76 */     this.jPanel2.setName("jPanel2");
/*  77 */     this.jPanel2.setLayout(new GridLayout(1, 8));
/*     */     
/*  79 */     this.jPanel1.setName("jPanel1");
/*     */     
/*  81 */     this.jLabel1.setText("MIDI Input");
/*  82 */     this.jLabel1.setName("jLabel1");
/*  83 */     this.jPanel1.add(this.jLabel1);
/*     */     
/*  85 */     this.cbInput.setModel(new DefaultComboBoxModel<>(new String[] { "None" }));
/*  86 */     this.cbInput.setName("cbInput");
/*  87 */     this.cbInput.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*  89 */             PanelMelody.this.cbInputActionPerformed(evt);
/*     */           }
/*     */         });
/*  92 */     this.jPanel1.add(this.cbInput);
/*     */     
/*  94 */     this.jPanel2.add(this.jPanel1);
/*     */     
/*  96 */     this.jPanel3.setName("jPanel3");
/*     */     
/*  98 */     this.jLabel2.setText("Output Track");
/*  99 */     this.jLabel2.setName("jLabel2");
/* 100 */     this.jPanel3.add(this.jLabel2);
/*     */     
/* 102 */     this.cbTrack.setModel(new DefaultComboBoxModel<>(new String[] { "V", "VA", "VB", "VC", "VD", "VE", "VF", "VG", "VH", "VI" }));
/* 103 */     this.cbTrack.setName("cbTrack");
/* 104 */     this.cbTrack.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/* 106 */             PanelMelody.this.cbTrackActionPerformed(evt);
/*     */           }
/*     */         });
/* 109 */     this.jPanel3.add(this.cbTrack);
/*     */     
/* 111 */     this.jPanel2.add(this.jPanel3);
/*     */     
/* 113 */     this.jPanel5.setName("jPanel5");
/*     */     
/* 115 */     this.jLabel3.setText("Interactive");
/* 116 */     this.jLabel3.setName("jLabel3");
/* 117 */     this.jPanel5.add(this.jLabel3);
/*     */     
/* 119 */     this.cbInteractive.setModel(new DefaultComboBoxModel<>(new String[] { "-", "V0", "V1", "V*", "VA", "VB", "VC", "VD" }));
/* 120 */     this.cbInteractive.setName("cbInteractive");
/* 121 */     this.cbInteractive.addItemListener(new ItemListener() {
/*     */           public void itemStateChanged(ItemEvent evt) {
/* 123 */             PanelMelody.this.cbInteractiveItemStateChanged(evt);
/*     */           }
/*     */         });
/* 126 */     this.jPanel5.add(this.cbInteractive);
/*     */     
/* 128 */     this.jPanel2.add(this.jPanel5);
/*     */     
/* 130 */     add(this.jPanel2, "First");
/*     */   }
/*     */   private JPanel jPanel1; private JPanel jPanel2; private JPanel jPanel3; private JPanel jPanel5; private JPanel panelKeyboard;
/*     */   
/*     */   void initInput() {
/* 135 */     int num = -1;
/* 136 */     this.maestro.getAudioEngineer(); this.midiDevices = AudioEngineer.getMidiInputDevices();
/* 137 */     this.cbInput.removeAllItems();
/* 138 */     this.cbInput.addItem("None");
/* 139 */     if (this.midiDevices.size() > 0) {
/* 140 */       for (int i = 0; i < this.midiDevices.size(); i++) {
/* 141 */         MidiDevice.Info dev = this.midiDevices.get(i);
/* 142 */         this.cbInput.addItem(dev.getName());
/* 143 */         if (dev == this.midiSelected) num = i; 
/*     */       } 
/* 145 */       if (num == -1) {
/* 146 */         num = 0;
/* 147 */         this.midiSelected = this.midiDevices.get(num);
/*     */       } 
/* 149 */       this.cbInput.setSelectedIndex(num + 1);
/*     */     } else {
/*     */       
/* 152 */       this.midiSelected = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void formComponentShown(ComponentEvent evt) {
/* 159 */     initInput();
/* 160 */     if (this.midiSelected != null) {
/* 161 */       this.maestro.getAudioEngineer().setInputDevice(this.midiSelected, this.piano);
/*     */     }
/* 163 */     this.parent.setMessage("Play manually using the MIDI keyboard", Color.GREEN);
/*     */   }
/*     */ 
/*     */   
/*     */   private void formComponentHidden(ComponentEvent evt) {
/* 168 */     System.out.println("Piano Hidden");
/*     */   }
/*     */ 
/*     */   
/*     */   private void cbTrackActionPerformed(ActionEvent evt) {
/* 173 */     this.track = this.cbTrack.getSelectedIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   private void cbInteractiveItemStateChanged(ItemEvent evt) {
/* 178 */     if (evt.getStateChange() != 1)
/* 179 */       return;  int value = this.cbInteractive.getSelectedIndex();
/* 180 */     this.maestro.setInteractive(value);
/*     */   }
/*     */   
/*     */   private void cbInputActionPerformed(ActionEvent evt) {
/* 184 */     int num = this.cbInput.getSelectedIndex();
/* 185 */     if (num <= 0) {
/* 186 */       this.maestro.getAudioEngineer().resetInputDevice();
/*     */     } else {
/*     */       
/* 189 */       this.maestro.getAudioEngineer().setInputDevice(this.midiDevices.get(num - 1), this.piano);
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
/*     */   public void midiOn(byte note, byte forte) {
/* 209 */     this.maestro.midiOn(note, forte, this.track);
/* 210 */     this.last_track = this.track;
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff(byte note) {
/* 215 */     this.maestro.midiOff(note, this.last_track);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PanelMelody.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */