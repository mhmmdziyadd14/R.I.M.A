package com.bric.image.transition;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface Transition {
  public static final int RIGHT = 1;
  
  public static final int LEFT = 2;
  
  public static final int UP = 3;
  
  public static final int DOWN = 4;
  
  public static final int COUNTER_CLOCKWISE = 5;
  
  public static final int CLOCKWISE = 6;
  
  public static final int IN = 7;
  
  public static final int OUT = 8;
  
  public static final int HORIZONTAL = 9;
  
  public static final int VERTICAL = 10;
  
  public static final int BIG = 11;
  
  public static final int MEDIUM = 12;
  
  public static final int SMALL = 13;
  
  public static final int TOP_LEFT = 14;
  
  public static final int TOP_RIGHT = 15;
  
  public static final int BOTTOM_LEFT = 16;
  
  public static final int BOTTOM_RIGHT = 17;
  
  void paint(Graphics2D paramGraphics2D, BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, float paramFloat);
}


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\bric\image\transition\Transition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */