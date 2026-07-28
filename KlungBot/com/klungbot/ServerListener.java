package com.klungbot;

public interface ServerListener {
  void play();
  
  void stop();
  
  void midiOn(byte paramByte1, byte paramByte2, byte paramByte3);
  
  void midiOff(byte paramByte1, byte paramByte2);
}


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\ServerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */