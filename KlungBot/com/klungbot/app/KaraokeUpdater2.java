/*     */ package com.klungbot.app;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Random;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.SwingWorker;
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
/*     */ public class KaraokeUpdater2
/*     */   extends JPanel
/*     */   implements ActionListener, PropertyChangeListener
/*     */ {
/*     */   private JProgressBar progressBar;
/*     */   private JButton startButton;
/*     */   private JTextArea taskOutput;
/*     */   private JLabel taskOutput2;
/*     */   private Task task;
/*     */   public int num_files;
/*     */   public int cur_files;
/*     */   
/*     */   class Task
/*     */     extends SwingWorker<Void, Void>
/*     */   {
/*     */     KaraokeUpdater2 ku;
/*     */     PanelKaraoke pk;
/*     */     int progress;
/*     */     
/*     */     Task(KaraokeUpdater2 k, PanelKaraoke p) {
/* 109 */       this.progress = 0;
/*     */       this.ku = k;
/*     */       this.pk = p; } public int exportFilestoDB(File directory, int max, Connection conn) {
/* 112 */       for (File file : directory.listFiles()) {
/* 113 */         if (file.isFile()) {
/* 114 */           String[] s = { "", "", "", "", "", "", "", "", "", "" };
/* 115 */           s[0] = file.getAbsolutePath();
/*     */           
/*     */           try {
/* 118 */             BufferedReader br = new BufferedReader(new FileReader(file));
/*     */             try {
/* 120 */               String line = br.readLine();
/* 121 */               String result = "";
/*     */ 
/*     */               
/* 124 */               while (line != null) {
/* 125 */                 if (line.startsWith("T:")) {
/* 126 */                   s[1] = line.substring(2).replace("'", "").trim();
/*     */                 }
/* 128 */                 if (line.startsWith("C:")) {
/* 129 */                   if (s[2] != "") s[2] = s[2] + ", "; 
/* 130 */                   s[2] = s[2] + line.substring(2).replace("'", "").trim();
/*     */                 } 
/* 132 */                 if (line.startsWith("A:")) {
/* 133 */                   if (s[3] != "") s[3] = s[3] + ", "; 
/* 134 */                   s[3] = s[3] + line.substring(2).replace("'", "").trim();
/*     */                 } 
/* 136 */                 if (line.startsWith("E:")) {
/* 137 */                   if (s[4] != "") s[4] = s[4] + ", "; 
/* 138 */                   s[4] = s[4] + line.substring(2).replace("'", "").trim();
/*     */                 } 
/* 140 */                 if (line.startsWith("G:")) {
/* 141 */                   if (s[5] != "") s[5] = s[5] + ", "; 
/* 142 */                   s[5] = s[5] + line.substring(2).replace("'", "").trim();
/*     */                 } 
/* 144 */                 if (line.startsWith("O:")) {
/* 145 */                   if (s[6] != "") s[6] = s[6] + ", "; 
/* 146 */                   s[6] = s[6] + line.substring(2).replace("'", "").trim();
/*     */                 } 
/* 148 */                 if (line.startsWith("M:")) {
/* 149 */                   s[7] = line.substring(2).replace("'", "").trim();
/*     */                 }
/* 151 */                 if (line.startsWith("Q:")) {
/* 152 */                   s[8] = line.substring(2).replace("'", "").trim();
/*     */                 }
/* 154 */                 if (line.startsWith("K:")) {
/* 155 */                   s[9] = line.substring(2).replace("'", "").trim();
/*     */                 }
/* 157 */                 line = br.readLine();
/*     */               } 
/*     */               
/* 160 */               insertIntoDB(conn, s);
/* 161 */               this.progress++;
/* 162 */               this.ku.set_cur_files(this.progress);
/* 163 */               setProgress(100 * this.progress / max);
/*     */             
/*     */             }
/* 166 */             catch (IOException ex) {
/* 167 */               Logger.getLogger(KaraokeUpdater2.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */             }
/*     */             finally {}
/* 170 */           } catch (FileNotFoundException ex) {
/* 171 */             Logger.getLogger(KaraokeUpdater2.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */           } 
/*     */         } 
/*     */         
/* 175 */         if (file.isDirectory()) {
/* 176 */           this.progress = exportFilestoDB(file, max, conn);
/*     */         }
/*     */       } 
/*     */       
/* 180 */       return this.progress;
/*     */     }
/*     */     
/*     */     public int countFilesInDirectory(File directory) {
/*     */       int count = 0;
/*     */       for (File file : directory.listFiles()) {
/*     */         if (file.isFile())
/*     */           count++; 
/*     */         if (file.isDirectory())
/*     */           count += countFilesInDirectory(file); 
/*     */       } 
/*     */       return count;
/*     */     }
/*     */     
/*     */     public Void doInBackground() {
/* 195 */       Random random = new Random();
/* 196 */       int progress = 0, cap_progress = 0;
/*     */ 
/*     */       
/* 199 */       File album = new File("album");
/* 200 */       cap_progress = countFilesInDirectory(album);
/* 201 */       this.ku.set_num_files(cap_progress);
/*     */ 
/*     */       
/*     */       try {
/* 205 */         Thread.sleep(1000L);
/* 206 */       } catch (InterruptedException ignore) {}
/*     */       
/* 208 */       setProgress(0);
/*     */       
/*     */       try {
/* 211 */         Thread.sleep((1000 + random.nextInt(2000)));
/* 212 */       } catch (InterruptedException ignore) {}
/*     */       
/* 214 */       Statement stmt = null;
/* 215 */       Connection conn = null;
/* 216 */       ResultSet rs = null;
/*     */       try {
/* 218 */         conn = DriverManager.getConnection("jdbc:derby:karaoke_db;create=true");
/* 219 */       } catch (SQLException ex) {
/* 220 */         Logger.getLogger(KaraokeUpdater2.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */       } 
/*     */ 
/*     */       
/* 224 */       String createSQL = "create table songs2 (id integer not null generated always as identity (start with 1, increment by 1),   filepath varchar(1024) not null, title varchar(128) not null,  composer varchar(128), arranger varchar(128), editor varchar(128), genre varchar(128), origin varchar(128), m varchar(8), q varchar(8), k varchar(8), constraint prim_key2 primary key (id))";
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
/*     */       try {
/* 239 */         stmt = conn.createStatement();
/* 240 */         DatabaseMetaData dbmd = conn.getMetaData();
/*     */         
/* 242 */         rs = dbmd.getTables(null, "APP", "SONGS2", null);
/* 243 */         if (!rs.next()) {
/* 244 */           stmt.execute(createSQL);
/*     */         }
/*     */       }
/* 247 */       catch (SQLException ex) {
/* 248 */         Logger.getLogger(KaraokeUpdater2.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */       } 
/*     */       
/*     */       try {
/* 252 */         stmt.executeUpdate("delete from songs2");
/* 253 */       } catch (SQLException ex) {
/* 254 */         Logger.getLogger(KaraokeUpdater2.class.getName()).log(Level.SEVERE, (String)null, ex);
/*     */       } 
/*     */       
/* 257 */       exportFilestoDB(album, cap_progress, conn);
/* 258 */       this.pk.updateSongList();
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
/* 280 */       return null;
/*     */     }
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
/*     */     public void done() {
/* 297 */       Toolkit.getDefaultToolkit().beep();
/* 298 */       KaraokeUpdater2.this.startButton.setEnabled(true);
/* 299 */       KaraokeUpdater2.this.taskOutput.append("Done!\n");
/* 300 */       KaraokeUpdater2.this.taskOutput2.setText("Done!");
/*     */     } public void insertIntoDB(Connection conn, String[] s) { String query = ""; query = "'" + s[0] + "','" + s[1] + "','" + s[2] + "','" + s[3] + "','" + s[4] + "','" + s[5] + "','" + s[6] + "','" + s[7] + "','" + s[8] + "','" + s[9] + "'"; try {
/*     */         PreparedStatement pstmt = conn.prepareStatement("insert into songs2 (filepath, title, composer, arranger, editor, genre, origin, m, q, k) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); pstmt.setString(1, s[0]); pstmt.setString(2, s[1]); pstmt.setString(3, s[2]); pstmt.setString(4, s[3]); pstmt.setString(5, s[4]); pstmt.setString(6, s[5]); pstmt.setString(7, s[6]); pstmt.setString(8, s[7]); pstmt.setString(9, s[8]); pstmt.setString(10, s[9]); Statement stmt = null; ResultSet rs = null; stmt = conn.createStatement(); rs = stmt.executeQuery("select count(*) from songs2 where filepath='" + s[0] + "'"); rs.next();
/*     */         if (rs.getInt(1) == 0)
/*     */           pstmt.executeUpdate(); 
/*     */       } catch (SQLException ex) {
/*     */         System.out.println("Containing special character for " + s[0]);
/* 307 */       }  } } public KaraokeUpdater2(PanelKaraoke p) { super(new BorderLayout());
/*     */ 
/*     */     
/* 310 */     this.startButton = new JButton("Start");
/* 311 */     this.startButton.setActionCommand("start");
/* 312 */     this.startButton.addActionListener(this);
/*     */     
/* 314 */     this.progressBar = new JProgressBar(0, 100);
/* 315 */     this.progressBar.setValue(0);
/*     */ 
/*     */ 
/*     */     
/* 319 */     this.progressBar.setStringPainted(true);
/*     */     
/* 321 */     this.taskOutput = new JTextArea(5, 20);
/* 322 */     this.taskOutput.setMargin(new Insets(5, 5, 5, 5));
/* 323 */     this.taskOutput.setEditable(false);
/*     */     
/* 325 */     this.taskOutput2 = new JLabel();
/* 326 */     this.taskOutput2.setText("Preparing...");
/* 327 */     JPanel panel = new JPanel();
/*     */ 
/*     */ 
/*     */     
/* 331 */     add(this.taskOutput2, "First");
/* 332 */     add(this.progressBar, "Center");
/*     */ 
/*     */ 
/*     */     
/* 336 */     setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
/* 337 */     setPreferredSize(new Dimension(400, 100));
/*     */     
/* 339 */     this.progressBar.setIndeterminate(true);
/* 340 */     this.task = new Task(this, p);
/* 341 */     this.task.addPropertyChangeListener(this);
/* 342 */     this.task.execute(); }
/*     */   
/* 344 */   public int get_num_files() { return this.num_files; }
/* 345 */   public void set_num_files(int a) { this.num_files = a; }
/* 346 */   public int get_cur_files() { return this.cur_files; } public void set_cur_files(int a) {
/* 347 */     this.cur_files = a;
/*     */   }
/*     */   
/*     */   public void actionPerformed(ActionEvent evt) {
/* 351 */     this.startButton.setEnabled(false);
/*     */   }
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
/*     */   public void propertyChange(PropertyChangeEvent evt) {
/* 364 */     if ("progress" == evt.getPropertyName()) {
/* 365 */       int progress = ((Integer)evt.getNewValue()).intValue();
/* 366 */       this.progressBar.setIndeterminate(false);
/* 367 */       this.progressBar.setValue(progress);
/* 368 */       this.taskOutput2.setText("Completing " + get_cur_files() + " of " + get_num_files() + " files task...");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\KaraokeUpdater2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */