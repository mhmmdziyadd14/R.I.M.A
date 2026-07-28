/*    */ package com.klungbot;
/*    */ 
/*    */ import javax.sound.midi.MidiMessage;
/*    */ import javax.sound.midi.Receiver;
/*    */ import javax.sound.midi.ShortMessage;
/*    */ import javax.sound.midi.Synthesizer;
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
/*    */ public class MidiPlayer
/*    */   implements Receiver
/*    */ {
/*    */   AudioEngineer pm;
/*    */   Synthesizer synthesizer;
/*    */   Receiver receiver;
/* 23 */   final int[] channelMap = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MidiPlayer(AudioEngineer pm) {
/* 29 */     this.pm = pm;
/* 30 */     this.synthesizer = pm.msyn;
/*    */   }
/*    */   
/*    */   public void open() {
/*    */     try {
/* 35 */       this.receiver = this.synthesizer.getReceiver();
/*    */     }
/* 37 */     catch (Exception x) {}
/*    */   }
/*    */   
/*    */   public boolean openChannel(int i) {
/* 41 */     synchronized (this.channelMap) {
/* 42 */       if (this.channelMap[i] == -1) {
/* 43 */         this.channelMap[i] = this.pm.borrowChannel();
/*    */       }
/*    */     } 
/* 46 */     return (this.channelMap[i] != -1);
/*    */   }
/*    */   
/*    */   public void closeChannel(int i) {
/* 50 */     synchronized (this.channelMap) {
/* 51 */       if (this.channelMap[i] != -1) {
/* 52 */         this.pm.returnChannel(this.channelMap[i]);
/* 53 */         this.channelMap[i] = -1;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void send(MidiMessage message, long timeStamp) {
/* 61 */     if (this.receiver == null)
/* 62 */       return;  this.receiver.send(message, timeStamp);
/*    */   }
/*    */   public void send(ShortMessage msg, long ts) {
/*    */     int chn2;
/* 66 */     if (this.receiver == null)
/*    */       return; 
/* 68 */     int chn1 = msg.getChannel();
/* 69 */     synchronized (this.channelMap) {
/* 70 */       chn2 = this.channelMap[chn1];
/*    */     } 
/* 72 */     if (chn2 < 0)
/*    */       return; 
/*    */     try {
/* 75 */       ShortMessage msg1 = new ShortMessage(msg.getCommand(), chn2, msg.getData1(), msg.getData2());
/* 76 */       this.receiver.send(msg1, ts);
/* 77 */     } catch (Exception ex) {}
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 82 */     for (int i = 0; i < this.channelMap.length; i++) {
/* 83 */       closeChannel(i);
/*    */     }
/* 85 */     this.receiver.close();
/* 86 */     this.receiver = null;
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\MidiPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */