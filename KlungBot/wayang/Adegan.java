/*    */ package wayang;
/*    */ 
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.image.BufferedImage;
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Adegan
/*    */   extends DefaultMutableTreeNode
/*    */ {
/* 17 */   static int resolution = 12; String name; int transition_length; int action_length;
/*    */   
/*    */   public static void setResolution(int res) {
/* 20 */     resolution = res;
/*    */   }
/*    */   long taction; long texit; long tick;
/*    */   public static int getResolution() {
/* 24 */     return resolution;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Adegan(int alength, int tlength) {
/* 35 */     this.action_length = alength;
/* 36 */     this.transition_length = tlength;
/* 37 */     this.name = "Lakon-" + this.tick;
/*    */   }
/*    */ 
/*    */   
/*    */   public Adegan(int alength, int tlength, Adegan parent) {
/* 42 */     this.action_length = alength;
/* 43 */     this.transition_length = tlength;
/* 44 */     this.name = "Lakon-" + this.tick;
/* 45 */     parent.add(this);
/*    */   }
/*    */   
/*    */   public void setName(String s) {
/* 49 */     this.name = s;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return this.name;
/*    */   }
/*    */   public BufferedImage getImage() {
/* 57 */     return null;
/*    */   }
/*    */   public void start(long tick, int width, int height) {
/* 60 */     this.tick = tick;
/* 61 */     this.taction = tick + this.transition_length;
/* 62 */     this.texit = this.taction + this.action_length;
/*    */   }
/*    */   
/*    */   public boolean isEnterTime(long tick) {
/* 66 */     return (tick >= this.tick);
/*    */   }
/*    */   
/*    */   public boolean isActionTime(long tick) {
/* 70 */     return (tick >= this.taction);
/*    */   }
/*    */   
/*    */   public boolean isExitTime(long tick) {
/* 74 */     return (tick >= this.texit);
/*    */   }
/*    */   
/*    */   public void display(Graphics2D g) {}
/*    */   
/*    */   public void enter(Graphics2D g, long tick, long note, Adegan prev) {}
/*    */   
/*    */   public void action(Graphics2D g, long tick, long note) {}
/*    */   
/*    */   public void finish() {}
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\wayang\Adegan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */