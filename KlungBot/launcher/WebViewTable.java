/*     */ package launcher;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.EventQueue;
/*     */ import javafx.application.Platform;
/*     */ import javafx.beans.Observable;
/*     */ import javafx.concurrent.Worker;
/*     */ import javafx.embed.swing.JFXPanel;
/*     */ import javafx.scene.Parent;
/*     */ import javafx.scene.Scene;
/*     */ import javafx.scene.layout.StackPane;
/*     */ import javafx.scene.web.WebEngine;
/*     */ import javafx.scene.web.WebView;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebViewTable
/*     */ {
/*     */   private JTable table;
/*     */   private WebView webView;
/*     */   
/*     */   public void initAndShowGUI() {
/*  29 */     JFrame frame = new JFrame("WebViewTable");
/*  30 */     frame.setDefaultCloseOperation(3);
/*  31 */     JFXPanel fxPanel = new JFXPanel()
/*     */       {
/*     */         public Dimension getPreferredSize()
/*     */         {
/*  35 */           return new Dimension(800, 400);
/*     */         }
/*     */       };
/*  38 */     frame.add((Component)fxPanel, "Center");
/*  39 */     this.table = new JTable()
/*     */       {
/*     */         public Dimension getPreferredScrollableViewportSize()
/*     */         {
/*  43 */           return new Dimension(800, 100);
/*     */         }
/*     */       };
/*  46 */     frame.add(new JScrollPane(this.table), "South");
/*  47 */     frame.pack();
/*  48 */     frame.setLocationRelativeTo((Component)null);
/*  49 */     frame.setVisible(true);
/*     */     
/*  51 */     Platform.runLater(() -> initFX(paramJFXPanel));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initFX(JFXPanel fxPanel) {
/*  58 */     Scene scene = createScene();
/*  59 */     fxPanel.setScene(scene);
/*     */   }
/*     */   
/*     */   private Scene createScene() {
/*  63 */     StackPane root = new StackPane();
/*  64 */     Scene scene = new Scene((Parent)root);
/*  65 */     this.webView = new WebView();
/*  66 */     WebEngine webEngine = this.webView.getEngine();
/*  67 */     Worker worker = webEngine.getLoadWorker();
/*  68 */     worker.stateProperty().addListener(o -> {
/*     */           if (paramWorker.getState() == Worker.State.SUCCEEDED) {
/*     */             EventQueue.invokeLater(());
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  95 */     webEngine.load("https://example.com");
/*  96 */     root.getChildren().add(this.webView);
/*  97 */     return scene;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 101 */     EventQueue.invokeLater(new WebViewTable()::initAndShowGUI);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\launcher\WebViewTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */