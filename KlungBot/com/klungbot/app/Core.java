/*     */ package com.klungbot.app;
/*     */ 
/*     */ import com.klungbot.Device;
/*     */ import com.klungbot.DoremiReader;
/*     */ import com.klungbot.Maestro;
/*     */ import com.klungbot.MaestroListener;
/*     */ import com.klungbot.Sequence;
/*     */ import com.klungbot.ServerListener;
/*     */ import java.io.File;
/*     */ import javafx.application.Application;
/*     */ import javafx.scene.Parent;
/*     */ import javafx.scene.Scene;
/*     */ import javafx.scene.layout.StackPane;
/*     */ import javafx.stage.Stage;
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
/*     */ public class Core
/*     */   extends Application
/*     */   implements MaestroListener, ServerListener
/*     */ {
/*     */   private int statusAngklung;
/*     */   Maestro maestro;
/*     */   boolean changed = false;
/*     */   DoremiReader doremi;
/*     */   String baseFolder;
/*     */   String draftFolder;
/*     */   String albumFolder;
/*     */   String midiFolder;
/*     */   String listFolder;
/*     */   String soundFolder;
/*     */   
/*     */   public Core(String bf) {
/*  41 */     this.baseFolder = bf;
/*  42 */     this.statusAngklung = 0;
/*     */     
/*  44 */     playDoremi("./album/PRD/02 - Que_Sera_Sera (kurulung-centok).123");
/*     */   }
/*     */   
/*     */   public void playDoremi(String fname) {
/*     */     try {
/*  49 */       File file = new File(fname);
/*  50 */       Sequence sequence = this.doremi.read(file);
/*  51 */       this.maestro.queue(sequence);
/*     */     }
/*  53 */     catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void started(Sequence seq) {
/*  59 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void finished(Sequence seq) {
/*  64 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void changeForte(int value) {
/*  69 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void changeTempo(int value) {
/*  74 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void changeKey(int value) {
/*  79 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void changeTick(long tick, long nextOn) {
/*  84 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void waiting(long waited) {
/*  89 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void connected(Device dev) {
/*  94 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnected(Device dev) {
/*  99 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void play() {
/* 104 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 109 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOn(byte data1, byte data2, byte data3) {
/* 114 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
/*     */ 
/*     */   
/*     */   public void midiOff(byte data1, byte data2) {
/* 119 */     throw new UnsupportedOperationException("Not supported yet.");
/*     */   }
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
/*     */   public void start(Stage primaryStage) {
/* 144 */     String baseFolder = "." + File.separator;
/*     */     
/* 146 */     StackPane root = new StackPane();
/* 147 */     Scene scene = new Scene((Parent)root, 300.0D, 250.0D);
/*     */     
/* 149 */     primaryStage.setTitle("Hello World!");
/* 150 */     primaryStage.setScene(scene);
/* 151 */     primaryStage.show();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 156 */     launch(args);
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\Core.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */