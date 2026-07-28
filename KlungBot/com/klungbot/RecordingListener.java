package com.klungbot;

public interface RecordingListener {
  void record(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void recordNoteOn(int paramInt1, int paramInt2, int paramInt3);
  
  void recordNoteOff(int paramInt1, int paramInt2);
  
  void recordAllNotesOff(int paramInt);
  
  void recordSetTempo(int paramInt);
}


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\RecordingListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */