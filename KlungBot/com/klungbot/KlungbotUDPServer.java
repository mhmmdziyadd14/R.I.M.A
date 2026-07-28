/*    */ package com.klungbot;
/*    */ 
/*    */ import java.net.DatagramPacket;
/*    */ import java.net.DatagramSocket;
/*    */ import java.net.InetAddress;
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
/*    */ public class KlungbotUDPServer
/*    */   extends Thread
/*    */ {
/* 19 */   private int port = 8123;
/*    */   DatagramSocket socket;
/*    */   KlungbotServerListener listener;
/*    */   
/*    */   public KlungbotUDPServer(KlungbotServerListener l) {
/* 24 */     this.listener = l;
/*    */   }
/*    */   
/*    */   public String getAddress() {
/*    */     try {
/* 29 */       InetAddress thisIp = InetAddress.getLocalHost();
/* 30 */       return thisIp.getHostAddress() + ":" + this.socket.getLocalPort();
/*    */     }
/* 32 */     catch (Exception ex) {
/* 33 */       return "0.0.0.0";
/*    */     } 
/*    */   }
/*    */   public void begin() {
/* 37 */     if (this.socket != null)
/*    */       return;  try {
/* 39 */       this.socket = new DatagramSocket(this.port);
/* 40 */       this.listener.log("UDP server is ready", null);
/* 41 */       this.listener.log("UDP address", getAddress());
/* 42 */       Thread thread = new Thread()
/*    */         {
/* 44 */           public void run() { byte[] data = new byte[1024];
/* 45 */             DatagramPacket packet = new DatagramPacket(data, data.length);
/*    */             while (true) {
/*    */               try {
/* 48 */                 KlungbotUDPServer.this.socket.receive(packet);
/* 49 */                 KlungbotUDPServer.this.data_handler(packet.getData(), packet.getLength());
/*    */               }
/* 51 */               catch (Exception e) {}
/* 52 */               if (KlungbotUDPServer.this.socket.isClosed()) {
/* 53 */                 KlungbotUDPServer.this.socket = null;
/* 54 */                 KlungbotUDPServer.this.listener.log("UDP server closed", null); return;
/*    */               } 
/*    */             }  } };
/* 57 */       thread.start();
/*    */     }
/* 59 */     catch (Exception e) {
/* 60 */       System.err.println("UDP Server :" + e.getMessage());
/*    */       return;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void finish() {
/* 66 */     if (this.socket == null)
/*    */       return;  try {
/* 68 */       this.socket.close();
/*    */     }
/* 70 */     catch (Exception ex) {}
/*    */   }
/*    */   
/*    */   private void data_handler(byte[] data, int len) {
/* 74 */     System.out.println("UDP: " + (data[0] & 0xF0) + " " + (data[0] & 0xF) + " " + data[1] + " " + data[2]);
/*    */ 
/*    */ 
/*    */     
/* 78 */     switch (data[0] & 0xF0) { case 128:
/* 79 */         this.listener.midiOff(data[1], (byte)(data[0] & 0xF)); break;
/* 80 */       case 144: this.listener.midiOn(data[1], data[2], (byte)(data[0] & 0xF));
/*    */         break; }
/*    */   
/*    */   }
/*    */   public void send(byte type, byte channel, byte note, byte velocity) {
/* 85 */     byte[] data = new byte[3];
/* 86 */     data[0] = (byte)(type | channel);
/* 87 */     data[1] = note;
/* 88 */     data[2] = velocity;
/*    */     try {
/* 90 */       DatagramSocket ds = new DatagramSocket();
/* 91 */       InetAddress toAddr = InetAddress.getLocalHost();
/* 92 */       DatagramPacket packet = new DatagramPacket(data, data.length, toAddr, this.port);
/*    */       
/* 94 */       ds.send(packet);
/* 95 */     } catch (Exception ex) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\KlungbotUDPServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */