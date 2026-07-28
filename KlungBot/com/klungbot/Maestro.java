/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.doremi.Chord;
/*     */ import com.klungbot.doremi.Doremi;
/*     */ import com.klungbot.doremi.Effect;
/*     */ import com.klungbot.doremi.Rythm;
/*     */ import com.klungbot.doremi.Scale;
/*     */ import com.klungbot.util.Options;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Vector;
/*     */ import java.util.concurrent.locks.LockSupport;
/*     */ import javax.sound.midi.MidiMessage;
/*     */ import javax.sound.midi.MidiSystem;
/*     */ import javax.sound.midi.Receiver;
/*     */ import javax.sound.midi.Sequence;
/*     */ import javax.sound.midi.Sequencer;
/*     */ import javax.sound.midi.ShortMessage;
/*     */ import javax.sound.midi.Transmitter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Maestro
/*     */   implements Runnable, Receiver
/*     */ {
/*  30 */   public static int NROBOT = 3;
/*  31 */   public static int NNOTES = 12;
/*  32 */   public static int DELAY_PAUSE = 50;
/*  33 */   public static int DELAY_OFF = 20;
/*  34 */   public static int DELAY_STACATO = 50;
/*     */   
/*  36 */   public static int MAX_MUTES = 8;
/*  37 */   public static int DEFAULT_SPEED = 100;
/*     */   
/*     */   MaestroListener listener;
/*     */   
/*     */   AudioEngineer engineer;
/*     */   RecordingEngineer recordingEngineer;
/*     */   final Vector queue;
/*  44 */   final Object flag = new Object();
/*  45 */   long waitedNotes = 0L;
/*  46 */   long pressedNotes = 0L;
/*     */   
/*     */   Sequencer midiSequencer;
/*     */   
/*     */   final Sequencer sequencer;
/*     */   
/*     */   public int key;
/*     */   
/*     */   public int volume;
/*     */   
/*     */   public int speed;
/*     */   
/*     */   public int tempo;
/*     */   
/*     */   public boolean repeat;
/*     */   
/*     */   private final Player[] tracks;
/*     */   
/*     */   Effect effect;
/*     */   
/*     */   Chord chord;
/*     */   
/*     */   Rythm rhythm;
/*     */   
/*     */   Thread thread;
/*     */   
/*     */   boolean recording;
/*     */   
/*     */   String baseFolder;
/*     */   
/*     */   boolean running;
/*     */   boolean playing;
/*     */   boolean sequencing;
/*     */   boolean canceling;
/*     */   long tick;
/*     */   int tick_periode;
/*     */   MidiPlayer midiPlayer;
/*     */   final int[] playerMap;
/*     */   
/*     */   public Player[] getPlayers() {
/*  86 */     return this.tracks;
/*     */   }
/*     */ 
/*     */   
/*     */   public void initPlayers() {
/*  91 */     for (int i = 0; i < Doremi.trackNames.length; i++) {
/*  92 */       String opt = Options.get("track.player." + Doremi.trackNames[i]);
/*  93 */       if (opt != null) {
/*  94 */         String[] names = opt.split(";");
/*  95 */         for (String name : names) {
/*  96 */           this.tracks[i] = AudioEngineer.getPlayer(name);
/*  97 */           if (this.tracks[i] != null) {
/*  98 */             (this.tracks[i]).volume = 80;
/*  99 */             this.tracks[i].setEngineer(this.engineer);
/* 100 */             this.tracks[i].attach(i);
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 106 */     (this.tracks[0]).volume = 80;
/*     */   }
/*     */ 
/*     */   
/*     */   public AudioEngineer getAudioEngineer() {
/* 111 */     return this.engineer;
/*     */   }
/*     */   
/*     */   public void startPlayers() {
/* 115 */     synchronized (this.tracks) {
/* 116 */       for (Player p : this.tracks) {
/* 117 */         if (p != null) p.start(); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void resetPlayers() {
/* 123 */     synchronized (this.tracks) {
/* 124 */       for (Player p1 : this.tracks) {
/* 125 */         if (p1 != null)
/* 126 */           p1.nextOn = p1.nextStacato = p1.nextOff = 0L; 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public Player getPlayer(int i) {
/* 132 */     synchronized (this.tracks) {
/* 133 */       return this.tracks[i];
/*     */     } 
/*     */   }
/*     */   
/*     */   public Player setPlayer(int track, Player p) {
/*     */     Player p1;
/* 139 */     synchronized (this.sequencer) {
/* 140 */       synchronized (this.tracks) {
/* 141 */         p1 = this.tracks[track];
/* 142 */         if (p1 != null) {
/* 143 */           p1.detach();
/*     */         }
/* 145 */         if (p != null) {
/* 146 */           p.attach(track);
/*     */         }
/* 148 */         this.tracks[track] = p;
/*     */       } 
/*     */     } 
/* 151 */     return p1;
/*     */   }
/*     */   
/*     */   public ArrayList<Player> getActivePlayers() {
/* 155 */     ArrayList<Player> ps = new ArrayList<>();
/* 156 */     for (Player p : this.tracks) {
/* 157 */       if (p != null && 
/* 158 */         !ps.contains(p))
/* 159 */         ps.add(p); 
/*     */     } 
/* 161 */     return ps;
/*     */   }
/*     */   
/*     */   public void delay(long ms) {
/*     */     try {
/* 166 */       Thread.sleep(ms);
/*     */     }
/* 168 */     catch (Exception x) {}
/*     */   }
/*     */   
/*     */   public void delayPause() {
/* 172 */     delay(DELAY_PAUSE);
/*     */   }
/*     */   
/*     */   public void delayOff() {
/* 176 */     delay(DELAY_OFF);
/*     */   }
/*     */   
/*     */   void delayStacato() {
/* 180 */     delay(DELAY_STACATO);
/*     */   }
/*     */ 
/*     */   
/*     */   static long tempoTick(int tempo) {
/* 185 */     return 60000L / tempo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSpeed(int speed) {
/* 194 */     System.out.println("Set speed = " + speed);
/* 195 */     if (speed > 0) {
/* 196 */       this.speed = speed;
/* 197 */       synchronized (this.sequencer) {
/* 198 */         this.tick_periode = 60000 / this.tempo * speed / DEFAULT_SPEED / 24;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public int getSpeed() {
/* 203 */     return this.speed;
/*     */   }
/*     */ 
/*     */   
/*     */   void setTempo(int tempo) {
/* 208 */     if (tempo > 0) {
/* 209 */       this.tempo = tempo;
/* 210 */       this.tick_periode = (int)(60000L / tempo * this.speed / DEFAULT_SPEED / 24L);
/* 211 */       this.engineer.setTempo(tempo);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void start() {
/* 216 */     synchronized (this.sequencer) {
/* 217 */       playOff(0L);
/* 218 */       this.tick = 0L;
/* 219 */       this.sequencer.start();
/* 220 */       this.canceling = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean pause() {
/* 225 */     synchronized (this.sequencer) {
/* 226 */       this.playing = !this.playing;
/* 227 */       if (this.playing) this.sequencer.notify(); 
/*     */     } 
/* 229 */     return this.playing;
/*     */   }
/*     */ 
/*     */   
/*     */   public void finish() {
/* 234 */     this.running = false;
/* 235 */     if (!this.playing) {
/* 236 */       this.sequencer.notify();
/* 237 */       this.playing = true;
/*     */     } 
/*     */     
/* 240 */     synchronized (this.flag) {
/* 241 */       if (this.waitedNotes != 0L) {
/* 242 */         this.waitedNotes = 0L;
/* 243 */         this.flag.notify();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void queue(Object seq) {
/* 249 */     synchronized (this.sequencer) {
/* 250 */       this.canceling = true;
/*     */     } 
/* 252 */     synchronized (this.queue) {
/* 253 */       this.queue.clear();
/* 254 */       this.queue.add(seq);
/* 255 */       this.queue.notify();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void cancel(int i) {
/* 260 */     synchronized (this.queue) {
/* 261 */       this.queue.remove(i);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVolume(int p) {
/* 268 */     this.volume = p;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVolume() {
/* 273 */     return this.volume;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getForte(int f) {
/* 278 */     return (byte)(this.volume * f / 100);
/*     */   }
/*     */   
/*     */   public void setKey(int key) {
/* 282 */     if (key != this.key) {
/* 283 */       System.out.println("Set key =" + key);
/* 284 */       this.key = key;
/*     */     } 
/*     */   }
/*     */   
/*     */   long getBits(long l) {
/* 289 */     if (this.key == 0) return l; 
/* 290 */     if (this.key > 0) return l << this.key; 
/* 291 */     return l >> -this.key;
/*     */   }
/*     */   
/*     */   public void playOff(long l) {
/* 295 */     synchronized (this.tracks) {
/* 296 */       for (Player p : this.tracks) {
/* 297 */         if (p != null) p.playOff(l); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void playOff(int i, long l) {
/* 303 */     synchronized (this.tracks) {
/* 304 */       if (this.tracks[i] != null)
/* 305 */         this.tracks[i].playOff(l); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void playOn(int i, int forte, long l) {
/* 310 */     synchronized (this.tracks) {
/* 311 */       if (this.tracks[i] != null)
/* 312 */         this.tracks[i].playOn(l, getForte(forte)); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void playStacato(int i, int forte, long l) {
/* 317 */     synchronized (this.tracks) {
/* 318 */       if (this.tracks[i] != null)
/* 319 */         this.tracks[i].playOn(l, getForte(forte), 2); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isInteractive() {
/* 324 */     switch (this.effect.interactive) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/* 331 */         return true;
/*     */     } 
/* 333 */     return ((this.waitedNotes & this.pressedNotes) == this.waitedNotes);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOn(byte note, int forte) {
/* 338 */     this.pressedNotes |= Scale.midiToBits(note);
/* 339 */     synchronized (this.flag) {
/* 340 */       if (this.waitedNotes != 0L) {
/* 341 */         if (isInteractive()) {
/* 342 */           this.waitedNotes = 0L;
/* 343 */           this.flag.notify();
/*     */         } 
/*     */         return;
/*     */       } 
/*     */     } 
/* 348 */     synchronized (this.tracks) {
/* 349 */       if (this.tracks[0] == null)
/* 350 */         return;  this.tracks[0].mappedOn(note, getForte(forte));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void midiOn(byte note) {
/* 355 */     midiOn(note, 127);
/*     */   }
/*     */   
/*     */   public void midiOn(byte note, int forte, int channel) {
/* 359 */     channel %= this.tracks.length;
/* 360 */     if (channel == 0) { midiOn(note, forte); }
/*     */     else
/* 362 */     { synchronized (this.tracks) {
/* 363 */         if (this.tracks[channel] == null)
/* 364 */           return;  this.tracks[channel].mappedOn(note, getForte(forte));
/*     */       }  }
/*     */   
/*     */   }
/*     */   
/*     */   public void midiOff(byte note) {
/* 370 */     this.pressedNotes &= (Scale.midiToBits(note) ^ 0xFFFFFFFFFFFFFFFFL) & 0x7FFFFFFFL;
/* 371 */     synchronized (this.tracks) {
/* 372 */       if (this.tracks[0] == null)
/* 373 */         return;  this.tracks[0].mappedOff(note);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void midiOff(byte note, int channel) {
/* 378 */     channel %= this.tracks.length;
/* 379 */     if (channel == 0) {
/* 380 */       midiOff(note);
/*     */       return;
/*     */     } 
/* 383 */     synchronized (this.tracks) {
/* 384 */       if (this.tracks[channel] == null)
/* 385 */         return;  this.tracks[channel].mappedOff(note);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void fireTick(long nextOn) {
/* 391 */     this.listener.changeTick(this.tick, nextOn);
/*     */   }
/*     */   
/*     */   public void fireStarted(Sequence seq) {
/* 395 */     this.listener.started(seq);
/*     */   }
/*     */   
/*     */   public void fireFinished(Sequence seq) {
/* 399 */     this.listener.finished(seq);
/*     */   }
/*     */   
/*     */   public void fireWaiting(long waited) {
/* 403 */     this.listener.waiting(waited);
/*     */   }
/*     */   
/*     */   public Effect getEffect() {
/* 407 */     return this.effect;
/*     */   }
/*     */   
/*     */   public void setInteractive(int i) {
/* 411 */     synchronized (this.flag) {
/* 412 */       this.effect.interactive = i;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void checkInteractive(Track v) {
/* 417 */     synchronized (this.flag) {
/* 418 */       switch (this.effect.interactive) {
/*     */         
/*     */         case 4:
/* 421 */           if (v.channel == 1)
/* 422 */             this.waitedNotes |= getBits(v.current.data); 
/*     */           break;
/*     */         case 5:
/* 425 */           if (v.channel == 2)
/* 426 */             this.waitedNotes |= getBits(v.current.data); 
/*     */           break;
/*     */         case 6:
/* 429 */           if (v.channel == 3)
/* 430 */             this.waitedNotes |= getBits(v.current.data); 
/*     */           break;
/*     */         case 7:
/* 433 */           if (v.channel == 4)
/* 434 */             this.waitedNotes |= getBits(v.current.data); 
/*     */           break;
/*     */         case 1:
/*     */         case 2:
/* 438 */           if (v.voice != 1)
/*     */             break; 
/* 440 */         case 3: if (v.channel != 0)
/* 441 */             break;  this.waitedNotes |= getBits(v.current.data);
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     while (true) {
/* 450 */       playOff(0L);
/*     */       try {
/* 452 */         Thread.sleep(3000L);
/* 453 */         synchronized (this.queue) {
/*     */           
/* 455 */           while (this.queue.isEmpty())
/* 456 */             this.queue.wait(); 
/* 457 */           if (this.queue.isEmpty())
/*     */             break; 
/*     */         } 
/* 460 */         Object obj = this.queue.remove(0);
/* 461 */         this.tick = 0L;
/* 462 */         if (obj instanceof Sequence) {
/* 463 */           Sequence seq = (Sequence)obj;
/*     */ 
/*     */ 
/*     */           
/* 467 */           sequencerLoop(seq);
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 472 */         if (obj instanceof Sequence) {
/* 473 */           midiLoop((Sequence)obj);
/*     */         }
/* 475 */       } catch (InterruptedException e) {}
/*     */     } 
/*     */     
/* 478 */     this.thread = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void sequencerLoop(Sequence sq) {
/* 484 */     synchronized (this.sequencer) {
/* 485 */       this.sequencer.setSequence(sq);
/* 486 */       this.sequencer.start();
/* 487 */       this.tick = 0L;
/* 488 */       this.running = this.playing = true;
/* 489 */       this.canceling = false;
/*     */     } 
/* 491 */     long nextTime = System.currentTimeMillis();
/*     */     
/* 493 */     this.tick = -1L;
/*     */     while (true) {
/*     */       try {
/* 496 */         synchronized (this.sequencer) {
/* 497 */           if (!this.running) {
/* 498 */             this.sequencer.finish();
/* 499 */             fireFinished(sq);
/*     */             return;
/*     */           } 
/* 502 */           if (this.canceling) {
/* 503 */             this.sequencer.cancelled();
/*     */             return;
/*     */           } 
/* 506 */           if (!this.playing) {
/* 507 */             this.sequencer.pause();
/* 508 */             this.sequencer.wait();
/* 509 */             this.sequencer.resume();
/* 510 */             nextTime = System.currentTimeMillis();
/*     */           } 
/*     */         } 
/* 513 */         synchronized (this.flag) {
/* 514 */           if (this.waitedNotes != 0L && this.sequencer.isNextTick(this.tick)) {
/*     */             
/*     */             try {
/* 517 */               fireWaiting(this.waitedNotes);
/* 518 */               this.flag.wait();
/* 519 */               nextTime = System.currentTimeMillis();
/* 520 */               this.waitedNotes = 0L;
/*     */             
/*     */             }
/* 523 */             catch (Exception ex) {}
/*     */           }
/*     */         } 
/* 526 */         synchronized (this.sequencer) {
/* 527 */           Device.startSendAll(nextTime);
/* 528 */           this.tick = this.sequencer.play(this.tick);
/* 529 */           this.tick++;
/* 530 */           this.running = this.sequencer.isPlaying();
/* 531 */           nextTime += this.tick_periode;
/* 532 */           LockSupport.parkUntil(nextTime);
/*     */         } 
/* 534 */         fireTick(this.tick);
/* 535 */       } catch (InterruptedException e) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void midiLoop(Sequence sequence) {
/*     */     try {
/* 543 */       this.midiSequencer = MidiSystem.getSequencer(false);
/* 544 */       Transmitter t = this.midiSequencer.getTransmitter();
/* 545 */       t.setReceiver(this);
/* 546 */       this.running = this.playing = true;
/* 547 */       this.canceling = false;
/* 548 */       System.out.println("Playing MIDI ...");
/* 549 */       this.midiPlayer.open();
/* 550 */       this.midiSequencer.open();
/* 551 */       this.midiSequencer.setSequence(sequence);
/* 552 */       this.midiSequencer.start();
/* 553 */       while (this.midiSequencer.isRunning()) {
/*     */         try {
/* 555 */           Thread.sleep(1000L);
/* 556 */         } catch (InterruptedException e) {}
/* 557 */         if (this.canceling) {
/* 558 */           System.out.println("Cancelled");
/*     */           break;
/*     */         } 
/* 561 */         if (!this.running) {
/* 562 */           System.out.println("Stopped");
/*     */           break;
/*     */         } 
/*     */       } 
/* 566 */       this.midiSequencer.stop();
/* 567 */       this.midiSequencer.close();
/* 568 */       this.midiPlayer.close();
/* 569 */     } catch (Exception ex) {
/* 570 */       System.out.println(ex.toString());
/*     */     } 
/* 572 */     finish();
/* 573 */     if (!this.canceling) fireFinished(null); 
/* 574 */     this.running = false;
/* 575 */     System.out.println("MIDI loop return");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(MidiMessage message, long timeStamp) {
/* 581 */     if (message instanceof ShortMessage)
/*     */     {
/*     */       
/* 584 */       decodeMessage((ShortMessage)message, timeStamp);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Maestro(String bfolder, MaestroListener l) {
/* 595 */     this.playerMap = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0 }; this.baseFolder = bfolder; this.listener = l; Device.loadDevices(l); AudioEngineer.loadSoundbanks(bfolder); this.engineer = new AudioEngineer(); this.engineer.initPlayers(bfolder); this.midiPlayer = this.engineer.getMidiPlayer(); this.tracks = new Player[10]; this.effect = new Effect(MAX_MUTES, 10); this.volume = 100; this.key = 0; this.speed = DEFAULT_SPEED; setTempo(Doremi.DEFAULT_TEMPO);
/*     */     this.queue = new Vector();
/*     */     this.sequencer = new Sequencer(this);
/*     */     this.thread = new Thread(this);
/*     */     this.thread.setPriority(10);
/* 600 */     this.thread.start(); } public int[] getMidiMap() { return this.playerMap; }
/*     */ 
/*     */   
/*     */   public int getMidiMap(int ch) {
/* 604 */     return this.playerMap[ch];
/*     */   }
/*     */   
/*     */   public int getMidiMapLength() {
/* 608 */     return this.playerMap.length;
/*     */   }
/*     */   
/*     */   public void setMidiMap() {
/* 612 */     for (int i = 0; i < this.playerMap.length; i++) {
/* 613 */       this.playerMap[i] = 0;
/*     */     }
/* 615 */     this.playerMap[9] = 4;
/*     */   }
/*     */   
/*     */   public void setMidiMap(MidiInfo info) {
/* 619 */     for (int row = 0; row < info.infos.size(); row++) {
/* 620 */       setMidiMap(info, row);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean setMidiMap(MidiInfo info, int row) {
/* 625 */     boolean result = true;
/* 626 */     MidiInfo.ChannelInfo ci = info.infos.get(row);
/* 627 */     int old_map = this.playerMap[ci.chnum];
/* 628 */     int new_map = ci.map;
/*     */     
/* 630 */     if (old_map >= 0) {
/* 631 */       Player p = this.tracks[old_map];
/* 632 */       if (p != null) p.midiOff();
/*     */     
/* 634 */     } else if (old_map == -1) {
/* 635 */       this.midiPlayer.closeChannel(ci.chnum);
/*     */     } 
/*     */ 
/*     */     
/* 639 */     if (new_map == -1 && 
/* 640 */       !this.midiPlayer.openChannel(ci.chnum)) {
/* 641 */       result = false;
/* 642 */       new_map = -2;
/* 643 */       ci.map = -2;
/*     */     } 
/*     */     
/* 646 */     synchronized (this.playerMap) {
/* 647 */       this.playerMap[ci.chnum] = new_map;
/*     */     } 
/* 649 */     return true;
/*     */   }
/*     */   
/*     */   public void decodeMessage(ShortMessage message, long timeStamp) {
/* 653 */     int nChannel = message.getChannel();
/*     */     
/* 655 */     synchronized (this.playerMap) {
/* 656 */       int np = this.playerMap[nChannel];
/* 657 */       if (np >= 0) {
/* 658 */         synchronized (this.tracks) {
/* 659 */           if (this.tracks[np] == null)
/* 660 */             return;  if (this.effect.isTrackMuted(np))
/*     */             return; 
/* 662 */           switch (message.getCommand()) {
/*     */             case 128:
/* 664 */               this.tracks[np].midiOff((byte)message.getData1());
/*     */               break;
/*     */             case 144:
/* 667 */               this.tracks[np].midiOn((byte)message.getData1(), 
/* 668 */                   getForte(message.getData2()));
/*     */               break;
/*     */           } 
/*     */         } 
/*     */       }
/* 673 */       if (np == -1) {
/* 674 */         this.midiPlayer.send(message, timeStamp);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */   
/*     */   public void enableRecording() {
/* 684 */     synchronized (this.sequencer) {
/* 685 */       this.recording = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void disableRecording() {
/* 690 */     synchronized (this.sequencer) {
/* 691 */       this.recording = false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Maestro.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */