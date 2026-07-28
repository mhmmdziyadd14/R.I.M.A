/*    */ package com.klungbot;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import javax.sound.midi.MidiEvent;
/*    */ import javax.sound.midi.MidiMessage;
/*    */ import javax.sound.midi.MidiSystem;
/*    */ import javax.sound.midi.Sequence;
/*    */ import javax.sound.midi.ShortMessage;
/*    */ import javax.sound.midi.Track;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MidiConverter
/*    */ {
/* 26 */   public static final String[] NOTE_NAMES = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
/*    */ 
/*    */   
/* 29 */   StringBuilder header = new StringBuilder();
/* 30 */   ArrayList<StringBuilder> tracks = new ArrayList<>(); public static final int NOTE_ON = 144;
/*    */   public static final int NOTE_OFF = 128;
/*    */   
/*    */   private void addHeader(String s) {
/* 34 */     this.header.append(s);
/* 35 */     this.header.append("\n");
/*    */   }
/*    */   
/*    */   private void addChannel(int ch, String s) {
/* 39 */     this.header.append(s);
/* 40 */     this.header.append("\n");
/*    */   }
/*    */   
/*    */   private void decodeMessage(MidiMessage message) {
/* 44 */     System.out.println("Other message: " + message.getStatus() + " " + message.getMessage());
/*    */   }
/*    */   
/*    */   private void decodeShortMessage(ShortMessage sm) {
/* 48 */     System.out.print("Channel: " + sm.getChannel() + " ");
/* 49 */     if (sm.getCommand() == 144) {
/* 50 */       int key = sm.getData1();
/* 51 */       int octave = key / 12 - 1;
/* 52 */       int note = key % 12;
/* 53 */       String noteName = NOTE_NAMES[note];
/* 54 */       int velocity = sm.getData2();
/* 55 */       System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
/* 56 */     } else if (sm.getCommand() == 128) {
/* 57 */       int key = sm.getData1();
/* 58 */       int octave = key / 12 - 1;
/* 59 */       int note = key % 12;
/* 60 */       String noteName = NOTE_NAMES[note];
/* 61 */       int velocity = sm.getData2();
/* 62 */       System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
/*    */     } else {
/* 64 */       System.out.println("Command:" + sm.getCommand());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder read(String fname) throws Exception {
/* 70 */     Sequence sequence = MidiSystem.getSequence(new File(fname));
/* 71 */     int trackNumber = 0;
/* 72 */     for (Track track : sequence.getTracks()) {
/* 73 */       trackNumber++;
/* 74 */       System.out.println("Track " + trackNumber + ": size = " + track.size());
/* 75 */       System.out.println();
/* 76 */       for (int i = 0; i < track.size(); i++) {
/* 77 */         MidiEvent event = track.get(i);
/* 78 */         System.out.print("@" + event.getTick() + " ");
/* 79 */         MidiMessage message = event.getMessage();
/* 80 */         if (message instanceof ShortMessage) {
/* 81 */           decodeShortMessage((ShortMessage)message);
/*    */         } else {
/* 83 */           decodeMessage(message);
/*    */         } 
/*    */       } 
/*    */       
/* 87 */       System.out.println();
/*    */     } 
/* 89 */     return this.header;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\MidiConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */