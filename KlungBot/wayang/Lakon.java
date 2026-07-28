/*    */ package wayang;
/*    */ 
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Lakon
/*    */   extends DefaultMutableTreeNode
/*    */ {
/*    */   String name;
/*    */   int meter;
/*    */   int meter_beat;
/*    */   String info;
/*    */   
/*    */   public Lakon(String name, int meter, int meter_beat) {
/* 20 */     this.name = name;
/* 21 */     this.meter = meter;
/* 22 */     this.meter_beat = meter_beat;
/*    */   }
/*    */   
/*    */   public void setInfo(String info) {
/* 26 */     this.info = info;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 30 */     return this.name;
/*    */   }
/*    */   
/*    */   public String getInfo() {
/* 34 */     return this.info;
/*    */   }
/*    */   
/*    */   public String getMeterStr() {
/* 38 */     return this.meter + "/" + this.meter_beat;
/*    */   }
/*    */   
/*    */   public void add(Adegan adegan) {
/* 42 */     add(adegan);
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\wayang\Lakon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */