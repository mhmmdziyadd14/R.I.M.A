/*    */ package com.klungbot;
/*    */ 
/*    */ import gnu.io.CommPortIdentifier;
/*    */ import gnu.io.SerialPort;
/*    */ import gnu.io.SerialPortEvent;
/*    */ import gnu.io.SerialPortEventListener;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ 
/*    */ public class SerialDevice
/*    */   extends Device
/*    */   implements SerialPortEventListener
/*    */ {
/*    */   InputStream inputStream;
/*    */   OutputStream outputStream;
/*    */   BufferedReader ifile;
/*    */   SerialPort port;
/*    */   
/*    */   public boolean isConnected() {
/* 23 */     return (this.port != null);
/*    */   }
/*    */ 
/*    */   
/*    */   void write(byte[] data) throws IOException {
/* 28 */     if (this.port == null)
/* 29 */       return;  this.outputStream.write(data);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   void write(byte[] data, int len) throws IOException {
/* 35 */     if (this.port == null)
/* 36 */       return;  this.outputStream.write(data, 0, len);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   void flush() throws IOException {
/* 42 */     this.outputStream.flush();
/*    */   }
/*    */ 
/*    */   
/*    */   boolean open() {
/*    */     try {
/* 48 */       CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(this.devPort);
/* 49 */       if (portIdentifier != null && 
/* 50 */         !portIdentifier.isCurrentlyOwned()) {
/* 51 */         this.port = (SerialPort)portIdentifier.open(getClass().getName(), 2000);
/* 52 */         if (this.port != null) {
/* 53 */           this.inputStream = this.port.getInputStream();
/* 54 */           this.outputStream = this.port.getOutputStream();
/* 55 */           this.port.setSerialPortParams(115200, 8, 1, 0);
/*    */ 
/*    */ 
/*    */           
/* 59 */           this.port.addEventListener(this);
/* 60 */           this.port.notifyOnDataAvailable(true);
/* 61 */           return true;
/*    */         }
/*    */       
/*    */       }
/*    */     
/* 66 */     } catch (Exception ex) {
/* 67 */       System.err.println("ERROR: " + ex.getMessage());
/*    */     } 
/* 69 */     System.err.println("Cannot open device " + this.devPort + " for " + this.name);
/* 70 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean close() {
/* 75 */     if (this.port == null) return false; 
/* 76 */     this.port.removeEventListener();
/* 77 */     this.port.close();
/* 78 */     this.port = null;
/* 79 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void serialEvent(SerialPortEvent spe) {
/* 85 */     StringBuilder str = new StringBuilder();
/*    */     
/*    */     try {
/* 88 */       while (this.inputStream.available() > 0) {
/* 89 */         char ch = (char)this.inputStream.read();
/* 90 */         if (ch < ' ')
/* 91 */           break;  str.append(ch);
/*    */       } 
/* 93 */     } catch (Exception ex) {
/*    */       return;
/*    */     } 
/*    */     
/* 97 */     System.out.println(str);
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\SerialDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */