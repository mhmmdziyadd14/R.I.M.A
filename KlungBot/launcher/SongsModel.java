/*     */ package launcher;
/*     */ 
/*     */ import javafx.beans.property.SimpleStringProperty;
/*     */ import javafx.beans.property.StringProperty;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SongsModel
/*     */ {
/*     */   private final StringProperty title;
/*     */   private final StringProperty filepath;
/*     */   private final StringProperty composer;
/*     */   private final StringProperty arranger;
/*     */   private final StringProperty editor;
/*     */   private final StringProperty origin;
/*     */   private final StringProperty genre;
/*     */   
/*     */   public SongsModel() {
/*  22 */     this("-", null, "-");
/*     */   }
/*     */   
/*     */   public SongsModel(String title, String filepath, String composer) {
/*  26 */     this.title = (StringProperty)new SimpleStringProperty(title);
/*  27 */     this.filepath = (StringProperty)new SimpleStringProperty(filepath);
/*  28 */     this.composer = (StringProperty)new SimpleStringProperty(composer);
/*     */ 
/*     */     
/*  31 */     this.arranger = (StringProperty)new SimpleStringProperty("Arranger");
/*  32 */     this.editor = (StringProperty)new SimpleStringProperty("Editor");
/*  33 */     this.origin = (StringProperty)new SimpleStringProperty("Origin");
/*  34 */     this.genre = (StringProperty)new SimpleStringProperty("Genre");
/*     */   }
/*     */   
/*     */   public String getTitle() {
/*  38 */     return (String)this.title.get();
/*     */   }
/*     */   public void setTitle(String s) {
/*  41 */     this.title.set(s);
/*     */   }
/*     */   public StringProperty titleProperty() {
/*  44 */     return this.title;
/*     */   }
/*     */   
/*     */   public String getFilepath() {
/*  48 */     return (String)this.filepath.get();
/*     */   }
/*     */   public void setFilepath(String s) {
/*  51 */     this.filepath.set(s);
/*     */   }
/*     */   public StringProperty filepathProperty() {
/*  54 */     return this.filepath;
/*     */   }
/*     */   
/*     */   public String getComposer() {
/*  58 */     return (String)this.composer.get();
/*     */   }
/*     */   public void setComposer(String s) {
/*  61 */     this.composer.set(s);
/*     */   }
/*     */   public StringProperty composerProperty() {
/*  64 */     return this.composer;
/*     */   }
/*     */   
/*     */   public String getArranger() {
/*  68 */     return (String)this.arranger.get();
/*     */   }
/*     */   public void setArranger(String s) {
/*  71 */     this.arranger.set(s);
/*     */   }
/*     */   public StringProperty arrangerProperty() {
/*  74 */     return this.arranger;
/*     */   }
/*     */   
/*     */   public String getEditor() {
/*  78 */     return (String)this.editor.get();
/*     */   }
/*     */   public void setEditor(String s) {
/*  81 */     this.editor.set(s);
/*     */   }
/*     */   public StringProperty editorProperty() {
/*  84 */     return this.editor;
/*     */   }
/*     */   
/*     */   public String getOrigin() {
/*  88 */     return (String)this.origin.get();
/*     */   }
/*     */   public void setOrigin(String s) {
/*  91 */     this.origin.set(s);
/*     */   }
/*     */   public StringProperty originProperty() {
/*  94 */     return this.origin;
/*     */   }
/*     */   
/*     */   public String getGenre() {
/*  98 */     return (String)this.genre.get();
/*     */   }
/*     */   public void setGenre(String s) {
/* 101 */     this.genre.set(s);
/*     */   }
/*     */   public StringProperty genreProperty() {
/* 104 */     return this.genre;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\launcher\SongsModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */