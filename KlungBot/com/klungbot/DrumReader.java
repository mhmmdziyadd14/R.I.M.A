/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.doremi.Diatonic;
/*     */ import com.klungbot.doremi.Pattern;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.StringReader;
/*     */ import javax.sound.midi.Instrument;
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
/*     */ public class DrumReader
/*     */ {
/*     */   static Instrument readI(String line) throws ParserException {
/*  24 */     String[] fields = line.split(":");
/*  25 */     if (fields.length < 2) {
/*  26 */       throw new ParserException("Invalid I line");
/*     */     }
/*  28 */     String name = fields[1].trim();
/*  29 */     Instrument i1 = AudioEngineer.getLoadedInstrument(name);
/*  30 */     if (i1 == null) {
/*  31 */       throw new ParserException("Unknown instrumen " + name);
/*     */     }
/*  33 */     return i1;
/*     */   }
/*     */   
/*     */   static void readP(Percussion p, String line) throws Exception {
/*  37 */     String[] fields = line.split(":");
/*  38 */     if (fields.length < 2) {
/*  39 */       throw new ParserException("Invalid P line");
/*     */     }
/*  41 */     if (fields[0].length() < 2) {
/*  42 */       throw new ParserException("P line without note");
/*     */     }
/*  44 */     int index = Pattern.noteOf(fields[0].charAt(1));
/*  45 */     if (index == 0) {
/*  46 */       throw new ParserException("Unknown drum symbol (" + fields[0].charAt(1) + ")");
/*     */     }
/*  48 */     String[] columns = fields[1].split(",");
/*  49 */     int[] notes = new int[columns.length];
/*  50 */     for (int i = 0; i < columns.length; i++) {
/*  51 */       notes[i] = Integer.valueOf(columns[i].trim()).intValue();
/*  52 */       if (notes[i] <= 0 || notes[i] > 127) {
/*  53 */         throw new ParserException("Note (" + columns[i] + ") is out of range");
/*     */       }
/*     */     } 
/*  56 */     p.setMap(index, notes);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Percussion read(String id, BufferedReader r) throws Exception {
/*  61 */     int lnum = 0;
/*     */     
/*  63 */     Percussion percussion = null;
/*  64 */     Diatonic diatonic = new Diatonic();
/*     */     try {
/*     */       do {
/*  67 */         line = r.readLine();
/*  68 */         lnum++;
/*  69 */         if (line == null) {
/*  70 */           throw new ParserException("Empty text");
/*     */         }
/*     */       }
/*  73 */       while (!line.startsWith("I:"));
/*     */       
/*  75 */       Instrument i1 = readI(line);
/*  76 */       percussion = new Percussion(id, i1); String line;
/*  77 */       while ((line = r.readLine()) != null) {
/*  78 */         lnum++;
/*  79 */         line = line.trim();
/*  80 */         if (line.isEmpty())
/*  81 */           continue;  switch (line.charAt(0)) { case 'P':
/*  82 */             readP(percussion, line); continue;
/*     */           case '#':
/*     */           case '$':
/*     */             continue; }
/*     */         
/*  87 */         throw new ParserException("Unknown line ", lnum, 1);
/*     */       }
/*     */     
/*     */     }
/*  91 */     catch (ParserException ex) {
/*  92 */       ex.setLocation(lnum, 1);
/*  93 */       throw ex;
/*     */     }
/*  95 */     catch (Exception ex) {
/*  96 */       throw new ParserException(ex.getMessage(), lnum, 1);
/*     */     } 
/*  98 */     return percussion;
/*     */   }
/*     */   
/*     */   public static Percussion read(String id, String buffer) throws Exception {
/* 102 */     return read(id, new BufferedReader(new StringReader(buffer)));
/*     */   }
/*     */ 
/*     */   
/*     */   public static Percussion read(String id, File fname) throws FileNotFoundException, Exception {
/* 107 */     return read(id, new BufferedReader(new FileReader(fname)));
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\DrumReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */