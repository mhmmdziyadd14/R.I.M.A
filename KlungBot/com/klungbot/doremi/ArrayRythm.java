/*     */ package com.klungbot.doremi;
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
/*     */ public class ArrayRythm
/*     */   extends Rythm
/*     */ {
/*  17 */   protected int meter = 1;
/*  18 */   protected String[] drums = new String[] { "X", "Y", "Z" };
/*     */   
/*     */   public static final int INDEX_MASK = 15;
/*     */   
/*     */   public static final int OCTAVE_MASK = 240;
/*     */   
/*     */   public static final int ALL_MASK = 256;
/*     */   
/*     */   public static final int TYPE_MASK = 57344;
/*     */   
/*     */   public static final int OCTAVE_SHIFT = 4;
/*     */   public static final int TYPE_SHIFT = 10;
/*     */   public static final int OCTAVE_BASE = 8;
/*     */   public static final int OCTAVE_0 = 128;
/*     */   public static final int OCTAVE_1 = 112;
/*     */   public static final int OCTAVE_2 = 96;
/*     */   public static final int ALL_NOTES = 256;
/*  35 */   static final int[][] rXyyXY = new int[][] { { 24, 129 }, { 12, 386 }, { 12, 386 }, { 24, 113 }, { 24, 386 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  42 */   static final int[][] rXYXY = new int[][] { { 24, 129 }, { 24, 386 }, { 24, 113 }, { 24, 386 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   static final int[][] royoyoyoy = new int[][] { { 12, 0 }, { 12, 385 }, { 12, 0 }, { 12, 385 }, { 12, 0 }, { 12, 385 }, { 12, 0 }, { 12, 385 } };
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
/*  59 */   static final int[][] rxyzyxyzy = new int[][] { { 12, 129 }, { 12, 386 }, { 12, 115 }, { 12, 386 }, { 12, 129 }, { 12, 386 }, { 12, 115 }, { 12, 386 } };
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
/*  70 */   static final int[][] rXyxXY = new int[][] { { 24, 129 }, { 12, 386 }, { 12, 129 }, { 24, 129 }, { 24, 386 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  78 */   static final int[][] rxzyzyzzz = new int[][] { { 12, 113 }, { 12, 387 }, { 12, 130 }, { 12, 387 }, { 12, 130 }, { 12, 387 }, { 12, 115 }, { 12, 387 } };
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
/*  89 */   static final int[][] rxxxxxxxx = new int[][] { { 12, 385 }, { 12, 385 }, { 12, 385 }, { 12, 385 }, { 12, 385 }, { 12, 385 }, { 12, 385 }, { 12, 385 } };
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
/* 100 */   static final int[][] rxyzy = new int[][] { { 18, 129 }, { 6, 130 }, { 12, 387 }, { 12, 130 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 107 */   static final int[][] rXXX = new int[][] { { 24, 129 }, { 24, 385 }, { 24, 385 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 113 */   static final int[][] rXYY = new int[][] { { 24, 129 }, { 24, 386 }, { 24, 386 } };
/*     */ 
/*     */   
/*     */   int[][] current;
/*     */ 
/*     */   
/*     */   public static void put(String name, int[][] array) {
/* 120 */     put(name, new ArrayRythm(array));
/*     */   }
/*     */   
/*     */   public static void initDefault() {
/* 124 */     put("double", rxxxxxxxx);
/* 125 */     put("waltz2", rXYY);
/* 126 */     put("waltz", rXXX);
/* 127 */     put("tango", rxyzy);
/* 128 */     put("keroncong", rxzyzyzzz);
/* 129 */     put("jazz", rXyyXY);
/* 130 */     put("jazz2", rXyxXY);
/* 131 */     put("mars", rXyyXY);
/* 132 */     put("swing", rXYXY);
/* 133 */     put("calung", rxyzyxyzy);
/* 134 */     put("double_swing", rxyzyxyzy);
/* 135 */     put("swing$ref", rXyyXY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayRythm(int[][] array) {
/* 141 */     this.current = array;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLength() {
/* 146 */     int sum = 0;
/* 147 */     for (int i = 0; i < this.current.length; i++) {
/* 148 */       sum += this.current[i][0];
/*     */     }
/* 150 */     return sum;
/*     */   }
/*     */ 
/*     */   
/*     */   private int[][] getRythm(int[] chord, int[][] rythm) {
/* 155 */     int[] temp = new int[chord.length];
/*     */     
/* 157 */     int[][] r = new int[rythm.length][];
/* 158 */     for (int i = 0; i < rythm.length; i++) {
/* 159 */       int len = 0;
/* 160 */       for (int j = 1; j < (rythm[i]).length; j++) {
/* 161 */         int index = rythm[i][j] & 0xF;
/* 162 */         if (index != 0) {
/* 163 */           int octave = (rythm[i][j] & 0xF0) >> 4;
/* 164 */           octave = (octave - 8) * 12;
/* 165 */           index--;
/* 166 */           if ((rythm[i][j] & 0x100) == 0) {
/* 167 */             if (index < chord.length) {
/* 168 */               temp[len++] = chord[index] + octave;
/*     */             }
/*     */           }
/*     */           else {
/*     */             
/* 173 */             for (; index < chord.length; index++) {
/* 174 */               temp[len++] = chord[index] + octave;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/* 179 */       System.out.println();
/* 180 */       r[i] = new int[len + 2];
/* 181 */       r[i][0] = rythm[i][0];
/* 182 */       r[i][1] = ((rythm[i][1] & 0xE000) >> 10) + 1;
/* 183 */       System.arraycopy(temp, 0, r[i], 2, len);
/*     */     } 
/* 185 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[][] getRythm(int[] chord) {
/* 190 */     return getRythm(chord, this.current);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\ArrayRythm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */