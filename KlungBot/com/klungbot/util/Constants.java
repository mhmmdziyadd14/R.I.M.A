/*    */ package com.klungbot.util;
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
/*    */ public class Constants
/*    */ {
/* 14 */   public static final String osname = System.getProperty("os.name");
/*    */ 
/*    */ 
/*    */   
/* 18 */   public static final boolean isOSX = osname.equalsIgnoreCase("Mac OS X");
/*    */ 
/*    */ 
/*    */   
/* 22 */   public static final boolean isLinux = osname.equalsIgnoreCase("Linux");
/*    */ 
/*    */ 
/*    */   
/* 26 */   public static final boolean isSolaris = osname.equalsIgnoreCase("SunOS");
/*    */ 
/*    */ 
/*    */   
/* 30 */   public static final boolean isVista = osname.equalsIgnoreCase("Windows Vista");
/*    */ 
/*    */ 
/*    */   
/* 34 */   public static final boolean isWindows = (!isOSX && !isLinux && !isSolaris);
/*    */   public static final String extDomisol = ".123";
/*    */   public static final String extMidiMap = ".12m";
/*    */   public static final String extPlaylist = ".12p";
/*    */   public static final String extChord = ".12c";
/*    */   public static final String extDrum = ".12d";
/*    */   public static final String extRythm = ".12r";
/*    */   public static final String extScale = ".12s";
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbo\\util\Constants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */