package com.klungbot.app;

import com.klungbot.Sequence;

public interface MainListener {
  void changeSequence(Sequence paramSequence);
  
  void start(Sequence paramSequence);
  
  void changeTick(int paramInt, long paramLong);
  
  void waiting(long paramLong);
  
  void finished(Sequence paramSequence);
  
  boolean isReady();
}


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\MainListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */