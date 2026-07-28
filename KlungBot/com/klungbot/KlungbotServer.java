/*     */ package com.klungbot;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Arrays;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class KlungbotServer
/*     */   extends Thread
/*     */ {
/*  19 */   private int port = 8123;
/*  20 */   ServerSocket serversocket = null;
/*     */   Maestro maestro;
/*     */   KlungbotServerListener listener;
/*     */   String baseFolder;
/*     */   
/*     */   public KlungbotServer(Maestro m, String baseFolder) {
/*  26 */     this.maestro = m;
/*  27 */     this.baseFolder = baseFolder;
/*     */   }
/*     */   
/*     */   public KlungbotServer(Maestro m, int port) {
/*  31 */     this.maestro = m;
/*  32 */     this.port = port;
/*     */   }
/*     */   
/*     */   public void setListener(KlungbotServerListener l) {
/*  36 */     this.listener = l;
/*     */   }
/*     */   
/*     */   public void begin() {
/*  40 */     if (this.serversocket != null)
/*     */       return;  try {
/*  42 */       this.serversocket = new ServerSocket(this.port);
/*  43 */       System.out.println("Starting server on " + this.serversocket
/*  44 */           .getInetAddress() + ":" + this.port + "...");
/*  45 */       Thread thread = new Thread()
/*     */         {
/*     */           public void run() { while (true) {
/*  48 */               System.out.println("Server is waiting ...");
/*     */               try {
/*  50 */                 Socket connectionsocket = KlungbotServer.this.serversocket.accept();
/*  51 */                 InetAddress client = connectionsocket.getInetAddress();
/*     */ 
/*     */                 
/*  54 */                 BufferedReader input = new BufferedReader(new InputStreamReader(connectionsocket.getInputStream()));
/*     */                 
/*  56 */                 DataOutputStream output = new DataOutputStream(connectionsocket.getOutputStream());
/*  57 */                 PrintWriter extra = new PrintWriter(connectionsocket.getOutputStream());
/*  58 */                 KlungbotServer.this.http_handler(input, output, extra);
/*     */               }
/*  60 */               catch (Exception e) {
/*  61 */                 System.out.println("Error:" + e.getMessage());
/*     */               } 
/*  63 */               if (KlungbotServer.this.serversocket.isClosed()) {
/*  64 */                 KlungbotServer.this.serversocket = null;
/*  65 */                 System.out.println("Server was closed"); return;
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
/*  79 */       return "http://" + thisIp.getHostAddress() + ":" + this.serversocket.getLocalPort();
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
/*     */ 
/*     */ 
/*     */   
/*     */   private void http_handler(BufferedReader input, DataOutputStream output, PrintWriter extra) {
/*     */     try {
/*  98 */       String request = input.readLine();
/*  99 */       StringTokenizer st = new StringTokenizer(request);
/* 100 */       String method = st.nextToken(" ");
/* 101 */       method.toUpperCase();
/* 102 */       if (method.startsWith("POST")) {
/* 103 */         post_handler(input, output, st);
/*     */       }
/* 105 */       else if (method.startsWith("GET")) {
/* 106 */         get_handler(input, output, st, extra);
/*     */       } else {
/*     */         
/*     */         try {
/* 110 */           output.writeBytes(construct_http_header(501, 0));
/* 111 */           output.close();
/*     */           
/*     */           return;
/* 114 */         } catch (Exception e3) {
/* 115 */           System.out.println("error:" + e3.getMessage());
/*     */         }
/*     */       
/*     */       } 
/* 119 */     } catch (Exception e) {}
/*     */   }
/*     */   
/*     */   private String construct_http_header(int return_code, int file_type) {
/* 123 */     StringBuilder s = new StringBuilder("");
/* 124 */     switch (return_code) {
/*     */ 
/*     */ 
/*     */       
/*     */       case 400:
/* 129 */         s.append("400 Bad Request");
/*     */         break;
/*     */       case 403:
/* 132 */         s.append("403 Forbidden");
/*     */         break;
/*     */       case 404:
/* 135 */         s.append("404 Not Found");
/*     */         break;
/*     */       case 500:
/* 138 */         s.append("500 Internal Server Error");
/*     */         break;
/*     */       case 501:
/* 141 */         s.append("501 Not Implemented");
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 148 */     switch (file_type)
/*     */     
/*     */     { 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 0:
/* 168 */         s.append("\r\n");
/* 169 */         return s.toString();
/*     */       case 1: s.append("Content-Type: image/jpeg\r\n");
/*     */       case 2:
/*     */         s.append("Content-Type: image/gif\r\n");
/*     */       case 3:
/*     */         s.append("Content-Type: application/x-zip-compressed\r\n");
/*     */       case 4:
/* 176 */         s.append("Content-Type: image/x-icon\r\n"); }  s.append("Content-Type: text/html\r\n"); } private void get_album(BufferedReader input, DataOutputStream output, PrintWriter extra) { FilenameFilter filter = new FilenameFilter()
/*     */       {
/*     */         public boolean accept(File d, String f) {
/* 179 */           return f.endsWith(".123");
/*     */         }
/*     */       };
/* 182 */     String[] dir = (new File(this.baseFolder)).list(filter);
/* 183 */     Arrays.sort((Object[])dir);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 190 */     extra.println("<script type=text/javascript charset=utf-8>");
/* 191 */     extra.println("var list = [");
/*     */     
/* 193 */     for (int i = 0; i < dir.length; i++) {
/* 194 */       if (i == 0) { extra.println("'" + dir[i].replace(" ", "_").replace("'", "`") + "'"); }
/* 195 */       else { extra.println(", '" + dir[i].replace(" ", "_").replace("'", "`") + "'"); }
/*     */     
/*     */     } 
/* 198 */     extra.println("];");
/* 199 */     extra.println("</script>");
/*     */     
/* 201 */     extra.flush(); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void get_handler(BufferedReader input, DataOutputStream output, StringTokenizer req, PrintWriter extra) {
/* 208 */     String fname, path = req.nextToken(" ?");
/*     */     
/* 210 */     String identifier = null;
/*     */     
/* 212 */     if (path.endsWith("/")) {
/* 213 */       fname = "index.html";
/* 214 */       identifier = "1";
/*     */     } else {
/* 216 */       fname = path.substring(path.lastIndexOf('/') + 1);
/* 217 */       identifier = "1";
/*     */     } 
/*     */     
/* 220 */     if (identifier == "1") {
/* 221 */       get_album(input, output, extra);
/* 222 */       identifier = "0";
/*     */     } 
/*     */     
/* 225 */     System.out.println("\nClient requested:" + (new File(fname)).getAbsolutePath() + "\n");
/* 226 */     FileInputStream requestedfile = null;
/*     */     try {
/* 228 */       requestedfile = new FileInputStream(fname);
/* 229 */     } catch (Exception ex) {
/*     */       try {
/* 231 */         output.writeBytes(construct_http_header(404, 0));
/* 232 */         output.close();
/* 233 */       } catch (Exception e2) {}
/* 234 */       System.out.println("error" + ex.getMessage());
/*     */       return;
/*     */     } 
/*     */     try {
/* 238 */       int type_is = 0;
/* 239 */       if (path.endsWith(".zip")) {
/* 240 */         type_is = 3;
/*     */       }
/* 242 */       if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
/* 243 */         type_is = 1;
/*     */       }
/* 245 */       if (path.endsWith(".gif")) {
/* 246 */         type_is = 2;
/*     */       }
/* 248 */       if (path.endsWith(".ico")) {
/* 249 */         type_is = 3;
/*     */       }
/* 251 */       output.writeBytes(construct_http_header(200, type_is));
/*     */       
/* 253 */       byte[] buffer = new byte[1024];
/*     */       while (true) {
/* 255 */         int b = requestedfile.read(buffer, 0, 1024);
/* 256 */         if (b == -1) {
/*     */           break;
/*     */         }
/* 259 */         output.write(buffer, 0, b);
/*     */       } 
/* 261 */       output.close();
/* 262 */       requestedfile.close();
/* 263 */     } catch (Exception e) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void post_handler(BufferedReader input, DataOutputStream output, StringTokenizer req) throws Exception {
/* 270 */     String path = req.nextToken(" ?");
/* 271 */     String service = path.substring(path.lastIndexOf('/') + 1);
/* 272 */     System.out.println(service);
/* 273 */     if (service.endsWith(".123")) {
/*     */       
/* 275 */       if (this.listener != null) {
/* 276 */         service = service.replace("_", " ").replace("%60", "'");
/* 277 */         this.listener.voteSong(this.baseFolder + "/" + service);
/*     */       } 
/*     */       
/* 280 */       System.out.println(service);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 287 */     if (!service.endsWith(".jap"))
/*     */       return; 
/* 289 */     String parameter = req.nextToken("? ");
/* 290 */     int note = Integer.valueOf(parameter.substring(2)).intValue();
/*     */     
/* 292 */     if (parameter.substring(0, 1).equalsIgnoreCase("p"))
/* 293 */       System.out.println("P " + note); 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\KlungbotServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */