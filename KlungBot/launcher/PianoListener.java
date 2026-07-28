/*    */ package launcher;
/*    */ 
/*    */ import com.klungbot.Maestro;
/*    */ import java.awt.Button;
/*    */ import javafx.fxml.FXML;
/*    */ import javafx.scene.layout.Pane;
/*    */ import javax.sound.midi.MidiMessage;
/*    */ import javax.sound.midi.Receiver;
/*    */ import javax.sound.midi.ShortMessage;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PianoListener
/*    */   implements Receiver
/*    */ {
/*    */   @FXML
/*    */   Pane piano;
/*    */   @FXML
/*    */   Button btn1;
/*    */   Maestro maestro;
/*    */   
/*    */   PianoListener(Maestro m) {
/* 27 */     this.maestro = m;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void send(MidiMessage message, long timeStamp) {
/* 33 */     if (message instanceof ShortMessage)
/*    */     {
/* 35 */       decodeMessage((ShortMessage)message); } 
/*    */   }
/*    */   
/*    */   public void decodeMessage(ShortMessage message) {
/*    */     byte note, velocity;
/* 40 */     String strMessage = null;
/* 41 */     int nChannel = message.getChannel();
/*    */     
/* 43 */     int cmd = message.getCommand();
/* 44 */     switch (cmd) {
/*    */       case 128:
/* 46 */         note = (byte)message.getData1();
/* 47 */         this.maestro.midiOff(note);
/*    */         break;
/*    */ 
/*    */       
/*    */       case 144:
/* 52 */         note = (byte)message.getData1();
/* 53 */         velocity = (byte)message.getData2();
/* 54 */         if (velocity <= 0) {
/* 55 */           this.maestro.midiOff(note); break;
/*    */         } 
/* 57 */         this.maestro.midiOn(note, (byte)(velocity / 2 + 64));
/*    */         break;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void close() {}
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\launcher\PianoListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */