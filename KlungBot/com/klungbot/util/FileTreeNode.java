/*    */ package com.klungbot.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileTreeNode
/*    */   extends DefaultMutableTreeNode
/*    */ {
/*    */   File file;
/*    */   
/*    */   public FileTreeNode(String name) {
/* 21 */     this.file = new File(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public FileTreeNode(File node) {
/* 26 */     this.file = node;
/*    */   }
/*    */   
/*    */   public void expand(String extension) {
/* 30 */     String[] names = this.file.list();
/* 31 */     if (names == null)
/* 32 */       return;  ArrayList<File> files = new ArrayList<>();
/* 33 */     Arrays.sort((Object[])names);
/* 34 */     for (int i = 0; i < names.length; i++) {
/* 35 */       File f = new File(this.file, names[i]);
/* 36 */       if (f.isFile()) {
/* 37 */         files.add(f);
/*    */       } else {
/*    */         
/* 40 */         add(new FileTreeNode(f));
/*    */       } 
/*    */     } 
/* 43 */     for (File f : files) {
/* 44 */       add(new FileTreeNode(f));
/*    */     }
/*    */   }
/*    */   
/*    */   public void expandAll(String extension) {
/* 49 */     String[] names = this.file.list();
/* 50 */     if (names == null)
/* 51 */       return;  ArrayList<File> files = new ArrayList<>();
/* 52 */     Arrays.sort((Object[])names);
/* 53 */     for (int i = 0; i < names.length; i++) {
/* 54 */       File f = new File(this.file, names[i]);
/* 55 */       if (f.isDirectory()) {
/* 56 */         FileTreeNode child = new FileTreeNode(f);
/* 57 */         add(child);
/* 58 */         child.expandAll(extension);
/*    */       }
/* 60 */       else if (f.getName().endsWith(extension)) {
/* 61 */         files.add(f);
/*    */       } 
/*    */     } 
/* 64 */     for (File f : files) {
/* 65 */       add(new FileTreeNode(f));
/*    */     }
/*    */   }
/*    */   
/*    */   public void expandFolder() {
/* 70 */     String[] names = this.file.list();
/* 71 */     if (names == null)
/* 72 */       return;  Arrays.sort((Object[])names);
/* 73 */     for (int i = 0; i < names.length; i++) {
/* 74 */       File f = new File(this.file, names[i]);
/* 75 */       if (f.isDirectory()) {
/* 76 */         FileTreeNode child = new FileTreeNode(f);
/* 77 */         add(child);
/* 78 */         child.expandFolder();
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean isLeaf() {
/* 84 */     return this.file.isFile();
/*    */   }
/*    */   
/*    */   public File getFile() {
/* 88 */     return this.file;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 93 */     return this.file.getName();
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbo\\util\FileTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */