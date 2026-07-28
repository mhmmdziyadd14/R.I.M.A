/*      */ package launcher;
/*      */ import com.klungbot.Player;
/*      */ import com.klungbot.doremi.Scale;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.util.Vector;
/*      */ import javafx.application.Platform;
/*      */ import javafx.beans.value.ChangeListener;
/*      */ import javafx.beans.value.ObservableValue;
/*      */ import javafx.collections.FXCollections;
/*      */ import javafx.collections.ObservableList;
/*      */ import javafx.collections.transformation.FilteredList;
/*      */ import javafx.concurrent.Task;
/*      */ import javafx.event.Event;
/*      */ import javafx.event.EventHandler;
/*      */ import javafx.fxml.FXML;
/*      */ import javafx.scene.Node;
/*      */ import javafx.scene.control.Button;
/*      */ import javafx.scene.control.ChoiceBox;
/*      */ import javafx.scene.control.Label;
/*      */ import javafx.scene.control.ListView;
/*      */ import javafx.scene.control.TableColumn;
/*      */ import javafx.scene.input.MouseEvent;
/*      */ import javafx.scene.input.TouchEvent;
/*      */ import javafx.scene.layout.VBox;
/*      */ import javax.sound.midi.ShortMessage;
/*      */ import org.controlsfx.dialog.Dialog;
/*      */ 
/*      */ public class Controller implements Receiver {
/*      */   public TableView<SongsModel> tableSongDB;
/*      */   public TableColumn<SongsModel, String> dbTitle;
/*      */   public TableColumn<SongsModel, String> dbComposer;
/*      */   public TableView<SongsModel> tableSongPlaylist;
/*      */   public TableColumn<SongsModel, String> plTitle;
/*      */   public TableColumn<SongsModel, String> plComposer;
/*      */   public ChoiceBox trackChoicebox;
/*      */   @FXML
/*      */   VBox helpBox;
/*      */   @FXML
/*      */   ImageView gallery;
/*      */   @FXML
/*      */   Label labelPlaylist;
/*      */   @FXML
/*      */   Label labelDB;
/*      */   @FXML
/*      */   TabPane mainpane;
/*      */   @FXML
/*      */   StackPane splash;
/*      */   @FXML
/*      */   Label midiDeviceLabel;
/*      */   @FXML
/*      */   GridPane playerSetting;
/*      */   @FXML
/*      */   ToggleButton btAuto;
/*      */   @FXML
/*      */   ToggleButton btRepeat;
/*      */   @FXML
/*      */   Tab playlistCount;
/*      */   @FXML
/*      */   TextField searchField;
/*      */   @FXML
/*      */   Label currentSong;
/*      */   @FXML
/*      */   Label currentSongDB;
/*      */   @FXML
/*      */   ListView<String> Playlist;
/*      */   @FXML
/*      */   ListView<String> Songs;
/*      */   @FXML
/*      */   ListView<String> showInfo;
/*      */   @FXML
/*      */   Pane piano;
/*      */   @FXML
/*      */   Button p13;
/*      */   @FXML
/*      */   Button p14;
/*      */   @FXML
/*      */   Button p15;
/*      */   @FXML
/*      */   Button p16;
/*      */   @FXML
/*      */   Button p17;
/*      */   @FXML
/*      */   Button p18;
/*   85 */   private ObservableList<SongsModel> songDB = FXCollections.observableArrayList(); @FXML Button p19; @FXML Button p20; @FXML Button p21; @FXML Button p22; @FXML Button p23; @FXML Button p24; @FXML Button p25; @FXML Button p26; @FXML Button p27; @FXML Button p28; @FXML Button p29; @FXML Button p30; @FXML Button p31; @FXML Button p32; @FXML Button p33; @FXML Button p34; @FXML Button p35; @FXML Button p36; @FXML Button p37; @FXML Button p38; @FXML Button p39; @FXML Button p40; @FXML Button p41; @FXML Button p42; @FXML Button p43; @FXML Button p44; @FXML Button p45; @FXML Button p46; @FXML Button p47; @FXML Button p48; @FXML
/*   86 */   Button p49; private ObservableList<SongsModel> songPlaylist = FXCollections.observableArrayList();
/*      */   private SortedList<SongsModel> sortedData;
/*      */   boolean finished = true;
/*      */   boolean btAutoState = true;
/*   90 */   String[] tootLast = new String[9999]; Button[] btLast = new Button[9999]; int track = 0, lastTrack = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FXML
/*      */   void initialize() {
/*   97 */     this.dbTitle.setCellValueFactory(cellData -> ((SongsModel)cellData.getValue()).titleProperty());
/*   98 */     this.dbComposer.setCellValueFactory(cellData -> ((SongsModel)cellData.getValue()).composerProperty());
/*   99 */     this.plTitle.setCellValueFactory(cellData -> ((SongsModel)cellData.getValue()).titleProperty());
/*  100 */     this.plComposer.setCellValueFactory(cellData -> ((SongsModel)cellData.getValue()).composerProperty());
/*      */     
/*  102 */     FilteredList<SongsModel> filteredData = new FilteredList(this.songDB, p -> true);
/*  103 */     this.searchField.getProperties().put("vkType", "text");
/*  104 */     this.searchField.textProperty().addListener((observable, oldValue, newValue) -> paramFilteredList.setPredicate(()));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  123 */     this.sortedData = new SortedList((ObservableList)filteredData);
/*  124 */     this.sortedData.comparatorProperty().bind((ObservableValue)this.tableSongDB.comparatorProperty());
/*      */     
/*  126 */     this.tableSongDB.setItems((ObservableList)this.sortedData);
/*  127 */     this.tableSongDB.getSortOrder().addAll((Object[])new TableColumn[] { this.dbTitle });
/*      */     
/*  129 */     this.tableSongPlaylist.setItems(this.songPlaylist);
/*      */     
/*  131 */     File album = new File("files/album");
/*  132 */     insertIntoDB(album);
/*      */     
/*  134 */     ObservableList<String> names = FXCollections.observableArrayList((Object[])new String[] { "Composer", "Arranger", "Editor", "Origin" });
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  139 */     ObservableList<String> ol = FXCollections.observableArrayList();
/*  140 */     final Vector<Player> players = AudioEngineer.getPlayers();
/*      */     
/*      */     int i;
/*      */     
/*  144 */     for (i = 0; i < players.size(); i++) {
/*  145 */       ol.add(((Player)players.get(i)).toString());
/*      */     }
/*      */     
/*  148 */     for (i = 0; i < 10; i++) {
/*  149 */       Label l = new Label();
/*  150 */       l.setText(Doremi.getTrackName(i));
/*      */       
/*  152 */       final int index = i;
/*  153 */       ChoiceBox<String> cb = new ChoiceBox(ol);
/*  154 */       cb.getSelectionModel().select(Main.maestro.getPlayer(i).toString());
/*  155 */       cb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
/*      */           {
/*      */             public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue)
/*      */             {
/*  159 */               Task<Integer> task = new Task<Integer>() {
/*      */                   protected Integer call() throws Exception {
/*  161 */                     System.out.println("Set " + index + " to" + newValue);
/*  162 */                     Main.maestro.setPlayer(index, players.get(newValue.intValue()));
/*  163 */                     return Integer.valueOf(1);
/*      */                   }
/*      */                 };
/*  166 */               task.run();
/*      */             }
/*      */           });
/*      */ 
/*      */       
/*  171 */       FlowPane fp = new FlowPane();
/*      */ 
/*      */       
/*  174 */       this.playerSetting.add((Node)l, i % 2 * 2, 1 + i / 2);
/*  175 */       this.playerSetting.add((Node)cb, i % 2 * 2 + 1, 1 + i / 2);
/*      */       
/*  177 */       if (Main.midiDeviceSelected != null) this.midiDeviceLabel.setText("Midi Device: Connected");
/*      */     
/*      */     } 
/*      */ 
/*      */     
/*  182 */     this.trackChoicebox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
/*      */         {
/*      */           public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue)
/*      */           {
/*  186 */             Task<Integer> task = new Task<Integer>() {
/*      */                 protected Integer call() throws Exception {
/*  188 */                   Controller.this.track = newValue.intValue();
/*  189 */                   return Integer.valueOf(1);
/*      */                 }
/*      */               };
/*  192 */             task.run();
/*      */           }
/*      */         });
/*      */ 
/*      */     
/*  197 */     EventHandler<MouseEvent> noteOn = new EventHandler<MouseEvent>() {
/*      */         public void handle(MouseEvent mouseEvent) {
/*  199 */           Button b = (Button)mouseEvent.getSource();
/*  200 */           String s = b.getId().toString().substring(1);
/*  201 */           Main.maestro.midiOn(Scale.indexToMidi(Byte.valueOf(s).byteValue()), 127, Controller.this.track);
/*  202 */           Controller.this.tootLast[0] = s; Controller.this.btLast[0] = b; Controller.this.lastTrack = Controller.this.track;
/*      */           
/*  204 */           b.getStyleClass().add("buttonPressed");
/*      */         }
/*      */       };
/*  207 */     EventHandler<MouseEvent> noteOff = new EventHandler<MouseEvent>() {
/*      */         public void handle(MouseEvent mouseEvent) {
/*  209 */           if (Controller.this.tootLast[0] != null) {
/*  210 */             Main.maestro.midiOff(Scale.indexToMidi(Byte.valueOf(Controller.this.tootLast[0]).byteValue()), Controller.this.lastTrack);
/*  211 */             Controller.this.btLast[0].getStyleClass().remove("buttonPressed");
/*      */             
/*  213 */             Controller.this.tootLast[0] = null; Controller.this.btLast[0] = null;
/*      */           } 
/*      */         }
/*      */       };
/*      */     
/*  218 */     EventHandler<TouchEvent> noteOnTouch = new EventHandler<TouchEvent>() {
/*      */         public void handle(TouchEvent touchEvent) {
/*  220 */           int pointID = touchEvent.getTouchPoint().getId() + 1;
/*      */           
/*  222 */           if (pointID < 10000) {
/*  223 */             Button b = (Button)touchEvent.getSource();
/*  224 */             String s = b.getId().toString().substring(1);
/*  225 */             Main.maestro.midiOn(Scale.indexToMidi(Byte.valueOf(s).byteValue()), 127, Controller.this.track);
/*  226 */             Controller.this.tootLast[pointID] = s; Controller.this.btLast[pointID] = b; Controller.this.lastTrack = Controller.this.track;
/*      */             
/*  228 */             b.getStyleClass().add("buttonPressed");
/*      */           } 
/*      */         }
/*      */       };
/*      */     
/*  233 */     EventHandler<TouchEvent> noteOffTouch = new EventHandler<TouchEvent>() {
/*      */         public void handle(TouchEvent touchEvent) {
/*  235 */           int pointID = touchEvent.getTouchPoint().getId() + 1;
/*      */           
/*  237 */           if (pointID < 10000 && 
/*  238 */             Controller.this.tootLast[pointID] != null) {
/*  239 */             Main.maestro.midiOff(Scale.indexToMidi(Byte.valueOf(Controller.this.tootLast[pointID]).byteValue()), Controller.this.lastTrack);
/*  240 */             Controller.this.btLast[pointID].getStyleClass().remove("buttonPressed");
/*      */             
/*  242 */             Controller.this.tootLast[pointID] = null;
/*  243 */             Controller.this.btLast[pointID] = null;
/*      */           } 
/*      */         }
/*      */       };
/*      */ 
/*      */ 
/*      */     
/*  250 */     EventHandler<MouseEvent> noteDrag = new EventHandler<MouseEvent>() {
/*      */         public void handle(MouseEvent mouseEvent) {
/*  252 */           Button b = (Button)mouseEvent.getSource();
/*  253 */           b.startFullDrag();
/*  254 */           b.getStyleClass().remove("buttonPressed");
/*      */         }
/*      */       };
/*  257 */     EventHandler<MouseEvent> noteMove = new EventHandler<MouseEvent>() {
/*      */         public void handle(MouseEvent mouseEvent) {
/*  259 */           if (Controller.this.tootLast != null) {
/*  260 */             Main.maestro.midiOff(Scale.indexToMidi(Byte.valueOf(Controller.this.tootLast[0]).byteValue()), Controller.this.lastTrack);
/*  261 */             Controller.this.btLast[0].getStyleClass().remove("buttonPressed");
/*      */             
/*  263 */             Button b = (Button)mouseEvent.getSource();
/*  264 */             String s = b.getId().toString().substring(1);
/*      */             
/*  266 */             Main.maestro.midiOn(Scale.indexToMidi(Byte.valueOf(s).byteValue()), 127, Controller.this.track);
/*  267 */             b.getStyleClass().add("buttonPressed");
/*      */             
/*  269 */             Controller.this.tootLast[0] = s; Controller.this.btLast[0] = b;
/*      */           } 
/*      */         }
/*      */       };
/*      */     
/*  274 */     this.p13.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  275 */     this.p14.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  276 */     this.p15.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  277 */     this.p16.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  278 */     this.p17.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  279 */     this.p18.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  280 */     this.p19.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  281 */     this.p20.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  282 */     this.p21.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  283 */     this.p22.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  284 */     this.p23.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  285 */     this.p24.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  286 */     this.p25.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  287 */     this.p26.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  288 */     this.p27.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  289 */     this.p28.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  290 */     this.p29.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  291 */     this.p30.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  292 */     this.p31.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  293 */     this.p32.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  294 */     this.p33.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  295 */     this.p34.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  296 */     this.p35.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  297 */     this.p36.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  298 */     this.p37.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  299 */     this.p38.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  300 */     this.p39.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  301 */     this.p40.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  302 */     this.p41.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  303 */     this.p42.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  304 */     this.p43.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  305 */     this.p44.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  306 */     this.p45.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  307 */     this.p46.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  308 */     this.p47.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  309 */     this.p48.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*  310 */     this.p49.addEventFilter(MouseEvent.MOUSE_PRESSED, noteOn);
/*      */ 
/*      */     
/*  313 */     this.p13.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  314 */     this.p14.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  315 */     this.p15.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  316 */     this.p16.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  317 */     this.p17.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  318 */     this.p18.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  319 */     this.p19.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  320 */     this.p20.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  321 */     this.p21.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  322 */     this.p22.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  323 */     this.p23.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  324 */     this.p24.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  325 */     this.p25.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  326 */     this.p26.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  327 */     this.p27.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  328 */     this.p28.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  329 */     this.p29.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  330 */     this.p30.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  331 */     this.p31.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  332 */     this.p32.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  333 */     this.p33.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  334 */     this.p34.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  335 */     this.p35.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  336 */     this.p36.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  337 */     this.p37.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  338 */     this.p38.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  339 */     this.p39.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  340 */     this.p40.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  341 */     this.p41.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  342 */     this.p42.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  343 */     this.p43.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  344 */     this.p44.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  345 */     this.p45.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  346 */     this.p46.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  347 */     this.p47.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  348 */     this.p48.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*  349 */     this.p49.addEventFilter(MouseEvent.MOUSE_RELEASED, noteOff);
/*      */     
/*  351 */     this.p13.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  352 */     this.p14.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  353 */     this.p15.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  354 */     this.p16.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  355 */     this.p17.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  356 */     this.p18.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  357 */     this.p19.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  358 */     this.p20.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  359 */     this.p21.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  360 */     this.p22.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  361 */     this.p23.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  362 */     this.p24.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  363 */     this.p25.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  364 */     this.p26.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  365 */     this.p27.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  366 */     this.p28.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  367 */     this.p29.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  368 */     this.p30.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  369 */     this.p31.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  370 */     this.p32.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  371 */     this.p33.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  372 */     this.p34.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  373 */     this.p35.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  374 */     this.p36.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  375 */     this.p37.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  376 */     this.p38.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  377 */     this.p39.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  378 */     this.p40.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  379 */     this.p41.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  380 */     this.p42.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  381 */     this.p43.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  382 */     this.p44.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  383 */     this.p45.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  384 */     this.p46.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  385 */     this.p47.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  386 */     this.p48.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*  387 */     this.p49.addEventFilter(MouseEvent.DRAG_DETECTED, noteDrag);
/*      */     
/*  389 */     this.p13.setOnMouseDragEntered(noteMove);
/*  390 */     this.p14.setOnMouseDragEntered(noteMove);
/*  391 */     this.p15.setOnMouseDragEntered(noteMove);
/*  392 */     this.p16.setOnMouseDragEntered(noteMove);
/*  393 */     this.p17.setOnMouseDragEntered(noteMove);
/*  394 */     this.p18.setOnMouseDragEntered(noteMove);
/*  395 */     this.p19.setOnMouseDragEntered(noteMove);
/*  396 */     this.p20.setOnMouseDragEntered(noteMove);
/*  397 */     this.p21.setOnMouseDragEntered(noteMove);
/*  398 */     this.p22.setOnMouseDragEntered(noteMove);
/*  399 */     this.p23.setOnMouseDragEntered(noteMove);
/*  400 */     this.p24.setOnMouseDragEntered(noteMove);
/*  401 */     this.p25.setOnMouseDragEntered(noteMove);
/*  402 */     this.p26.setOnMouseDragEntered(noteMove);
/*  403 */     this.p27.setOnMouseDragEntered(noteMove);
/*  404 */     this.p28.setOnMouseDragEntered(noteMove);
/*  405 */     this.p29.setOnMouseDragEntered(noteMove);
/*  406 */     this.p30.setOnMouseDragEntered(noteMove);
/*  407 */     this.p31.setOnMouseDragEntered(noteMove);
/*  408 */     this.p32.setOnMouseDragEntered(noteMove);
/*  409 */     this.p33.setOnMouseDragEntered(noteMove);
/*  410 */     this.p34.setOnMouseDragEntered(noteMove);
/*  411 */     this.p35.setOnMouseDragEntered(noteMove);
/*  412 */     this.p36.setOnMouseDragEntered(noteMove);
/*  413 */     this.p37.setOnMouseDragEntered(noteMove);
/*  414 */     this.p38.setOnMouseDragEntered(noteMove);
/*  415 */     this.p39.setOnMouseDragEntered(noteMove);
/*  416 */     this.p40.setOnMouseDragEntered(noteMove);
/*  417 */     this.p41.setOnMouseDragEntered(noteMove);
/*  418 */     this.p42.setOnMouseDragEntered(noteMove);
/*  419 */     this.p43.setOnMouseDragEntered(noteMove);
/*  420 */     this.p44.setOnMouseDragEntered(noteMove);
/*  421 */     this.p45.setOnMouseDragEntered(noteMove);
/*  422 */     this.p46.setOnMouseDragEntered(noteMove);
/*  423 */     this.p47.setOnMouseDragEntered(noteMove);
/*  424 */     this.p48.setOnMouseDragEntered(noteMove);
/*  425 */     this.p49.setOnMouseDragEntered(noteMove);
/*      */     
/*  427 */     this.p13.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  428 */     this.p14.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  429 */     this.p15.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  430 */     this.p16.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  431 */     this.p17.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  432 */     this.p18.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  433 */     this.p19.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  434 */     this.p20.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  435 */     this.p21.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  436 */     this.p22.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  437 */     this.p23.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  438 */     this.p24.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  439 */     this.p25.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  440 */     this.p26.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  441 */     this.p27.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  442 */     this.p28.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  443 */     this.p29.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  444 */     this.p30.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  445 */     this.p31.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  446 */     this.p32.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  447 */     this.p33.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  448 */     this.p34.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  449 */     this.p35.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  450 */     this.p36.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  451 */     this.p37.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  452 */     this.p38.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  453 */     this.p39.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  454 */     this.p40.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  455 */     this.p41.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  456 */     this.p42.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  457 */     this.p43.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  458 */     this.p44.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  459 */     this.p45.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  460 */     this.p46.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  461 */     this.p47.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  462 */     this.p48.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*  463 */     this.p49.addEventFilter(TouchEvent.TOUCH_PRESSED, noteOnTouch);
/*      */     
/*  465 */     this.p13.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  466 */     this.p14.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  467 */     this.p15.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  468 */     this.p16.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  469 */     this.p17.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  470 */     this.p18.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  471 */     this.p19.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  472 */     this.p20.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  473 */     this.p21.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  474 */     this.p22.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  475 */     this.p23.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  476 */     this.p24.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  477 */     this.p25.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  478 */     this.p26.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  479 */     this.p27.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  480 */     this.p28.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  481 */     this.p29.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  482 */     this.p30.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  483 */     this.p31.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  484 */     this.p32.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  485 */     this.p33.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  486 */     this.p34.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  487 */     this.p35.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  488 */     this.p36.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  489 */     this.p37.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  490 */     this.p38.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  491 */     this.p39.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  492 */     this.p40.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  493 */     this.p41.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  494 */     this.p42.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  495 */     this.p43.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  496 */     this.p44.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  497 */     this.p45.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  498 */     this.p46.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  499 */     this.p47.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  500 */     this.p48.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*  501 */     this.p49.addEventFilter(TouchEvent.TOUCH_RELEASED, noteOffTouch);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void showAbout() {
/*  529 */     Dialog dlg = new Dialog(Main.pStage, "About Klungbot");
/*      */     
/*  531 */     VBox vb = new VBox(8.0D);
/*  532 */     vb.setAlignment(Pos.CENTER);
/*      */     
/*  534 */     Label l1 = new Label();
/*  535 */     l1.setText("Klungbot Maestro v 1.0_FX");
/*  536 */     l1.setStyle("-fx-font-weight: bold");
/*      */     
/*  538 */     Label l2 = new Label();
/*  539 */     l2.setText("Integrated angklung robot controller and midi synthesizer");
/*  540 */     Label l3 = new Label();
/*  541 */     l3.setText("to play, practice, and compose music with doremi music notation");
/*  542 */     Label l4 = new Label(); l4.setText("");
/*  543 */     Label l5 = new Label();
/*  544 */     l5.setText("(c) Eko Mursito Budi, 2012");
/*  545 */     Label l6 = new Label();
/*  546 */     l6.setText("All rights reserved.");
/*      */     
/*  548 */     Label l7 = new Label();
/*  549 */     l7.setText("");
/*      */     
/*  551 */     Label l8 = new Label();
/*  552 */     l8.setText("Passionately developed by :");
/*  553 */     l8.setStyle("-fx-font-weight: bold");
/*  554 */     Label l9 = new Label();
/*  555 */     l9.setText("Eko Mursito Budi");
/*  556 */     Label l10 = new Label();
/*  557 */     l10.setText("Karismanto Rahmadika, Krisna Diastama");
/*  558 */     Label l11 = new Label();
/*  559 */     l11.setText("Fariza D. Prasetya, Alvin N. Wijaya, Ari A. Rochim,");
/*  560 */     Label l12 = new Label();
/*  561 */     l12.setText("Nugroho H. Wibowo, Sigit Yudanto.");
/*      */     
/*  563 */     Label l13 = new Label();
/*  564 */     l13.setText("");
/*  565 */     Label l14 = new Label();
/*  566 */     l14.setText("Inspired by the angklung mastery of:");
/*  567 */     l14.setStyle("-fx-font-weight: bold");
/*  568 */     Label l15 = new Label();
/*  569 */     l15.setText("Asep Suhada, Sunata, Yayan Udjo, Handiman");
/*  570 */     Label l16 = new Label();
/*  571 */     l16.setText("");
/*  572 */     Label l17 = new Label();
/*  573 */     l17.setText("Please visit our website at:");
/*  574 */     Label l18 = new Label();
/*  575 */     l18.setText("www.klungbot.com");
/*  576 */     l18.setStyle("-fx-font-weight: bold");
/*  577 */     Label l19 = new Label();
/*  578 */     l19.setText("");
/*  579 */     Label l20 = new Label();
/*  580 */     l20.setText("");
/*      */ 
/*      */     
/*  583 */     vb.getChildren().addAll((Object[])new Node[] { (Node)l1, (Node)l2, (Node)l3, (Node)l4, (Node)l5, (Node)l6, (Node)l7, (Node)l8, (Node)l9, (Node)l10, (Node)l11, (Node)l12, (Node)l13, (Node)l14, (Node)l15, (Node)l16, (Node)l17, (Node)l18, (Node)l19, (Node)l20 });
/*      */     
/*  585 */     dlg.setContent((Node)vb);
/*  586 */     dlg.show();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void startSlide(File directory) {
/*  621 */     int counter = 0, index = 0;
/*  622 */     for (File file : directory.listFiles()) {
/*  623 */       if (file.isFile()) {
/*  624 */         counter++;
/*      */       }
/*      */     } 
/*  627 */     Image[] images = new Image[counter];
/*  628 */     for (File file : directory.listFiles()) {
/*  629 */       if (file.isFile()) {
/*  630 */         System.out.println(file.getAbsolutePath());
/*      */         
/*  632 */         index++;
/*      */       } 
/*      */     } 
/*  635 */     this.gallery.setImage(images[0]);
/*      */   }
/*      */   
/*      */   public int insertIntoDB(File directory) {
/*  639 */     int count = 0;
/*      */     
/*  641 */     for (File file : directory.listFiles()) {
/*  642 */       if (file.isFile()) {
/*  643 */         System.out.println(file.getAbsolutePath());
/*      */         try {
/*  645 */           BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
/*      */           try {
/*  647 */             String line = br.readLine();
/*  648 */             String result = "";
/*      */             
/*  650 */             SongsModel sm = new SongsModel();
/*      */             
/*  652 */             sm.setFilepath(file.getAbsolutePath().toString());
/*      */ 
/*      */             
/*  655 */             while (line != null) {
/*  656 */               if (line.startsWith("T:")) {
/*  657 */                 sm.setTitle(line.substring(2).replace("'", "").trim());
/*      */               }
/*  659 */               if (line.startsWith("C:")) {
/*  660 */                 sm.setComposer(line.substring(2).replace("'", "").trim());
/*      */               }
/*  662 */               if (line.startsWith("A:")) {
/*  663 */                 sm.setArranger(line.substring(2).replace("'", "").trim());
/*      */               }
/*  665 */               if (line.startsWith("E:")) {
/*  666 */                 sm.setEditor(line.substring(2).replace("'", "").trim());
/*      */               }
/*  668 */               if (line.startsWith("O:")) {
/*  669 */                 sm.setOrigin(line.substring(2).replace("'", "").trim());
/*      */               }
/*  671 */               if (line.startsWith("V1:")) {
/*      */                 break;
/*      */               }
/*  674 */               line = br.readLine();
/*      */             } 
/*      */             
/*  677 */             this.songDB.add(sm);
/*      */           }
/*  679 */           catch (FileNotFoundException e) {
/*  680 */             System.out.println("Folder Album not found");
/*      */           } 
/*  682 */         } catch (IOException e) {
/*  683 */           e.printStackTrace();
/*      */         } 
/*  685 */         count++;
/*      */       } 
/*  687 */       if (file.isDirectory()) {
/*  688 */         count += insertIntoDB(file);
/*      */       }
/*      */     } 
/*  691 */     return count;
/*      */   }
/*      */   @FXML
/*      */   public void handleMouseClick(MouseEvent arg0) {
/*  695 */     System.out.println("clicked on " + (String)this.Playlist.getSelectionModel().getSelectedItem());
/*      */     
/*  697 */     String tmp = (String)this.Playlist.getSelectionModel().getSelectedItem();
/*      */     
/*  699 */     this.Playlist.getItems().remove(this.Playlist.getSelectionModel().getSelectedIndex());
/*  700 */     this.Playlist.getItems().add(1, tmp);
/*      */   }
/*      */   @FXML
/*      */   public void changeInfo(MouseEvent mouseEvent) {
/*  704 */     String tmp = (String)this.showInfo.getSelectionModel().getSelectedItem();
/*  705 */     System.out.println(tmp);
/*      */     
/*  707 */     this.dbComposer.setText(tmp);
/*      */     
/*  709 */     if (tmp == "Composer") { this.dbComposer.setCellValueFactory(cellData -> ((SongsModel)cellData.getValue()).composerProperty()); }
/*  710 */     else if (tmp == "Arranger") { this.dbComposer.setCellValueFactory(cellData -> ((SongsModel)cellData.getValue()).arrangerProperty()); }
/*  711 */     else if (tmp == "Editor") { this.dbComposer.setCellValueFactory(cellData -> ((SongsModel)cellData.getValue()).editorProperty()); }
/*  712 */     else if (tmp == "Origin") { this.dbComposer.setCellValueFactory(cellData -> ((SongsModel)cellData.getValue()).originProperty()); }
/*      */     
/*  714 */     this.tableSongDB.setItems((ObservableList)this.sortedData);
/*      */   }
/*      */   
/*  717 */   int totalQuery = 0;
/*      */   public void addToPlaylist(MouseEvent mouseEvent) {
/*  719 */     if (this.selectedSongDB != null) {
/*  720 */       this.songPlaylist.add(this.selectedSongDB);
/*      */     }
/*  722 */     this.totalQuery++;
/*  723 */     this.playlistCount.setText("PLAYLIST (" + this.totalQuery + ")");
/*      */   }
/*      */   
/*  726 */   private SongsModel selectedSongDB = null;
/*      */   public void dbSelected(MouseEvent mouseEvent) {
/*  728 */     this.selectedSongDB = (SongsModel)this.tableSongDB.getSelectionModel().getSelectedItem();
/*      */   }
/*      */ 
/*      */   
/*  732 */   private SongsModel selectedSongPL = null;
/*  733 */   private int indexOnPL = 0, indexPlay = 0;
/*      */   public void selectPlaylist(MouseEvent mouseEvent) {
/*  735 */     this.selectedSongPL = (SongsModel)this.tableSongPlaylist.getSelectionModel().getSelectedItem();
/*  736 */     this.indexOnPL = this.tableSongPlaylist.getSelectionModel().getFocusedIndex();
/*      */   }
/*      */   
/*      */   public void btplay(MouseEvent mouseEvent) {
/*  740 */     play();
/*      */   }
/*      */   
/*      */   void play() {
/*  744 */     if (this.labelPlaylist.getText() == "PAUSED") { Main.pause(); this.labelPlaylist.setText("NOW PLAYING");
/*  745 */       this.labelDB.setText("NOW PLAYING"); }
/*      */     
/*  747 */     if (this.songPlaylist.size() > 0) {
/*  748 */       if (this.selectedSongPL == null) {
/*  749 */         this.tableSongPlaylist.requestFocus();
/*  750 */         this.tableSongPlaylist.getSelectionModel().select(0);
/*  751 */         this.tableSongPlaylist.getFocusModel().focus(0);
/*      */         
/*  753 */         this.selectedSongPL = (SongsModel)this.tableSongPlaylist.getSelectionModel().getSelectedItem();
/*  754 */         this.indexOnPL = this.tableSongPlaylist.getSelectionModel().getFocusedIndex();
/*      */       } 
/*      */       
/*  757 */       if (!this.btAuto.isSelected() && 
/*  758 */         this.btAutoState == true) {
/*  759 */         this.btAuto.setSelected(true);
/*  760 */         this.btAuto.setText("Autoplay: On");
/*      */       } 
/*      */ 
/*      */       
/*  764 */       this.currentSong.setText(this.selectedSongPL.getTitle() + " by " + this.selectedSongPL.getComposer());
/*  765 */       this.currentSongDB.setText(this.selectedSongPL.getTitle() + " by " + this.selectedSongPL.getComposer());
/*  766 */       Main.playDoremi(this.selectedSongPL.getFilepath());
/*  767 */       this.indexPlay = this.indexOnPL;
/*      */     } 
/*      */   }
/*      */   
/*      */   public void btpause(MouseEvent mouseEvent) {
/*  772 */     if (this.currentSong.getText() != "(Please Select Song)") {
/*  773 */       if (this.labelDB.getText() == "PAUSED") {
/*  774 */         this.labelDB.setText("NOW PLAYING");
/*  775 */         this.labelPlaylist.setText("NOW PLAYING");
/*      */       } else {
/*  777 */         this.labelDB.setText("PAUSED");
/*  778 */         this.labelPlaylist.setText("PAUSED");
/*      */       } 
/*  780 */       Main.pause();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void btstop(MouseEvent mouseEvent) {
/*  785 */     this.currentSong.setText("(Please Select Song)");
/*  786 */     this.currentSongDB.setText("(Please Select Song)");
/*      */     
/*  788 */     if (this.btAuto.isSelected()) {
/*  789 */       this.btAutoState = true;
/*  790 */       this.btAuto.setText("Autoplay: Off");
/*  791 */       this.btAuto.setSelected(false);
/*  792 */       Main.finish();
/*      */     } else {
/*  794 */       this.btAutoState = false;
/*  795 */       Main.finish();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void btprevious(MouseEvent mouseEvent) {
/*  800 */     if (this.songPlaylist.size() > 0) {
/*  801 */       this.tableSongPlaylist.requestFocus();
/*  802 */       this.tableSongPlaylist.getSelectionModel().select(this.indexPlay - 1);
/*  803 */       this.tableSongPlaylist.getFocusModel().focus(this.indexPlay - 1);
/*      */       
/*  805 */       this.selectedSongPL = (SongsModel)this.tableSongPlaylist.getSelectionModel().getSelectedItem();
/*  806 */       this.indexOnPL = this.tableSongPlaylist.getSelectionModel().getFocusedIndex();
/*  807 */       this.indexPlay = this.indexOnPL;
/*      */       
/*  809 */       this.currentSong.setText(this.selectedSongPL.getTitle() + " by " + this.selectedSongPL.getComposer());
/*  810 */       this.currentSongDB.setText(this.selectedSongPL.getTitle() + " by " + this.selectedSongPL.getComposer());
/*  811 */       Main.playDoremi(this.selectedSongPL.getFilepath());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void btnext(MouseEvent mouseEvent) {
/*  816 */     playNext();
/*      */   }
/*      */ 
/*      */   
/*      */   void lala() {
/*  821 */     Platform.runLater(() -> autoplay());
/*      */   }
/*      */   
/*      */   void autoplay() {
/*  825 */     if (this.btAuto.isSelected())
/*  826 */       playNext(); 
/*      */   }
/*      */   
/*      */   void playNext() {
/*  830 */     if (this.indexPlay < this.songPlaylist.size() - 1) {
/*  831 */       this.tableSongPlaylist.requestFocus();
/*  832 */       this.tableSongPlaylist.getSelectionModel().select(this.indexPlay + 1);
/*  833 */       this.tableSongPlaylist.getFocusModel().focus(this.indexPlay + 1);
/*      */       
/*  835 */       this.selectedSongPL = (SongsModel)this.tableSongPlaylist.getSelectionModel().getSelectedItem();
/*  836 */       this.indexOnPL = this.tableSongPlaylist.getSelectionModel().getFocusedIndex();
/*  837 */       this.indexPlay = this.indexOnPL;
/*      */       
/*  839 */       this.currentSong.setText(this.selectedSongPL.getTitle() + " by " + this.selectedSongPL.getComposer());
/*  840 */       this.currentSongDB.setText(this.selectedSongPL.getTitle() + " by " + this.selectedSongPL.getComposer());
/*  841 */       Main.playDoremi(this.selectedSongPL.getFilepath());
/*  842 */     } else if (this.btRepeat.isSelected() && 
/*  843 */       this.songPlaylist.size() > 0) {
/*  844 */       this.tableSongPlaylist.requestFocus();
/*  845 */       this.tableSongPlaylist.getSelectionModel().select(0);
/*  846 */       this.tableSongPlaylist.getFocusModel().focus(0);
/*      */       
/*  848 */       this.selectedSongPL = (SongsModel)this.tableSongPlaylist.getSelectionModel().getSelectedItem();
/*  849 */       this.indexOnPL = this.tableSongPlaylist.getSelectionModel().getFocusedIndex();
/*  850 */       this.indexPlay = this.indexOnPL;
/*      */       
/*  852 */       this.currentSong.setText(this.selectedSongPL.getTitle() + " by " + this.selectedSongPL.getComposer());
/*  853 */       this.currentSongDB.setText(this.selectedSongPL.getTitle() + " by " + this.selectedSongPL.getComposer());
/*  854 */       Main.playDoremi(this.selectedSongPL.getFilepath());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void btdelete(MouseEvent mouseEvent) {
/*  860 */     if (this.songPlaylist.size() > 0) {
/*  861 */       this.songPlaylist.remove(this.indexOnPL);
/*      */       
/*  863 */       this.tableSongPlaylist.requestFocus();
/*  864 */       this.tableSongPlaylist.getSelectionModel().select(this.indexOnPL);
/*  865 */       this.tableSongPlaylist.getFocusModel().focus(this.indexOnPL);
/*      */       
/*  867 */       this.selectedSongPL = (SongsModel)this.tableSongPlaylist.getSelectionModel().getSelectedItem();
/*  868 */       this.indexOnPL = this.tableSongPlaylist.getSelectionModel().getFocusedIndex();
/*      */       
/*  870 */       this.totalQuery--;
/*  871 */       this.playlistCount.setText("PLAYLIST (" + this.totalQuery + ")");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void btup(MouseEvent mouseEvent) {
/*  876 */     if (this.indexOnPL > 0) {
/*  877 */       this.songPlaylist.remove(this.indexOnPL);
/*  878 */       this.songPlaylist.add(this.indexOnPL - 1, this.selectedSongPL);
/*      */       
/*  880 */       this.tableSongPlaylist.requestFocus();
/*  881 */       this.tableSongPlaylist.getSelectionModel().select(this.indexOnPL - 1);
/*  882 */       this.tableSongPlaylist.getFocusModel().focus(this.indexOnPL - 1);
/*      */       
/*  884 */       this.selectedSongPL = (SongsModel)this.tableSongPlaylist.getSelectionModel().getSelectedItem();
/*  885 */       this.indexOnPL = this.tableSongPlaylist.getSelectionModel().getFocusedIndex();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void btdown(MouseEvent mouseEvent) {
/*  890 */     if (this.indexOnPL < this.songPlaylist.size() - 1) {
/*  891 */       this.songPlaylist.remove(this.indexOnPL);
/*  892 */       this.songPlaylist.add(this.indexOnPL + 1, this.selectedSongPL);
/*      */       
/*  894 */       this.tableSongPlaylist.requestFocus();
/*  895 */       this.tableSongPlaylist.getSelectionModel().select(this.indexOnPL + 1);
/*  896 */       this.tableSongPlaylist.getFocusModel().focus(this.indexOnPL + 1);
/*      */       
/*  898 */       this.selectedSongPL = (SongsModel)this.tableSongPlaylist.getSelectionModel().getSelectedItem();
/*  899 */       this.indexOnPL = this.tableSongPlaylist.getSelectionModel().getFocusedIndex();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void quickplay(MouseEvent mouseEvent) {
/*  905 */     if (this.labelPlaylist.getText() == "PAUSED" && 
/*  906 */       this.selectedSongDB != null) {
/*  907 */       if (!this.btAuto.isSelected() && 
/*  908 */         this.btAutoState == true) {
/*  909 */         this.btAuto.setSelected(true);
/*  910 */         this.btAuto.setText("Autoplay: On");
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  915 */       if (this.labelPlaylist.getText() == "PAUSED") {
/*  916 */         this.labelPlaylist.setText("NOW PLAYING");
/*  917 */         this.labelDB.setText("NOW PLAYING");
/*      */       } 
/*  919 */       this.currentSong.setText(this.selectedSongDB.getTitle() + " by " + this.selectedSongDB.getComposer());
/*  920 */       this.currentSongDB.setText(this.selectedSongDB.getTitle() + " by " + this.selectedSongDB.getComposer());
/*  921 */       Main.playDoremi(this.selectedSongDB.getFilepath());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void btrepeat(MouseEvent mouseEvent) {
/*  927 */     if (this.btRepeat.isSelected()) { this.btRepeat.setText("Repeat: On"); }
/*  928 */     else { this.btRepeat.setText("Repeat: Off"); }
/*      */   
/*      */   }
/*      */   public void btauto(MouseEvent mouseEvent) {
/*  932 */     if (this.btAuto.isSelected()) { this.btAuto.setText("Autoplay: On"); }
/*  933 */     else { this.btAuto.setText("Autoplay: Off"); }
/*      */   
/*      */   }
/*      */   
/*      */   public void send(MidiMessage message, long timeStamp) {
/*  938 */     if (message instanceof ShortMessage)
/*      */     {
/*  940 */       decodeMessage((ShortMessage)message); } 
/*      */   }
/*      */   
/*      */   public void decodeMessage(ShortMessage message) {
/*      */     byte note, velocity;
/*  945 */     String strMessage = null;
/*  946 */     int nChannel = message.getChannel();
/*      */     
/*  948 */     int cmd = message.getCommand();
/*      */     
/*  950 */     switch (cmd) {
/*      */       case 128:
/*  952 */         note = (byte)message.getData1();
/*      */         
/*  954 */         if (note > 47 && note < 85) {
/*  955 */           Button btn = (Button)Main.scene.lookup("#p" + (note - 35));
/*  956 */           Platform.runLater(() -> paramButton.getStyleClass().remove("buttonPressed"));
/*      */         } 
/*      */         
/*  959 */         Main.maestro.midiOff(note, this.lastTrack);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 144:
/*  964 */         note = (byte)message.getData1();
/*      */ 
/*      */ 
/*      */         
/*  968 */         velocity = (byte)message.getData2();
/*  969 */         if (velocity <= 0) {
/*  970 */           Main.maestro.midiOff(note, this.lastTrack);
/*      */         } else {
/*  972 */           Main.maestro.midiOn(note, (byte)(velocity / 2 + 64), this.track);
/*      */         } 
/*      */         
/*  975 */         if (note > 47 && note < 85) {
/*  976 */           Button btn = (Button)Main.scene.lookup("#p" + (note - 35));
/*  977 */           if (velocity <= 0) { Platform.runLater(() -> paramButton.getStyleClass().remove("buttonPressed")); break; }
/*  978 */            Platform.runLater(() -> paramButton.getStyleClass().add("buttonPressed"));
/*      */         } 
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() {}
/*      */ 
/*      */ 
/*      */   
/*      */   public void quickstop(MouseEvent mouseEvent) {
/*  991 */     this.currentSong.setText("(Please Select Song)");
/*  992 */     this.currentSongDB.setText("(Please Select Song)");
/*      */     
/*  994 */     if (this.btAuto.isSelected()) {
/*  995 */       this.btAutoState = true;
/*  996 */       this.btAuto.setText("Autoplay: Off");
/*  997 */       this.btAuto.setSelected(false);
/*  998 */       Main.finish();
/*      */     } else {
/* 1000 */       this.btAutoState = false;
/* 1001 */       Main.finish();
/*      */     } 
/*      */   }
/*      */   void fullStop() {
/* 1005 */     if (this.btAuto.isSelected()) {
/* 1006 */       this.btAuto.setSelected(false);
/* 1007 */       Main.finish();
/* 1008 */       this.btAuto.setSelected(true);
/*      */     } else {
/* 1010 */       Main.finish();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void playPlaylist(MouseEvent mouseEvent) {
/* 1015 */     play();
/* 1016 */     SingleSelectionModel<Tab> selectionModel = this.mainpane.getSelectionModel();
/* 1017 */     selectionModel.select(1);
/*      */   }
/*      */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\launcher\Controller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */