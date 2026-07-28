/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.util.FileTreeNode;
/*     */ import java.awt.AWTException;
/*     */ import java.awt.Robot;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Enumeration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KlungbotHttpServer
/*     */   extends Thread
/*     */ {
/*     */   private int port;
/*  26 */   ServerSocket serversocket = null;
/*     */   
/*     */   int current_key_play;
/*     */   
/*     */   int current_key_stop;
/*     */   
/*     */   KlungbotServerListener listener;
/*     */   
/*     */   public int ix;
/*     */ 
/*     */   
/*     */   public void setPort() {
/*  38 */     this.port = this.port;
/*     */   }
/*     */   
/*     */   public void begin() {
/*  42 */     if (this.serversocket != null)
/*     */       return;  try {
/*  44 */       this.serversocket = new ServerSocket(this.port);
/*  45 */       Thread thread = new Thread()
/*     */         {
/*  47 */           public void run() { KlungbotHttpServer.this.listener.log("HTTP server is ready", null);
/*  48 */             KlungbotHttpServer.this.listener.log("HTTP address", KlungbotHttpServer.this.getAddress());
/*     */             while (true) {
/*     */               try {
/*  51 */                 Socket connectionsocket = KlungbotHttpServer.this.serversocket.accept();
/*  52 */                 InetAddress client = connectionsocket.getInetAddress();
/*     */                 
/*  54 */                 BufferedReader input = new BufferedReader(new InputStreamReader(connectionsocket.getInputStream()));
/*     */                 
/*  56 */                 DataOutputStream output = new DataOutputStream(connectionsocket.getOutputStream());
/*  57 */                 KlungbotHttpServer.this.http_handler(client, input, output);
/*  58 */                 output.close();
/*     */               }
/*  60 */               catch (Exception e) {
/*  61 */                 System.err.println("HTTP server :" + e.getMessage());
/*     */               } 
/*  63 */               if (KlungbotHttpServer.this.serversocket.isClosed()) {
/*  64 */                 KlungbotHttpServer.this.serversocket = null;
/*  65 */                 KlungbotHttpServer.this.listener.log("HTTP server closed", null); return;
/*     */               } 
/*     */             }  } };
/*  68 */       thread.start();
/*     */     }
/*  70 */     catch (Exception e) {
/*  71 */       System.out.println("\nFatal Error:" + e.getMessage());
/*     */       return;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getAddress() {
/*     */     try {
/*  78 */       InetAddress thisIp = InetAddress.getLocalHost();
/*  79 */       return thisIp.getHostAddress() + ":" + this.serversocket.getLocalPort();
/*     */     }
/*  81 */     catch (Exception ex) {
/*  82 */       return "0.0.0.0";
/*     */     } 
/*     */   }
/*     */   public void finish() {
/*  86 */     if (this.serversocket == null)
/*     */       return;  try {
/*  88 */       this.serversocket.close();
/*     */     }
/*  90 */     catch (Exception ex) {}
/*     */   }
/*     */   
/*  93 */   public KlungbotHttpServer(KlungbotServerListener l) { this.ix = 0;
/*     */     this.listener = l;
/*     */     this.port = 8777; } private void midi_handler(String[] splitter) throws IOException, AWTException {
/*  96 */     byte data1 = 0;
/*  97 */     byte data2 = 100;
/*  98 */     byte data3 = 0;
/*  99 */     data1 = Byte.parseByte(splitter[2]);
/* 100 */     if (splitter.length >= 3) {
/* 101 */       data2 = Byte.parseByte(splitter[3]);
/* 102 */       if (splitter.length >= 4) {
/* 103 */         data3 = Byte.parseByte(splitter[4]);
/*     */       }
/*     */     } 
/* 106 */     Robot robot = new Robot();
/* 107 */     switch (splitter[1]) {
/*     */       case "100":
/* 109 */         this.listener.log("HTTP play", "first choice");
/* 110 */         this.listener.play();
/*     */         break;
/*     */       case "80":
/* 113 */         if (this.current_key_play != data1) {
/* 114 */           this.listener.midiOn(data1, data2, (byte)(data3 - 1));
/*     */           
/* 116 */           this.current_key_stop = -1;
/*     */ 
/*     */ 
/*     */           
/* 120 */           robot.keyPress(39);
/*     */           
/* 122 */           robot.keyRelease(39);
/*     */         } 
/*     */         break;
/*     */ 
/*     */       
/*     */       case "90":
/* 128 */         if (this.current_key_stop != data1) {
/*     */           
/* 130 */           this.listener.midiOff(data1, (byte)(data3 - 1));
/*     */           
/* 132 */           this.current_key_play = -1;
/*     */         } 
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void printTree(PrintWriter w, FileTreeNode t) {
/* 139 */     if (t.isLeaf()) {
/* 140 */       String s = t.getFile().getPath();
/*     */       
/* 142 */       w.println(s);
/*     */     } else {
/*     */       
/* 145 */       Enumeration<FileTreeNode> children = t.children();
/* 146 */       while (children.hasMoreElements()) {
/* 147 */         FileTreeNode t1 = children.nextElement();
/* 148 */         printTree(w, t1);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void http_handler(InetAddress client, BufferedReader input, DataOutputStream output) throws IOException {
/* 155 */     PrintWriter extra = new PrintWriter(output);
/*     */     try {
/* 157 */       String tmp = input.readLine();
/*     */       
/* 159 */       System.out.println(tmp);
/* 160 */       if (tmp.startsWith("POST")) {
/* 161 */         FileTreeNode root; StringBuilder s; int i; String[] splitter = tmp.split("/");
/* 162 */         System.out.println(splitter[1]);
/* 163 */         switch (splitter[1]) {
/*     */           case "list":
/* 165 */             root = new FileTreeNode(this.listener.getAlbumFolder());
/* 166 */             root.expandAll(".123");
/* 167 */             extra.println("HTTP/1.0 200 OK");
/* 168 */             extra.println("Content-Type: text/html");
/* 169 */             extra.println("Server: Bot");
/* 170 */             extra.println("");
/* 171 */             printTree(extra, root);
/* 172 */             this.listener.log("HTTP get list", client.getHostAddress());
/*     */             break;
/*     */           case "play":
/* 175 */             extra.println("HTTP/1.0 200 OK");
/* 176 */             s = new StringBuilder();
/* 177 */             s.append(splitter[2]);
/* 178 */             for (i = 3; i < splitter.length - 2; i++) {
/* 179 */               s.append(File.separator);
/* 180 */               s.append(splitter[i].replace('@', '_'));
/*     */             } 
/* 182 */             this.listener.log("HTTP play", s.toString());
/* 183 */             this.listener.play(s.toString());
/*     */             break;
/*     */           case "finish":
/* 186 */             extra.println("HTTP/1.0 200 OK");
/* 187 */             this.listener.finish();
/* 188 */             this.listener.log("HTTP finish", null);
/*     */             break;
/*     */           default:
/* 191 */             midi_handler(splitter);
/* 192 */             extra.println("HTTP/1.0 200 OK");
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */       
/*     */       } 
/* 199 */     } catch (Exception e) {
/* 200 */       System.out.println("Server Error " + e.getMessage());
/*     */     } finally {
/*     */       
/* 203 */       extra.flush();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\KlungbotHttpServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */