/*     */ package com.bric.math.function;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
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
/*     */ public class PiecewiseFunction
/*     */   implements Function
/*     */ {
/*     */   Function[] functions;
/*     */   double[] upperBounds;
/*     */   
/*     */   public static PiecewiseFunction create(Function f, Function fDeriv, double min, double max, int functions) {
/*  45 */     Function[] array = new Function[functions];
/*  46 */     double[] bounds = new double[functions - 1];
/*  47 */     for (int a = 0; a < functions; a++) {
/*  48 */       double minX = min + (max - min) * a / functions;
/*  49 */       double maxX = min + (max - min) * (a + 1) / functions;
/*     */       
/*  51 */       double minY = f.evaluate(minX);
/*  52 */       double maxY = f.evaluate(maxX);
/*     */       
/*  54 */       if (Double.isNaN(minY) || Double.isNaN(maxY)) {
/*  55 */         throw new IllegalArgumentException("f(" + minX + ") = " + minY + ", f(" + maxX + ") = " + maxY);
/*     */       }
/*     */       
/*  58 */       double minYDeriv = fDeriv.evaluate(minX);
/*  59 */       double maxYDeriv = fDeriv.evaluate(maxX);
/*     */       
/*  61 */       if (Double.isNaN(minYDeriv) || Double.isNaN(maxYDeriv) || 
/*  62 */         Double.isInfinite(minYDeriv) || Double.isInfinite(maxYDeriv)) {
/*  63 */         array[a] = f;
/*     */       } else {
/*     */         try {
/*  66 */           array[a] = PolynomialFunction.createFit(new double[] { minX, maxX }, new double[] { minY, maxY }, new double[] { minYDeriv, maxYDeriv });
/*     */         
/*     */         }
/*  69 */         catch (RuntimeException e) {
/*  70 */           System.err.println("a = " + a);
/*  71 */           System.err.println("mixX = " + minX);
/*  72 */           System.err.println("maxX = " + maxX);
/*  73 */           System.err.println("minY = " + minY);
/*  74 */           System.err.println("maxY = " + maxY);
/*  75 */           System.err.println("minYDeriv = " + minYDeriv);
/*  76 */           System.err.println("maxYDeriv = " + maxYDeriv);
/*  77 */           throw e;
/*     */         } 
/*     */       } 
/*  80 */       if (a != 0)
/*  81 */         bounds[a - 1] = minX; 
/*     */     } 
/*  83 */     return new PiecewiseFunction(array, bounds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   private double fixedIntervalLength = -1.0D;
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
/*     */   public PiecewiseFunction(Function[] functions, double[] upperBounds) {
/* 105 */     if (upperBounds.length + 1 != functions.length) {
/* 106 */       throw new IllegalArgumentException("there should be 1 less upperbounds (" + upperBounds.length + ") than functions (" + functions.length + ")");
/*     */     }
/* 108 */     this.functions = new Function[functions.length];
/* 109 */     System.arraycopy(functions, 0, this.functions, 0, functions.length);
/*     */     
/* 111 */     this.upperBounds = new double[upperBounds.length];
/* 112 */     System.arraycopy(upperBounds, 0, this.upperBounds, 0, upperBounds.length);
/*     */     
/* 114 */     if (upperBounds.length > 2) {
/* 115 */       double delta = upperBounds[1] - upperBounds[0];
/* 116 */       for (int a = 2; a < upperBounds.length; a++) {
/* 117 */         double k = upperBounds[a] - upperBounds[a - 1];
/* 118 */         if (Math.abs(delta - k) > 1.0E-11D) {
/*     */           return;
/*     */         }
/*     */       } 
/* 122 */       this.fixedIntervalLength = delta;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 128 */     StringBuffer sb = new StringBuffer("PiecewiseFunction[ ");
/* 129 */     sb.append(" x=(-inf, " + this.upperBounds[0] + "] " + this.functions[0]);
/* 130 */     for (int a = 1; a < this.upperBounds.length; a++) {
/* 131 */       sb.append(", ");
/* 132 */       sb.append(" x=(" + this.upperBounds[a - 1] + ", " + this.upperBounds[a] + "] " + this.functions[a]);
/*     */     } 
/* 134 */     sb.append(" x=(" + this.upperBounds[this.upperBounds.length - 1] + ", +inf) " + this.functions[this.functions.length - 1]);
/* 135 */     sb.append(" ]");
/* 136 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public double evaluate(double x) {
/* 140 */     if (this.fixedIntervalLength > 0.0D) {
/* 141 */       double min = this.upperBounds[0] - this.fixedIntervalLength;
/* 142 */       double max = this.upperBounds[this.upperBounds.length - 1] + this.fixedIntervalLength;
/* 143 */       int index = (int)((x - min) / (max - min) * this.functions.length);
/* 144 */       if (index == this.functions.length)
/* 145 */         index--; 
/* 146 */       return this.functions[index].evaluate(x);
/*     */     } 
/*     */     
/* 149 */     for (int a = 0; a < this.upperBounds.length; a++) {
/* 150 */       if (x < this.upperBounds[a])
/* 151 */         return this.functions[a].evaluate(x); 
/*     */     } 
/* 153 */     return this.functions[this.functions.length - 1].evaluate(x);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] evaluateInverse(double y) {
/* 159 */     HashSet<Double> set = new HashSet<>();
/* 160 */     for (int a = 0; a < this.functions.length; a++) {
/* 161 */       double[] x = this.functions[a].evaluateInverse(y);
/* 162 */       double minX = (a == 0) ? Double.MIN_VALUE : this.upperBounds[a - 1];
/* 163 */       double maxX = (a == this.functions.length - 1) ? Double.MAX_VALUE : this.upperBounds[a];
/* 164 */       for (int b = 0; b < x.length; b++) {
/* 165 */         if (x[b] >= minX && x[b] <= maxX) {
/* 166 */           set.add(new Double(x[b]));
/*     */         }
/*     */       } 
/*     */     } 
/* 170 */     int ctr = 0;
/* 171 */     double[] array = new double[set.size()];
/* 172 */     Iterator<Double> i = set.iterator();
/* 173 */     while (i.hasNext()) {
/* 174 */       array[ctr++] = ((Double)i.next()).doubleValue();
/*     */     }
/* 176 */     return array;
/*     */   }
/*     */   
/*     */   public Function getFunction(int index) {
/* 180 */     return this.functions[index];
/*     */   }
/*     */   
/*     */   public int getFunctionCount() {
/* 184 */     return this.functions.length;
/*     */   }
/*     */   
/*     */   public void setFunction(int index, Function f) {
/* 188 */     this.functions[index] = f;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\math\function\PiecewiseFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */