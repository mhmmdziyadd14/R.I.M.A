/*     */ package com.klungbot;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MetaMessage;
/*     */ import javax.sound.midi.MidiDevice;
/*     */ import javax.sound.midi.MidiEvent;
/*     */ import javax.sound.midi.MidiMessage;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.MidiUnavailableException;
/*     */ import javax.sound.midi.Receiver;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.Synthesizer;
/*     */ import javax.sound.midi.Track;
/*     */ import javax.sound.sampled.AudioFileFormat;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MidiRenderer
/*     */ {
/*     */   public static void createWavFile(Synthesizer synth, Sequence sequence, File outputFile) throws MidiUnavailableException, InvalidMidiDataException, IOException {
/*  54 */     synth = findAudioSynthesizer(synth);
/*  55 */     AudioInputStream stream = getAudioInputStream(sequence, synth);
/*     */     
/*     */     try {
/*  58 */       if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, stream)) {
/*  59 */         AudioSystem.write(stream, AudioFileFormat.Type.WAVE, outputFile);
/*     */       }
/*     */     }
/*  62 */     catch (Exception ex) {
/*  63 */       ex.printStackTrace();
/*     */     }
/*     */     finally {
/*     */       
/*  67 */       stream.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Synthesizer findAudioSynthesizer(Synthesizer s1) throws MidiUnavailableException {
/*     */     Class<?> audioSynthesizerClass;
/*     */     try {
/*  80 */       audioSynthesizerClass = Class.forName("com.sun.media.sound.AudioSynthesizer");
/*  81 */     } catch (ClassNotFoundException e) {
/*     */       
/*  83 */       return null;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     if (audioSynthesizerClass.isInstance(s1)) {
/*  94 */       return s1;
/*     */     }
/*     */ 
/*     */     
/*  98 */     MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
/*  99 */     for (int i = 0; i < midiDeviceInfo.length; i++) {
/* 100 */       MidiDevice dev = MidiSystem.getMidiDevice(midiDeviceInfo[i]);
/* 101 */       if (audioSynthesizerClass.isInstance(dev)) {
/* 102 */         return (Synthesizer)dev;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 107 */     return null;
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
/*     */   public static AudioInputStream getAudioInputStream(Sequence sequence, Synthesizer synth) throws MidiUnavailableException, InvalidMidiDataException, IOException {
/* 126 */     AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
/* 127 */     Map<String, Object> info = new HashMap<>();
/* 128 */     info.put("interpolation", "sinc");
/* 129 */     info.put("max polyphony", "1024");
/* 130 */     Method openStreamMethod = null;
/*     */ 
/*     */     
/*     */     try {
/* 134 */       openStreamMethod = synth.getClass().getMethod("openStream", new Class[] { AudioFormat.class, Map.class });
/*     */     
/*     */     }
/* 137 */     catch (NoSuchMethodException e) {
/*     */       
/* 139 */       throw new MidiUnavailableException(e.getMessage());
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 144 */       stream = (AudioInputStream)openStreamMethod.invoke(synth, new Object[] { format, info });
/*     */     }
/* 146 */     catch (Exception e) {
/*     */       
/* 148 */       throw new MidiUnavailableException(e.getMessage());
/*     */     } 
/*     */     
/* 151 */     double total = send(sequence, synth.getReceiver());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 157 */     long len = (long)(stream.getFormat().getFrameRate() * (total + 4.0D));
/* 158 */     AudioInputStream stream = new AudioInputStream(stream, stream.getFormat(), len);
/*     */     
/* 160 */     return stream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static double send(Sequence seq, Receiver recv) {
/* 168 */     float divtype = seq.getDivisionType();
/* 169 */     assert seq.getDivisionType() == 0.0F;
/* 170 */     Track[] tracks = seq.getTracks();
/* 171 */     int[] trackspos = new int[tracks.length];
/* 172 */     int mpq = 500000;
/* 173 */     int seqres = seq.getResolution();
/* 174 */     long lasttick = 0L;
/* 175 */     long curtime = 0L;
/*     */     while (true) {
/* 177 */       MidiEvent selevent = null;
/* 178 */       int seltrack = -1;
/* 179 */       for (int i = 0; i < tracks.length; i++) {
/* 180 */         int trackpos = trackspos[i];
/* 181 */         Track track = tracks[i];
/* 182 */         if (trackpos < track.size()) {
/* 183 */           MidiEvent event = track.get(trackpos);
/* 184 */           if (selevent == null || event
/* 185 */             .getTick() < selevent.getTick()) {
/* 186 */             selevent = event;
/* 187 */             seltrack = i;
/*     */           } 
/*     */         } 
/*     */       } 
/* 191 */       if (seltrack == -1)
/*     */         break; 
/* 193 */       trackspos[seltrack] = trackspos[seltrack] + 1;
/* 194 */       long tick = selevent.getTick();
/* 195 */       if (divtype == 0.0F) {
/* 196 */         curtime += (tick - lasttick) * mpq / seqres;
/*     */       } else {
/* 198 */         curtime = (long)(tick * 1000000.0D * divtype / seqres);
/* 199 */       }  lasttick = tick;
/* 200 */       MidiMessage msg = selevent.getMessage();
/* 201 */       if (msg instanceof MetaMessage) {
/* 202 */         if (divtype == 0.0F && (
/* 203 */           (MetaMessage)msg).getType() == 81) {
/* 204 */           byte[] data = ((MetaMessage)msg).getData();
/* 205 */           mpq = (data[0] & 0xFF) << 16 | (data[1] & 0xFF) << 8 | data[2] & 0xFF;
/*     */         } 
/*     */         continue;
/*     */       } 
/* 209 */       if (recv != null) {
/* 210 */         recv.send(msg, curtime);
/*     */       }
/*     */     } 
/* 213 */     return curtime / 1000000.0D;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\MidiRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */