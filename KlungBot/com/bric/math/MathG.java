/*     */ package com.bric.math;
/*     */ 
/*     */ import com.bric.math.function.Function;
/*     */ import com.bric.math.function.PiecewiseFunction;
/*     */ import com.bric.math.function.PolynomialFunction;
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
/*     */ public abstract class MathG
/*     */ {
/*     */   private static final double PI = 3.141592653589793D;
/*     */   private static final double TWO_PI = 6.283185307179586D;
/*     */   private static final double PI_OVER_2 = 1.5707963267948966D;
/*     */   
/*     */   public static final double floorDouble(double d) {
/*  46 */     int id = (int)d;
/*  47 */     return (d == id || d > 0.0D) ? id : (id - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int floorInt(double d) {
/*  54 */     int id = (int)d;
/*  55 */     return (d == id || d > 0.0D) ? id : (id - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int roundInt(double d) {
/*     */     int i;
/*  63 */     if (d >= 0.0D) {
/*  64 */       i = (int)(d + 0.5D);
/*     */     } else {
/*  66 */       i = (int)(d - 0.5D);
/*     */     } 
/*  68 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final double roundDouble(double d) {
/*     */     int i;
/*  76 */     if (d >= 0.0D) {
/*  77 */       i = (int)(d + 0.5D);
/*     */     } else {
/*  79 */       i = (int)(d - 0.5D);
/*     */     } 
/*  81 */     return i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int ceilInt(double d) {
/*  88 */     int id = (int)d;
/*  89 */     return (d == id || d < 0.0D) ? id : (-((int)-d) + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final double ceilDouble(double d) {
/*  96 */     int id = (int)d;
/*  97 */     return (d == id || d < 0.0D) ? id : (-((int)-d) + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 104 */   private static Function sinFunction01 = (Function)PolynomialFunction.createFit(new double[] { 0.0D, 1.5707963267948966D }, new double[] {
/*     */         
/* 106 */         Math.sin(0.0D), Math.sin(1.5707963267948966D) }, new double[] {
/* 107 */         Math.cos(0.0D), Math.cos(1.5707963267948966D)
/*     */       });
/* 109 */   private static Function sinFunction00004 = (Function)PolynomialFunction.createFit(new double[] { 0.0D, 0.7853981633974483D, 1.5707963267948966D }, new double[] {
/*     */         
/* 111 */         Math.sin(0.0D), Math.sin(0.7853981633974483D), Math.sin(1.5707963267948966D) }, new double[] {
/* 112 */         Math.cos(0.0D), Math.cos(0.7853981633974483D), Math.cos(1.5707963267948966D)
/*     */       });
/*     */   
/*     */   private static Function acosFunction;
/*     */   
/*     */   static {
/* 118 */     Function acos = new Function() {
/*     */         public double evaluate(double x) {
/* 120 */           return Math.acos(x);
/*     */         }
/*     */         public double[] evaluateInverse(double y) {
/* 123 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/* 126 */     Function acosD = new Function() {
/*     */         public double evaluate(double x) {
/* 128 */           return -1.0D / Math.sqrt(1.0D - x * x);
/*     */         }
/*     */         public double[] evaluateInverse(double y) {
/* 131 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */     
/* 135 */     PiecewiseFunction p = PiecewiseFunction.create(acos, acosD, 0.0D, 1.0D, 512);
/* 136 */     p.setFunction(p.getFunctionCount() - 1, 
/* 137 */         (Function)PiecewiseFunction.create(acos, acosD, 1.0D - 1.0D / p
/* 138 */           .getFunctionCount(), 1.0D, 64));
/*     */     
/* 140 */     acosFunction = (Function)p;
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
/*     */   public static final double sin01(double v) {
/*     */     double finalMultiplier;
/* 161 */     if (v < 0.0D) {
/* 162 */       finalMultiplier = -1.0D;
/* 163 */       v = -v;
/*     */     } else {
/* 165 */       finalMultiplier = 1.0D;
/*     */     } 
/*     */     
/* 168 */     if (v > 1.0E10D) {
/* 169 */       if (!printedOverflowError) {
/* 170 */         printedOverflowError = true;
/* 171 */         System.err.println("Warning: MathG is not designed to estimate the sine of values of 1.0e10.  Math.sin() will be used, which may result in slower performance.");
/*     */       } 
/* 173 */       return finalMultiplier * Math.sin(v);
/* 174 */     }  if (v < 0.01D)
/*     */     {
/*     */       
/* 177 */       return v * finalMultiplier;
/*     */     }
/*     */     
/* 180 */     if (v > 6.283185307179586D) {
/*     */       
/* 182 */       long m = (long)(v / 6.283185307179586D);
/* 183 */       v -= m * 6.283185307179586D;
/*     */     } 
/* 185 */     if (v > Math.PI) {
/* 186 */       v -= Math.PI;
/* 187 */       finalMultiplier = -finalMultiplier;
/*     */     } 
/* 189 */     if (v > 1.5707963267948966D) {
/* 190 */       v = Math.PI - v;
/*     */     }
/*     */ 
/*     */     
/* 194 */     double result = sinFunction01.evaluate(v);
/* 195 */     result *= finalMultiplier;
/*     */     
/* 197 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean printedOverflowError = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final double cos01(double v) {
/* 215 */     if (v > 1.0E10D || v < 1.0E-10D)
/* 216 */       return Math.cos(v); 
/* 217 */     return sin01(v - 1.5707963267948966D);
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
/*     */   public static final double sin00004(double v) {
/*     */     double finalMultiplier;
/* 230 */     if (v < 0.0D) {
/* 231 */       finalMultiplier = -1.0D;
/* 232 */       v = -v;
/*     */     } else {
/* 234 */       finalMultiplier = 1.0D;
/*     */     } 
/*     */     
/* 237 */     if (v < 0.01D)
/*     */     {
/*     */       
/* 240 */       return v * finalMultiplier; } 
/* 241 */     if (v > 1.0E10D) {
/* 242 */       if (!printedOverflowError) {
/* 243 */         printedOverflowError = true;
/* 244 */         System.err.println("Warning: MathG is not designed to estimate the sine of values of 1.0e10.  Math.sin() will be used, which may result in slower performance.");
/*     */       } 
/* 246 */       return finalMultiplier * Math.sin(v);
/*     */     } 
/*     */     
/* 249 */     if (v > 6.283185307179586D) {
/*     */       
/* 251 */       long m = (long)(v / 6.283185307179586D);
/* 252 */       v -= m * 6.283185307179586D;
/*     */     } 
/* 254 */     if (v > Math.PI) {
/* 255 */       v -= Math.PI;
/* 256 */       finalMultiplier = -finalMultiplier;
/*     */     } 
/* 258 */     if (v > 1.5707963267948966D) {
/* 259 */       v = Math.PI - v;
/*     */     }
/*     */     
/* 262 */     double result = sinFunction00004.evaluate(v);
/* 263 */     result *= finalMultiplier;
/*     */     
/* 265 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final double acos(double v) {
/* 275 */     if (v < -1.0D || v > 1.0D) throw new IllegalArgumentException("v (" + v + ") must be within [-1,1]"); 
/* 276 */     if (v < 0.0D) {
/* 277 */       v = -v;
/* 278 */       return Math.PI - acos(v);
/*     */     } 
/* 280 */     return acosFunction.evaluate(v);
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
/*     */   public static final double cos00004(double v) {
/* 293 */     if (v > 1.0E10D || v < 1.0E-10D)
/* 294 */       return Math.cos(v); 
/* 295 */     return sin00004(v - 1.5707963267948966D);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\math\MathG.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */