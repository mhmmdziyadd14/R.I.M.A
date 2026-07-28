/*     */ package com.klungbot.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Options
/*     */   extends Properties
/*     */ {
/*  27 */   static final Options singleton = new Options();
/*  28 */   static String fileName = "klungbot.ini";
/*     */   
/*     */   public static void initDefault() {
/*  31 */     singleton.setProperty("rhythm.waltz", "doremi.rhythm.Waltz");
/*  32 */     singleton.setProperty("rhythm.waltz2", "doremi.rhythm.Waltz2");
/*  33 */     singleton.setProperty("rhythm.swing", "doremi.rhythm.Swing");
/*  34 */     singleton.setProperty("rhythm.double-swing", "doremi.rhythm.DoubleSwing");
/*  35 */     singleton.setProperty("rhythm.jazz", "doremi.rhythm.Jazz");
/*  36 */     singleton.setProperty("rhythm.polka", "doremi.rhythm.Polka");
/*  37 */     singleton.setProperty("rhythm.tango", "doremi.rhythm.Tango");
/*  38 */     singleton.setProperty("rhythm.boogie", "doremi.rhythm.Boogie");
/*  39 */     singleton.setProperty("rhythm.gypsy", "doremi.rhythm.Gypsy");
/*  40 */     singleton.setProperty("rhythm.calung", "doremi.rhythm.DoubleSwing");
/*  41 */     singleton.setProperty("rhythm.keroncong", "doremi.rhythm.Keroncong");
/*  42 */     singleton.setProperty("rhythm.jazz2", "doremi.rhythm.Jazz2");
/*  43 */     singleton.setProperty("rhythm.mars", "doremi.rhythm.Mars");
/*     */     
/*  45 */     singleton.setProperty("scale.diatonic", "doremi.Diatonic");
/*  46 */     singleton.setProperty("scale.major", "doremi.Diatonic");
/*  47 */     singleton.setProperty("scale.diatonic-major", "doremi.Diatonic");
/*  48 */     singleton.setProperty("scale.minor", "doremi.DiatonicMinor");
/*  49 */     singleton.setProperty("scale.diatonic-minor", "doremi.DiatonicMinor");
/*  50 */     singleton.setProperty("scale.pentatonic", "doremi.Pentatonic");
/*  51 */     singleton.setProperty("scale.pelog", "doremi.Pentatonic");
/*  52 */     singleton.setProperty("scale.pentatonic-major", "doremi.Pentatonic");
/*  53 */     singleton.setProperty("scale.slendro", "doremi.PentatonicMinor");
/*  54 */     singleton.setProperty("scale.pentatonic-minor", "doremi.PentatonicMinor");
/*     */     
/*  56 */     singleton.setProperty("device.class.Klungbot", "klungbot.SerialDevice");
/*  57 */     singleton.setProperty("device.latency", "480");
/*     */     
/*  59 */     singleton.setProperty("device.port.linux.Klungbot", "/dev/ttyACM0");
/*  60 */     singleton.setProperty("device.port.windows.Klungbot", "COM3");
/*  61 */     singleton.setProperty("device.port.mac.Klungbot", "/dev/tty.usbmodemfd121");
/*     */     
/*  63 */     singleton.setProperty("device.player.Klungbot.0", "Klungbot");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  68 */     singleton.setProperty("player.instrument.0", "Klungbot");
/*  69 */     singleton.setProperty("player.instrument.1", "Klungbot Accomp");
/*     */     
/*  71 */     singleton.setProperty("player.percussion.04", "Perkusi");
/*  72 */     singleton.setProperty("player.synthesizer.00", "Angklung;Angklung (centok)");
/*  73 */     singleton.setProperty("player.synthesizer.03", "Gambang Pengiring");
/*  74 */     singleton.setProperty("player.synthesizer.02", "Bass Betot");
/*  75 */     singleton.setProperty("player.synthesizer.01", "Gambang Melodi");
/*  76 */     singleton.setProperty("player.synthesizer.05", "Biola");
/*  77 */     singleton.setProperty("player.synthesizer.06", "Seruling");
/*  78 */     singleton.setProperty("player.synthesizer.07", "Kecapi");
/*  79 */     singleton.setProperty("player.synthesizer.09", "Rindik");
/*  80 */     singleton.setProperty("player.synthesizer.10", "Kolintang");
/*  81 */     singleton.setProperty("player.synthesizer.08", "Bass Lodong");
/*     */     
/*  83 */     singleton.setProperty("player.default.21", "0");
/*  84 */     singleton.setProperty("player.default.22", "13");
/*  85 */     singleton.setProperty("player.default.23", "16");
/*  86 */     singleton.setProperty("player.default.24", "24");
/*  87 */     singleton.setProperty("player.default.25", "40");
/*  88 */     singleton.setProperty("player.default.26", "48");
/*  89 */     singleton.setProperty("player.default.27", "65");
/*  90 */     singleton.setProperty("player.default.28", "71");
/*  91 */     singleton.setProperty("player.default.29", "73");
/*  92 */     singleton.setProperty("player.default.30", "107");
/*     */     
/*  94 */     singleton.setProperty("folder.sounds", "sounds");
/*  95 */     singleton.setProperty("folder.album", "album");
/*  96 */     singleton.setProperty("folder.draft", "draft");
/*  97 */     singleton.setProperty("folder.chord", "chord");
/*  98 */     singleton.setProperty("folder.drum", "drum");
/*  99 */     singleton.setProperty("folder.midi", "midi");
/* 100 */     singleton.setProperty("folder.ensemble", "ensemble");
/* 101 */     singleton.setProperty("folder.pattern", "pattern");
/* 102 */     singleton.setProperty("folder.rhythm", "rhythm");
/*     */   }
/*     */   
/*     */   public static ArrayList<String> getKeys(String filter) {
/* 106 */     ArrayList<String> set2 = new ArrayList<>();
/* 107 */     Enumeration<Object> keys = singleton.keys();
/* 108 */     while (keys.hasMoreElements()) {
/* 109 */       String s = (String)keys.nextElement();
/* 110 */       if (s.startsWith(filter))
/* 111 */         set2.add(s); 
/*     */     } 
/* 113 */     if (set2.size() == 0) return null; 
/* 114 */     Collections.sort(set2);
/* 115 */     return set2;
/*     */   }
/*     */   
/*     */   public static String cut(String str, int n) {
/* 119 */     int i = 0;
/* 120 */     while (i < str.length() && n > 1) {
/* 121 */       if (str.charAt(i) == '.') n--; 
/* 122 */       i++;
/*     */     } 
/* 124 */     if (i >= str.length()) return null; 
/* 125 */     StringBuilder s = new StringBuilder();
/* 126 */     while (i < str.length()) {
/* 127 */       char c = str.charAt(i);
/* 128 */       if (c == '.')
/* 129 */         break;  s.append(c);
/* 130 */       i++;
/*     */     } 
/* 132 */     return s.toString();
/*     */   }
/*     */   
/*     */   public static String get(String key) {
/* 136 */     return singleton.getProperty(key);
/*     */   }
/*     */   
/*     */   static void storeSorted(Writer w) throws IOException {
/* 140 */     Enumeration<Object> keys = singleton.keys();
/* 141 */     TreeSet<String> tm = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
/* 142 */     while (keys.hasMoreElements()) {
/* 143 */       tm.add((String)keys.nextElement());
/*     */     }
/* 145 */     Iterator<String> t = tm.iterator();
/* 146 */     while (t.hasNext()) {
/* 147 */       String key = t.next();
/* 148 */       String value = singleton.getProperty(key);
/* 149 */       w.write(key + "=" + value + "\n");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void store() {
/*     */     try {
/* 156 */       Writer writer = new FileWriter(fileName);
/* 157 */       storeSorted(writer);
/* 158 */       writer.close();
/*     */     }
/* 160 */     catch (Exception ex) {
/* 161 */       System.err.println(ex.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean load(String base) {
/* 166 */     fileName = base + File.separator + fileName;
/*     */     try {
/* 168 */       Reader reader = new FileReader(fileName);
/* 169 */       singleton.load(reader);
/* 170 */       return true;
/*     */     }
/* 172 */     catch (Exception ex) {
/* 173 */       System.err.println("Error loading options :" + ex.getMessage());
/* 174 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbo\\util\Options.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */