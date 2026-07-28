/*     */ package com.klungbot.doremi;
/*     */ 
/*     */ import com.klungbot.ParserException;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
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
/*     */ public class ListRythm
/*     */   extends Rythm
/*     */ {
/*  27 */   public final int INFINITY = Integer.MAX_VALUE;
/*     */   
/*     */   ArrayList<Item> items;
/*     */   
/*     */   public class Item
/*     */   {
/*     */     public int length;
/*     */     public int first;
/*     */     
/*     */     public Item(int length, int start, int end, int octave, int shift) {
/*  37 */       this.length = length;
/*  38 */       this.first = start;
/*  39 */       this.last = end;
/*  40 */       this.octave = octave;
/*  41 */       this.shift = shift;
/*     */     }
/*     */     public int last;
/*     */     public int octave;
/*     */     public int shift; }
/*     */   
/*     */   public ListRythm() {
/*  48 */     this.items = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public Item add(int length, int start, int end, int octave) {
/*  52 */     Item it = new Item(length, start, end, octave, 0);
/*  53 */     this.items.add(it);
/*  54 */     return it;
/*     */   }
/*     */   
/*     */   public Item add(int length, int start, int end, int octave, int shift) {
/*  58 */     Item it = new Item(length, start, end, octave, shift);
/*  59 */     this.items.add(it);
/*  60 */     return it;
/*     */   }
/*     */   
/*     */   public int getLength() {
/*  64 */     int sum = 0;
/*  65 */     for (Item i : this.items) {
/*  66 */       sum += i.length;
/*     */     }
/*  68 */     return sum;
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
/*     */   public int[][] getRythm(int[] chord) {
/*  80 */     int num = this.items.size();
/*  81 */     int[][] r = new int[num][];
/*  82 */     for (int i = 0; i < num; i++) {
/*  83 */       int len; Item it = this.items.get(i);
/*  84 */       if (it.first >= chord.length) {
/*  85 */         len = 0;
/*     */       }
/*  87 */       else if (it.last >= chord.length) {
/*  88 */         len = chord.length - it.first;
/*     */       } else {
/*     */         
/*  91 */         len = it.last - it.first;
/*     */       } 
/*  93 */       r[i] = new int[len + 1];
/*  94 */       r[i][0] = it.length;
/*  95 */       int index = it.first;
/*  96 */       int octave = it.octave * 12;
/*  97 */       for (int j = 0; i < len; j++) {
/*  98 */         r[i][j] = chord[index++] + octave + it.shift;
/*     */       }
/*     */     } 
/* 101 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Rythm read(BufferedReader reader) throws ParserException {
/*     */     ListRythm rythm;
/* 109 */     int lnum = 0;
/*     */     
/* 111 */     long tick = 0L; try {
/*     */       String line;
/*     */       do {
/* 114 */         line = reader.readLine();
/* 115 */         lnum++;
/* 116 */         if (line == null) {
/* 117 */           throw new ParserException("Empty text");
/*     */         }
/* 119 */       } while (!line.startsWith("T:"));
/*     */       
/* 121 */       rythm = new ListRythm();
/*     */       do {
/* 123 */         line = line.trim();
/* 124 */         switch (line.charAt(0)) {
/*     */         
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 136 */         line = reader.readLine();
/* 137 */         lnum++;
/* 138 */       } while (line != null);
/*     */     }
/* 140 */     catch (ParserException ex) {
/* 141 */       rythm = null;
/* 142 */       ex.setLocation(lnum, 0);
/* 143 */       throw ex;
/*     */     }
/* 145 */     catch (IOException ex) {
/* 146 */       throw new ParserException(ex.getMessage(), lnum, 0);
/*     */     } 
/* 148 */     return rythm;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Rythm read(File fname) throws FileNotFoundException, ParserException {
/* 155 */     return read(new BufferedReader(new FileReader(fname)));
/*     */   }
/*     */   
/*     */   public static void loadAll(String folder) {}
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\ListRythm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */