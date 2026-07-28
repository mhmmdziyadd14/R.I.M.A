/*     */ package com.klungbot;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Instrument
/*     */   extends Player
/*     */ {
/*     */   Device device;
/*     */   byte channel;
/*     */   
/*     */   public Instrument(String id, Device dev) {
/*  18 */     super(id);
/*  19 */     this.device = dev;
/*  20 */     this.channel = 0;
/*     */   }
/*     */   
/*     */   public Instrument(String id, Device dev, byte channel) {
/*  24 */     super(id);
/*  25 */     this.device = dev;
/*  26 */     this.channel = channel;
/*     */   }
/*     */   
/*     */   public Device getDevice() {
/*  30 */     return this.device;
/*     */   }
/*     */   
/*     */   public int getChannel() {
/*  34 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean attach(int track) {
/*  39 */     if (super.attach(track)) {
/*  40 */       return true;
/*     */     }
/*  42 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean detach() {
/*  47 */     if (super.detach()) {
/*  48 */       return true;
/*     */     }
/*  50 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isAttached() {
/*  54 */     return this.device.isConnected();
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOn(long notes, int forte) {
/*  59 */     Device.Event v = this.device.acquire();
/*  60 */     if (v == null) {
/*     */       return;
/*     */     }
/*  63 */     v.len = 2;
/*  64 */     v.data[0] = -12;
/*  65 */     v.data[1] = (byte)(0x10 | this.channel);
/*  66 */     notes &= Long.MAX_VALUE;
/*  67 */     byte note = 36;
/*  68 */     while (notes != 0L) {
/*  69 */       if ((notes & 0x1L) != 0L) {
/*  70 */         v.data[v.len++] = note;
/*     */       }
/*  72 */       note = (byte)(note + 1);
/*  73 */       notes >>= 1L;
/*     */     } 
/*  75 */     v.data[v.len++] = -9;
/*  76 */     this.device.send(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOff(long notes) {
/*  81 */     Device.Event v = this.device.acquire();
/*  82 */     if (v == null)
/*  83 */       return;  if (notes == 0L) {
/*  84 */       midiOff();
/*     */       return;
/*     */     } 
/*  87 */     v.len = 2;
/*  88 */     v.data[0] = -12;
/*  89 */     v.data[1] = (byte)(0x0 | this.channel);
/*  90 */     notes &= Long.MAX_VALUE;
/*  91 */     byte note = 36;
/*  92 */     while (notes != 0L) {
/*  93 */       if ((notes & 0x1L) != 0L) {
/*  94 */         v.data[v.len++] = note;
/*     */       }
/*  96 */       note = (byte)(note + 1);
/*  97 */       notes >>= 1L;
/*     */     } 
/*  99 */     v.data[v.len++] = -9;
/* 100 */     this.device.send(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOn(long notes, int forte, int accent) {
/* 105 */     Device.Event v = this.device.acquire();
/* 106 */     if (v == null)
/* 107 */       return;  v.len = 2;
/* 108 */     v.data[0] = -12;
/* 109 */     if (accent == 2) {
/* 110 */       v.data[1] = (byte)(0x20 | this.channel);
/*     */     }
/*     */     else {
/*     */       
/* 114 */       v.data[1] = (byte)(0x10 | this.channel);
/*     */     } 
/*     */     
/* 117 */     notes &= Long.MAX_VALUE;
/* 118 */     byte note = 36;
/* 119 */     while (notes != 0L) {
/* 120 */       if ((notes & 0x1L) != 0L) {
/* 121 */         v.data[v.len++] = note;
/*     */       }
/* 123 */       note = (byte)(note + 1);
/* 124 */       notes >>= 1L;
/*     */     } 
/* 126 */     v.data[v.len++] = -9;
/* 127 */     this.device.send(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void playOff(long notes, int accent) {
/* 132 */     playOff(notes);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOn(byte note, int forte) {
/* 137 */     Device.Event v = this.device.acquire();
/* 138 */     if (v == null) {
/*     */       return;
/*     */     }
/* 141 */     v.len = 3;
/* 142 */     v.data[0] = (byte)(0x90 | this.channel);
/* 143 */     v.data[1] = note;
/* 144 */     v.data[2] = (byte)forte;
/* 145 */     this.device.send(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff(byte note) {
/* 150 */     Device.Event v = this.device.acquire();
/* 151 */     if (v == null) {
/*     */       return;
/*     */     }
/* 154 */     v.len = 3;
/* 155 */     v.data[0] = (byte)(0x80 | this.channel);
/* 156 */     v.data[1] = note;
/* 157 */     v.data[2] = 0;
/* 158 */     this.device.send(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff() {
/* 163 */     Device.Event v = this.device.acquire();
/* 164 */     if (v == null)
/* 165 */       return;  v.len = 3;
/* 166 */     v.data[0] = (byte)(0x80 | this.channel);
/* 167 */     v.data[1] = 0;
/* 168 */     v.data[2] = 0;
/* 169 */     this.device.send(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLatency() {
/* 174 */     return this.device.getLatency();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLatency(int ms) {
/* 179 */     this.device.setLatency(ms);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Instrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */