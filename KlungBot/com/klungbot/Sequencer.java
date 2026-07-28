/*     */ package com.klungbot;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Stack;
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
/*     */ public class Sequencer
/*     */ {
/*     */   Maestro maestro;
/*     */   Sequence sequence;
/*     */   long last;
/*     */   long current;
/*     */   int tempo;
/*     */   int forte;
/*     */   boolean keyChanged;
/*     */   ArrayList<Player> players;
/*     */   Stack<Event> stack;
/*     */   HashMap<Integer, Event> labels;
/*     */   long tempo_tick;
/*     */   long tempo_length;
/*     */   int tempo_delta;
/*     */   int tempo_start;
/*     */   long forte_tick;
/*     */   long forte_length;
/*     */   int forte_delta;
/*     */   int forte_start;
/*     */   long nextTick;
/*     */   long nextPauseTick;
/*     */   long nextSustainTick;
/*     */   long nextJumpTick;
/*     */   long jumpTick;
/*     */   int phase;
/*     */   boolean stillPlaying;
/*     */   
/*     */   public Sequencer(Maestro m) {
/* 131 */     this.nextTick = 0L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 137 */     this.stillPlaying = true; this.maestro = m; this.players = new ArrayList<>(); this.stack = new Stack<>(); this.labels = new HashMap<>();
/*     */   }
/*     */   void setSequence(Sequence seq) { this.sequence = seq; } void start() { this.maestro.fireStarted(this.sequence); for (Track v : this.sequence.tracks)
/* 140 */       v.start();  this.maestro.startPlayers(); this.last = this.current = 0L; this.keyChanged = false; this.players.clear(); this.labels.clear(); this.stack.clear(); this.forte = this.sequence.forte; this.tempo = this.sequence.tempo; this.maestro.setTempo(this.tempo * this.sequence.meter_beat / 4); this.nextTick = -1L; this.tempo_tick = 0L; this.tempo_length = 0L; this.tempo_delta = 0; this.tempo_start = this.tempo; this.forte_tick = 0L; this.forte_length = 0L; this.forte_delta = 0; this.forte_start = this.forte; prepare(0L); this.phase = 2; } long doCommand(long tick, Track v) { long start_tick = tick;
/*     */     
/* 142 */     v.lastNote = 0L;
/* 143 */     while (v.current.tick <= tick) {
/* 144 */       int id; switch (v.current.accent & 0xF000) {
/*     */         case 4096:
/* 146 */           this.keyChanged = true;
/*     */           break;
/*     */         case 8192:
/* 149 */           this.forte_tick = tick;
/* 150 */           this.forte_length = (v.current.getData2() * 24);
/* 151 */           this.forte_start = this.forte;
/* 152 */           this.forte_delta = v.current.getData1();
/*     */           break;
/*     */         case 12288:
/* 155 */           this.tempo_tick = tick;
/* 156 */           this.tempo_length = (v.current.getData2() * 24);
/* 157 */           this.tempo_start = this.tempo;
/* 158 */           this.tempo_delta = v.current.getData1();
/* 159 */           System.out.println("Set Tempo " + this.tempo_delta);
/*     */           break;
/*     */         case 20480:
/* 162 */           id = (int)v.current.data & 0xFFFF;
/* 163 */           if (!this.labels.containsKey(Integer.valueOf(id))) {
/* 164 */             this.labels.put(Integer.valueOf(id), v.current);
/* 165 */             System.out.println("LABEL " + tick + " " + v.current.data);
/*     */           } 
/*     */           break;
/*     */         case 28672:
/* 169 */           this.stack.push(v.current);
/*     */           
/* 171 */           id = (int)v.current.data & 0xFFFF;
/*     */           
/* 173 */           v.current = this.labels.get(Integer.valueOf(id));
/* 174 */           tick = v.current.tick;
/* 175 */           v.next();
/*     */           
/* 177 */           return tick - start_tick;
/*     */         case 24576:
/* 179 */           if (this.stack.isEmpty())
/*     */             break;  try {
/* 181 */             System.out.println("GOBACK " + tick + " " + v.current.data);
/* 182 */             v.current = this.stack.pop();
/* 183 */             tick = v.current.tick;
/* 184 */             System.out.println("CMD " + tick + " " + v.current.data);
/*     */           
/*     */           }
/* 187 */           catch (Exception ex) {}
/*     */           break;
/*     */       } 
/*     */       
/* 191 */       if (v.next() == null)
/*     */         break; 
/* 193 */     }  return tick - start_tick; }
/*     */   long getEffectBits(long l) { if (this.maestro.effect.multinote) l = this.sequence.scale.polynotesOf(l);  return this.sequence.scale.transpose(l, this.maestro.key); }
/*     */   Player doMix(long tick, Track v) { long noteOn = 0L; long stacatoOn = 0L; boolean muted = (this.maestro.effect.isTrackMuted(v.channel) || (v.channel == 0 && this.maestro.effect.isMuted(v.voice))); do { if (muted)
/*     */         continue;  switch (v.current.accent) { case 1: this.maestro.checkInteractive(v); if (v.channel == 0 && v.voice == 1) { noteOn |= getEffectBits(v.current.data); break; }  noteOn |= this.maestro.getBits(v.current.data); break;case 2: this.maestro.checkInteractive(v); if (v.channel == 0 && v.voice == 1) { stacatoOn |= getEffectBits(v.current.data); break; }  stacatoOn |= this.maestro.getBits(v.current.data); break; }  } while (v.next() != null && tick >= v.current.tick); Player p = this.maestro.getPlayer(v.channel); if (p != null) { p.nextOn |= noteOn; p.nextStacato |= stacatoOn; p.nextOff |= v.lastNote; if (!this.players.contains(p))
/* 197 */         this.players.add(p);  v.lastNote = noteOn; }  return p; } private void prepareVoices(long tick) { this.nextTick = tick;
/* 198 */     this.nextPauseTick = this.nextTick - 2L;
/* 199 */     this.nextSustainTick = this.nextTick + 2L;
/* 200 */     this.maestro.resetPlayers();
/* 201 */     this.stillPlaying = false;
/* 202 */     for (int i = 1; i < this.sequence.tracks.size(); i++) {
/* 203 */       Track v = this.sequence.tracks.get(i);
/* 204 */       if (v.current != null) {
/* 205 */         this.stillPlaying = true;
/* 206 */         if (v.current.tick <= this.nextTick)
/* 207 */           doMix(this.nextTick, v); 
/*     */       } 
/*     */     }  }
/*     */ 
/*     */   
/*     */   private void prepareForward(Track v, long tick) {
/* 213 */     this.jumpTick = tick - 2L;
/* 214 */     System.out.println("FORWARD  TO " + tick);
/* 215 */     for (int i = 1; i < this.sequence.tracks.size(); i++) {
/* 216 */       Track t = this.sequence.tracks.get(i);
/* 217 */       t.nextUntil(tick);
/* 218 */       System.out.println("V" + t.voice + ":" + t.current.tick);
/*     */     } 
/* 220 */     prepareVoices(tick);
/*     */   }
/*     */   
/*     */   private void prepareBackward(Track v, long tick) {
/* 224 */     this.jumpTick = tick - 2L;
/* 225 */     System.out.println("BACKWARD TO " + tick);
/* 226 */     for (int i = 1; i < this.sequence.tracks.size(); i++) {
/* 227 */       Track t = this.sequence.tracks.get(i);
/* 228 */       t.prevUntil(tick);
/* 229 */       System.out.println("V" + t.voice + ":" + t.current.tick);
/*     */     } 
/* 231 */     prepareVoices(tick);
/*     */   }
/*     */   
/*     */   private boolean prepare(long tick) {
/* 235 */     long deltaTick = 0L;
/* 236 */     long minTick = Long.MAX_VALUE;
/* 237 */     for (Track t : this.sequence.tracks) {
/* 238 */       if (t.current != null && 
/* 239 */         t.current.tick < minTick)
/* 240 */         minTick = t.current.tick; 
/*     */     } 
/* 242 */     this.nextJumpTick = minTick - 2L;
/* 243 */     Track v = this.sequence.tracks.get(0);
/* 244 */     if (v.current != null) {
/* 245 */       deltaTick = doCommand(minTick, v);
/* 246 */       if (deltaTick > 0L) {
/* 247 */         prepareForward(v, minTick + deltaTick);
/* 248 */         return true;
/*     */       } 
/* 250 */       if (deltaTick < 0L) {
/* 251 */         prepareBackward(v, minTick + deltaTick);
/* 252 */         return true;
/*     */       } 
/*     */     } 
/* 255 */     prepareVoices(minTick);
/* 256 */     return false;
/*     */   }
/*     */   
/*     */   private void playPause(long tick) {
/* 260 */     if (this.players.isEmpty())
/* 261 */       return;  for (Player p : this.players) {
/* 262 */       if (this.keyChanged) {
/* 263 */         p.playOff(0L);
/* 264 */         p.nextOff = 0L;
/*     */       } 
/* 266 */       p.nextPause = p.currentOn & p.nextOn | p.currentOn & p.nextStacato;
/* 267 */       p.nextOff = p.nextOff & (p.nextPause ^ 0xFFFFFFFFFFFFFFFFL) & (p.nextOn ^ 0xFFFFFFFFFFFFFFFFL);
/* 268 */       if (p.nextPause != 0L) {
/* 269 */         p.playOff(p.nextPause);
/*     */       }
/*     */     } 
/* 272 */     if (!this.maestro.effect.sustain) {
/* 273 */       for (Player p : this.players) {
/* 274 */         p.playOff(p.nextOff);
/*     */       }
/*     */     }
/* 277 */     if (this.tempo_delta != 0) {
/*     */       
/* 279 */       long dtick = tick - this.tempo_tick;
/* 280 */       if (this.tempo_length == 0L || dtick >= this.tempo_length) {
/* 281 */         this.tempo = this.tempo_start + this.tempo_delta;
/* 282 */         this.tempo_delta = 0;
/*     */       } else {
/*     */         
/* 285 */         int d = (int)(dtick * this.tempo_delta / this.tempo_length);
/* 286 */         this.tempo = this.tempo_start + d;
/*     */       } 
/* 288 */       if (this.tempo > 300) { this.tempo = 300; }
/* 289 */       else if (this.tempo < 30) { this.tempo = 30; }
/* 290 */        this.maestro.setTempo(this.tempo * this.sequence.meter_beat / 4);
/*     */     } 
/*     */     
/* 293 */     if (this.forte_length > 0L) {
/* 294 */       long dtick = tick - this.forte_tick;
/* 295 */       if (dtick >= this.forte_length) {
/* 296 */         this.forte = this.forte_start + this.forte_delta;
/* 297 */         this.forte_length = 0L;
/*     */       } else {
/*     */         
/* 300 */         int d = (int)(dtick * this.forte_delta / this.forte_length);
/* 301 */         this.forte = this.forte_start + d;
/*     */       } 
/* 303 */       if (this.forte > 100) { this.forte = 100; }
/* 304 */       else if (this.forte < 0) { this.forte = 0; }
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   private void playOn(long tick) {
/* 310 */     if (this.players.isEmpty());
/* 311 */     for (Player p : this.players) {
/* 312 */       p.playOn(p.nextOn, this.maestro.getForte(this.forte));
/* 313 */       if (p.nextStacato != 0L) {
/* 314 */         p.playOn(p.nextStacato, this.maestro.getForte(this.forte), 2);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void playOff(long tick) {
/* 320 */     if (this.players.isEmpty())
/* 321 */       return;  for (Player p : this.players) {
/* 322 */       if (this.maestro.effect.sustain && p.nextOff != 0L) {
/* 323 */         p.playOff(p.nextOff);
/*     */       }
/* 325 */       if (p.nextStacato != 0L) {
/* 326 */         p.playOff(p.nextStacato, 2);
/*     */       }
/* 328 */       p.currentOn = p.nextOn;
/*     */     } 
/* 330 */     this.players.clear();
/* 331 */     this.keyChanged = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long play(long tick) {
/* 336 */     switch (this.phase) {
/*     */       case 0:
/* 338 */         this.phase = prepare(tick) ? 11 : 1;
/*     */         break;
/*     */       case 1:
/* 341 */         if (tick >= this.nextPauseTick) {
/* 342 */           playPause(tick);
/* 343 */           this.phase = 2;
/*     */         } 
/*     */         break;
/*     */       case 2:
/* 347 */         if (tick >= this.nextTick) {
/* 348 */           playOn(tick);
/* 349 */           this.phase = 3;
/*     */         } 
/*     */         break;
/*     */       case 3:
/* 353 */         if (tick >= this.nextSustainTick) {
/* 354 */           playOff(tick);
/* 355 */           this.phase = 0;
/*     */         } 
/*     */         break;
/*     */       case 11:
/* 359 */         if (tick >= this.nextJumpTick) {
/* 360 */           allSoundOff();
/* 361 */           this.phase = 2;
/* 362 */           return this.jumpTick - 1L;
/*     */         }  break;
/*     */     } 
/* 365 */     return tick;
/*     */   }
/*     */   
/*     */   public boolean isNextTick(long tick) {
/* 369 */     return (this.nextTick <= tick);
/*     */   }
/*     */   
/*     */   public boolean isPlaying() {
/* 373 */     return this.stillPlaying;
/*     */   }
/*     */   
/*     */   void allSoundOff() {
/* 377 */     for (Track t : this.sequence.tracks) {
/* 378 */       if (t.voice == 1) {
/* 379 */         Player p = this.maestro.getPlayer(t.channel);
/* 380 */         if (p != null)
/* 381 */           p.midiOff(); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   public void pause() {
/* 386 */     allSoundOff();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resume() {}
/*     */ 
/*     */   
/*     */   public void finish() {
/* 394 */     allSoundOff();
/* 395 */     this.maestro.finish();
/* 396 */     this.maestro.fireFinished(this.sequence);
/*     */   }
/*     */   
/*     */   public void cancelled() {
/* 400 */     allSoundOff();
/* 401 */     this.maestro.finish();
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Sequencer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */