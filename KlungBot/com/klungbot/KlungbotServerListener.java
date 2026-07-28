package com.klungbot;

public interface KlungbotServerListener {
  void voteSong(String paramString);
  
  void play(String paramString);
  
  void play();
  
  void finish();
  
  void midiOn(byte paramByte1, byte paramByte2, byte paramByte3);
  
  void midiOff(byte paramByte1, byte paramByte2);
  
  String getAlbumFolder();
  
  void log(String paramString1, String paramString2);
}


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\KlungbotServerListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */