/*    */ package com.bric.math;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MutableInteger
/*    */   extends Number
/*    */   implements Comparable<Number>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public int value;
/*    */   
/*    */   public MutableInteger() {}
/*    */   
/*    */   public MutableInteger(int v) {
/* 37 */     this.value = v;
/*    */   }
/*    */   
/*    */   public int compareTo(Number n) {
/* 41 */     int i = n.intValue();
/* 42 */     if (this.value == i) return 0; 
/* 43 */     if (this.value < i) return -1; 
/* 44 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 49 */     return Integer.toString(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object clone() {
/* 54 */     return new MutableInteger(this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object t) {
/* 59 */     if (t instanceof Number) {
/* 60 */       return (((Number)t).intValue() == this.value);
/*    */     }
/* 62 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 67 */     return intValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public double doubleValue() {
/* 72 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public float floatValue() {
/* 77 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public int intValue() {
/* 82 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public long longValue() {
/* 87 */     return this.value;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\math\MutableInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */