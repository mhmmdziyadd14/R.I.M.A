/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.doremi.Scale;
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
/*     */ public class Event
/*     */ {
/*     */   public static final int DEFAULT = 0;
/*     */   public static final int NOTE = 1;
/*     */   public static final int STACATO = 2;
/*     */   public static final int MUTED = 3;
/*     */   public static final int BAR = 0;
/*     */   public static final int KEY = 4096;
/*     */   public static final int FORTE = 8192;
/*     */   public static final int TEMPO = 12288;
/*     */   public static final int METRO = 16384;
/*     */   public static final int LABEL = 20480;
/*     */   public static final int GOBACK = 24576;
/*     */   public static final int GOTO = 28672;
/*     */   public static final int COMMAND_MASK = 61440;
/*     */   public static final int L0 = 0;
/*     */   public static final int L_GRACE = 1;
/*     */   public static final int L1 = 24;
/*     */   public static final int L_8 = 3;
/*     */   public static final int L_4 = 6;
/*     */   public static final int L_3 = 8;
/*     */   public static final int L_2 = 12;
/*     */   public static final int L2 = 48;
/*     */   public static final int L3 = 72;
/*     */   public static final int L4 = 96;
/*     */   public static final int L5 = 120;
/*     */   public static final int L6 = 144;
/*     */   public static final int L7 = 168;
/*     */   public static final int L8 = 192;
/*     */   public int accent;
/*     */   public long tick;
/*  47 */   public long data = 0L;
/*     */   Event next;
/*     */   Event prev;
/*     */   
/*     */   public Event(long tick) {
/*  52 */     this.tick = tick;
/*  53 */     this.accent = 1;
/*  54 */     this.next = this.prev = null;
/*     */   }
/*     */   
/*     */   public Event(long tick, int type) {
/*  58 */     this.accent = type;
/*  59 */     this.tick = tick;
/*     */   }
/*     */   
/*     */   public Event(long tick, Event v) {
/*  63 */     this.tick = tick;
/*  64 */     this.accent = v.accent;
/*  65 */     this.data = v.data;
/*     */   }
/*     */   
/*     */   public Event(long tick, int type, long data) {
/*  69 */     this.accent = type;
/*  70 */     this.tick = tick;
/*  71 */     this.data = data;
/*     */   }
/*     */   
/*     */   public Event(long tick, int type, int note) {
/*  75 */     this.accent = type;
/*  76 */     this.tick = tick;
/*  77 */     setOn(note);
/*     */   }
/*     */   
/*     */   public void setType(int t) {
/*  81 */     this.accent = t;
/*     */   }
/*     */   
/*     */   public void setData(long d) {
/*  85 */     this.data = d;
/*     */   }
/*     */   
/*     */   public long getData() {
/*  89 */     return this.data;
/*     */   }
/*     */   
/*     */   public void setData(int d1, int d2) {
/*  93 */     this.data = (d1 + 4096 | d2 << 16);
/*     */   }
/*     */   
/*     */   public int getData1() {
/*  97 */     int d = (int)(this.data & 0xFFFFL);
/*  98 */     return d - 4096;
/*     */   }
/*     */   
/*     */   public int getData2() {
/* 102 */     return (int)(this.data >> 16L) & 0xFFFF;
/*     */   }
/*     */   
/*     */   public void setOn(int n) {
/* 106 */     if (n > 0 && n <= 60)
/* 107 */       this.data |= Scale.bitsOf(n); 
/*     */   }
/*     */   
/*     */   public void setOff(int n) {
/* 111 */     if (n >= 0 && n <= 60)
/* 112 */       this.data &= Scale.bitsOf(n) ^ 0xFFFFFFFFFFFFFFFFL; 
/*     */   }
/*     */   
/*     */   Event next() {
/* 116 */     return this.next;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Event.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */