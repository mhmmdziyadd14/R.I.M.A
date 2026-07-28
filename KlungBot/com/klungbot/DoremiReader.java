/*      */ package com.klungbot;
/*      */ 
/*      */ import com.klungbot.doremi.Pattern;
/*      */ import com.klungbot.doremi.Rythm;
/*      */ import com.klungbot.doremi.Scale;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileReader;
/*      */ import java.io.StringReader;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DoremiReader
/*      */ {
/*      */   Sequence seq;
/*      */   BufferedReader reader;
/*      */   int key;
/*      */   long startTick;
/*      */   long nextTick;
/*      */   long currentTick;
/*      */   int currentBeat;
/*      */   Track track;
/*      */   Track commands;
/*      */   Rythm rhythm;
/*      */   Pattern drum;
/*   33 */   StringBuffer token = new StringBuffer();
/*   34 */   StringBuffer token2 = new StringBuffer();
/*      */   int line_octave;
/*      */   int line_accent;
/*   37 */   int last_forte = 80;
/*   38 */   int last_tempo = 100; boolean legato = false; String str;
/*      */   int sl;
/*      */   
/*      */   public Sequence read(String buffer) throws ParserException {
/*   42 */     Sequence sq = read(new BufferedReader(new StringReader(buffer)));
/*   43 */     if (sq.titles.isEmpty()) {
/*   44 */       sq.titles.add("notitle");
/*      */     }
/*   46 */     return sq;
/*      */   }
/*      */   int si; char last_char; Label lastLabel;
/*      */   
/*      */   public Sequence read(File fname) throws FileNotFoundException, ParserException {
/*   51 */     Sequence sq = read(new BufferedReader(new FileReader(fname)));
/*   52 */     if (sq.titles.isEmpty()) {
/*   53 */       sq.titles.add(fname.getName());
/*      */     }
/*   55 */     return sq;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void startTokenizer(String l) {
/*   63 */     this.str = l;
/*   64 */     this.sl = l.length();
/*   65 */     this.si = 0;
/*   66 */     this.last_char = ' ';
/*      */   }
/*      */   
/*      */   void startTokenizer(String l, int start) {
/*   70 */     startTokenizer(l);
/*   71 */     this.si = start;
/*      */   }
/*      */   
/*      */   int getPosition() {
/*   75 */     return this.si;
/*      */   }
/*      */   
/*      */   void setPosition(int position) {
/*   79 */     this.si = position;
/*      */   }
/*      */   
/*      */   char getChar() {
/*   83 */     if (this.si < this.sl) {
/*   84 */       this.last_char = this.str.charAt(this.si++);
/*      */     } else {
/*      */       
/*   87 */       this.last_char = Character.MIN_VALUE;
/*      */     } 
/*   89 */     return this.last_char;
/*      */   }
/*      */   
/*      */   char getToken() {
/*   93 */     while (this.si < this.sl) {
/*   94 */       this.last_char = this.str.charAt(this.si++);
/*   95 */       if (this.last_char != ' ') return this.last_char; 
/*      */     } 
/*   97 */     this.last_char = Character.MIN_VALUE;
/*   98 */     return Character.MIN_VALUE;
/*      */   }
/*      */   
/*      */   char getLast() {
/*  102 */     return this.last_char;
/*      */   }
/*      */   
/*      */   void skipWhite() {
/*  106 */     while (this.last_char == ' ') {
/*  107 */       getChar();
/*      */     }
/*      */   }
/*      */   
/*      */   String getToken(char delim) {
/*  112 */     StringBuilder s = new StringBuilder();
/*  113 */     skipWhite();
/*  114 */     while (this.si < this.sl) {
/*  115 */       this.last_char = this.str.charAt(this.si++);
/*  116 */       if (this.last_char == delim)
/*  117 */         break;  s.append(this.last_char);
/*      */     } 
/*  119 */     return s.toString();
/*      */   }
/*      */   
/*      */   String getToken(String delim) {
/*  123 */     StringBuilder s = new StringBuilder();
/*  124 */     while (this.si < this.sl) {
/*  125 */       this.last_char = this.str.charAt(this.si++);
/*  126 */       for (int i = 0; i < delim.length(); i++) {
/*  127 */         if (this.last_char == delim.charAt(i)) {
/*  128 */           return s.toString();
/*      */         }
/*      */       } 
/*  131 */       s.append(this.last_char);
/*      */     } 
/*  133 */     return s.toString();
/*      */   }
/*      */   
/*      */   String getIdentifier() throws ParserException {
/*  137 */     StringBuilder s = new StringBuilder();
/*  138 */     while (Character.isLetterOrDigit(this.last_char)) {
/*  139 */       s.append(this.last_char);
/*  140 */       getChar();
/*      */     } 
/*  142 */     return s.toString();
/*      */   }
/*      */   
/*      */   int getInteger() throws ParserException {
/*  146 */     StringBuilder a = new StringBuilder();
/*  147 */     skipWhite();
/*  148 */     while (Character.isDigit(this.last_char)) {
/*  149 */       a.append(this.last_char);
/*  150 */       getChar();
/*      */     } 
/*  152 */     if (a.length() == 0) {
/*  153 */       throw new ParserException("Integer is expected");
/*      */     }
/*  155 */     return Integer.parseInt(a.toString());
/*      */   }
/*      */   
/*      */   int getInteger(int min, int max) throws ParserException {
/*  159 */     int value = getInteger();
/*  160 */     if (value < min) {
/*  161 */       throw new ParserException("Minimum value is " + min);
/*      */     }
/*  163 */     if (value > max) {
/*  164 */       throw new ParserException("Maximum value is " + max);
/*      */     }
/*  166 */     return value;
/*      */   }
/*      */   
/*      */   private void readHeader1(String line) throws Exception {
/*  170 */     int idx = line.indexOf(':');
/*  171 */     String r = line.substring(idx + 1).trim();
/*  172 */     switch (line.charAt(0)) { case 'X':
/*  173 */         this.seq.index = Integer.parseInt(r);
/*  174 */       case 'T': this.seq.addTitle(r);
/*  175 */       case 'C': this.seq.addComposer(r);
/*  176 */       case 'A': this.seq.addArranger(r);
/*  177 */       case 'E': this.seq.addEditor(r);
/*  178 */       case 'M': readMeter(r);
/*  179 */       case 'S': readScale(r);
/*      */       case 'K':
/*  181 */         if (this.seq.scale == null) {
/*  182 */           readScale("diatonic");
/*      */         }
/*  184 */         this.key = this.seq.key = this.seq.scale.shiftOfKey(r);
/*  185 */       case 'Q': this.last_tempo = this.seq.tempo = Integer.parseInt(r);
/*  186 */       case 'F': this.last_forte = this.seq.forte = Integer.parseInt(r);
/*  187 */       case 'R': readRythm(line);
/*  188 */       case 'O': this.seq.origin = r;
/*  189 */       case 'D': this.seq.discography = r;
/*  190 */       case 'G': this.seq.genre = r;
/*      */       case 'N':
/*      */       case 'U':
/*      */         return; }
/*  194 */      throw new Exception("Unknown header line");
/*      */   }
/*      */ 
/*      */   
/*      */   private void readHeader2(String line) throws Exception {
/*  199 */     int idx = line.indexOf(':');
/*  200 */     String r = line.substring(idx + 1).trim();
/*  201 */     char ch = line.charAt(0);
/*  202 */     switch (ch) { case 'M':
/*  203 */         readMeter(r); return;
/*  204 */       case 'K': readKey(r); return;
/*  205 */       case 'Q': readTempo(r); return;
/*  206 */       case 'F': readForte(r); return;
/*  207 */       case 'R': readRythm(line); return; }
/*      */     
/*  209 */     throw new ParserException("Unknown modifier line " + ch);
/*      */   }
/*      */ 
/*      */   
/*      */   void readScale(String r) throws ParserException {
/*      */     try {
/*  215 */       this.seq.scale = Scale.createScale(r);
/*      */     }
/*  217 */     catch (Exception ex) {
/*  218 */       throw new ParserException(ex.getMessage());
/*      */     } 
/*      */   }
/*      */   
/*      */   void readMeter(String r) throws ParserException {
/*  223 */     String[] s = r.split("/");
/*  224 */     if (s.length != 2) {
/*  225 */       throw new ParserException("Invalid format of M: value");
/*      */     }
/*  227 */     this.seq.meter = Integer.parseInt(s[0]);
/*  228 */     this.seq.meter_beat = Integer.parseInt(s[1]);
/*      */   }
/*      */   
/*      */   void readKey(String r) throws Exception {
/*  232 */     Event event = new Event(this.nextTick, 4096);
/*  233 */     event.data = (this.key = this.seq.scale.shiftOfKey(r));
/*  234 */     this.commands.add(event);
/*      */   }
/*      */   
/*      */   void readForte(String r) throws Exception {
/*  238 */     startTokenizer(r);
/*  239 */     this.currentTick = this.nextTick;
/*      */     while (true) {
/*  241 */       int forte = getInteger(0, 100);
/*  242 */       int length = 0;
/*  243 */       skipWhite();
/*  244 */       if (this.last_char == '/') {
/*  245 */         getChar();
/*  246 */         length = getInteger(0, 100);
/*      */       } 
/*  248 */       Event event = new Event(this.nextTick, 8192);
/*  249 */       event.setData(forte - this.last_forte, length);
/*  250 */       this.commands.add(event);
/*  251 */       this.last_forte = forte;
/*  252 */       skipWhite();
/*  253 */       if (this.last_char == '\000')
/*  254 */         return;  if (this.last_char != ';') {
/*  255 */         throw new ParserException("Expecting delimiter ;");
/*      */       }
/*  257 */       getChar();
/*      */     } 
/*      */   }
/*      */   
/*      */   void readTempo(String r) throws Exception {
/*  262 */     startTokenizer(r);
/*  263 */     this.currentTick = this.nextTick;
/*      */     while (true) {
/*  265 */       skipWhite();
/*  266 */       int data = getInteger(30, 300);
/*  267 */       int length = 0;
/*  268 */       skipWhite();
/*  269 */       if (this.last_char == '/') {
/*  270 */         getChar();
/*  271 */         length = getInteger(0, 100);
/*      */       } 
/*  273 */       Event event = new Event(this.nextTick, 12288);
/*  274 */       event.setData(data - this.last_tempo, length);
/*  275 */       this.commands.add(event);
/*  276 */       this.last_tempo = data;
/*  277 */       skipWhite();
/*  278 */       if (this.last_char == '\000') {
/*      */         return;
/*      */       }
/*  281 */       if (this.last_char != ';') {
/*  282 */         throw new ParserException("Expecting delimiter ;");
/*      */       }
/*  284 */       getChar();
/*      */     } 
/*      */   }
/*      */   
/*      */   void readRythm(String line) throws ParserException {
/*  289 */     String[] r = line.split(":");
/*  290 */     if (r.length < 2) {
/*  291 */       throw new ParserException("Invalid R line");
/*      */     }
/*  293 */     this.rhythm = Rythm.get(r[1].trim());
/*  294 */     if (this.rhythm == null) {
/*  295 */       throw new ParserException("Unknown rythm " + r[1]);
/*      */     }
/*  297 */     if (r[0].length() <= 1) {
/*  298 */       this.seq.setRythm(this.rhythm);
/*      */     } else {
/*      */       
/*  301 */       char ch = r[0].charAt(1);
/*  302 */       if (!Character.isUpperCase(ch)) {
/*  303 */         throw new ParserException("Invalid track name " + r[0]);
/*      */       }
/*  305 */       int channel = Character.getNumericValue(ch) - Character.getNumericValue('A') + 1;
/*  306 */       channel %= 10;
/*  307 */       this.seq.setRythm(this.rhythm, channel);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   int readGraceNote(int type, int octave, int note) throws ParserException {
/*  313 */     int length = 1;
/*  314 */     this.track.addNote(this.currentTick, type, octave, note);
/*      */     while (true) {
/*  316 */       getChar();
/*  317 */       switch (this.last_char) {
/*      */         case '.':
/*  319 */           length++; continue;
/*      */         case ' ':
/*      */         case '+':
/*      */         case '-':
/*      */         case '=':
/*      */           continue;
/*      */       }  break;
/*  326 */     }  if (Character.isDigit(this.last_char)) {
/*  327 */       this.currentTick += length;
/*  328 */       return length;
/*      */     } 
/*      */     
/*  331 */     throw new ParserException("Unexpected symbol " + this.last_char);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int readMelody1() throws Exception {
/*  339 */     int grace_length = 0;
/*  340 */     int octave = this.line_octave;
/*  341 */     int type = this.line_accent;
/*  342 */     int length = 24;
/*  343 */     int note = this.seq.scale.indexOfNum(this.last_char, this.key);
/*  344 */     getToken();
/*      */     while (true) {
/*  346 */       switch (this.last_char) { case '-':
/*  347 */           length /= 2; break;
/*  348 */         case '=': length /= 4; break;
/*  349 */         case '+': length = length * 2 / 3; break;
/*  350 */         case '!': length = (int)(this.nextTick - this.currentTick); break;
/*  351 */         case '/': note++; break;
/*  352 */         case '\\': note--; break;
/*  353 */         case '\'': octave++; break;
/*  354 */         case '"': octave += 2; break;
/*  355 */         case ',': octave--; break;
/*  356 */         case ';': octave -= 2; break;
/*  357 */         case '^': type = 2; break;
/*  358 */         case '~': type = 3; break;
/*      */         case '_':
/*  360 */           if (this.legato)
/*  361 */             break;  grace_length += readGraceNote(type, octave, note);
/*  362 */           octave = this.line_octave;
/*  363 */           type = this.line_accent;
/*  364 */           note = this.seq.scale.indexOfNum(this.last_char, this.key); break;
/*      */         case ' ': break;
/*      */         default:
/*  367 */           this.track.addNote(this.currentTick, type, octave, note);
/*  368 */           if (length < grace_length) {
/*  369 */             throw new ParserException("To many grace notes");
/*      */           }
/*  371 */           this.currentTick += (length - grace_length);
/*  372 */           return 1; }
/*      */       
/*  374 */       getChar();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   int decodeChord1() throws Exception {
/*  381 */     int note = this.seq.scale.indexOfNum(this.last_char);
/*  382 */     getChar();
/*  383 */     switch (this.last_char) {
/*      */       case '#':
/*      */       case '/':
/*  386 */         note++;
/*  387 */         getChar();
/*      */         break;
/*      */       case '\\':
/*  390 */         note--;
/*  391 */         getChar();
/*      */         break;
/*      */     } 
/*  394 */     this.token.setLength(0);
/*  395 */     while (Character.isLetterOrDigit(this.last_char)) {
/*  396 */       this.token.append(this.last_char);
/*  397 */       getChar();
/*      */     } 
/*  399 */     return note;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   int decodeChordA() throws Exception {
/*  405 */     int note = this.seq.scale.indexOfSymbol(this.last_char);
/*  406 */     if (note == 0) {
/*  407 */       throw new ParserException("Chord letter expected");
/*      */     }
/*  409 */     getChar();
/*  410 */     switch (this.last_char) {
/*      */       case '#':
/*      */       case '/':
/*  413 */         note++;
/*  414 */         getChar();
/*      */         break;
/*      */       case '\\':
/*  417 */         note--;
/*  418 */         getChar();
/*      */         break;
/*      */     } 
/*  421 */     this.token.setLength(0);
/*  422 */     while (Character.isLetterOrDigit(this.last_char)) {
/*  423 */       this.token.append(this.last_char);
/*  424 */       getChar();
/*      */     } 
/*  426 */     return note;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   int readChord(int note) throws ParserException {
/*  432 */     int octave = this.line_octave;
/*  433 */     int type = this.line_accent;
/*  434 */     int length = 24;
/*  435 */     int start = -1;
/*  436 */     int stop = -1;
/*      */     while (true) {
/*  438 */       switch (this.last_char) { case ' ': break;
/*      */         case '-':
/*  440 */           length /= 2; break;
/*  441 */         case '=': length /= 4; break;
/*  442 */         case '+': length = length * 2 / 3; break;
/*  443 */         case '!': length = (int)(this.nextTick - this.currentTick); break;
/*  444 */         case '^': type = 2; break;
/*  445 */         case '~': type = 3; break;
/*  446 */         case '"': octave++;
/*  447 */         case '\'': octave++; break;
/*  448 */         case ';': octave--;
/*  449 */         case ',': octave--; break;
/*      */         case '(':
/*  451 */           getChar();
/*  452 */           start = getInteger();
/*  453 */           if (this.last_char == ':') {
/*  454 */             getChar();
/*  455 */             if (this.last_char != ')') {
/*  456 */               stop = getInteger();
/*      */             }
/*      */           } else {
/*      */             
/*  460 */             stop = start;
/*      */           } 
/*  462 */           if (this.last_char != ')') {
/*  463 */             throw new ParserException("Closing ) was expected");
/*      */           }
/*      */           break;
/*      */         default:
/*  467 */           if (start <= 0) {
/*  468 */             this.track.addChord(this.currentTick, type, octave, note, this.token.toString());
/*      */           } else {
/*      */             
/*  471 */             this.track.addChord(this.currentTick, type, octave, note, this.token.toString(), start, stop);
/*      */           } 
/*  473 */           this.currentTick += length;
/*  474 */           return 1; }
/*      */ 
/*      */       
/*  477 */       getChar();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   int readChord() throws Exception {
/*      */     int n;
/*  485 */     if (Character.isDigit(this.last_char)) {
/*  486 */       n = decodeChord1() + this.key;
/*      */     } else {
/*      */       
/*  489 */       n = decodeChordA();
/*      */     } 
/*  491 */     return readChord(n);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int readRhythm(int note, String chord) throws ParserException {
/*  498 */     int octave = this.line_octave;
/*  499 */     int type = this.line_accent;
/*  500 */     int mlength = this.seq.meter * 24;
/*      */     while (true) {
/*  502 */       switch (this.last_char) { case '-':
/*  503 */           mlength /= 2; break;
/*  504 */         case '=': mlength /= 4; break;
/*  505 */         case '+': mlength = mlength * 2 / 3; break;
/*  506 */         case ';': octave--;
/*  507 */         case ',': octave--; break;
/*  508 */         case '"': octave++;
/*  509 */         case '\'': octave++; break;
/*  510 */         case '^': type = 2; break;
/*  511 */         case '~': type = 3; break;
/*      */         default:
/*  513 */           this.track.addRythm(this.currentTick, mlength, type, octave, note, chord);
/*  514 */           this.currentTick += mlength;
/*  515 */           return 1; }
/*      */       
/*  517 */       getChar();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int readRythm() throws Exception {
/*  525 */     if (Character.isDigit(this.last_char)) {
/*  526 */       int i = decodeChord1();
/*  527 */       return readRhythm(i + this.key, this.token.toString());
/*      */     } 
/*      */     
/*  530 */     this.last_char = Character.toLowerCase(this.last_char);
/*  531 */     int n = decodeChordA();
/*  532 */     return readRhythm(n, this.token.toString());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   int readIdem(int note, String chord) throws ParserException {
/*  538 */     int octave = this.line_octave;
/*  539 */     int type = 0;
/*  540 */     int mlength = this.seq.meter * 24;
/*      */     while (true) {
/*  542 */       switch (this.last_char) { case '-':
/*  543 */           mlength /= 2; break;
/*  544 */         case '=': mlength /= 4; break;
/*  545 */         case '+': mlength = mlength * 2 / 3; break;
/*  546 */         case '\'': octave++; break;
/*  547 */         case ',': octave--; break;
/*  548 */         case '"': octave += 2; break;
/*  549 */         case ';': octave -= 2; break;
/*  550 */         case '^': type = 2; break;
/*  551 */         case '~': type = 3; break;
/*      */         default:
/*  553 */           this.track.addIdem(this.currentTick, mlength, type, octave, note, chord);
/*  554 */           this.currentTick += mlength;
/*  555 */           return 1; }
/*      */       
/*  557 */       getChar();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int readIdem0() throws ParserException {
/*  565 */     int mlength = this.seq.meter * 24;
/*      */     while (true) {
/*  567 */       switch (this.last_char) { case '-':
/*  568 */           mlength /= 2; break;
/*  569 */         case '=': mlength /= 4; break;
/*  570 */         case '+': mlength = mlength * 2 / 3; break;
/*      */         default:
/*  572 */           this.track.addIdem(this.currentTick, mlength);
/*  573 */           this.currentTick += mlength;
/*  574 */           return 1; }
/*      */       
/*  576 */       getChar();
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   void readIdem() throws Exception {
/*  582 */     if (Character.isDigit(this.last_char)) {
/*  583 */       int n = decodeChord1();
/*  584 */       readIdem(n + this.key, this.token.toString());
/*      */     }
/*  586 */     else if (this.last_char >= 'a' && this.last_char <= 'g') {
/*  587 */       int n = decodeChordA();
/*  588 */       readIdem(n, this.token.toString());
/*      */     } else {
/*      */       
/*  591 */       readIdem0();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   int readPercussion() throws ParserException {
/*  598 */     this.token.setLength(0);
/*  599 */     this.token.append(this.last_char);
/*  600 */     int length = 24;
/*  601 */     int accent = this.line_accent;
/*      */     while (true) {
/*  603 */       getChar();
/*  604 */       switch (this.last_char) { case '-':
/*  605 */           length /= 2; continue;
/*  606 */         case '=': length /= 4; continue;
/*  607 */         case '+': length = length * 2 / 3; continue;
/*  608 */         case '^': accent = 2; continue;
/*  609 */         case '~': accent = 3; continue; }
/*      */        break;
/*  611 */     }  this.track.addDrum(this.currentTick, accent, this.token.toString());
/*  612 */     this.currentTick += length;
/*  613 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   void addPattern(StringBuffer s, int m) throws ParserException {
/*  619 */     int[][] r = this.drum.getPattern(s.toString());
/*  620 */     if (r == null) {
/*  621 */       throw new ParserException("Unknown drum pattern " + s);
/*      */     }
/*  623 */     int max = 0;
/*  624 */     for (int i = 0; i < r.length; i++) {
/*  625 */       max += r[i][0];
/*      */     }
/*  627 */     max -= m;
/*  628 */     int sum = 0;
/*  629 */     for (int j = 0; j < r.length; j++) {
/*  630 */       Event e = new Event(this.currentTick + sum);
/*  631 */       for (int k = 1; k < (r[j]).length; k++) {
/*  632 */         e.data |= 1L << r[j][k] - 1;
/*      */       }
/*  634 */       this.track.add(e);
/*  635 */       sum += r[j][0];
/*  636 */       if (sum >= max) {
/*  637 */         sum = max;
/*      */         break;
/*      */       } 
/*      */     } 
/*  641 */     this.currentTick += sum;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   int readPattern1() throws ParserException {
/*  647 */     this.token.setLength(0);
/*  648 */     this.token.append(this.last_char);
/*      */     while (true) {
/*  650 */       getChar();
/*  651 */       if ((this.last_char >= 'h' && this.last_char <= 'z') || (this.last_char >= 'H' && this.last_char <= 'Z')) {
/*      */         
/*  653 */         this.token.append(this.last_char);
/*      */         
/*      */         continue;
/*      */       } 
/*      */       break;
/*      */     } 
/*  659 */     int mlength = 0;
/*      */     while (true) {
/*  661 */       switch (this.last_char) { case ' ': break;
/*      */         case '-':
/*  663 */           mlength += 24; break;
/*  664 */         case '=': mlength += 12; break;
/*      */         default:
/*  666 */           addPattern(this.token, mlength);
/*  667 */           return 1; }
/*      */       
/*  669 */       getChar();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   int readZero() throws ParserException {
/*  676 */     int length = 24;
/*  677 */     this.track.addNote(this.currentTick, 1, 0, 0);
/*      */     
/*      */     while (true) {
/*  680 */       getChar();
/*  681 */       switch (this.last_char) { case ' ': continue;
/*      */         case '-':
/*  683 */           length /= 2; continue;
/*  684 */         case '=': length /= 4; continue;
/*  685 */         case '+': length = length * 2 / 3; continue; }
/*      */        break;
/*  687 */     }  this.currentTick += length;
/*  688 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int readDot() throws ParserException {
/*  695 */     int length = 24;
/*      */     while (true) {
/*  697 */       getChar();
/*  698 */       switch (this.last_char) { case ' ': continue;
/*      */         case '-':
/*  700 */           length /= 2; continue;
/*  701 */         case '=': length /= 4; continue;
/*  702 */         case '+': length = length * 2 / 3; continue; }
/*      */        break;
/*  704 */     }  this.currentTick += length;
/*  705 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int readTrack() throws Exception {
/*      */     while (true) {
/*  714 */       switch (this.last_char) {
/*      */         case '.':
/*  716 */           readDot(); continue;
/*      */         case '|':
/*  718 */           this.track.addBar(this.currentTick);
/*  719 */           getToken();
/*      */           continue;
/*      */         case '0':
/*  722 */           readZero();
/*      */           continue;
/*      */         case '%':
/*  725 */           getChar();
/*  726 */           if (this.last_char == '@') {
/*  727 */             getChar();
/*  728 */             readRythm();
/*      */             continue;
/*      */           } 
/*  731 */           readIdem();
/*      */           continue;
/*      */         
/*      */         case '@':
/*  735 */           getChar();
/*  736 */           if (this.last_char == '%' || this.last_char == '@') {
/*  737 */             getChar();
/*  738 */             readRythm();
/*      */             continue;
/*      */           } 
/*  741 */           readChord();
/*      */           continue;
/*      */         
/*      */         case ' ':
/*      */         case '_':
/*  746 */           getChar();
/*      */           continue;
/*      */         case '{':
/*  749 */           this.legato = true; getToken(); continue;
/*      */         case '}':
/*  751 */           this.legato = false; getToken(); continue;
/*      */         case '!':
/*  753 */           this.track.addBar(this.currentTick);
/*  754 */           if (this.track.voice != 1) {
/*  755 */             this.currentTick = this.nextTick;
/*      */           }
/*      */         case '\000':
/*  758 */           return 0;
/*      */       } 
/*  760 */       if (this.last_char >= '1' && this.last_char <= '9') {
/*  761 */         readMelody1(); continue;
/*      */       } 
/*  763 */       if (this.last_char >= 'a' && this.last_char <= 'g') {
/*  764 */         readChord(); continue;
/*      */       } 
/*  766 */       if (this.last_char >= 'A' && this.last_char <= 'G') {
/*  767 */         this.last_char = Character.toLowerCase(this.last_char);
/*  768 */         readRythm(); continue;
/*      */       } 
/*  770 */       if (this.last_char >= 'O' && this.last_char <= 'Z') {
/*  771 */         this.last_char = Character.toLowerCase(this.last_char);
/*  772 */         readPercussion(); continue;
/*      */       } 
/*  774 */       if (this.last_char >= 'o' && this.last_char <= 'z') {
/*  775 */         readPercussion(); continue;
/*      */       }  break;
/*      */     } 
/*  778 */     throw new ParserException("Unexpected character " + this.last_char);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void readVoice1() throws Exception {
/*      */     Event stopEvent;
/*  786 */     if (this.track.channel == 0 && this.track.voice == 1) {
/*  787 */       this.startTick = this.nextTick;
/*      */     }
/*  789 */     if (this.track.isEmpty() || this.track.lastTick != this.startTick) {
/*  790 */       stopEvent = new Event(this.startTick);
/*  791 */       this.track.resetBar();
/*      */     } else {
/*      */       
/*  794 */       stopEvent = this.track.removeLast();
/*      */     } 
/*  796 */     this.currentTick = this.startTick;
/*  797 */     this.legato = false;
/*      */     
/*  799 */     getChar();
/*  800 */     readTrack();
/*  801 */     if (this.track.channel == 0 && this.track.voice == 1) {
/*  802 */       this.nextTick = this.currentTick;
/*      */     
/*      */     }
/*  805 */     else if (this.currentTick != this.nextTick) {
/*  806 */       int dtick = (int)(this.currentTick - this.nextTick);
/*  807 */       char sign = '+';
/*  808 */       if (dtick < 0) {
/*  809 */         sign = '+';
/*  810 */         dtick = -dtick;
/*      */       } 
/*  812 */       int b = dtick / 24;
/*  813 */       int d = dtick % 24;
/*  814 */       if (d != 0) {
/*  815 */         int f = 24 / dtick % 24;
/*  816 */         throw new ParserException("Different voice length (" + sign + b + " 1/" + f + ")");
/*      */       } 
/*      */ 
/*      */       
/*  820 */       throw new ParserException("Different voice length (" + sign + b + ")");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  826 */     this.track.lastTick = this.currentTick;
/*  827 */     stopEvent.tick = this.nextTick;
/*  828 */     this.track.add(stopEvent);
/*      */   }
/*      */   
/*      */   void readVoice(String line) throws Exception {
/*  832 */     int channel = 0;
/*  833 */     int voice = 1;
/*  834 */     this.line_octave = 0;
/*  835 */     this.line_accent = 1;
/*  836 */     this.track = this.seq.getTrack(channel, voice);
/*  837 */     if (this.track == null) {
/*  838 */       this.track = this.seq.addTrack(channel, voice);
/*      */     }
/*  840 */     startTokenizer(line);
/*  841 */     readVoice1();
/*      */   }
/*      */ 
/*      */   
/*      */   void readVoiceV(String line) throws Exception {
/*  846 */     int channel = 0;
/*  847 */     int voice = 0;
/*  848 */     this.line_octave = 0;
/*  849 */     this.line_accent = 1;
/*  850 */     int idx = 1;
/*  851 */     char c = line.charAt(idx++);
/*  852 */     if (Character.isUpperCase(c)) {
/*  853 */       channel = Character.getNumericValue(c) - Character.getNumericValue('A') + 1;
/*  854 */       channel %= 10;
/*  855 */       c = line.charAt(idx++);
/*      */     } 
/*  857 */     if (Character.isDigit(c)) {
/*  858 */       voice = Character.getNumericValue(c) - Character.getNumericValue('0');
/*  859 */       c = line.charAt(idx++);
/*      */     } 
/*  861 */     while (Character.isDigit(c)) {
/*  862 */       voice *= 10;
/*  863 */       voice += Character.getNumericValue(c) - Character.getNumericValue('0');
/*  864 */       c = line.charAt(idx++);
/*      */     } 
/*  866 */     while (c != ':') {
/*  867 */       switch (c) { case '\'':
/*  868 */           this.line_octave++; break;
/*  869 */         case ',': this.line_octave--; break;
/*  870 */         case '"': this.line_octave += 2; break;
/*  871 */         case ';': this.line_octave -= 2; break;
/*  872 */         case '^': this.line_accent = 2; break;
/*  873 */         case '~': this.line_accent = 3; break;
/*      */         default:
/*  875 */           throw new ParserException("Unexpected symbol " + c); }
/*      */       
/*  877 */       c = line.charAt(idx++);
/*      */     } 
/*  879 */     if (voice != 0) {
/*  880 */       this.track = this.seq.getTrack(channel, voice);
/*  881 */       if (this.track == null) {
/*  882 */         this.track = this.seq.addTrack(channel, voice);
/*      */       }
/*  884 */       startTokenizer(line, idx);
/*  885 */       readVoice1();
/*      */     } else {
/*      */       
/*  888 */       voice = 1;
/*  889 */       this.track = this.seq.getTrack(channel, voice);
/*  890 */       if (this.track == null) {
/*  891 */         this.track = this.seq.addTrack(channel, voice);
/*      */       }
/*      */       do {
/*  894 */         startTokenizer(line, idx);
/*  895 */         readVoice1();
/*  896 */         voice++;
/*  897 */         this.track = this.seq.getTrack(channel, voice);
/*  898 */       } while (this.track != null);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   void readLabel(String line) throws Exception {
/*  904 */     String label = null;
/*  905 */     startTokenizer(line, 1);
/*  906 */     getChar();
/*  907 */     while (this.last_char == '$' || this.last_char == ' ') {
/*  908 */       getChar();
/*      */     }
/*  910 */     if (this.last_char != '/' && this.last_char != '\000') {
/*  911 */       label = getIdentifier();
/*  912 */       if (label.isEmpty()) label = null;
/*      */     
/*      */     } 
/*  915 */     if (this.lastLabel == null) {
/*  916 */       this.lastLabel = this.seq.addLabel(label, this.nextTick);
/*  917 */       this.commands.addLabel(this.nextTick, this.lastLabel);
/*      */       
/*      */       return;
/*      */     } 
/*  921 */     if (this.lastLabel.tick == this.nextTick && 
/*  922 */       label != null) {
/*  923 */       this.seq.addAlias(label, this.lastLabel);
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  928 */     this.commands.addEndLabel(this.nextTick, this.lastLabel);
/*  929 */     if (label != null) {
/*  930 */       this.lastLabel = this.seq.addLabel(label, this.nextTick);
/*  931 */       this.commands.addLabel(this.nextTick, this.lastLabel);
/*      */       return;
/*      */     } 
/*  934 */     this.lastLabel = null;
/*      */   }
/*      */   
/*      */   void readJump(String line) throws ParserException {
/*  938 */     String label = null;
/*  939 */     startTokenizer(line, 1);
/*  940 */     getChar();
/*  941 */     while (this.last_char == '%' || this.last_char == ' ') {
/*  942 */       getChar();
/*      */     }
/*  944 */     if (this.last_char != '\000') {
/*  945 */       label = getIdentifier();
/*  946 */       if (label.isEmpty()) label = null; 
/*      */     } 
/*  948 */     if (this.lastLabel != null) {
/*  949 */       this.commands.addEndLabel(this.nextTick, this.lastLabel);
/*  950 */       this.lastLabel = null;
/*      */     } 
/*  952 */     if (label == null) {
/*      */       return;
/*      */     }
/*  955 */     Label l = this.seq.getLabel(label);
/*  956 */     if (l == null) {
/*  957 */       throw new ParserException("Undefined label " + label);
/*      */     }
/*  959 */     if (l.tick == this.nextTick) {
/*  960 */       throw new ParserException("Useless repeat to the same place " + label);
/*      */     }
/*  962 */     l.incCount();
/*  963 */     this.commands.addRepeat(this.nextTick, l);
/*      */   }
/*      */   
/*      */   int readComment(String line, BufferedReader reader) throws Exception {
/*  967 */     int lcount = 0;
/*  968 */     if (line.charAt(1) == '$') {
/*      */       do {
/*  970 */         line = reader.readLine();
/*  971 */         lcount++;
/*  972 */         if (line == null) {
/*  973 */           throw new ParserException("Unclosed multiline comment");
/*      */         }
/*  975 */       } while (!line.startsWith("##"));
/*      */     }
/*      */     
/*  978 */     return lcount;
/*      */   }
/*      */ 
/*      */   
/*      */   void readWord(String r) {}
/*      */   
/*      */   public Sequence read(BufferedReader r) throws ParserException {
/*  985 */     this.reader = r;
/*  986 */     int lnum = 0;
/*      */ 
/*      */     
/*  989 */     this.commands = null;
/*      */ 
/*      */     
/*  992 */     this.key = 0;
/*  993 */     this.startTick = this.nextTick = this.currentTick = 24L;
/*  994 */     this.currentBeat = 0;
/*  995 */     this.lastLabel = null;
/*      */     try {
/*  997 */       this.seq = new Sequence();
/*      */       while (true) {
/*  999 */         String line = this.reader.readLine();
/* 1000 */         lnum++;
/* 1001 */         if (line == null) {
/* 1002 */           throw new ParserException("Uncomplete header");
/*      */         }
/* 1004 */         line = line.trim();
/* 1005 */         if (line.isEmpty())
/* 1006 */           continue;  char ch = line.charAt(0);
/* 1007 */         if (ch == '$')
/* 1008 */           continue;  if (ch == '#') {
/* 1009 */           lnum += readComment(line, this.reader);
/*      */           continue;
/*      */         } 
/* 1012 */         readHeader1(line);
/* 1013 */         if (ch == 'K')
/*      */           break; 
/* 1015 */       }  this.commands = this.seq.addTrack(0, 0); String str;
/* 1016 */       while ((str = this.reader.readLine()) != null) {
/* 1017 */         lnum++;
/* 1018 */         str = str.trim();
/* 1019 */         if (str.isEmpty())
/* 1020 */           continue;  char c = str.charAt(0);
/* 1021 */         switch (c) { case 'V':
/* 1022 */             readVoiceV(str); continue;
/* 1023 */           case '$': readLabel(str); continue;
/* 1024 */           case '%': readJump(str); continue;
/* 1025 */           case '#': lnum += readComment(str, this.reader); continue;
/* 1026 */           case 'W': readWord(str); continue;
/*      */           case '.': case '|':
/* 1028 */             readVoice(str); continue; }
/*      */         
/* 1030 */         if (Character.isDigit(c)) {
/* 1031 */           readVoice(str);
/*      */           continue;
/*      */         } 
/* 1034 */         readHeader2(str);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1039 */       this.seq.max_tick = (int)this.nextTick;
/*      */     }
/* 1041 */     catch (ParserException ex) {
/* 1042 */       this.seq = null;
/* 1043 */       ex.setLocation(lnum, this.si);
/* 1044 */       throw ex;
/*      */     }
/* 1046 */     catch (Exception ex) {
/* 1047 */       throw new ParserException(ex.getMessage(), lnum, this.si);
/*      */     } 
/* 1049 */     return this.seq;
/*      */   }
/*      */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\DoremiReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */