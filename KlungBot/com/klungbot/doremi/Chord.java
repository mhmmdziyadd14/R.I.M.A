/*     */ package com.klungbot.doremi;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Set;
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
/*     */ public class Chord
/*     */ {
/*  17 */   public int base = 13;
/*  18 */   public int range = 16;
/*  19 */   public int root = 13;
/*  20 */   HashMap<String, int[]> maps = (HashMap)new HashMap<>();
/*     */   
/*     */   Scale scale;
/*     */   String[] rootSymbols;
/*  24 */   int[] cC = new int[] { 4, 3 };
/*  25 */   int[] cCm = new int[] { 3, 4 };
/*  26 */   int[] cCdim = new int[] { 3, 3 };
/*  27 */   int[] cCaug = new int[] { 4, 4 };
/*  28 */   int[] cC7 = new int[] { 4, 3, 3 };
/*  29 */   int[] cCm7 = new int[] { 3, 4, 3 };
/*  30 */   int[] cCaug7 = new int[] { 4, 4, 2 };
/*  31 */   int[] cCdim7 = new int[] { 3, 3, 3 };
/*     */   
/*     */   public Chord() {
/*  34 */     this.scale = new Diatonic();
/*  35 */     initDefault();
/*     */   }
/*     */   
/*     */   public Chord(Scale scale) {
/*  39 */     this.scale = scale;
/*  40 */     this.base = 29 - scale.getOctave();
/*  41 */     this.root = 25;
/*  42 */     initDefault();
/*     */   }
/*     */   
/*     */   private void initDefault() {
/*  46 */     this.rootSymbols = Diatonic.keySymbols;
/*  47 */     this.maps.put("M", this.cC);
/*  48 */     this.maps.put("m", this.cCm);
/*  49 */     this.maps.put("dim", this.cCdim);
/*  50 */     this.maps.put("aug", this.cCaug);
/*  51 */     this.maps.put("7", this.cC7);
/*  52 */     this.maps.put("M7", this.cC7);
/*  53 */     this.maps.put("m7", this.cCm7);
/*  54 */     this.maps.put("dim7", this.cCdim7);
/*  55 */     this.maps.put("aug7", this.cCaug7);
/*     */   }
/*     */   
/*     */   public void setBase(int note) {
/*  59 */     this.base = note;
/*     */   }
/*     */   
/*     */   public void setRoot(int note) {
/*  63 */     this.root = note;
/*     */   }
/*     */   
/*     */   public void setBase(String symbol) {
/*  67 */     this.base = this.base;
/*     */   }
/*     */   
/*     */   public void setRoot(String symbol) {
/*  71 */     this.root = this.root;
/*     */   }
/*     */   
/*     */   public void add(String cKey, int[] cIntervals) {
/*  75 */     this.maps.put(cKey, cIntervals);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long chordToBits(String symbol, int key, int mpitch) {
/*  83 */     long bits = chordOf(symbol, key);
/*  84 */     if (mpitch > 0) {
/*  85 */       return bits << mpitch;
/*     */     }
/*  87 */     if (mpitch < 0) {
/*  88 */       return bits >> -mpitch;
/*     */     }
/*  90 */     return bits;
/*     */   }
/*     */   
/*     */   public String[] split(String symbol) {
/*  94 */     String[] s = new String[2];
/*  95 */     long bits = 0L;
/*  96 */     if (symbol.length() <= 1) {
/*  97 */       s[0] = symbol;
/*  98 */       s[1] = this.scale.getDefaultChordType();
/*     */     }
/* 100 */     else if (symbol.charAt(1) != '#') {
/* 101 */       s[0] = symbol.substring(0, 1);
/* 102 */       s[1] = symbol.substring(1);
/*     */     } else {
/*     */       
/* 105 */       s[0] = symbol.substring(0, 2);
/* 106 */       s[1] = symbol.substring(2);
/*     */     } 
/* 108 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long chordOf(String symbol) {
/* 116 */     long bits = 0L;
/* 117 */     String[] ss = split(symbol);
/* 118 */     for (int i = 0; i < this.rootSymbols.length; i++) {
/* 119 */       if (ss[0].equals(this.rootSymbols[i])) {
/* 120 */         int note = this.root + i;
/* 121 */         if (note < this.base) { note += this.scale.getOctave(); }
/* 122 */         else if (note >= this.base + this.scale.getOctave()) { note -= this.scale.getOctave(); }
/* 123 */          bits = 1L << note - 1;
/* 124 */         int[] cm = this.maps.get(ss[1]);
/* 125 */         if (cm != null) {
/* 126 */           for (int j = 0; j < cm.length; j++) {
/* 127 */             note += cm[j];
/* 128 */             if (note >= this.base + this.scale.getOctave())
/* 129 */               note -= this.scale.getOctave(); 
/* 130 */             bits |= 1L << note - 1;
/*     */           } 
/*     */         }
/*     */         break;
/*     */       } 
/*     */     } 
/* 136 */     return bits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long chordOf(String symbol, int key) {
/* 144 */     long bits = 0L;
/* 145 */     String[] ss = split(symbol);
/* 146 */     for (int i = 0; i < this.rootSymbols.length; i++) {
/* 147 */       if (ss[0].equals(this.rootSymbols[i])) {
/* 148 */         int note = this.root + key + i;
/* 149 */         if (note < this.base) { note += this.scale.getOctave(); }
/* 150 */         else if (note >= this.base + this.scale.getOctave()) { note -= this.scale.getOctave(); }
/* 151 */          bits = 1L << note - 1;
/* 152 */         int[] cm = this.maps.get(ss[1]);
/* 153 */         if (cm != null) {
/* 154 */           for (int j = 0; j < cm.length; j++) {
/* 155 */             note += cm[j];
/* 156 */             if (note >= this.base + this.scale.getOctave())
/* 157 */               note -= this.scale.getOctave(); 
/* 158 */             bits |= 1L << note - 1;
/*     */           } 
/*     */         }
/*     */         break;
/*     */       } 
/*     */     } 
/* 164 */     return bits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long chordOf(String symbol, int key, int mpitch) {
/* 173 */     long bits = chordOf(symbol, key);
/* 174 */     if (mpitch > 0) {
/* 175 */       return bits << mpitch;
/*     */     }
/* 177 */     if (mpitch < 0) {
/* 178 */       return bits >> -mpitch;
/*     */     }
/* 180 */     return bits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getChord(String symbol) {
/* 188 */     return getChord(symbol, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getChord(String symbol, int key) {
/* 196 */     String[] ss = split(symbol);
/* 197 */     for (int i = 0; i < this.rootSymbols.length; i++) {
/* 198 */       if (ss[0].equals(this.rootSymbols[i])) {
/* 199 */         int chords[], note = this.root + i + key;
/* 200 */         if (note < this.base) { note += this.scale.getOctave(); }
/* 201 */         else if (note >= this.base + this.scale.getOctave()) { note -= this.scale.getOctave(); }
/*     */         
/* 203 */         int[] cm = this.maps.get(ss[1]);
/* 204 */         if (cm == null) {
/* 205 */           chords = new int[1];
/* 206 */           chords[0] = note;
/*     */         } else {
/*     */           
/* 209 */           chords = new int[1 + cm.length];
/* 210 */           chords[0] = note;
/* 211 */           for (int j = 0; j < cm.length; j++) {
/* 212 */             note += cm[j];
/* 213 */             if (note >= this.base + this.scale.getOctave())
/* 214 */               note -= this.scale.getOctave(); 
/* 215 */             chords[j + 1] = note;
/*     */           } 
/*     */         } 
/* 218 */         return chords;
/*     */       } 
/*     */     } 
/* 221 */     return null;
/*     */   }
/*     */   
/*     */   public String[] getChordSymbols() {
/* 225 */     Set<String> set = this.maps.keySet();
/* 226 */     String[] s = new String[set.size() * this.rootSymbols.length];
/* 227 */     int j = 0;
/* 228 */     for (String k : set) {
/* 229 */       for (int i = 0; i < this.rootSymbols.length; i++) {
/* 230 */         s[j++] = this.rootSymbols[i] + k;
/*     */       }
/*     */     } 
/* 233 */     return s;
/*     */   }
/*     */   
/*     */   public String[] getDoremiChordSymbols() {
/* 237 */     Set<String> set = this.maps.keySet();
/* 238 */     String[] s = new String[set.size() * Scale.doremiChordSymbols.length];
/* 239 */     int j = 0;
/* 240 */     for (String k : set) {
/* 241 */       for (int i = 0; i < Scale.doremiChordSymbols.length; i++) {
/* 242 */         s[j++] = Scale.doremiChordSymbols[i] + k;
/*     */       }
/*     */     } 
/* 245 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int[] chordToIntervals(String name) {
/* 253 */     if (name.isEmpty())
/* 254 */       return this.maps.get(this.scale.getDefaultChordType()); 
/* 255 */     return this.maps.get(name);
/*     */   }
/*     */   
/*     */   public long chordToBits(int root, String ch_name) {
/* 259 */     int[] ch = chordToIntervals(ch_name);
/* 260 */     if (ch == null) return 0L; 
/* 261 */     int note = (root - this.base + 60) % 12;
/* 262 */     long bits = 1L << note + this.base - 1;
/* 263 */     for (int i = 0; i < ch.length; i++) {
/* 264 */       note += ch[i];
/* 265 */       if (i == 1 && note > 12) {
/* 266 */         note %= 12;
/*     */       }
/* 268 */       bits |= 1L << note + this.base - 1;
/*     */     } 
/* 270 */     return bits;
/*     */   }
/*     */ 
/*     */   
/*     */   public long chordToBits(int root, String ch_name, int start, int end) {
/* 275 */     int[] ch = chordToIntervals(ch_name);
/* 276 */     if (ch == null) return 0L; 
/* 277 */     int note = (root - this.base + 60) % 12;
/* 278 */     long bits = 0L;
/* 279 */     start--;
/* 280 */     if (start <= 0)
/* 281 */       bits |= 1L << note + this.base - 1; 
/* 282 */     for (int i = 0; i < ch.length; i++) {
/* 283 */       note += ch[i];
/* 284 */       if (i == 1 && note > 12) {
/* 285 */         note %= 12;
/*     */       }
/* 287 */       if (i >= start)
/* 288 */         bits |= 1L << note + this.base - 1; 
/* 289 */       if (i >= end)
/*     */         break; 
/* 291 */     }  return bits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] chordToArray(int root, String ch_name) {
/* 300 */     int[] ch = chordToIntervals(ch_name);
/* 301 */     if (ch == null) return null; 
/* 302 */     int[] array = new int[ch.length + 1];
/* 303 */     int note = (root - this.base + 60) % 12;
/* 304 */     array[0] = this.base + note;
/* 305 */     for (int i = 0; i < ch.length; i++) {
/* 306 */       note += ch[i];
/* 307 */       if (i == 1 && note > 12) {
/* 308 */         note -= 12;
/*     */       }
/* 310 */       array[i + 1] = note + this.base;
/*     */     } 
/* 312 */     return array;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\Chord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */