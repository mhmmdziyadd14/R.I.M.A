/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.doremi.Diatonic;
/*     */ import com.klungbot.doremi.Doremi;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import javax.sound.midi.InvalidMidiDataException;
/*     */ import javax.sound.midi.MidiEvent;
/*     */ import javax.sound.midi.MidiMessage;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.ShortMessage;
/*     */ import javax.sound.midi.Track;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MidiInfo
/*     */ {
/*  39 */   public final int CHANNEL_MAX = 16;
/*     */   
/*     */   String title;
/*     */   
/*     */   long tickLength;
/*     */   
/*     */   long duration;
/*     */   
/*     */   String divisionType;
/*     */   int resolution;
/*     */   String resolutionType;
/*     */   String name;
/*     */   String copyRight;
/*     */   String notes;
/*     */   File midiFile;
/*     */   AudioEngineer pm;
/*     */   boolean changed = false;
/*  56 */   static int[] programMaps = new int[] { 0, 3, 0, 5, 2, 8, 0, 7, 7, 6, 9, -1, -1, 5, 7, -1 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   static int[] notes16 = new int[] { 55, 57, 58, 59, 60, 62, 64, 65, 66, 67, 69, 70, 71, 72, 74, 76 };
/*     */   ChannelInfo[] channels;
/*     */   ArrayList<ChannelInfo> infos;
/*     */   
/*     */   public boolean isNote16(int note) {
/*  80 */     if (note < 55) return false; 
/*  81 */     if (note > 76) return false; 
/*  82 */     for (int i = 0; i < notes16.length; i++) {
/*  83 */       if (note == notes16[i]) return true; 
/*     */     } 
/*  85 */     return false;
/*     */   }
/*     */   
/*     */   class ProgramInfo {
/*     */     long tick;
/*     */     int bank;
/*     */     int program;
/*     */     String name;
/*     */     
/*     */     ProgramInfo(int b, int p, long t) {
/*  95 */       this.tick = t;
/*  96 */       this.bank = b;
/*  97 */       this.program = p;
/*  98 */       this.name = MidiInfo.this.pm.getMidiInstrumentName(b, this.program);
/*     */     }
/*     */   }
/*     */   
/*     */   class ChannelInfo
/*     */     implements Comparable {
/*     */     int chnum;
/*     */     int bank;
/*     */     int count;
/*     */     int max_note;
/*     */     
/*     */     public ChannelInfo(int num) {
/* 110 */       this.chnum = num;
/* 111 */       if (num == 9) {
/* 112 */         this.bank = 128;
/* 113 */         this.map = 4;
/*     */       } else {
/*     */         
/* 116 */         this.bank = 0;
/* 117 */         this.map = 0;
/*     */       } 
/* 119 */       this.programs = null;
/* 120 */       this.min_note = 128;
/* 121 */       this.max_note = 0;
/* 122 */       this.count = 0;
/* 123 */       this.note16 = true;
/*     */     }
/*     */     int min_note; boolean note16; ArrayList<MidiInfo.ProgramInfo> programs; int map;
/*     */     boolean isNotThere(int b, int p) {
/* 127 */       for (MidiInfo.ProgramInfo i : this.programs) {
/* 128 */         if (i.bank == b && i.program == p) return false; 
/*     */       } 
/* 130 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     void setProgram(int p, long tick) {
/* 135 */       if (this.programs == null) {
/* 136 */         this.programs = new ArrayList<>();
/*     */       }
/* 138 */       if (this.bank == 128) {
/* 139 */         this.map = 4;
/* 140 */         p = 0;
/*     */       }
/* 142 */       else if (p == 1) {
/* 143 */         this.map = 1;
/*     */       } else {
/*     */         
/* 146 */         this.map = MidiInfo.programMaps[p / 8];
/*     */       } 
/* 148 */       if (isNotThere(this.bank, p)) {
/* 149 */         MidiInfo.ProgramInfo pi = new MidiInfo.ProgramInfo(this.bank, p, tick);
/* 150 */         this.programs.add(pi);
/* 151 */         System.out.println(this.chnum + " " + p + " " + this.map + " " + pi.name);
/*     */       } 
/*     */     }
/*     */     
/*     */     void setBank(int b, long tick) {
/* 156 */       this.bank = (this.chnum == 9) ? 128 : 0;
/*     */     }
/*     */ 
/*     */     
/*     */     public int compareTo(Object o) {
/* 161 */       ChannelInfo p = (ChannelInfo)o;
/* 162 */       return this.bank * 2 + this.min_note - p.bank * 2 + p.min_note;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MidiInfo(File midiFile, Maestro m) {
/* 170 */     this.title = midiFile.getName();
/* 171 */     this.midiFile = midiFile;
/* 172 */     this.channels = new ChannelInfo[16];
/* 173 */     this.infos = new ArrayList<>(17);
/* 174 */     this.pm = m.getAudioEngineer();
/*     */   }
/*     */   
/*     */   public void openInfo() {
/* 178 */     dump(this.midiFile);
/* 179 */     openMap();
/*     */   }
/*     */   
/*     */   public File getMapFile() {
/* 183 */     StringBuilder fname = new StringBuilder(this.midiFile.getAbsolutePath());
/* 184 */     int idx = fname.lastIndexOf(".");
/* 185 */     if (idx > 0) {
/* 186 */       fname.setLength(idx);
/*     */     }
/* 188 */     fname.append(".12m");
/* 189 */     File fmap = new File(fname.toString());
/* 190 */     return fmap;
/*     */   }
/*     */   
/*     */   public int getLength() {
/* 194 */     return this.infos.size();
/*     */   }
/*     */   
/*     */   public String getInstrument(int row) {
/* 198 */     if (((ChannelInfo)this.infos.get(row)).programs == null)
/* 199 */       return "default"; 
/* 200 */     return ((ProgramInfo)((ChannelInfo)this.infos.get(row)).programs.get(0)).name;
/*     */   }
/*     */   
/*     */   public String getInstruments(int row) {
/* 204 */     if (((ChannelInfo)this.infos.get(row)).programs == null)
/* 205 */       return "default"; 
/* 206 */     StringBuilder s = new StringBuilder();
/* 207 */     Iterator<ProgramInfo> it = ((ChannelInfo)this.infos.get(row)).programs.iterator();
/* 208 */     ProgramInfo i = it.next();
/*     */     while (true) {
/* 210 */       s.append(i.name);
/* 211 */       if (!it.hasNext())
/* 212 */         break;  s.append(",");
/* 213 */       i = it.next();
/*     */     } 
/* 215 */     return s.toString();
/*     */   }
/*     */   
/*     */   public int getChannel(int row) {
/* 219 */     return ((ChannelInfo)this.infos.get(row)).chnum + 1;
/*     */   }
/*     */   public int getBank(int row) {
/* 222 */     return ((ChannelInfo)this.infos.get(row)).bank;
/*     */   }
/*     */   public int getProgram(int row) {
/* 225 */     return ((ProgramInfo)((ChannelInfo)this.infos.get(row)).programs.get(0)).program;
/*     */   }
/*     */   
/*     */   public int getCount(int row) {
/* 229 */     return ((ChannelInfo)this.infos.get(row)).count;
/*     */   }
/*     */   
/*     */   public String getLowestNote(int row) {
/* 233 */     return Diatonic.nameOfMidiNote(((ChannelInfo)this.infos.get(row)).min_note);
/*     */   }
/*     */   
/*     */   public String getHighestNote(int row) {
/* 237 */     return Diatonic.nameOfMidiNote(((ChannelInfo)this.infos.get(row)).max_note);
/*     */   }
/*     */   
/*     */   public boolean isMelody16(int row) {
/* 241 */     ChannelInfo ci = this.infos.get(row);
/* 242 */     return ci.note16;
/*     */   }
/*     */   public boolean isMelody32(int row) {
/* 245 */     ChannelInfo ci = this.infos.get(row);
/* 246 */     return (ci.min_note >= 53 && ci.max_note <= 84);
/*     */   }
/*     */   public boolean isMelody37(int row) {
/* 249 */     ChannelInfo ci = this.infos.get(row);
/* 250 */     return (ci.min_note >= 48 && ci.max_note <= 84);
/*     */   }
/*     */   
/*     */   public boolean isAccomp16(int row) {
/* 254 */     ChannelInfo ci = this.infos.get(row);
/* 255 */     return (ci.min_note >= 48 && ci.max_note <= 63);
/*     */   }
/*     */   
/*     */   public String getMelodyRequirement(int row) {
/* 259 */     if (isMelody16(row)) return "MS-16"; 
/* 260 */     if (isMelody32(row)) return "M-32"; 
/* 261 */     if (isMelody37(row)) return "M-37";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 268 */     return "";
/*     */   }
/*     */   
/*     */   public String getAccompRequirement(int row) {
/* 272 */     if (isAccomp16(row)) return "S-16";
/*     */     
/* 274 */     return "";
/*     */   }
/*     */   
/*     */   public boolean isMapped(int row, int chn) {
/* 278 */     return (((ChannelInfo)this.infos.get(row)).map == chn);
/*     */   }
/*     */   
/*     */   public void setMap(int row, int track) {
/* 282 */     ((ChannelInfo)this.infos.get(row)).map = track;
/* 283 */     this.changed = true;
/*     */   }
/*     */   
/*     */   public boolean openMap() {
/* 287 */     boolean result = false;
/*     */     try {
/* 289 */       File fmap = getMapFile();
/* 290 */       if (fmap.exists()) {
/* 291 */         BufferedReader reader = new BufferedReader(new FileReader(fmap));
/* 292 */         result = readMap(reader);
/* 293 */         reader.close();
/*     */       }
/*     */     
/* 296 */     } catch (Exception ex) {
/* 297 */       System.err.println("Open Midi Map: " + ex.getMessage());
/*     */     } 
/* 299 */     return result;
/*     */   }
/*     */   
/*     */   public void autoSaveMap() {
/* 303 */     if (!this.changed)
/* 304 */       return;  saveMap();
/*     */   }
/*     */   
/*     */   public boolean saveMap() {
/* 308 */     File fmap = getMapFile();
/*     */     
/*     */     try {
/* 311 */       BufferedWriter writer = new BufferedWriter(new FileWriter(fmap));
/* 312 */       writer.write("$ Doremi midi mapping");
/* 313 */       writer.newLine();
/* 314 */       writer.write("T: " + this.title);
/* 315 */       writer.newLine();
/* 316 */       for (ChannelInfo ch : this.infos) {
/* 317 */         if (ch.map >= 0) {
/* 318 */           writer.write("" + (ch.chnum + 1) + ": " + Doremi.getTrackName(ch.map));
/* 319 */           writer.newLine(); continue;
/* 320 */         }  if (ch.map == -1) {
/* 321 */           writer.write("" + (ch.chnum + 1) + ": midi");
/* 322 */           writer.newLine();
/*     */         } 
/*     */       } 
/* 325 */       writer.close();
/*     */     }
/* 327 */     catch (Exception ex) {
/* 328 */       System.err.println("Save midi map: " + ex.getMessage());
/*     */     } 
/* 330 */     return false;
/*     */   }
/*     */   
/*     */   boolean readMap(BufferedReader r) {
/* 334 */     boolean hasInfo = (this.infos.size() > 0);
/*     */     
/*     */     try {
/* 337 */       while (r.ready()) {
/* 338 */         String line = r.readLine().trim();
/* 339 */         if (line.isEmpty() || 
/* 340 */           line.startsWith("$"))
/* 341 */           continue;  String[] cols = line.split(":");
/* 342 */         if (cols[0].startsWith("T")) {
/* 343 */           this.title = cols[1].trim();
/*     */           continue;
/*     */         } 
/*     */         try {
/* 347 */           int channel = Integer.parseInt(cols[0]) - 1;
/* 348 */           if (channel >= 0 && channel < this.channels.length) {
/* 349 */             if (this.channels[channel] == null) {
/* 350 */               if (hasInfo)
/* 351 */                 continue;  this.channels[channel] = new ChannelInfo(channel);
/* 352 */               this.infos.add(this.channels[channel]);
/*     */             } 
/* 354 */             int track = -1;
/* 355 */             String s = cols[1].trim();
/* 356 */             if (s.startsWith("V")) {
/* 357 */               track = Doremi.getTrackIndex(s);
/*     */             }
/* 359 */             (this.channels[channel]).map = track;
/*     */           }
/*     */         
/* 362 */         } catch (Exception ex) {}
/*     */       } 
/* 364 */       return true;
/*     */     }
/* 366 */     catch (Exception ex) {
/* 367 */       System.err.println("Read Midi map: " + ex.getMessage());
/*     */       
/* 369 */       return false;
/*     */     } 
/*     */   }
/*     */   private void dump(File midiFile) {
/* 373 */     Sequence sequence = null;
/*     */     try {
/* 375 */       sequence = MidiSystem.getSequence(midiFile);
/* 376 */     } catch (InvalidMidiDataException e) {
/*     */       return;
/* 378 */     } catch (IOException e) {
/*     */       return;
/*     */     } 
/*     */     
/* 382 */     if (sequence != null) {
/* 383 */       this.tickLength = sequence.getTickLength();
/* 384 */       this.duration = sequence.getMicrosecondLength();
/* 385 */       float fDivisionType = sequence.getDivisionType();
/* 386 */       String strDivisionType = null;
/* 387 */       if (fDivisionType == 0.0F) {
/* 388 */         strDivisionType = "PPQ";
/* 389 */       } else if (fDivisionType == 24.0F) {
/* 390 */         strDivisionType = "SMPTE, 24 frames per second";
/* 391 */       } else if (fDivisionType == 25.0F) {
/* 392 */         strDivisionType = "SMPTE, 25 frames per second";
/* 393 */       } else if (fDivisionType == 29.97F) {
/* 394 */         strDivisionType = "SMPTE, 29.97 frames per second";
/* 395 */       } else if (fDivisionType == 30.0F) {
/* 396 */         strDivisionType = "SMPTE, 30 frames per second";
/*     */       } 
/* 398 */       this.divisionType = strDivisionType;
/*     */       
/* 400 */       String strResolutionType = null;
/* 401 */       if (sequence.getDivisionType() == 0.0F) {
/* 402 */         strResolutionType = " ticks per beat";
/*     */       } else {
/* 404 */         strResolutionType = " ticks per frame";
/*     */       } 
/* 406 */       this.resolution = sequence.getResolution();
/* 407 */       this.resolutionType = strResolutionType;
/* 408 */       Track[] tracks = sequence.getTracks();
/* 409 */       for (int nTrack = 0; nTrack < tracks.length; nTrack++) {
/* 410 */         Track track = tracks[nTrack];
/* 411 */         for (int nEvent = 0; nEvent < track.size(); nEvent++) {
/* 412 */           MidiEvent event = track.get(nEvent);
/* 413 */           MidiMessage message = event.getMessage();
/* 414 */           long lTicks = event.getTick();
/* 415 */           if (message instanceof ShortMessage) {
/* 416 */             decodeMessage((ShortMessage)message, lTicks);
/*     */           }
/*     */         } 
/*     */       } 
/* 420 */       for (int i = this.infos.size(); i-- > 0; ) {
/* 421 */         ChannelInfo ci = this.infos.get(i);
/* 422 */         if (ci.programs == null)
/* 423 */           this.infos.remove(i); 
/*     */       } 
/* 425 */       Collections.sort(this.infos);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void decodeMessage(ShortMessage message, long tick) {
/* 431 */     int note, nChannel = message.getChannel();
/* 432 */     if (nChannel >= 16)
/* 433 */       return;  ChannelInfo ch = this.channels[nChannel];
/* 434 */     if (ch == null) {
/* 435 */       ch = new ChannelInfo(nChannel);
/* 436 */       this.channels[nChannel] = ch;
/* 437 */       this.infos.add(ch);
/*     */     } 
/* 439 */     switch (message.getCommand()) {
/*     */       case 144:
/* 441 */         note = message.getData1();
/* 442 */         if (ch.min_note > note) ch.min_note = note; 
/* 443 */         if (ch.max_note < note) ch.max_note = note; 
/* 444 */         if (!isNote16(note)) ch.note16 = false; 
/* 445 */         ch.count++;
/*     */         break;
/*     */       case 192:
/* 448 */         ch.setProgram(message.getData1(), tick);
/*     */         break;
/*     */       case 176:
/* 451 */         ch.setBank(message.getData1() * 128 + message.getData2(), tick);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 458 */     return this.title;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\MidiInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */