/*    */ package com.klungbot.doremi;
/*    */ 
/*    */ import java.util.HashMap;
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
/*    */ public abstract class Rythm
/*    */ {
/* 17 */   static HashMap<String, Rythm> rMap = new HashMap<>();
/*    */   
/*    */   public static Rythm get(String name) {
/* 20 */     return rMap.get(name);
/*    */   }
/*    */   
/*    */   public static Rythm alias(String alias, String name) {
/* 24 */     Rythm r = rMap.get(name);
/* 25 */     if (r != null) {
/* 26 */       rMap.put(alias, r);
/*    */     }
/* 28 */     return r;
/*    */   }
/*    */   
/*    */   public static void put(String name, Rythm r) {
/* 32 */     rMap.put(name, r);
/*    */   }
/*    */   
/*    */   public static void remove(String name) {
/* 36 */     rMap.remove(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public void register(String name) {
/* 41 */     rMap.put(name, this);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract int getLength();
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract int[][] getRythm(int[] paramArrayOfint);
/*    */ 
/*    */ 
/*    */   
/*    */   public static void init(String folder) {
/* 55 */     ArrayRythm.initDefault();
/* 56 */     ListRythm.loadAll(folder);
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\doremi\Rythm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */