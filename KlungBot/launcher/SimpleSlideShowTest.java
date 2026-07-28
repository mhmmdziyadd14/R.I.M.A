/*    */ package launcher;
/*    */ 
/*    */ import javafx.animation.Animation;
/*    */ import javafx.animation.FadeTransition;
/*    */ import javafx.animation.PauseTransition;
/*    */ import javafx.animation.SequentialTransition;
/*    */ import javafx.application.Application;
/*    */ import javafx.scene.Node;
/*    */ import javafx.scene.Parent;
/*    */ import javafx.scene.Scene;
/*    */ import javafx.scene.image.Image;
/*    */ import javafx.scene.image.ImageView;
/*    */ import javafx.scene.layout.StackPane;
/*    */ import javafx.stage.Stage;
/*    */ import javafx.util.Duration;
/*    */ 
/*    */ public class SimpleSlideShowTest
/*    */   extends Application
/*    */ {
/*    */   class SimpleSlideShow {
/* 21 */     StackPane root = new StackPane();
/*    */     ImageView[] slides;
/*    */     
/*    */     public SimpleSlideShow() {
/* 25 */       this.slides = new ImageView[4];
/* 26 */       Image image1 = new Image(getClass().getResource("/images/DSC_0151.jpg").toExternalForm());
/* 27 */       Image image2 = new Image(getClass().getResource("/images/DSC_0151.jpg").toExternalForm());
/* 28 */       Image image3 = new Image(getClass().getResource("/images/DSC_0151.jpg").toExternalForm());
/* 29 */       Image image4 = new Image(getClass().getResource("/images/DSC_0151.jpg").toExternalForm());
/* 30 */       this.slides[0] = new ImageView(image1);
/* 31 */       this.slides[1] = new ImageView(image2);
/* 32 */       this.slides[2] = new ImageView(image3);
/* 33 */       this.slides[3] = new ImageView(image4);
/*    */     }
/*    */     
/*    */     public StackPane getRoot() {
/* 37 */       return this.root;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public void start() {
/* 44 */       SequentialTransition slideshow = new SequentialTransition();
/*    */       
/* 46 */       for (ImageView slide : this.slides) {
/*    */         
/* 48 */         SequentialTransition sequentialTransition = new SequentialTransition();
/*    */         
/* 50 */         FadeTransition fadeIn = getFadeTransition(slide, 0.0D, 1.0D, 2000);
/* 51 */         PauseTransition stayOn = new PauseTransition(Duration.millis(2000.0D));
/* 52 */         FadeTransition fadeOut = getFadeTransition(slide, 1.0D, 0.0D, 2000);
/*    */         
/* 54 */         sequentialTransition.getChildren().addAll((Object[])new Animation[] { (Animation)fadeIn, (Animation)stayOn, (Animation)fadeOut });
/* 55 */         slide.setOpacity(0.0D);
/* 56 */         this.root.getChildren().add(slide);
/* 57 */         slideshow.getChildren().add(sequentialTransition);
/*    */       } 
/*    */       
/* 60 */       slideshow.play();
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public FadeTransition getFadeTransition(ImageView imageView, double fromValue, double toValue, int durationInMilliseconds) {
/* 67 */       FadeTransition ft = new FadeTransition(Duration.millis(durationInMilliseconds), (Node)imageView);
/* 68 */       ft.setFromValue(fromValue);
/* 69 */       ft.setToValue(toValue);
/*    */       
/* 71 */       return ft;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public static void main(String[] args) {
/* 77 */     launch(args);
/*    */   }
/*    */ 
/*    */   
/*    */   public void start(Stage primaryStage) throws Exception {
/* 82 */     SimpleSlideShow simpleSlideShow = new SimpleSlideShow();
/* 83 */     Scene scene = new Scene((Parent)simpleSlideShow.getRoot());
/* 84 */     primaryStage.setScene(scene);
/* 85 */     primaryStage.show();
/* 86 */     simpleSlideShow.start();
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\launcher\SimpleSlideShowTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */