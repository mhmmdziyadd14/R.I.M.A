/*    */ package javax.media.jai;
/*    */ 
/*    */ import java.text.MessageFormat;
/*    */ import java.util.Locale;
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
/*    */ class JaiI18N
/*    */ {
/* 18 */   static String packageName = "javax.media.jai";
/*    */ 
/*    */   
/*    */   public static String getString(String key) {
/* 22 */     return key;
/*    */   }
/*    */   
/*    */   public static String formatMsg(String key, Object[] args) {
/* 26 */     MessageFormat mf = new MessageFormat(getString(key));
/* 27 */     mf.setLocale(Locale.getDefault());
/*    */     
/* 29 */     return mf.format(args);
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\javax\media\jai\JaiI18N.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */