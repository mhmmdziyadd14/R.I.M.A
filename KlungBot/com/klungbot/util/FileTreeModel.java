/*    */ package com.klungbot.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import javax.swing.event.TreeModelListener;
/*    */ import javax.swing.tree.TreeModel;
/*    */ import javax.swing.tree.TreePath;
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
/*    */ public class FileTreeModel
/*    */   implements TreeModel
/*    */ {
/*    */   protected FileTreeNode1 root;
/*    */   
/*    */   public FileTreeModel(String rootName) {
/* 25 */     this.root = new FileTreeNode1(rootName);
/*    */   }
/*    */   
/*    */   public FileTreeModel(File root) {
/* 29 */     this.root = new FileTreeNode1(root);
/*    */   }
/*    */   
/*    */   public Object getRoot() {
/* 33 */     return this.root;
/*    */   }
/*    */   public boolean isLeaf(Object node) {
/* 36 */     return ((FileTreeNode1)node).isLeaf();
/*    */   }
/*    */   
/*    */   public int getChildCount(Object parent) {
/* 40 */     return ((FileTreeNode1)parent).getCount();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getChild(Object parent, int index) {
/* 47 */     return ((FileTreeNode1)parent).getChild(index);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getIndexOfChild(Object parent, Object child) {
/* 52 */     return ((FileTreeNode1)parent).getIndexOfChild((FileTreeNode1)child);
/*    */   }
/*    */   
/*    */   public void valueForPathChanged(TreePath path, Object newvalue) {}
/*    */   
/*    */   public void addTreeModelListener(TreeModelListener l) {}
/*    */   
/*    */   public void removeTreeModelListener(TreeModelListener l) {}
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbo\\util\FileTreeModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */