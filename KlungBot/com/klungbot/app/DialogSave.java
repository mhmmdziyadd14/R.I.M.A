/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.util.FileTreeNode;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.File;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.MutableTreeNode;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class DialogSave extends JDialog {
/*     */   public static final int RET_CANCEL = 0;
/*     */   public static final int RET_OK = 1;
/*     */   FileTreeNode ftree;
/*     */   DefaultTreeModel ftmodel;
/*     */   Main parent;
/*     */   File current;
/*     */   private JButton btNewFolder;
/*     */   private JButton cancelButton;
/*     */   private JLabel jLabel1;
/*     */   private JLabel jLabel2;
/*     */   
/*     */   public DialogSave(Main parent, String baseFolder) {
/*  39 */     super(parent, true);
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
/* 228 */     this.returnStatus = 0;
/*     */     initComponents();
/*     */     setLocationRelativeTo(parent);
/*     */     this.ftree = new FileTreeNode(baseFolder);
/*     */     this.ftree.expandFolder();
/*     */     this.ftmodel = new DefaultTreeModel((TreeNode)this.ftree);
/*     */     this.tFolder.setModel(this.ftmodel);
/*     */     this.parent = parent;
/*     */     this.tfFile.requestFocus();
/*     */   }
/*     */   
/*     */   private JPanel jPanel1;
/*     */   private JPanel jPanel2;
/*     */   private JPanel jPanel3;
/*     */   private JPanel jPanel4;
/*     */   private JScrollPane jScrollPane1;
/*     */   private JButton okButton;
/*     */   private JTree tFolder;
/*     */   private JTextField tfFile;
/*     */   private JTextField tfFolder;
/*     */   private int returnStatus;
/*     */   
/*     */   public int getReturnStatus() {
/*     */     return this.returnStatus;
/*     */   }
/*     */   
/*     */   private void initComponents() {
/*     */     this.jPanel1 = new JPanel();
/*     */     this.okButton = new JButton();
/*     */     this.cancelButton = new JButton();
/*     */     this.jPanel2 = new JPanel();
/*     */     this.jScrollPane1 = new JScrollPane();
/*     */     this.tFolder = new JTree();
/*     */     this.jPanel3 = new JPanel();
/*     */     this.jLabel1 = new JLabel();
/*     */     this.tfFolder = new JTextField();
/*     */     this.btNewFolder = new JButton();
/*     */     this.jPanel4 = new JPanel();
/*     */     this.tfFile = new JTextField();
/*     */     this.jLabel2 = new JLabel();
/*     */     setTitle("Save File");
/*     */     setIconImage(null);
/*     */     setMinimumSize(new Dimension(400, 300));
/*     */     setModal(true);
/*     */     setType(Window.Type.UTILITY);
/*     */     addWindowListener(new WindowAdapter() {
/*     */           public void windowClosing(WindowEvent evt) {
/*     */             DialogSave.this.closeDialog(evt);
/*     */           }
/*     */         });
/*     */     this.okButton.setText("OK");
/*     */     this.okButton.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*     */             DialogSave.this.okButtonActionPerformed(evt);
/*     */           }
/*     */         });
/*     */     this.jPanel1.add(this.okButton);
/*     */     this.cancelButton.setText("Cancel");
/*     */     this.cancelButton.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*     */             DialogSave.this.cancelButtonActionPerformed(evt);
/*     */           }
/*     */         });
/*     */     this.jPanel1.add(this.cancelButton);
/*     */     getContentPane().add(this.jPanel1, "Last");
/*     */     this.jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/*     */     this.jPanel2.setLayout(new BorderLayout());
/*     */     this.jScrollPane1.setViewportView(this.tFolder);
/*     */     this.jPanel2.add(this.jScrollPane1, "Center");
/*     */     this.jPanel3.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
/*     */     this.jPanel3.setLayout(new BorderLayout());
/*     */     this.jLabel1.setText("Folder:  ");
/*     */     this.jPanel3.add(this.jLabel1, "West");
/*     */     this.jPanel3.add(this.tfFolder, "Center");
/*     */     this.btNewFolder.setText("New");
/*     */     this.btNewFolder.addActionListener(new ActionListener() {
/*     */           public void actionPerformed(ActionEvent evt) {
/*     */             DialogSave.this.btNewFolderActionPerformed(evt);
/*     */           }
/*     */         });
/*     */     this.jPanel3.add(this.btNewFolder, "East");
/*     */     this.jPanel2.add(this.jPanel3, "First");
/*     */     this.jPanel4.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
/*     */     this.jPanel4.setLayout(new BorderLayout());
/*     */     this.tfFile.setText("jTextField1");
/*     */     this.jPanel4.add(this.tfFile, "Center");
/*     */     this.jLabel2.setHorizontalAlignment(2);
/*     */     this.jLabel2.setText("File:  ");
/*     */     this.jPanel4.add(this.jLabel2, "West");
/*     */     this.jPanel2.add(this.jPanel4, "Last");
/*     */     getContentPane().add(this.jPanel2, "Center");
/*     */     pack();
/*     */   }
/*     */   
/*     */   private void okButtonActionPerformed(ActionEvent evt) {
/*     */     doClose(1);
/*     */   }
/*     */   
/*     */   private void cancelButtonActionPerformed(ActionEvent evt) {
/*     */     doClose(0);
/*     */   }
/*     */   
/*     */   private void closeDialog(WindowEvent evt) {
/*     */     doClose(0);
/*     */   }
/*     */   
/*     */   private void btNewFolderActionPerformed(ActionEvent evt) {
/*     */     FileTreeNode selected = (FileTreeNode)this.tFolder.getSelectionPath().getLastPathComponent();
/*     */     if (selected == null)
/*     */       selected = (FileTreeNode)this.ftmodel.getRoot(); 
/*     */     String fname = selected.getFile().getPath() + File.separator + this.tfFolder.getText();
/*     */     File newFolder = new File(fname);
/*     */     try {
/*     */       if (newFolder.mkdir()) {
/*     */         FileTreeNode newNode = new FileTreeNode(newFolder);
/*     */         this.ftmodel.insertNodeInto((MutableTreeNode)newNode, (MutableTreeNode)selected, 0);
/*     */         TreePath path = new TreePath((Object[])this.ftmodel.getPathToRoot((TreeNode)newNode));
/*     */         this.tFolder.setSelectionPath(path);
/*     */         this.tFolder.expandPath(path);
/*     */       } 
/*     */     } catch (Exception ex) {
/*     */       this.parent.setMessage("Could not create folder " + fname, Color.red);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doClose(int retStatus) {
/*     */     this.returnStatus = retStatus;
/*     */     setVisible(false);
/*     */   }
/*     */   
/*     */   public int showDialog(File fname) {
/*     */     this.current = fname;
/*     */     this.tfFile.setText(fname.getName());
/*     */     setVisible(true);
/*     */     return this.returnStatus;
/*     */   }
/*     */   
/*     */   public String getFilePath() {
/*     */     String fname;
/*     */     TreePath selected = this.tFolder.getSelectionPath();
/*     */     if (selected != null) {
/*     */       FileTreeNode node = (FileTreeNode)selected.getLastPathComponent();
/*     */       fname = node.getFile().getPath() + File.separator + this.tfFile.getText();
/*     */     } else {
/*     */       String fparent = this.current.getParent();
/*     */       if (fparent == null) {
/*     */         fname = this.parent.albumFolder + this.tfFile.getText();
/*     */       } else {
/*     */         fname = fparent + File.separator + this.tfFile.getText();
/*     */       } 
/*     */     } 
/*     */     return fname;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\DialogSave.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */