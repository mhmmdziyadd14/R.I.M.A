/*     */ package com.klungbot.doremi;
/*     */ 
/*     */ import java.util.HashMap;
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
/*     */ public class Pattern
/*     */ {
/*     */   public static final int DRUM_MID = 25;
/*     */   public static final int O = 25;
/*     */   public static final int P = 26;
/*     */   public static final int Q = 27;
/*     */   public static final int R = 28;
/*     */   public static final int S = 29;
/*     */   public static final int T = 30;
/*     */   public static final int U = 31;
/*     */   public static final int V = 32;
/*     */   public static final int W = 33;
/*     */   public static final int X = 34;
/*     */   public static final int Y = 35;
/*     */   public static final int Z = 36;
/*     */   public static final int DRUM_MAX = 12;
/*     */   public static final String drumSymbols = "opqrstuvwxyz";
/*     */   String name;
/*     */   HashMap<String, int[][]> table;
/*     */   
/*     */   public static int noteOf(char ch) {
/*  51 */     int i = "opqrstuvwxyz".indexOf(ch);
/*  52 */     if (i >= 0) {
/*  53 */       return 25 + i;
/*     */     }
/*  55 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int noteOf(String symbol) {
/*  63 */     return noteOf(symbol.charAt(0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long bitsOf(String symbol) {
/*  72 */     int n = noteOf(symbol);
/*  73 */     if (n == 0) return 0L; 
/*  74 */     return 1L << n - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long bitsOf(int note) {
/*  82 */     return 1L << note - 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Pattern(String name) {
/*  89 */     this.name = name;
/*  90 */     this.table = (HashMap)new HashMap<>();
/*     */   }
/*     */   
/*     */   public Pattern() {
/*  94 */     this("default");
/*     */   }
/*     */   
/*     */   protected void initTable() {
/*  98 */     int[][] x = { { 24, 34 } };
/*     */ 
/*     */     
/* 101 */     int[][] y = { { 24, 35 } };
/*     */ 
/*     */ 
/*     */     
/* 105 */     int[][] z = { { 24, 36 } };
/*     */ 
/*     */ 
/*     */     
/* 109 */     int[][] XYY = { { 24, 34 }, { 24, 35 }, { 24, 35 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     int[][] WXY = { { 24, 33 }, { 24, 34 }, { 24, 35 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     int[][] XYXY = { { 24, 34 }, { 24, 35 }, { 24, 34 }, { 24, 35 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 128 */     int[][] XYXZ = { { 24, 34 }, { 24, 35 }, { 24, 34 }, { 24, 35, 36 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     int[][] XZXZ = { { 24, 34 }, { 24, 35, 36 }, { 24, 34 }, { 24, 35, 36 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 142 */     int[][] XYxxY = { { 24, 34 }, { 24, 35 }, { 12, 34 }, { 12, 34 }, { 24, 35 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     int[][] XYxxZ = { { 24, 34 }, { 24, 35 }, { 12, 34 }, { 12, 34 }, { 24, 35, 36 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 158 */     int[][] XYxx_Z = { { 24, 34 }, { 24, 35 }, { 6, 34 }, { 18, 34 }, { 24, 35, 36 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 166 */     int[][] XxYxxY = { { 6, 34 }, { 18, 34 }, { 24, 35 }, { 6, 34 }, { 18, 34 }, { 24, 35 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 175 */     int[][] XxYxxZ = { { 6, 34 }, { 18, 33 }, { 24, 35, 36 }, { 6, 34 }, { 18, 33 }, { 24, 35, 36 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 185 */     int[][] XyxXY = { { 24, 34 }, { 12, 35 }, { 12, 33 }, { 24, 34 }, { 24, 35 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 193 */     int[][] XyxXZ = { { 24, 34 }, { 12, 35 }, { 12, 33 }, { 24, 34 }, { 24, 35, 36 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 201 */     int[][] XzxXZ = { { 24, 34 }, { 12, 35, 36 }, { 12, 33 }, { 24, 34 }, { 24, 35, 36 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 210 */     int[][] XyxXyx = { { 24, 34 }, { 12, 35 }, { 12, 33 }, { 24, 34 }, { 12, 35 }, { 12, 33 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 219 */     int[][] XzxXzx = { { 24, 34 }, { 12, 35, 36 }, { 12, 33 }, { 24, 34 }, { 12, 35, 36 }, { 12, 34 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 228 */     int[][] xxyXxyx = { { 12, 34 }, { 12, 33 }, { 12, 35 }, { 24, 34 }, { 12, 33 }, { 12, 35 }, { 12, 34 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 238 */     this.table.put("X", x);
/* 239 */     this.table.put("Y", y);
/* 240 */     this.table.put("Z", z);
/* 241 */     this.table.put("XYY", XYY);
/* 242 */     this.table.put("WXY", WXY);
/* 243 */     this.table.put("XYXY", XYXY);
/* 244 */     this.table.put("XYxxY", XYxxY);
/* 245 */     this.table.put("XxYxxY", XxYxxY);
/* 246 */     this.table.put("XYXZ", XYXZ);
/* 247 */     this.table.put("XZXZ", XZXZ);
/* 248 */     this.table.put("XYxxZ", XYxxZ);
/* 249 */     this.table.put("XxYxxZ", XxYxxZ);
/* 250 */     this.table.put("XyxXY", XyxXY);
/* 251 */     this.table.put("XyxXZ", XyxXZ);
/* 252 */     this.table.put("XzxXZ", XzxXZ);
/* 253 */     this.table.put("XyxXyx", XyxXyx);
/* 254 */     this.table.put("XzxXzx", XzxXzx);
/* 255 */     this.table.put("xxyXxyx", xxyXxyx);
/*     */   }
/*     */   
/*     */   public void addPattern(String pattern, int[][] sequence) {
/* 259 */     this.table.put(this.name, sequence);
/*     */   }
/*     */   
/*     */   public int getLength(int[][] pattern) {
/* 263 */     int sum = 0;
/* 264 */     for (int i = 0; i < pattern.length; i++) {
/* 265 */       sum += pattern[i][0];
/*     */     }
/* 267 */     return sum;
/*     */   }
/*     */   
/*     */   public int[][] getPattern(String symbol) {
/* 271 */     return this.table.get(symbol);
/*     */   }
/*     */   
/*     */   public static Pattern createPattern() throws Exception {
/* 275 */     Pattern d = new Pattern();
/* 276 */     d.initTable();
/* 277 */     return d;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\Pattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */