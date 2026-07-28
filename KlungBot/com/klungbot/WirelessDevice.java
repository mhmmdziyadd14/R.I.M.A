/*    */ package com.klungbot;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.DatagramPacket;
/*    */ import java.net.DatagramSocket;
/*    */ import java.net.InetAddress;
/*    */ import java.net.SocketException;
/*    */ import java.util.Arrays;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WirelessDevice
/*    */   extends Device
/*    */ {
/*    */   DatagramSocket ds;
/*    */   DatagramPacket dp;
/*    */   
/*    */   public boolean isConnected() {
/* 27 */     System.out.println("Checking connection...");
/*    */     try {
/* 29 */       this.ds = new DatagramSocket();
/* 30 */       this.ds.setBroadcast(true);
/* 31 */     } catch (SocketException ex) {
/* 32 */       Logger.getLogger(WirelessDevice.class.getName()).log(Level.SEVERE, (String)null, ex);
/*    */     } 
/* 34 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   void write(byte[] data) throws IOException {
/* 39 */     this.dp = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"), 10002);
/* 40 */     this.ds.send(this.dp);
/* 41 */     System.out.println("Writing " + Arrays.toString(data));
/*    */   }
/*    */ 
/*    */   
/*    */   void write(byte[] data, int len) throws IOException {
/* 46 */     this.dp = new DatagramPacket(data, len, InetAddress.getByName("255.255.255.255"), 10002);
/* 47 */     this.ds.send(this.dp);
/* 48 */     System.out.println("Writing " + Arrays.toString(data) + " with length " + len);
/*    */   }
/*    */ 
/*    */   
/*    */   void flush() throws IOException {
/* 53 */     System.out.println("Flushing...");
/*    */   }
/*    */ 
/*    */   
/*    */   boolean open() {
/* 58 */     System.out.println("Open...");
/* 59 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean close() {
/* 64 */     System.out.println("Closing...");
/* 65 */     this.ds.close();
/* 66 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\WirelessDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */