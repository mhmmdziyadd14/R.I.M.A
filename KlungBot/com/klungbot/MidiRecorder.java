/*     */ package com.klungbot;
/*     */ 
/*     */ import java.io.File;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MetaMessage;
/*     */ import javax.sound.midi.MidiChannel;
/*     */ import javax.sound.midi.MidiEvent;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.Receiver;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.Sequencer;
/*     */ import javax.sound.midi.ShortMessage;
/*     */ import javax.sound.midi.Track;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MidiRecorder
/*     */   implements RecordingListener
/*     */ {
/*  24 */   Sequencer sequencer = null;
/*  25 */   Sequence sequence = null;
/*  26 */   Receiver receiver = null;
/*  27 */   Track track = null;
/*     */   Maestro maestro;
/*     */   long lstart;
/*     */   
/*     */   public MidiRecorder(Maestro m) {
/*  32 */     this.maestro = m;
/*     */   }
/*     */   
/*     */   boolean isIncluded(int i, Sequence seq) {
/*  36 */     for (Track t : seq.tracks) {
/*  37 */       if (t.channel == i) return true; 
/*     */     } 
/*  39 */     return false;
/*     */   }
/*     */   
/*     */   public void start(Sequence seq) {
/*     */     try {
/*  44 */       this.sequencer = MidiSystem.getSequencer();
/*  45 */       this.receiver = this.sequencer.getReceiver();
/*  46 */       this.sequence = new Sequence(0.0F, 24);
/*  47 */       this.track = this.sequence.createTrack();
/*  48 */       this.sequencer.open();
/*  49 */       this.sequencer.setSequence(this.sequence);
/*  50 */       this.sequencer.recordEnable(this.track, -1);
/*  51 */       this.sequencer.startRecording();
/*  52 */       this.lstart = System.currentTimeMillis();
/*  53 */       AudioEngineer pm = this.maestro.getAudioEngineer();
/*  54 */       for (int n = (this.maestro.getPlayers()).length; n-- >= 0; ) {
/*  55 */         if (!isIncluded(n, seq))
/*  56 */           continue;  Player p = this.maestro.getPlayer(n);
/*  57 */         if (p instanceof Synthesizer) {
/*  58 */           Synthesizer s = (Synthesizer)p;
/*  59 */           for (int i = 0; i < s.channels.length; i++) {
/*  60 */             int nch = s.channels[i];
/*  61 */             MidiChannel ch = pm.channels[nch];
/*  62 */             int prog = ch.getProgram();
/*  63 */             recordProgramChange(nch, prog);
/*     */           } 
/*     */         } 
/*     */       } 
/*  67 */       this.sequencer.setTempoInBPM(seq.tempo);
/*  68 */       System.out.println("BPM = " + this.sequencer.getTempoInBPM());
/*  69 */       System.out.println("MPQ = " + this.sequencer.getTempoInMPQ());
/*     */     }
/*  71 */     catch (Exception e) {
/*  72 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void finish(File outFile) {
/*     */     try {
/*  78 */       MidiSystem.write(this.sequence, 0, outFile);
/*  79 */       System.out.println("Saving midi file " + outFile.getName());
/*     */     }
/*  81 */     catch (Exception e) {
/*     */       
/*  83 */       e.printStackTrace();
/*     */     } finally {
/*     */       
/*  86 */       this.track = null;
/*  87 */       this.receiver = null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void record(int chn, int nCommand, int data1, int data2) {
/* 112 */     ShortMessage message = new ShortMessage();
/*     */     try {
/* 114 */       message.setMessage(nCommand, chn, data1, data2);
/* 115 */       long lstamp = System.currentTimeMillis() - this.lstart;
/* 116 */       MidiEvent event = new MidiEvent(message, this.maestro.tick);
/* 117 */       this.receiver.send(message, lstamp * 1000L);
/*     */     }
/* 119 */     catch (InvalidMidiDataException e) {
/* 120 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void recordMetaEvent(int type, byte[] data) {
/* 126 */     MetaMessage message = new MetaMessage();
/*     */     
/*     */     try {
/* 129 */       message.setMessage(type, data, data.length);
/* 130 */       MidiEvent event = new MidiEvent(message, this.maestro.tick);
/* 131 */       this.track.add(event);
/*     */     }
/* 133 */     catch (InvalidMidiDataException e) {
/*     */       
/* 135 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void recordNoteOn(int chn, int note, int forte) {
/* 140 */     record(chn, 144, note, forte);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordNoteOff(int chn, int note) {
/* 146 */     record(chn, 128, note, 0);
/*     */   }
/*     */   
/*     */   public void recordProgramChange(int chn, int program) {
/* 150 */     record(chn, 192, program, 0);
/* 151 */     System.out.println("Record Program " + chn + " = " + program);
/*     */   }
/*     */ 
/*     */   
/*     */   public void recordAllNotesOff(int channel) {
/* 156 */     record(channel, 176, 120, 0);
/* 157 */     System.out.println("Record ALL notes off " + channel);
/*     */   }
/*     */ 
/*     */   
/*     */   public void recordSetTempo(int bpm) {
/* 162 */     this.sequencer.setTempoInBPM(bpm);
/* 163 */     System.out.println("Record BPM= " + bpm);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\MidiRecorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */