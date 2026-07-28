package com.klungbot;

public interface MaestroListener extends DeviceListener {
  void started(Sequence paramSequence);
  
  void finished(Sequence paramSequence);
  
  void changeForte(int paramInt);
  
  void changeTempo(int paramInt);
  
  void changeKey(int paramInt);
  
  void changeTick(long paramLong1, long paramLong2);
  
  void waiting(long paramLong);
}


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\MaestroListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */