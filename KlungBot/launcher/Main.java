/*     */ package launcher;
/*     */ 
/*     */ import com.klungbot.AudioEngineer;
/*     */ import com.klungbot.Device;
/*     */ import com.klungbot.DoremiReader;
/*     */ import com.klungbot.Maestro;
/*     */ import com.klungbot.MaestroListener;
/*     */ import com.klungbot.MidiInfo;
/*     */ import com.klungbot.Sequence;
/*     */ import com.klungbot.ServerListener;
/*     */ import com.klungbot.Synthesizer;
/*     */ import com.klungbot.doremi.Effect;
/*     */ import com.klungbot.util.FileTreeNode;
/*     */ import com.klungbot.util.Options;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import javafx.application.Application;
/*     */ import javafx.fxml.FXMLLoader;
/*     */ import javafx.scene.Parent;
/*     */ import javafx.scene.Scene;
/*     */ import javafx.stage.Stage;
/*     */ import javafx.stage.WindowEvent;
/*     */ import javax.sound.midi.MidiDevice;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Main
/*     */   extends Application
/*     */   implements MaestroListener, ServerListener
/*     */ {
/*     */   private int statusAngklung;
/*     */   static Maestro maestro;
/*     */   Effect effect;
/*     */   FileTreeNode albumSelected;
/*     */   FileTreeNode midiSelected;
/*     */   FileTreeNode draftSelected;
/*     */   boolean changed = false;
/*     */   static DoremiReader doremi;
/*     */   String baseFolder;
/*     */   String draftFolder;
/*     */   String albumFolder;
/*     */   String midiFolder;
/*     */   String listFolder;
/*     */   String soundFolder;
/*     */   Synthesizer midi;
/*     */   MidiInfo midiInfo;
/*     */   ArrayList<MidiDevice.Info> midiDevices;
/*     */   public static MidiDevice.Info midiDeviceSelected;
/*     */   public static Stage pStage;
/*     */   public static Scene scene;
/*     */   Controller ctrl;
/*     */   
/*     */   public void start(Stage primaryStage) throws Exception {
/*  57 */     pStage = primaryStage;
/*     */     
/*  59 */     this.baseFolder = "." + File.separator + "files" + File.separator;
/*  60 */     this.statusAngklung = 0;
/*     */     
/*  62 */     initOptions();
/*     */     
/*  64 */     doremi = new DoremiReader();
/*  65 */     maestro = new Maestro(this.baseFolder, this);
/*  66 */     this.effect = maestro.getEffect();
/*     */     
/*  68 */     initMaestro();
/*  69 */     initMidi();
/*  70 */     initAlbum();
/*  71 */     initDraft();
/*  72 */     this.albumSelected = null;
/*  73 */     this.midiSelected = null;
/*  74 */     this.draftSelected = null;
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
/*  87 */     maestro.getAudioEngineer(); this.midiDevices = AudioEngineer.getMidiInputDevices();
/*  88 */     if (this.midiDevices.size() > 0) {
/*  89 */       System.out.println("Midi device(s) are found");
/*  90 */       midiDeviceSelected = this.midiDevices.get(0);
/*     */     } 
/*     */     
/*  93 */     FXMLLoader loader = new FXMLLoader(getClass().getResource("klungbotFX.fxml"));
/*  94 */     Parent root = (Parent)loader.load();
/*     */     
/*  96 */     this.ctrl = (Controller)loader.getController();
/*     */ 
/*     */     
/*  99 */     if (this.midiDevices.size() > 0) {
/* 100 */       maestro.getAudioEngineer().setInputDevice(midiDeviceSelected, this.ctrl);
/*     */     }
/*     */     
/* 103 */     primaryStage.setTitle("Klungbot FX - PIPO Version (1280 x 800)");
/* 104 */     scene = new Scene(root, 1280.0D, 800.0D);
/* 105 */     primaryStage.setScene(scene);
/* 106 */     primaryStage.setFullScreen(true);
/* 107 */     primaryStage.show();
/* 108 */     primaryStage.setOnCloseRequest(e -> closing());
/*     */   }
/*     */   
/*     */   void closing() {
/*     */     try {
/* 113 */       maestro.finish();
/* 114 */       maestro.playOff(0L);
/*     */     }
/* 116 */     catch (Exception ex) {}
/*     */     
/* 118 */     System.exit(0);
/*     */   }
/*     */   
/*     */   void initOptions() {
/* 122 */     if (!Options.load(this.baseFolder)) {
/* 123 */       Options.initDefault();
/*     */     }
/* 125 */     this.draftFolder = Options.get("folder.draft") + File.separator;
/* 126 */     this.albumFolder = Options.get("folder.album") + File.separator;
/* 127 */     this.midiFolder = Options.get("folder.midi") + File.separator;
/* 128 */     this.listFolder = Options.get("folder.list") + File.separator;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void playDoremi(String fname) {
/* 134 */     System.out.println("Playing: " + fname);
/*     */     try {
/* 136 */       File file = new File(fname);
/* 137 */       Sequence sequence = doremi.read(file);
/* 138 */       maestro.queue(sequence);
/* 139 */       finishedNumber = 0;
/*     */     }
/* 141 */     catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void initMaestro() {
/* 147 */     maestro.initPlayers();
/*     */   }
/*     */   
/*     */   void initMidi() {
/* 151 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.midiFolder);
/* 152 */     root.expandAll(".mid");
/* 153 */     DefaultTreeModel model = new DefaultTreeModel((TreeNode)root);
/* 154 */     this.midiSelected = null;
/*     */   }
/*     */   
/*     */   public void initAlbum() {
/* 158 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.albumFolder);
/* 159 */     root.expandAll(".123");
/* 160 */     DefaultTreeModel model = new DefaultTreeModel((TreeNode)root);
/* 161 */     this.albumSelected = null;
/*     */   }
/*     */   
/*     */   public void initDraft() {
/* 165 */     FileTreeNode root = new FileTreeNode(this.baseFolder + File.separator + this.draftFolder);
/* 166 */     root.expandAll(".123");
/* 167 */     this.draftSelected = null;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 171 */     launch(args);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void play() {}
/*     */ 
/*     */   
/*     */   public static void pause() {
/* 180 */     maestro.pause();
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 185 */     maestro.finish();
/*     */   }
/*     */   
/*     */   public static void finish() {
/* 189 */     maestro.finish();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void midiOn(byte data1, byte data2, byte data3) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void midiOff(byte data1, byte data2) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connected(Device dev) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void disconnected(Device dev) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void started(Sequence seq) {}
/*     */ 
/*     */ 
/*     */   
/* 217 */   static int finishedNumber = 0;
/*     */   
/*     */   public void finished(Sequence seq) {
/* 220 */     if (finishedNumber == 0) {
/* 221 */       this.ctrl.lala();
/*     */     }
/*     */     
/* 224 */     finishedNumber++;
/*     */   }
/*     */   
/*     */   public void changeForte(int value) {}
/*     */   
/*     */   public void changeTempo(int value) {}
/*     */   
/*     */   public void changeKey(int value) {}
/*     */   
/*     */   public void changeTick(long tick, long nextOn) {}
/*     */   
/*     */   public void waiting(long waited) {}
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\launcher\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */