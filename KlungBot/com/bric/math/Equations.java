/*     */ package com.bric.math;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
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
/*     */ public class Equations
/*     */ {
/*     */   public static boolean VERBOSE_EXCEPTIONS = true;
/*     */   
/*  35 */   private static Comparator<double[]> coefficientComparator = new Comparator<double[]>() {
/*     */       public int compare(double[] d1, double[] d2) {
/*  37 */         int v1 = 0;
/*  38 */         int v2 = 0;
/*     */         int a;
/*  40 */         for (a = 0; a < d1.length; a++) {
/*  41 */           if (d1[a] == 1.0D) {
/*  42 */             v1 = a;
/*  43 */             a = d1.length;
/*     */           } 
/*     */         } 
/*  46 */         for (a = 0; a < d2.length; a++) {
/*  47 */           if (d2[a] == 1.0D) {
/*  48 */             v2 = a;
/*  49 */             a = d2.length;
/*     */           } 
/*     */         } 
/*  52 */         return v1 - v2;
/*     */       }
/*     */     };
/*     */   
/*     */   public static String toString(double[][] d) {
/*  57 */     String s = "";
/*  58 */     for (int a = 0; a < d.length; a++) {
/*  59 */       s = s + toString(d[a]) + "\n";
/*     */     }
/*  61 */     return s.trim();
/*     */   }
/*     */   
/*     */   public static String toString(double[] d) {
/*  65 */     String s = "[";
/*  66 */     for (int a = 0; a < d.length; a++) {
/*  67 */       if (a == 0) {
/*  68 */         s = s + " " + d[a];
/*     */       } else {
/*  70 */         s = s + ", " + d[a];
/*     */       } 
/*     */     } 
/*  73 */     return s + " ]";
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
/*     */   public static void solve(double[][] coefficients) {
/* 107 */     solve(coefficients, true);
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
/*     */   public static void solve(double[][] coefficients, boolean sort) {
/* 148 */     if (coefficients == null) throw new NullPointerException("The coefficients matrix is null."); 
/* 149 */     int size = coefficients.length;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 154 */     boolean[] b = new boolean[coefficients.length];
/*     */ 
/*     */ 
/*     */     
/* 158 */     int[] order = new int[b.length];
/*     */     
/* 160 */     int ctr = 0;
/* 161 */     int row = 0;
/*     */ 
/*     */     
/* 164 */     int errorCounter = 0;
/*     */     
/* 166 */     while (ctr < b.length) {
/* 167 */       if ((coefficients[row]).length != size + 1)
/* 168 */         throw new IllegalArgumentException("The matrix must be N x (N+1) units long.  The matrix provided is " + size + " x " + (coefficients[row]).length + " units."); 
/* 169 */       if (!b[row] && Math.abs(coefficients[row][ctr]) > 1.0E-10D) {
/*     */         
/* 171 */         errorCounter = 0;
/*     */ 
/*     */ 
/*     */         
/* 175 */         double t = 1.0D / coefficients[row][ctr]; int i;
/* 176 */         for (i = 0; i < (coefficients[row]).length; i++) {
/* 177 */           coefficients[row][i] = coefficients[row][i] * t;
/*     */         }
/* 179 */         coefficients[row][ctr] = 1.0D;
/*     */         
/* 181 */         b[row] = true;
/* 182 */         for (i = 0; i < coefficients.length; i++) {
/* 183 */           if (!b[i]) {
/*     */             
/* 185 */             t = coefficients[i][ctr];
/* 186 */             for (int j = 0; j < (coefficients[i]).length; j++) {
/* 187 */               coefficients[i][j] = coefficients[i][j] - coefficients[row][j] * t;
/*     */             }
/*     */           } 
/*     */         } 
/*     */         
/* 192 */         order[ctr++] = row;
/*     */       } 
/*     */       
/* 195 */       errorCounter++;
/* 196 */       row++;
/* 197 */       row %= coefficients.length;
/* 198 */       if (errorCounter > coefficients.length) {
/* 199 */         if (VERBOSE_EXCEPTIONS) {
/* 200 */           throw new IllegalArgumentException("The coefficient matrix cannot be solved.  Either it has infinitely many solutions, or zero solutions:\n" + toString(coefficients));
/*     */         }
/* 202 */         throw new IllegalArgumentException("The coefficient matrix cannot be solved.  Either it has infinitely many solutions, or zero solutions.");
/*     */       } 
/*     */     } 
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
/* 226 */     ctr = 0;
/* 227 */     for (int a = order.length - 2; a >= 0; a--) {
/* 228 */       row = order[a];
/* 229 */       for (int i = (coefficients[row]).length - 2; i > a; i--) {
/* 230 */         double t = coefficients[row][i] * coefficients[order[i]][(coefficients[row]).length - 1];
/* 231 */         coefficients[row][(coefficients[row]).length - 1] = coefficients[row][(coefficients[row]).length - 1] - t;
/* 232 */         coefficients[row][i] = 0.0D;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 237 */     if (sort)
/* 238 */       Arrays.sort(coefficients, (Comparator)coefficientComparator); 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\math\Equations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */