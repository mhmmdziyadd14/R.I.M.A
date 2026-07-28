/*     */ package com.klungbot.util;
/*     */ 
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Paint;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.TexturePaint;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.HashMap;
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
/*     */ public class ColorTable
/*     */ {
/*  25 */   public static final Color[] colors = new Color[] { Color.RED, Color.BLACK, Color.ORANGE, Color.BLACK, Color.YELLOW, Color.GREEN, Color.BLACK, Color.BLUE, Color.BLACK, new Color(128, 0, 128), Color.BLACK, new Color(255, 0, 128) };
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
/*  40 */   public static final Paint[] paints = new Paint[] { Color.RED, Color.BLACK, Color.ORANGE, Color.BLACK, Color.YELLOW, Color.GREEN, Color.BLACK, Color.BLUE, Color.BLACK, new Color(128, 0, 128), Color.BLACK, new Color(255, 0, 128) };
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
/*  58 */   static final HashMap<String, Color> table = new HashMap<>(); static {
/*  59 */     table.put("BLACK", Color.BLACK);
/*  60 */     table.put("BLUE", Color.BLUE);
/*  61 */     table.put("CYAN", Color.CYAN);
/*  62 */     table.put("DARK_GRAY", Color.DARK_GRAY);
/*  63 */     table.put("DARK_GREEN", new Color(0, 128, 0));
/*  64 */     table.put("GRAY", Color.GRAY);
/*  65 */     table.put("GREEN", Color.GREEN);
/*  66 */     table.put("LIGHT_GRAY", Color.LIGHT_GRAY);
/*  67 */     table.put("MAGENTA", Color.MAGENTA);
/*  68 */     table.put("ORANGE", Color.ORANGE);
/*  69 */     table.put("PINK", Color.PINK);
/*  70 */     table.put("RED", Color.RED);
/*  71 */     table.put("WHITE", Color.WHITE);
/*  72 */     table.put("YELLOW", Color.YELLOW);
/*     */     
/*  74 */     int[] blacks = { 1, 3, 7, 9, 11 };
/*  75 */     BasicStroke s1 = new BasicStroke(5.0F, 2, 2);
/*  76 */     for (int i = 0; i < colors.length; i++) {
/*  77 */       if (colors[i] != Color.BLACK) {
/*  78 */         paints[i] = colors[i];
/*     */       } else {
/*     */         
/*  81 */         BufferedImage bi = new BufferedImage(16, 16, 1);
/*  82 */         Graphics2D big = bi.createGraphics();
/*  83 */         big.setColor(colors[i - 1]);
/*  84 */         big.fillRect(0, 0, 16, 16);
/*  85 */         big.setStroke(s1);
/*  86 */         big.setColor(Color.BLACK);
/*  87 */         big.drawLine(0, 16, 16, 0);
/*  88 */         Rectangle r = new Rectangle(4, 4, 12, 12);
/*  89 */         paints[i] = new TexturePaint(bi, r);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Color get(String name) {
/*  95 */     return get(name, Color.BLACK);
/*     */   }
/*     */   
/*     */   public static Color get(String name, Color def) {
/*  99 */     Color c = table.get(name);
/* 100 */     if (c != null) return c; 
/*     */     try {
/* 102 */       return Color.decode(name);
/*     */     }
/* 104 */     catch (Exception ex) {
/* 105 */       return def;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbo\\util\ColorTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */