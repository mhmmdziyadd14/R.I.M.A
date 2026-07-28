/*     */ package com.klungbot.util;
/*     */ 
/*     */ import java.io.File;
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
/*     */ public class FileTreeNode1
/*     */ {
/*     */   public File file;
/*     */   public static final String WINDOWS_MYCOMPUTER = "::{20D04FE0-3AEA-1069-A2D8-08002B30309D}";
/*     */   public static final String WINDOWS_MYNETWORKPLACES = "::{208D2C60-3AEA-1069-A2D7-08002B30309D}";
/*     */   public static final String WINDOWSVISTA_NETWORK = "::{F02C1A0D-BE21-4350-88B0-7367FC96EF3C}";
/*     */   
/*     */   public FileTreeNode1(String fname) {
/*  37 */     this.file = new File(fname);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileTreeNode1(File file) {
/*  46 */     if (file == null) {
/*  47 */       throw new IllegalArgumentException("Null file not allowed");
/*     */     }
/*  49 */     this.file = file;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  58 */     String name = this.file.getName();
/*  59 */     if (!Constants.isWindows) {
/*  60 */       return name;
/*     */     }
/*  62 */     if (name.length() == 0) {
/*  63 */       return this.file.getPath();
/*     */     }
/*  65 */     if (Constants.isVista) {
/*     */       
/*  67 */       if (name.equals("::{20D04FE0-3AEA-1069-A2D8-08002B30309D}"))
/*  68 */         return "Computer"; 
/*  69 */       if (name.equals("::{F02C1A0D-BE21-4350-88B0-7367FC96EF3C}"))
/*  70 */         return "Network"; 
/*  71 */       return name;
/*     */     } 
/*     */ 
/*     */     
/*  75 */     if (name.equals("::{20D04FE0-3AEA-1069-A2D8-08002B30309D}"))
/*  76 */       return "My Computer"; 
/*  77 */     if (name.equals("::{208D2C60-3AEA-1069-A2D7-08002B30309D}")) {
/*  78 */       return "My Network Places";
/*     */     }
/*  80 */     return name;
/*     */   }
/*     */   
/*     */   public boolean isLeaf() {
/*  84 */     return this.file.isFile();
/*     */   }
/*     */   
/*     */   public int getCount() {
/*  88 */     String[] children = this.file.list();
/*  89 */     if (children == null) return 0; 
/*  90 */     return children.length;
/*     */   }
/*     */   
/*     */   public FileTreeNode1 getChild(int index) {
/*  94 */     String[] children = this.file.list();
/*  95 */     if (children == null || index >= children.length) return null; 
/*  96 */     File child = new File(this.file, children[index]);
/*  97 */     return new FileTreeNode1(child);
/*     */   }
/*     */   
/*     */   public int getIndexOfChild(FileTreeNode1 child) {
/* 101 */     String[] children = this.file.list();
/* 102 */     if (children == null) return -1; 
/* 103 */     String childname = child.file.getName();
/* 104 */     for (int i = 0; i < children.length; i++) {
/* 105 */       if (childname.equals(children[i])) return i; 
/*     */     } 
/* 107 */     return -1;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbo\\util\FileTreeNode1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */