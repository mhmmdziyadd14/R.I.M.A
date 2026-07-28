/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.util.Options;
/*     */ import com.sun.media.sound.ModelPatch;
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Vector;
/*     */ import javax.sound.midi.Instrument;
/*     */ import javax.sound.midi.MidiChannel;
/*     */ import javax.sound.midi.MidiDevice;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.MidiUnavailableException;
/*     */ import javax.sound.midi.Patch;
/*     */ import javax.sound.midi.Receiver;
/*     */ import javax.sound.midi.Soundbank;
/*     */ import javax.sound.midi.Synthesizer;
/*     */ import javax.sound.midi.Transmitter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AudioEngineer
/*     */ {
/*  25 */   static final int[] percussionChannels = new int[] { 9 };
/*  26 */   static final int[][] trackChannels = new int[][] { { 0, 1, 2, 3, 9, 4, 5, 6, 7, 8 }, { 15, 14, 13, 12, 9, 11, 10, 6, 7, 8 } };
/*     */   
/*     */   static Soundbank[] loadedSoundbanks;
/*     */   
/*     */   static Vector<Player> players;
/*     */   
/*     */   Synthesizer msyn;
/*     */   
/*     */   MidiChannel[] channels;
/*     */   int[] channelCounts;
/*     */   
/*     */   public static int loadSoundbanks(String baseFolder) {
/*  38 */     String sbFolder = baseFolder + Options.get("folder.sounds");
/*  39 */     FilenameFilter filter = new FilenameFilter()
/*     */       {
/*     */         public boolean accept(File d, String f) {
/*  42 */           return f.endsWith(".sf2");
/*     */         }
/*     */       };
/*  45 */     String[] sfs = (new File(sbFolder)).list(filter);
/*  46 */     if (sfs == null) {
/*  47 */       System.err.println("No sound font at " + sbFolder);
/*  48 */       return 0;
/*     */     } 
/*  50 */     loadedSoundbanks = new Soundbank[sfs.length];
/*  51 */     int count = 0;
/*  52 */     for (int i = 0; i < sfs.length; i++) {
/*     */       try {
/*  54 */         File f = new File(sbFolder + File.separator + sfs[i]);
/*  55 */         System.out.println("Loading soundbank " + sfs[i]);
/*  56 */         loadedSoundbanks[count] = MidiSystem.getSoundbank(f);
/*  57 */         count++;
/*     */       }
/*  59 */       catch (Exception ex) {
/*  60 */         System.err.println("Failed to load soundbank " + sfs[i]);
/*  61 */         System.err.println("Error: " + ex.getMessage());
/*     */       } 
/*     */     } 
/*  64 */     return count;
/*     */   }
/*     */   
/*     */   public static Instrument getLoadedInstrument(String name) {
/*  68 */     for (Soundbank sb : loadedSoundbanks) {
/*  69 */       if (sb != null)
/*  70 */         for (Instrument in : sb.getInstruments()) {
/*  71 */           String name1 = in.getName();
/*  72 */           if (name1.equals(name)) return in; 
/*     */         }  
/*     */     } 
/*  75 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void initPlayers(String baseFolder) {
/*  80 */     players = new Vector<>();
/*  81 */     initInstrumentPlayers();
/*  82 */     for (int i = 0; i < loadedSoundbanks.length; i++) {
/*  83 */       if (loadedSoundbanks[i] != null)
/*  84 */         initMelodyPlayers(loadedSoundbanks[i]); 
/*     */     } 
/*  86 */     initBuiltinPlayers();
/*  87 */     initPercussionPlayers(baseFolder);
/*     */   }
/*     */   
/*     */   public void initInstrumentPlayers() {
/*  91 */     for (Device d : Device.getDevices()) {
/*  92 */       for (Player p : d.getPlayers()) {
/*  93 */         players.add(p);
/*  94 */         p.setEngineer(this);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void initMelodyPlayers(Soundbank sb) {
/* 100 */     for (Instrument in1 : sb.getInstruments()) {
/* 101 */       String name1 = in1.getName();
/* 102 */       Patch patch = in1.getPatch();
/* 103 */       System.out.println(name1 + ": " + patch.getBank() + "." + patch.getProgram());
/* 104 */       if (patch instanceof ModelPatch) {
/* 105 */         ModelPatch p1 = (ModelPatch)patch;
/* 106 */         if (p1.isPercussion())
/*     */           continue; 
/* 108 */       }  Player p = getPlayer(name1);
/* 109 */       if (p == null) {
/* 110 */         String name2 = Options.get("player.multi." + name1);
/* 111 */         if (name2 != null) {
/* 112 */           Instrument in2 = getLoadedInstrument(name2);
/* 113 */           if (in2 == null) {
/* 114 */             System.err.print("Warning: cannot find secondary instrument " + name2);
/* 115 */             p = new Synthesizer(name1, in1);
/*     */           } else {
/*     */             
/* 118 */             p = new Synthesizer(name1, in1, in2);
/*     */           } 
/*     */         } else {
/*     */           
/* 122 */           p = new Synthesizer(name1, in1);
/*     */         } 
/* 124 */         p.setEngineer(this);
/* 125 */         players.add(p);
/*     */       } 
/*     */       continue;
/*     */     } 
/*     */   } public void initBuiltinPlayers() {
/* 130 */     ArrayList<String> dps = Options.getKeys("player.default");
/* 131 */     for (String dp : dps) {
/* 132 */       String str = Options.get(dp);
/*     */       try {
/* 134 */         int num = Integer.valueOf(str).intValue();
/* 135 */         Patch patch = new Patch(0, num);
/*     */         
/* 137 */         Instrument i1 = MidiSystem.getSynthesizer().getDefaultSoundbank().getInstrument(patch);
/* 138 */         Player p1 = new Synthesizer(i1.getName(), i1);
/* 139 */         p1.setEngineer(this);
/* 140 */         players.add(p1);
/*     */       }
/* 142 */       catch (Exception ex) {
/* 143 */         System.err.println("Cannot load default instrument " + str);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void initPercussionPlayers(String baseFolder) {
/* 150 */     String folder = baseFolder + Options.get("folder.drums");
/* 151 */     FilenameFilter filter = new FilenameFilter()
/*     */       {
/*     */         public boolean accept(File d, String f) {
/* 154 */           return f.endsWith(".12d");
/*     */         }
/*     */       };
/* 157 */     String[] fs = (new File(folder)).list(filter);
/* 158 */     if (fs == null) {
/* 159 */       System.err.println("No drums at " + folder);
/*     */       return;
/*     */     } 
/* 162 */     for (int i = 0; i < fs.length; i++) {
/*     */       try {
/* 164 */         System.out.println("Loading drums " + fs[i]);
/* 165 */         File f = new File(folder + File.separator + fs[i]);
/* 166 */         int idx = fs[i].lastIndexOf('.');
/* 167 */         String name = fs[i].substring(0, idx);
/* 168 */         Player p = DrumReader.read(name, f);
/* 169 */         players.add(p);
/* 170 */         p.setEngineer(this);
/*     */       }
/* 172 */       catch (Exception ex) {
/* 173 */         System.err.println("Failed to load drum " + fs[i]);
/* 174 */         System.err.print("Error: " + ex.getMessage());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Vector<Player> getPlayers() {
/* 180 */     return players;
/*     */   }
/*     */   
/*     */   public static Player getPlayer(String name) {
/* 184 */     for (Player p : players) {
/* 185 */       if (name.equals(p.id)) {
/* 186 */         return p;
/*     */       }
/*     */     } 
/* 189 */     return null;
/*     */   }
/*     */   
/*     */   public static Player getPlayer(int i) {
/* 193 */     return players.get(i);
/*     */   }
/*     */ 
/*     */   
/*     */   public static ArrayList<MidiDevice.Info> getMidiInputDevices() {
/* 198 */     ArrayList<MidiDevice.Info> inputs = new ArrayList<>();
/*     */     
/* 200 */     MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
/* 201 */     for (int i = 0; i < infos.length; i++) {
/*     */       try {
/* 203 */         MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
/* 204 */         int mt = device.getMaxTransmitters();
/* 205 */         if (mt != 0 && 
/* 206 */           !(device instanceof javax.sound.midi.Sequencer))
/*     */         
/*     */         { 
/* 209 */           Transmitter t = device.getTransmitter();
/* 210 */           inputs.add(infos[i]); } 
/* 211 */       } catch (MidiUnavailableException e) {}
/*     */     } 
/*     */ 
/*     */     
/* 215 */     return inputs;
/*     */   }
/*     */ 
/*     */   
/*     */   public static MidiDevice.Info getMidiDeviceInfo(int index) {
/* 220 */     MidiDevice.Info[] aInfos = MidiSystem.getMidiDeviceInfo();
/* 221 */     if (index < 0 || index >= aInfos.length) {
/* 222 */       return null;
/*     */     }
/* 224 */     return aInfos[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 230 */   int nDeviceIndex = 1;
/*     */   MidiDevice inputDevice;
/*     */   MidiPlayer midiPlayer;
/*     */   
/*     */   public AudioEngineer() {
/* 235 */     open();
/*     */   }
/*     */   
/*     */   public void open() {
/*     */     try {
/* 240 */       this.msyn = MidiSystem.getSynthesizer();
/* 241 */       System.out.println("Opening Synthesizer " + this.msyn.getDeviceInfo());
/* 242 */       this.msyn.open();
/* 243 */       initSynthesizer(this.msyn);
/* 244 */       this.channels = this.msyn.getChannels();
/* 245 */       this.channelCounts = new int[this.channels.length];
/* 246 */     } catch (MidiUnavailableException e) {
/* 247 */       System.err.print(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void close() {
/* 252 */     this.msyn.close();
/*     */   }
/*     */   
/*     */   protected void initSynthesizer(Synthesizer synt) {
/* 256 */     if (loadedSoundbanks == null)
/* 257 */       return;  for (int i = 0; i < loadedSoundbanks.length; i++) {
/* 258 */       if (loadedSoundbanks[i] != null)
/* 259 */         synt.loadAllInstruments(loadedSoundbanks[i]); 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected int getChannel(int track, int mode) {
/* 264 */     return trackChannels[mode][track];
/*     */   }
/*     */   
/*     */   public MidiPlayer getMidiPlayer() {
/* 268 */     if (this.midiPlayer == null) {
/* 269 */       this.midiPlayer = new MidiPlayer(this);
/*     */     }
/* 271 */     return this.midiPlayer;
/*     */   }
/*     */   
/*     */   public String getMidiInstrumentName(int bank, int program) {
/* 275 */     if (bank == 128) {
/* 276 */       if (program > 0) return "Drum Kit " + program; 
/* 277 */       return "Drum Kit";
/*     */     } 
/* 279 */     Patch patch = new Patch(0, program);
/* 280 */     Instrument i = this.msyn.getDefaultSoundbank().getInstrument(patch);
/* 281 */     if (i != null) {
/* 282 */       return i.getName();
/*     */     }
/* 284 */     return "Unknown";
/*     */   }
/*     */   
/*     */   public Instrument[] getLoadedInstruments() {
/* 288 */     return this.msyn.getLoadedInstruments();
/*     */   }
/*     */   
/*     */   boolean isPercussionChannel(int i) {
/* 292 */     for (int j = 0; j < percussionChannels.length; j++) {
/* 293 */       if (i == percussionChannels[j]) return true; 
/*     */     } 
/* 295 */     return false;
/*     */   }
/*     */   
/*     */   public int borrowChannel() {
/* 299 */     for (int i = 0; i < this.channels.length; i++) {
/* 300 */       if (!isPercussionChannel(i) && 
/* 301 */         this.channelCounts[i] <= 0) {
/* 302 */         this.channelCounts[i] = 1;
/* 303 */         return i;
/*     */       } 
/*     */     } 
/* 306 */     return -1;
/*     */   }
/*     */   
/*     */   public int borrowPercussionChannel() {
/* 310 */     for (int j = 0; j < percussionChannels.length; j++) {
/* 311 */       int i = percussionChannels[j];
/* 312 */       if (this.channelCounts[i] <= 0) {
/* 313 */         this.channelCounts[i] = 1;
/* 314 */         return i;
/*     */       } 
/*     */     } 
/* 317 */     return -1;
/*     */   }
/*     */   
/*     */   public void returnChannel(int i) {
/* 321 */     this.channels[i].allNotesOff();
/* 322 */     this.channelCounts[i] = 0;
/* 323 */     System.out.println("Channel " + i + " closed");
/*     */   }
/*     */   
/*     */   public void attach(Synthesizer p) {
/* 327 */     for (int i = 0; i < p.channels.length; i++) {
/* 328 */       int chn = getChannel(p.track, i);
/* 329 */       p.channels[i] = chn;
/* 330 */       if (chn != -1) {
/* 331 */         this.channelCounts[chn] = this.channelCounts[chn] + 1;
/* 332 */         if (this.channelCounts[chn] == 1) {
/* 333 */           Instrument mi = p.instruments[i];
/* 334 */           Patch patch = mi.getPatch();
/* 335 */           this.channels[chn].programChange(patch.getBank(), patch.getProgram());
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public void detach(Synthesizer p) {
/* 341 */     for (int i = 0; i < p.channels.length; i++) {
/* 342 */       int chn = p.channels[i];
/* 343 */       if (chn != -1) {
/* 344 */         this.channelCounts[chn] = this.channelCounts[chn] - 1;
/* 345 */         if (this.channelCounts[chn] == 0)
/* 346 */           this.channels[chn].allNotesOff(); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void resetInputDevice() {
/* 352 */     if (this.inputDevice != null) {
/*     */       try {
/* 354 */         if (this.inputDevice.isOpen()) {
/* 355 */           this.inputDevice.close();
/*     */         }
/* 357 */         this.inputDevice = null;
/*     */       }
/* 359 */       catch (Exception ex) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public void setInputDevice(MidiDevice.Info info, Receiver r) {
/*     */     try {
/* 365 */       MidiDevice dev = MidiSystem.getMidiDevice(info);
/* 366 */       if (this.inputDevice != null && this.inputDevice.equals(dev) && 
/* 367 */         this.inputDevice.isOpen()) {
/* 368 */         this.inputDevice.close();
/*     */       }
/*     */       
/* 371 */       this.inputDevice = dev;
/* 372 */       if (this.inputDevice != null) {
/* 373 */         this.inputDevice.open();
/* 374 */         Transmitter t = this.inputDevice.getTransmitter();
/* 375 */         t.setReceiver(r);
/*     */       }
/*     */     
/* 378 */     } catch (Exception ex) {}
/*     */   }
/*     */   
/*     */   void channelOn(int chn, int forte, long l) {
/* 382 */     int note = 36;
/* 383 */     l &= Long.MAX_VALUE;
/*     */     try {
/* 385 */       while (l != 0L) {
/* 386 */         if ((l & 0x1L) != 0L) {
/* 387 */           this.channels[chn].noteOn(note, forte);
/*     */         }
/* 389 */         note++;
/* 390 */         l >>= 1L;
/*     */       }
/*     */     
/* 393 */     } catch (Exception ex) {}
/*     */   }
/*     */   
/*     */   void channelOff(int chn, long l) {
/* 397 */     if (l == 0L) {
/* 398 */       this.channels[chn].allNotesOff();
/*     */       return;
/*     */     } 
/* 401 */     l &= Long.MAX_VALUE;
/* 402 */     int note = 36;
/*     */     
/*     */     try {
/* 405 */       while (l != 0L) {
/* 406 */         if ((l & 0x1L) != 0L) {
/* 407 */           this.channels[chn].noteOff(note);
/*     */         }
/* 409 */         note++;
/* 410 */         l >>= 1L;
/*     */       }
/*     */     
/* 413 */     } catch (Exception ex) {}
/*     */   }
/*     */   
/*     */   public void midiOn(int chn, byte note, int forte) {
/* 417 */     this.channels[chn].noteOn(note, forte);
/*     */   }
/*     */   
/*     */   public void midiOff(int chn, byte note) {
/* 421 */     if (note == 0) {
/* 422 */       midiOff(chn);
/*     */     } else {
/*     */       
/* 425 */       this.channels[chn].noteOff(note, 0);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void midiOff(int chn) {
/* 430 */     this.channels[chn].allNotesOff();
/*     */   }
/*     */   
/*     */   public void setTempo(int bpm) {}
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\AudioEngineer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */