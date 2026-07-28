/*     */ package com.klungbot.app;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import javax.swing.table.AbstractTableModel;
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
/*     */ public class TopHits
/*     */   extends AbstractTableModel
/*     */ {
/*  27 */   ArrayList<Hit> list = new ArrayList<>();
/*  28 */   int maxVote = 5;
/*     */ 
/*     */   
/*     */   public void setMaxVote(int max) {
/*  32 */     this.maxVote = max;
/*     */   }
/*     */   
/*  35 */   String[] headers = new String[] { "Title", "Votes" };
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/*  39 */     return this.headers[column];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRowCount() {
/*  44 */     return this.list.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/*  49 */     return 2;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValueAt(int rowIndex, int columnIndex) {
/*  55 */     if (rowIndex > this.list.size()) return null; 
/*  56 */     Hit h = this.list.get(rowIndex);
/*  57 */     switch (columnIndex) { case 0:
/*  58 */         return h.title;
/*  59 */       case 1: return new Integer(h.vote); }
/*     */     
/*  61 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String vote(String t) {
/*  70 */     String voted = null;
/*  71 */     for (Hit h : this.list) {
/*  72 */       if (h.title.equals(t)) {
/*     */         
/*  74 */         h.addVote();
/*  75 */         if (h.vote >= this.maxVote) {
/*  76 */           voted = h.title;
/*  77 */           this.list.remove(h);
/*     */         } else {
/*     */           
/*  80 */           Collections.sort(this.list);
/*     */         } 
/*  82 */         fireTableDataChanged();
/*  83 */         return voted;
/*     */       } 
/*     */     } 
/*     */     
/*  87 */     this.list.add(new Hit(t));
/*  88 */     fireTableRowsInserted(this.list.size() - 1, this.list.size());
/*  89 */     return null;
/*     */   }
/*     */   
/*     */   public void revote(int i) {
/*  93 */     String voted = null;
/*  94 */     Hit h = this.list.get(i);
/*  95 */     if (h != null) {
/*  96 */       h.addVote();
/*  97 */       Collections.sort(this.list);
/*  98 */       fireTableDataChanged();
/*     */     } 
/*     */   }
/*     */   
/*     */   public String remove(int index) {
/* 103 */     if (index >= this.list.size()) return null; 
/* 104 */     Hit hit = this.list.remove(index);
/* 105 */     fireTableRowsDeleted(index, index);
/* 106 */     return hit.title;
/*     */   }
/*     */ 
/*     */   
/*     */   public void save(String fname) throws IOException {
/* 111 */     BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
/* 112 */     for (Hit h : this.list) {
/* 113 */       writer.write(h.title + ";" + h.vote);
/* 114 */       writer.newLine();
/*     */     } 
/* 116 */     writer.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void open(String fname) throws IOException {
/* 121 */     BufferedReader reader = new BufferedReader(new FileReader(fname));
/*     */     
/* 123 */     if (reader.ready()) {
/* 124 */       ArrayList<Hit> newList = new ArrayList<>();
/*     */       while (true) {
/* 126 */         String line = reader.readLine();
/* 127 */         String[] ss = line.split(";");
/* 128 */         if (ss.length >= 2 && !ss[0].isEmpty()) {
/* 129 */           Hit h = new Hit(ss[0]);
/*     */           try {
/* 131 */             h.vote = Integer.parseInt(ss[1]);
/* 132 */           } catch (Exception ex) {
/* 133 */             h.vote = 1;
/*     */           } 
/* 135 */           newList.add(h);
/*     */         } 
/* 137 */         if (!reader.ready())
/* 138 */         { this.list = newList;
/* 139 */           fireTableDataChanged(); break; } 
/*     */       } 
/* 141 */     }  reader.close();
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\TopHits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */