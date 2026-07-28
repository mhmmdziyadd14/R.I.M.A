/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.doremi.Doremi;
/*     */ import com.klungbot.doremi.Pattern;
/*     */ import com.klungbot.doremi.Rythm;
/*     */ import com.klungbot.doremi.Scale;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Track
/*     */ {
/*     */   Sequence seq;
/*     */   int channel;
/*     */   int voice;
/*     */   Event first;
/*     */   Event last;
/*  23 */   final int NOTE = 1;
/*  24 */   final int CHORD = 2;
/*  25 */   final int RYTHM = 3;
/*  26 */   final int DRUM = 4; long lastNote; Event current;
/*     */   int barLength;
/*     */   long barTick;
/*     */   long lastTick;
/*     */   Rythm rythm;
/*     */   ArrayList<Symbol> currentBar;
/*     */   ArrayList<Symbol> lastBar;
/*     */   
/*     */   class Symbol { int symbol;
/*     */     int note;
/*     */     String chord;
/*     */     int accent;
/*     */     
/*     */     Symbol(int symbol, int tick, int accent, int octave, int note, String chord, int start, int stop) {
/*  40 */       this.symbol = symbol;
/*  41 */       this.tick = tick;
/*  42 */       this.accent = accent;
/*  43 */       this.octave = octave;
/*  44 */       this.note = note;
/*  45 */       this.chord = chord;
/*  46 */       this.start = start;
/*  47 */       this.stop = stop;
/*     */     }
/*     */     int octave; int tick; int start; int stop;
/*     */     Symbol(int symbol, int tick, int accent, int octave, int note) {
/*  51 */       this(symbol, tick, accent, octave, note, null, -1, -1);
/*     */     }
/*     */     
/*     */     Symbol(int symbol, int tick, int accent, int octave, int note, String chord) {
/*  55 */       this(symbol, tick, accent, octave, note, chord, -1, -1);
/*     */     }
/*     */     
/*     */     Symbol(int symbol, int tick, int accent, String name) {
/*  59 */       this(symbol, tick, accent, -1, -1, name, -1, -1);
/*     */     } }
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
/*     */   public Track(Sequence seq, int channel, int voice) {
/*  76 */     this.seq = seq;
/*  77 */     this.channel = channel;
/*  78 */     this.voice = voice;
/*  79 */     this.lastNote = 0L;
/*  80 */     this.barLength = seq.meter * 24;
/*  81 */     this.currentBar = new ArrayList<>();
/*  82 */     this.lastBar = new ArrayList<>();
/*  83 */     this.barTick = -1L;
/*  84 */     this.rythm = seq.rythm;
/*     */   }
/*     */   
/*     */   public void resetBar() {
/*  88 */     this.barTick = -1L;
/*  89 */     this.lastBar.clear();
/*     */   }
/*     */   
/*     */   public int getChannel() {
/*  93 */     return this.channel;
/*     */   }
/*     */   
/*     */   public int getVoice() {
/*  97 */     return this.voice;
/*     */   }
/*     */   
/*     */   public Event getLast() {
/* 101 */     return this.last;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 105 */     return (this.first == null);
/*     */   }
/*     */   
/*     */   public Event removeLast() {
/* 109 */     Event v = this.last;
/* 110 */     if (v != null) {
/* 111 */       v.next = null;
/* 112 */       this.last = v.prev;
/* 113 */       v.prev = null;
/* 114 */       if (this.last != null) {
/* 115 */         this.last.next = null;
/*     */       } else {
/*     */         
/* 118 */         this.first = null;
/*     */       } 
/*     */     } 
/* 121 */     return v;
/*     */   }
/*     */   
/*     */   public void add(Event event) {
/* 125 */     if (this.last == null) {
/* 126 */       this.first = this.last = event;
/* 127 */       event.next = event.prev = null;
/*     */       return;
/*     */     } 
/* 130 */     if (this.last.tick <= event.tick) {
/* 131 */       this.last.next = event;
/* 132 */       event.next = null;
/* 133 */       event.prev = this.last;
/* 134 */       this.last = event;
/*     */       return;
/*     */     } 
/* 137 */     Event before = this.last.prev;
/* 138 */     while (before != null) {
/* 139 */       if (before.tick > event.tick) {
/* 140 */         before = before.prev;
/*     */         continue;
/*     */       } 
/* 143 */       event.next = before.next;
/* 144 */       event.prev = before;
/* 145 */       before.next.prev = event;
/* 146 */       before.next = event;
/*     */       
/*     */       return;
/*     */     } 
/* 150 */     event.prev = null;
/* 151 */     event.next = this.first;
/* 152 */     this.first.prev = event;
/* 153 */     this.first = event;
/*     */   }
/*     */   
/*     */   public Event start() {
/* 157 */     this.current = this.first;
/* 158 */     return this.current;
/*     */   }
/*     */   
/*     */   public Event next() {
/* 162 */     if (this.current != null) {
/* 163 */       this.current = this.current.next;
/*     */     }
/* 165 */     return this.current;
/*     */   }
/*     */   
/*     */   public Event prev() {
/* 169 */     if (this.current != null) {
/* 170 */       this.current = this.current.prev;
/*     */     }
/* 172 */     return this.current;
/*     */   }
/*     */   
/*     */   public void nextUntil(long tick) {
/* 176 */     while (this.current != null) {
/* 177 */       if (this.current.tick >= tick)
/* 178 */         return;  this.current = this.current.next;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void prevUntil(long tick) {
/* 183 */     while (this.current.prev != null) {
/* 184 */       if (this.current.prev.tick < tick)
/* 185 */         return;  this.current = this.current.prev;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setCurrent(Event v) {
/* 190 */     this.current = v;
/*     */   }
/*     */   
/*     */   public void setRythm(Rythm r) {
/* 194 */     this.rythm = r;
/*     */   }
/*     */   
/*     */   public void addLabel(long tick, Label label) {
/* 198 */     Event v = new Event(tick, 20480);
/* 199 */     v.data = label.id();
/* 200 */     add(v);
/*     */   }
/*     */   
/*     */   public void addEndLabel(long tick, Label label) {
/* 204 */     Event v = new Event(tick, 24576);
/* 205 */     v.data = label.id();
/* 206 */     add(v);
/*     */   }
/*     */   
/*     */   public void addRepeat(long tick, Label label) {
/* 210 */     Event v = new Event(tick, 28672);
/* 211 */     v.data = label.countId();
/* 212 */     add(v);
/*     */   }
/*     */   
/*     */   public void addBar(long tick) throws ParserException {
/* 216 */     if (tick == this.barTick) {
/*     */       return;
/*     */     }
/* 219 */     if (this.barTick >= 0L && this.barLength > 0) {
/* 220 */       long nextTick = this.barTick + this.barLength;
/* 221 */       if (nextTick > tick) {
/* 222 */         throw new ParserException("Bar is too short");
/*     */       }
/* 224 */       if (tick < this.barLength) {
/* 225 */         throw new ParserException("Bar is too long");
/*     */       }
/*     */     } 
/* 228 */     this.barTick = tick;
/* 229 */     ArrayList<Symbol> temp = this.lastBar;
/* 230 */     this.lastBar = this.currentBar;
/* 231 */     this.currentBar = temp;
/* 232 */     this.currentBar.clear();
/*     */   }
/*     */   
/*     */   public String getName() {
/* 236 */     return Doremi.getTrackName(this.channel, this.voice);
/*     */   }
/*     */   
/*     */   void checkTick(long tick) throws ParserException {
/* 240 */     if (this.current != null && 
/* 241 */       this.current.tick > tick) {
/* 242 */       throw new ParserException("Track " + getName() + " is already defined");
/*     */     }
/* 244 */     if (this.barTick >= 0L && this.barLength > 0) {
/* 245 */       long nextTick = this.barTick + this.barLength;
/* 246 */       if (tick > nextTick) {
/* 247 */         throw new ParserException("Bar is too long");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addNote(long tick, int accent, int octave, int note) throws ParserException {
/* 255 */     checkTick(tick);
/* 256 */     int note1 = note + octave * this.seq.scale.getOctave();
/* 257 */     this.current = new Event(tick, accent, note1);
/* 258 */     add(this.current);
/* 259 */     if (this.barTick >= 0L) {
/* 260 */       int delta = (int)(tick - this.barTick);
/* 261 */       this.currentBar.add(new Symbol(1, delta, accent, octave, note));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDrum(long tick, int accent, String drum) throws ParserException {
/* 268 */     checkTick(tick);
/* 269 */     long data = Pattern.bitsOf(drum);
/* 270 */     add(this.current = new Event(tick, accent, data));
/* 271 */     if (this.barTick >= 0L) {
/* 272 */       int delta = (int)(tick - this.barTick);
/* 273 */       this.currentBar.add(new Symbol(4, delta, accent, drum));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addChord(long tick, int type, int octave, int note, String chord) throws ParserException {
/* 279 */     checkTick(tick);
/* 280 */     long data = this.seq.scale.getChord().chordToBits(note, chord);
/* 281 */     data = this.seq.scale.transposeOctave(data, octave);
/* 282 */     add(this.current = new Event(tick, type, data));
/* 283 */     if (this.barTick >= 0L) {
/* 284 */       int delta = (int)(tick - this.barTick);
/* 285 */       this.currentBar.add(new Symbol(2, delta, type, octave, note, chord));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addChord(long tick, int type, int octave, int note, String chord, int start, int stop) throws ParserException {
/* 291 */     checkTick(tick);
/* 292 */     long data = this.seq.scale.getChord().chordToBits(note, chord, start, stop);
/* 293 */     data = this.seq.scale.transposeOctave(data, octave);
/* 294 */     add(this.current = new Event(tick, type, data));
/* 295 */     if (this.barTick >= 0L) {
/* 296 */       int delta = (int)(tick - this.barTick);
/* 297 */       this.currentBar.add(new Symbol(2, delta, type, octave, note, chord, start, stop));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addRythm(long tick, int length, int type, int octave, int note, String chord) throws ParserException {
/* 302 */     if (this.rythm == null) {
/* 303 */       this.rythm = this.seq.rythm;
/* 304 */       if (this.rythm == null) {
/* 305 */         throw new ParserException("Rythm has not defined.");
/*     */       }
/*     */     } 
/* 308 */     int max = this.rythm.getLength();
/* 309 */     if (length > max) {
/* 310 */       throw new ParserException("Rythm is not long enough.");
/*     */     }
/* 312 */     int[] ch = this.seq.scale.getChord().chordToArray(note, chord);
/* 313 */     int[][] r = this.rythm.getRythm(ch);
/* 314 */     int begin = (int)(tick - this.barTick);
/* 315 */     int end = begin + length;
/* 316 */     int rtick = 0;
/* 317 */     for (int i = 0; i < r.length && 
/* 318 */       rtick < end; i++) {
/* 319 */       if (this.barLength == 0) {
/* 320 */         throw new ParserException("Cannot use a rythm in a free length bar");
/*     */       }
/* 322 */       if (rtick > this.barLength) {
/* 323 */         throw new ParserException("Rythm exceeds the bar length");
/*     */       }
/* 325 */       if (rtick >= begin) {
/* 326 */         long data = 0L;
/* 327 */         for (int j = 2; j < (r[i]).length; j++) {
/* 328 */           if (r[i][j] > 0) {
/* 329 */             data |= Scale.bitsOf(r[i][j]);
/*     */           }
/*     */         } 
/* 332 */         data = this.seq.scale.transposeOctave(data, octave);
/* 333 */         add(this.current = new Event(this.barTick + rtick, type, data));
/*     */       } 
/* 335 */       rtick += r[i][0];
/*     */     } 
/* 337 */     if (this.barTick >= 0L) {
/* 338 */       int delta = (int)(tick - this.barTick);
/* 339 */       this.currentBar.add(new Symbol(3, delta, type, octave, note, chord));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addIdem(long tick, int length, int type, int octave, int note, String chord) throws ParserException {
/* 346 */     if (this.lastBar.isEmpty()) {
/* 347 */       throw new ParserException("No pattern to be repeated");
/*     */     }
/* 349 */     int begin = (int)(tick - this.barTick);
/* 350 */     int end = begin + length;
/* 351 */     int dnote = 0;
/* 352 */     int doctave = 0;
/* 353 */     for (Symbol s : this.lastBar) {
/* 354 */       if (s.note != 0) {
/* 355 */         dnote = note - s.note;
/* 356 */         doctave = octave - s.octave;
/*     */         break;
/*     */       } 
/*     */     } 
/* 360 */     for (Symbol s : this.lastBar) {
/* 361 */       if (s.tick < begin)
/* 362 */         continue;  if (s.tick > end)
/* 363 */         break;  note = (s.note == 0) ? 0 : (s.note + dnote);
/* 364 */       int accent = (type == 0) ? s.accent : type;
/* 365 */       switch (s.symbol) {
/*     */         case 1:
/* 367 */           addNote(this.barTick + s.tick, accent, s.octave + doctave, note);
/*     */         case 2:
/* 369 */           addChord(this.barTick + s.tick, accent, s.octave + doctave, note, chord);
/*     */         case 3:
/* 371 */           addRythm(this.barTick + s.tick, length, accent, s.octave + doctave, note, chord);
/*     */         case 4:
/* 373 */           addDrum(this.barTick + s.tick, accent, s.chord);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addIdem(long tick, int length, int type) throws ParserException {
/* 379 */     if (this.lastBar.isEmpty()) {
/* 380 */       throw new ParserException("No pattern to be repeated");
/*     */     }
/* 382 */     int begin = (int)(tick - this.barTick);
/* 383 */     int end = begin + length;
/* 384 */     for (Symbol s : this.lastBar) {
/* 385 */       if (s.tick < begin)
/* 386 */         continue;  if (s.tick > end)
/* 387 */         break;  switch (s.symbol) {
/*     */         case 1:
/* 389 */           addNote(this.barTick + s.tick, type, s.octave, s.note);
/*     */         case 2:
/* 391 */           addChord(this.barTick + s.tick, type, s.octave, s.note, s.chord);
/*     */         case 3:
/* 393 */           addRythm(this.barTick + s.tick, length, type, s.octave, s.note, s.chord);
/*     */         case 4:
/* 395 */           addDrum(this.barTick + s.tick, s.accent, s.chord);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addIdem(long tick, int length) throws ParserException {
/* 401 */     if (this.lastBar.isEmpty()) {
/* 402 */       throw new ParserException("No pattern to be repeated");
/*     */     }
/* 404 */     int begin = (int)(tick - this.barTick);
/* 405 */     int end = begin + length;
/* 406 */     for (Symbol s : this.lastBar) {
/* 407 */       if (s.tick < begin)
/* 408 */         continue;  if (s.tick > end)
/* 409 */         break;  switch (s.symbol) {
/*     */         case 1:
/* 411 */           addNote(this.barTick + s.tick, s.accent, s.octave, s.note);
/*     */         case 2:
/* 413 */           addChord(this.barTick + s.tick, s.accent, s.octave, s.note, s.chord);
/*     */         case 3:
/* 415 */           addRythm(this.barTick + s.tick, length, s.accent, s.octave, s.note, s.chord);
/*     */         case 4:
/* 417 */           addDrum(this.barTick + s.tick, s.accent, s.chord);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Track.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */