/*     */ package com.bric.util;
/*     */ 
/*     */ import com.bric.math.MutableInteger;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
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
/*     */ public class FloatArrayFactory
/*     */ {
/*     */   private static FloatArrayFactory globalFactory;
/*     */   
/*     */   public static FloatArrayFactory getStaticFactory() {
/*  43 */     if (globalFactory == null)
/*  44 */       globalFactory = new FloatArrayFactory(); 
/*  45 */     return globalFactory;
/*     */   }
/*     */   
/*  48 */   private Map<Number, Stack<float[]>> map = createMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<Number, Stack<float[]>> createMap() {
/*     */     try {
/*  56 */       Class<?> troveMap = Class.forName("gnu.trove.THashMap");
/*  57 */       Constructor[] arrayOfConstructor = (Constructor[])troveMap.getConstructors();
/*  58 */       for (int a = 0; a < arrayOfConstructor.length; a++) {
/*  59 */         if ((arrayOfConstructor[a].getParameterTypes()).length == 0)
/*  60 */           return arrayOfConstructor[a].newInstance(new Object[0]); 
/*     */       } 
/*  62 */     } catch (Throwable e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  67 */     return new Hashtable<>();
/*     */   }
/*     */   
/*  70 */   private MutableInteger key = new MutableInteger(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float[] getArray(int size) {
/*     */     Stack<float[]> stack;
/*  81 */     synchronized (this.key) {
/*  82 */       this.key.value = size;
/*  83 */       stack = this.map.get(this.key);
/*  84 */       if (stack == null) {
/*  85 */         stack = (Stack)new Stack<>();
/*  86 */         this.map.put(new MutableInteger(size), stack);
/*     */       } 
/*     */     } 
/*  89 */     if (stack.size() == 0) {
/*  90 */       return new float[size];
/*     */     }
/*  92 */     return stack.pop();
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
/*     */   public void putArray(float[] array) {
/*     */     Stack<float[]> stack;
/* 108 */     synchronized (this.key) {
/* 109 */       this.key.value = array.length;
/* 110 */       stack = this.map.get(this.key);
/* 111 */       if (stack == null) {
/* 112 */         stack = (Stack)new Stack<>();
/* 113 */         this.map.put(new MutableInteger(array.length), stack);
/*     */       } 
/*     */     } 
/* 116 */     stack.push(array);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bri\\util\FloatArrayFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */