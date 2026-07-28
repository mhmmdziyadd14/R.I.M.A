/*     */ package com.bric.math.function;
/*     */ 
/*     */ import com.bric.math.Equations;
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
/*     */ public class PolynomialFunction
/*     */   implements Function
/*     */ {
/*     */   double[] coeffs;
/*     */   
/*     */   public static PolynomialFunction createFit(double x1, double y1, double x2, double y2) {
/*  39 */     return new PolynomialFunction(new double[] { (y2 - y1) / (-x1 + x2), (y1 * x2 - y2 * x1) / (-x1 + x2) });
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
/*     */   public static PolynomialFunction createFit(double[] xs, double[] ys) {
/*  51 */     if (ys.length != xs.length) {
/*  52 */       throw new IllegalArgumentException("xs.length (" + xs.length + ") != ys.length (" + ys.length + ")");
/*     */     }
/*  54 */     double[][] coefficientsMatrix = new double[ys.length][ys.length + 1];
/*  55 */     for (int row = 0; row < coefficientsMatrix.length; row++) {
/*     */       
/*  57 */       for (int column = 0; column < (coefficientsMatrix[row]).length - 1; column++) {
/*  58 */         int power = ys.length - column - 1;
/*  59 */         coefficientsMatrix[row][column] = Math.pow(xs[row], power);
/*     */       } 
/*  61 */       coefficientsMatrix[row][(coefficientsMatrix[row]).length - 1] = ys[row];
/*     */     } 
/*     */     
/*  64 */     Equations.solve(coefficientsMatrix, true);
/*  65 */     double[] coeffs = new double[coefficientsMatrix.length];
/*  66 */     for (int a = 0; a < coeffs.length; a++) {
/*  67 */       coeffs[a] = coefficientsMatrix[a][(coefficientsMatrix[a]).length - 1];
/*     */     }
/*  69 */     return new PolynomialFunction(coeffs);
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
/*     */   public static PolynomialFunction createFit(double[] xs, double[] ys, double[] yDerivatives) {
/*  85 */     if (ys.length != yDerivatives.length)
/*  86 */       throw new IllegalArgumentException("ys.length (" + ys.length + ") != yDerivatives.length (" + yDerivatives.length + ")"); 
/*  87 */     if (ys.length != xs.length) {
/*  88 */       throw new IllegalArgumentException("xs.length (" + xs.length + ") != ys.length (" + ys.length + ")");
/*     */     }
/*  90 */     double[][] coefficientsMatrix = new double[ys.length * 2][ys.length * 2 + 1];
/*  91 */     for (int row = 0; row < coefficientsMatrix.length; row += 2) {
/*     */ 
/*     */       
/*  94 */       for (int column = 0; column < (coefficientsMatrix[row]).length - 1; column++) {
/*  95 */         int power = ys.length * 2 - column - 1;
/*  96 */         coefficientsMatrix[row][column] = Math.pow(xs[row / 2], power);
/*  97 */         if (power == 0) {
/*  98 */           coefficientsMatrix[row + 1][column] = 0.0D;
/*     */         } else {
/* 100 */           coefficientsMatrix[row + 1][column] = power * Math.pow(xs[row / 2], (power - 1));
/*     */         } 
/*     */       } 
/* 103 */       coefficientsMatrix[row][(coefficientsMatrix[row]).length - 1] = ys[row / 2];
/* 104 */       coefficientsMatrix[row + 1][(coefficientsMatrix[row]).length - 1] = yDerivatives[row / 2];
/*     */     } 
/*     */     
/* 107 */     Equations.solve(coefficientsMatrix, true);
/* 108 */     double[] coeffs = new double[coefficientsMatrix.length];
/* 109 */     for (int a = 0; a < coeffs.length; a++) {
/* 110 */       coeffs[a] = coefficientsMatrix[a][(coefficientsMatrix[a]).length - 1];
/*     */     }
/* 112 */     return new PolynomialFunction(coeffs);
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
/*     */   public PolynomialFunction(double[] coeffs) {
/* 125 */     this.coeffs = new double[coeffs.length];
/* 126 */     System.arraycopy(coeffs, 0, this.coeffs, 0, coeffs.length);
/*     */   }
/*     */   
/*     */   public double evaluate(double x) {
/* 130 */     double result = this.coeffs[0];
/* 131 */     for (int a = 1, n = this.coeffs.length; a < n; a++) {
/* 132 */       result = result * x + this.coeffs[a];
/*     */     }
/* 134 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 139 */     StringBuffer sb = new StringBuffer("y = ");
/* 140 */     for (int a = 0; a < this.coeffs.length; a++) {
/* 141 */       int degree = this.coeffs.length - a - 1;
/* 142 */       if (degree == 0) {
/* 143 */         sb.append(this.coeffs[a]);
/*     */       } else {
/* 145 */         sb.append(this.coeffs[a] + "*(x^" + degree + ")");
/*     */       } 
/* 147 */       if (a != this.coeffs.length - 1)
/* 148 */         sb.append("+"); 
/*     */     } 
/* 150 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public double[] evaluateInverse(double y) {
/* 154 */     if (this.coeffs.length == 2) {
/* 155 */       double x = (y - this.coeffs[1]) / this.coeffs[0];
/* 156 */       return new double[] { x };
/*     */     } 
/*     */     
/* 159 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\math\function\PolynomialFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */