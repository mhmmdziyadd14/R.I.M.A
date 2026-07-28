/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.doremi.Doremi;
/*     */ import com.klungbot.doremi.Rythm;
/*     */ import com.klungbot.doremi.Scale;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
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
/*     */ public class Sequence
/*     */ {
/*     */   int index;
/*  55 */   public ArrayList<String> titles = new ArrayList<>(); public ArrayList<String> composers; public ArrayList<String> arrangers; public String editor;
/*  56 */   public int key = 0; public String origin; public String discography; public String genre;
/*  57 */   public int meter = 4;
/*  58 */   public int meter_beat = 4;
/*  59 */   public int tempo = Doremi.DEFAULT_TEMPO;
/*  60 */   public int forte = Doremi.MAX_FORTE;
/*  61 */   public int max_tick = 0; public Scale scale;
/*     */   public Rythm rythm;
/*  63 */   public ArrayList<Track> tracks = new ArrayList<>();
/*  64 */   public HashMap<String, Label> labels = new LinkedHashMap<>();
/*  65 */   int labelNum = 1000;
/*     */ 
/*     */   
/*     */   public void addTitle(String s) {
/*  69 */     this.titles.add(s);
/*     */   }
/*     */   
/*     */   public void addComposer(String s) {
/*  73 */     if (this.composers == null) {
/*  74 */       this.composers = new ArrayList<>();
/*     */     }
/*  76 */     this.composers.add(s);
/*     */   }
/*     */   
/*     */   public void addArranger(String s) {
/*  80 */     if (this.arrangers == null) {
/*  81 */       this.arrangers = new ArrayList<>();
/*     */     }
/*  83 */     this.arrangers.add(s);
/*     */   }
/*     */   
/*     */   public void addEditor(String s) {
/*  87 */     this.editor = s;
/*     */   }
/*     */   
/*     */   public Track addTrack(int track, int voice) {
/*  91 */     Track t = new Track(this, track, voice);
/*  92 */     this.tracks.add(t);
/*  93 */     return t;
/*     */   }
/*     */   
/*     */   Track getTrack(int channel, int voice) {
/*  97 */     for (Track t : this.tracks) {
/*  98 */       if (t.voice == voice && t.channel == channel) {
/*  99 */         return t;
/*     */       }
/*     */     } 
/* 102 */     return null;
/*     */   }
/*     */   
/*     */   public void setRythm(Rythm r) {
/* 106 */     this.rythm = r;
/* 107 */     for (Track t : this.tracks) {
/* 108 */       t.setRythm(r);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setRythm(Rythm r, int channel) {
/* 113 */     boolean f = false;
/* 114 */     for (Track t : this.tracks) {
/* 115 */       if (t.channel != channel) {
/*     */         continue;
/*     */       }
/* 118 */       f = true;
/* 119 */       t.setRythm(r);
/*     */     } 
/* 121 */     if (!f) {
/* 122 */       Track t = addTrack(channel, 1);
/* 123 */       t.setRythm(r);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Label addLabel(String label, long tick) throws Exception {
/* 128 */     if (this.labels.containsKey(label)) {
/* 129 */       throw new Exception("Duplicate label " + label);
/*     */     }
/* 131 */     this.labelNum++;
/* 132 */     Label l = new Label(tick, this.labelNum);
/* 133 */     this.labels.put(label, l);
/* 134 */     return l;
/*     */   }
/*     */   
/*     */   public Label addAlias(String label, Label old) throws Exception {
/* 138 */     if (this.labels.containsKey(label)) {
/* 139 */       throw new Exception("Duplicate label " + label);
/*     */     }
/* 141 */     Label l = new Label(old.tick, old.num);
/* 142 */     this.labels.put(label, l);
/* 143 */     return l;
/*     */   }
/*     */   
/*     */   public Label getLabel(String label) {
/* 147 */     return this.labels.get(label);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 152 */     return this.titles.get(0);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Sequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */