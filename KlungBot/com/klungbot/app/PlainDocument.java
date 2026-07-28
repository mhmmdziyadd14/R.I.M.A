/*     */ package com.klungbot.app;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.text.AttributeSet;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.DefaultStyledDocument;
/*     */ import javax.swing.text.Element;
/*     */ import javax.swing.text.MutableAttributeSet;
/*     */ import javax.swing.text.SimpleAttributeSet;
/*     */ import javax.swing.text.StyleConstants;
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
/*     */ public class PlainDocument
/*     */   extends DefaultStyledDocument
/*     */ {
/*     */   public static final int DEFAULT_LINE = 0;
/*     */   public static final int COMMENT_LINE = 1;
/*     */   public static final int VOICE_LINE = 2;
/*     */   public static final int WORD_LINE = 3;
/*     */   public static final int HEADER_LINE = 4;
/*     */   public static final int NORMAL = 0;
/*     */   public static final int NOTE = 1;
/*     */   public static final int PITCH = 2;
/*     */   public static final int LENGTH = 3;
/*     */   public static final int CHORD = 4;
/*     */   public static final int PATTERN = 5;
/*     */   public static final int KEYWORD = 6;
/*     */   public static final int INFO = 7;
/*     */   public static final int WORD = 8;
/*     */   public static final int SEPARATOR = 9;
/*     */   public static final String spaces = " \t";
/*     */   public static final String separators = ":|";
/*     */   public static final String pitches = "',;\"\\/";
/*     */   public static final String lengths = "-=+";
/*     */   public static final char comment = '$';
/*     */   public static final char voice = 'V';
/*     */   public static final char word = 'W';
/*     */   public static final String headers = "ACDEFGHKLMNOPQRST";
/*     */   public static final String notes = "0123456789.";
/*     */   public static final String comment_mark = "$";
/*     */   public static final String start_mark = "|$";
/*     */   public static final String finish_mark = "$|";
/*     */   public static final String line_markers = "$VW";
/*     */   public static final char chord_mark = '@';
/*     */   public static final char pattern_mark = '%';
/*     */   public static final String delimiters = " \t:|',;\"\\/-=+";
/*     */   MutableAttributeSet voiceAttr;
/*     */   protected MutableAttributeSet[] lineStyles;
/*     */   protected MutableAttributeSet[] styles;
/*     */   protected DefaultStyledDocument doc;
/*     */   protected Element rootElement;
/*     */   protected boolean multiLineComment;
/*     */   protected int lineType;
/*     */   protected int tokenType;
/*     */   
/*     */   public PlainDocument() {
/*  66 */     this.doc = this;
/*  67 */     this.rootElement = this.doc.getDefaultRootElement();
/*  68 */     putProperty("__EndOfLine__", "\n");
/*  69 */     this.lineStyles = new MutableAttributeSet[2];
/*  70 */     this.styles = new MutableAttributeSet[10];
/*     */ 
/*     */     
/*  73 */     MutableAttributeSet s = new SimpleAttributeSet();
/*  74 */     StyleConstants.setFontFamily(s, "Courier New");
/*  75 */     StyleConstants.setFontSize(s, 14);
/*  76 */     StyleConstants.setItalic(s, false);
/*  77 */     StyleConstants.setBold(s, false);
/*  78 */     this.lineStyles[0] = s;
/*     */     
/*  80 */     s = new SimpleAttributeSet();
/*  81 */     StyleConstants.setFontFamily(s, "Courier New");
/*  82 */     StyleConstants.setFontSize(s, 14);
/*  83 */     StyleConstants.setForeground(s, Color.GRAY);
/*  84 */     StyleConstants.setItalic(s, true);
/*  85 */     StyleConstants.setBold(s, false);
/*  86 */     this.lineStyles[1] = s;
/*     */ 
/*     */     
/*  89 */     this.styles[0] = this.lineStyles[0];
/*  90 */     s = new SimpleAttributeSet();
/*  91 */     StyleConstants.setForeground(s, new Color(32768));
/*     */     
/*  93 */     this.styles[1] = s;
/*     */     
/*  95 */     s = new SimpleAttributeSet();
/*  96 */     StyleConstants.setForeground(s, Color.RED);
/*     */     
/*  98 */     this.styles[2] = s;
/*     */     
/* 100 */     s = new SimpleAttributeSet();
/* 101 */     StyleConstants.setForeground(s, Color.BLACK);
/*     */     
/* 103 */     this.styles[3] = s;
/*     */     
/* 105 */     s = new SimpleAttributeSet();
/* 106 */     StyleConstants.setForeground(s, Color.ORANGE);
/*     */     
/* 108 */     this.styles[4] = s;
/*     */     
/* 110 */     s = new SimpleAttributeSet();
/* 111 */     StyleConstants.setForeground(s, Color.MAGENTA);
/*     */     
/* 113 */     this.styles[5] = s;
/*     */     
/* 115 */     s = new SimpleAttributeSet();
/* 116 */     StyleConstants.setForeground(s, Color.BLUE);
/* 117 */     StyleConstants.setBold(s, true);
/* 118 */     this.styles[6] = s;
/*     */     
/* 120 */     s = new SimpleAttributeSet();
/* 121 */     StyleConstants.setForeground(s, Color.BLACK);
/* 122 */     this.styles[7] = s;
/*     */     
/* 124 */     s = new SimpleAttributeSet();
/* 125 */     StyleConstants.setForeground(s, Color.MAGENTA);
/* 126 */     this.styles[8] = s;
/*     */     
/* 128 */     s = new SimpleAttributeSet();
/* 129 */     StyleConstants.setForeground(s, Color.BLACK);
/* 130 */     this.styles[9] = s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
/* 138 */     if (str.equals("{")) {
/* 139 */       str = addMatchingBrace(offset);
/*     */     }
/*     */     
/* 142 */     super.insertString(offset, str, a);
/* 143 */     processChangedLines(offset, str.length());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(int offset, int length) throws BadLocationException {
/* 150 */     super.remove(offset, length);
/* 151 */     processChangedLines(offset, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processChangedLines(int offset, int length) throws BadLocationException {
/* 160 */     String content = this.doc.getText(0, this.doc.getLength());
/*     */ 
/*     */     
/* 163 */     int startLine = this.rootElement.getElementIndex(offset);
/* 164 */     int endLine = this.rootElement.getElementIndex(offset + length);
/*     */     
/* 166 */     for (int i = startLine; i <= endLine; i++) {
/* 167 */       applyHighlighting(content, i);
/*     */     }
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
/*     */   private void processChangedAll() {
/*     */     try {
/* 191 */       int length = this.doc.getLength();
/* 192 */       String content = this.doc.getText(0, length - 1);
/* 193 */       int endLine = this.rootElement.getElementIndex(length);
/* 194 */       for (int i = 1; i <= endLine; i++) {
/* 195 */         applyHighlighting(content, i);
/*     */       }
/*     */     }
/* 198 */     catch (Exception ex) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean commentLinesBefore(String content, int line) {
/* 207 */     int offset = this.rootElement.getElement(line).getStartOffset();
/*     */ 
/*     */     
/* 210 */     int startDelimiter = lastIndexOf(content, getStartDelimiter(), offset - 2);
/*     */     
/* 212 */     if (startDelimiter < 0) {
/* 213 */       return false;
/*     */     }
/*     */     
/* 216 */     int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);
/* 217 */     if ((((endDelimiter < offset) ? 1 : 0) & ((endDelimiter != -1) ? 1 : 0)) != 0) {
/* 218 */       return false;
/*     */     }
/* 220 */     this.doc.setParagraphAttributes(startDelimiter, offset - startDelimiter + 1, this.lineStyles[1], false);
/*     */     
/* 222 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void commentLinesAfter(String content, int line) {
/* 229 */     int offset = this.rootElement.getElement(line).getEndOffset();
/*     */     
/* 231 */     int endDelimiter = indexOf(content, getEndDelimiter(), offset);
/* 232 */     if (endDelimiter < 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 237 */     int startDelimiter = lastIndexOf(content, getStartDelimiter(), endDelimiter);
/*     */     
/* 239 */     if (startDelimiter < 0 || startDelimiter <= offset) {
/* 240 */       this.doc.setCharacterAttributes(offset, endDelimiter - offset + 1, this.lineStyles[1], false);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void highlightLinesAfter(String content, int line) throws BadLocationException {
/* 250 */     int offset = this.rootElement.getElement(line).getEndOffset();
/*     */     
/* 252 */     int startDelimiter = indexOf(content, getStartDelimiter(), offset);
/* 253 */     int endDelimiter = indexOf(content, getEndDelimiter(), offset);
/* 254 */     if (startDelimiter < 0) {
/* 255 */       startDelimiter = content.length();
/*     */     }
/* 257 */     if (endDelimiter < 0) {
/* 258 */       endDelimiter = content.length();
/*     */     }
/* 260 */     int delimiter = Math.min(startDelimiter, endDelimiter);
/* 261 */     if (delimiter < offset) {
/*     */       return;
/*     */     }
/*     */     
/* 265 */     int endLine = this.rootElement.getElementIndex(delimiter);
/* 266 */     for (int i = line + 1; i < endLine; i++) {
/* 267 */       Element branch = this.rootElement.getElement(i);
/* 268 */       Element leaf = this.doc.getCharacterElement(branch.getStartOffset());
/* 269 */       AttributeSet as = leaf.getAttributes();
/* 270 */       if (as.isEqual(this.lineStyles[1])) {
/* 271 */         applyHighlighting(content, i);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private int skipSpaces(String line, int start, int end) {
/* 277 */     int i = start;
/* 278 */     while (i < end && " \t".indexOf(line.charAt(i)) >= 0)
/* 279 */       i++; 
/* 280 */     return i;
/*     */   }
/*     */   
/*     */   protected int getLineType(String line, int start, int end) {
/* 284 */     if (line.isEmpty()) return 0; 
/* 285 */     int i = skipSpaces(line, start, end);
/* 286 */     if (i >= end) return 0; 
/* 287 */     char ch = line.charAt(i);
/* 288 */     switch (ch) { case 'V':
/* 289 */         return 2;
/* 290 */       case '$': return 1;
/* 291 */       case 'W': return 3; }
/*     */     
/* 293 */     if ("ACDEFGHKLMNOPQRST".indexOf(ch) >= 0) return 4; 
/* 294 */     return 0;
/*     */   }
/*     */   
/*     */   private int getTokenType(char token) {
/* 298 */     if ("0123456789.".indexOf(token) >= 0) return 1; 
/* 299 */     if ("',;\"\\/".indexOf(token) >= 0) return 2; 
/* 300 */     if ("-=+".indexOf(token) >= 0) return 3; 
/* 301 */     if (":|".indexOf(token) >= 0) return 9; 
/* 302 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyHighlighting(String content, int line) throws BadLocationException {
/* 310 */     int startOffset = this.rootElement.getElement(line).getStartOffset();
/* 311 */     int endOffset = this.rootElement.getElement(line).getEndOffset() - 1;
/* 312 */     int lineLength = endOffset - startOffset;
/* 313 */     int contentLength = content.length();
/* 314 */     if (endOffset >= contentLength) {
/* 315 */       endOffset = contentLength - 1;
/*     */     }
/* 317 */     int ltype = getLineType(content, startOffset, endOffset);
/* 318 */     this.doc.setCharacterAttributes(startOffset, lineLength, this.lineStyles[0], false);
/* 319 */     switch (ltype) { case 2:
/* 320 */         applyVoiceLine(content, startOffset, endOffset); break;
/* 321 */       case 3: applyWordLine(content, startOffset, endOffset); break;
/* 322 */       case 4: applyHeaderLine(content, startOffset, endOffset);
/*     */         break; }
/*     */   
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean startingMultiLineComment(String content, int startOffset, int endOffset) throws BadLocationException {
/* 354 */     int index = indexOf(content, getStartDelimiter(), startOffset);
/* 355 */     if (index < 0 || index > endOffset) {
/* 356 */       return false;
/*     */     }
/* 358 */     setMultiLineComment(true);
/* 359 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean endingMultiLineComment(String content, int startOffset, int endOffset) throws BadLocationException {
/* 368 */     int index = indexOf(content, getEndDelimiter(), startOffset);
/* 369 */     if (index < 0 || index > endOffset) {
/* 370 */       return false;
/*     */     }
/* 372 */     setMultiLineComment(false);
/* 373 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isMultiLineComment() {
/* 382 */     return this.multiLineComment;
/*     */   }
/*     */   
/*     */   private void setMultiLineComment(boolean value) {
/* 386 */     this.multiLineComment = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyHeaderLine(String content, int startOffset, int endOffset) {
/* 393 */     startOffset = skipSpaces(content, startOffset, endOffset);
/* 394 */     if ("ACDEFGHKLMNOPQRST".indexOf(content.charAt(startOffset)) >= 0) {
/* 395 */       int endOfToken = content.indexOf(':', startOffset);
/* 396 */       if (endOfToken < 0) endOfToken = startOffset + 1; 
/* 397 */       if (endOfToken < endOffset) {
/* 398 */         this.doc.setCharacterAttributes(startOffset, endOfToken - startOffset, this.styles[6], false);
/*     */       }
/*     */       
/* 401 */       startOffset = endOfToken + 1;
/* 402 */       if (startOffset < endOffset) {
/* 403 */         this.doc.setCharacterAttributes(startOffset, endOffset - startOffset, this.styles[7], false);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected int applyVoiceLine2(String content, int startOffset, int endOffset) {
/* 410 */     int idx = startOffset;
/* 411 */     int ttype = 0;
/* 412 */     if (content.charAt(idx) == '@') {
/* 413 */       ttype = 4;
/* 414 */       idx++;
/* 415 */       while (idx < content.length() && " \t:|',;\"\\/-=+"
/* 416 */         .indexOf(content.charAt(idx)) < 0) {
/* 417 */         idx++;
/*     */       }
/* 419 */       this.doc.setCharacterAttributes(startOffset, idx - startOffset, this.styles[ttype], false);
/*     */       
/* 421 */       return idx;
/*     */     } 
/* 423 */     if (content.charAt(idx) == '%') {
/* 424 */       idx++;
/* 425 */       ttype = 5;
/* 426 */       while (idx < content.length() && " \t:|',;\"\\/-=+"
/* 427 */         .indexOf(content.charAt(idx)) < 0) {
/* 428 */         idx++;
/*     */       }
/* 430 */       this.doc.setCharacterAttributes(startOffset, idx - startOffset, this.styles[ttype], false);
/*     */       
/* 432 */       return idx;
/*     */     } 
/*     */     
/* 435 */     return startOffset + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyVoiceLine(String content, int startOffset, int endOffset) {
/* 443 */     startOffset = skipSpaces(content, startOffset, endOffset);
/* 444 */     if (content.charAt(startOffset) == 'V') {
/* 445 */       int endOfToken = content.indexOf(':', startOffset);
/* 446 */       if (endOfToken < 0) endOfToken = startOffset + 1; 
/* 447 */       if (endOfToken < endOffset) {
/* 448 */         this.doc.setCharacterAttributes(startOffset, endOfToken - startOffset, this.styles[6], false);
/*     */       }
/*     */       
/* 451 */       startOffset = endOfToken + 1;
/* 452 */       while (startOffset < endOffset) {
/* 453 */         if (content.charAt(startOffset) == ' ') {
/* 454 */           startOffset++;
/*     */           continue;
/*     */         } 
/* 457 */         int ttype = getTokenType(content.charAt(startOffset));
/* 458 */         if (ttype >= 0) {
/* 459 */           this.doc.setCharacterAttributes(startOffset, 1, this.styles[ttype], false);
/* 460 */           startOffset++;
/*     */           continue;
/*     */         } 
/* 463 */         startOffset = applyVoiceLine2(content, startOffset, endOffset);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void applyWordLine(String content, int startOffset, int endOffset) {
/* 473 */     startOffset = skipSpaces(content, startOffset, endOffset);
/* 474 */     if (content.charAt(startOffset) == 'W') {
/* 475 */       int endOfToken = content.indexOf(':', startOffset);
/* 476 */       if (endOfToken < 0) endOfToken = startOffset + 1; 
/* 477 */       if (endOfToken < endOffset) {
/* 478 */         this.doc.setCharacterAttributes(startOffset, endOfToken - startOffset, this.styles[6], false);
/*     */       }
/*     */       
/* 481 */       startOffset = endOfToken + 1;
/* 482 */       if (startOffset < endOffset) {
/* 483 */         this.doc.setCharacterAttributes(startOffset, endOffset - startOffset, this.styles[8], false);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getQuoteToken(String content, int startOffset, int endOffset) {
/* 494 */     String quoteDelimiter = content.substring(startOffset, startOffset + 1);
/* 495 */     String escapeString = getEscapeString(quoteDelimiter);
/*     */     
/* 497 */     int endOfQuote = startOffset;
/*     */     
/* 499 */     int index = content.indexOf(escapeString, endOfQuote + 1);
/* 500 */     while (index > -1 && index < endOffset) {
/* 501 */       endOfQuote = index + 1;
/* 502 */       index = content.indexOf(escapeString, endOfQuote);
/*     */     } 
/*     */ 
/*     */     
/* 506 */     index = content.indexOf(quoteDelimiter, endOfQuote + 1);
/* 507 */     if (index < 0 || index > endOffset) {
/* 508 */       endOfQuote = endOffset;
/*     */     } else {
/* 510 */       endOfQuote = index;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 517 */     return endOfQuote + 1;
/*     */   }
/*     */   
/*     */   protected int getOtherToken(String content, int startOffset, int endOffset) {
/* 521 */     int endOfToken = startOffset + 1;
/* 522 */     while (endOfToken <= endOffset && 
/* 523 */       !isDelimiter(content.substring(endOfToken, endOfToken + 1)))
/*     */     {
/*     */       
/* 526 */       endOfToken++;
/*     */     }
/* 528 */     String token = content.substring(startOffset, endOfToken);
/* 529 */     if (isKeyword(token)) {
/* 530 */       this.doc.setCharacterAttributes(startOffset, endOfToken - startOffset, this.styles[6], false);
/*     */     }
/*     */     
/* 533 */     return endOfToken + 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void fireInsertUpdate(DocumentEvent evt) {
/* 540 */     if (evt.getLength() <= 0) {
/*     */       return;
/*     */     }
/*     */     try {
/* 544 */       super.fireInsertUpdate(evt);
/* 545 */       processChangedLines(evt.getOffset(), evt.getLength());
/* 546 */     } catch (BadLocationException ex) {
/* 547 */       System.out.println("" + ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void fireRemoveUpdate(DocumentEvent evt) {
/* 555 */     super.fireRemoveUpdate(evt);
/*     */     try {
/* 557 */       processChangedLines(evt.getOffset(), evt.getLength());
/* 558 */     } catch (BadLocationException ex) {
/* 559 */       System.out.println("" + ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int indexOf(String content, String needle, int offset) {
/*     */     int index;
/* 568 */     while ((index = content.indexOf(needle, offset)) != -1) {
/* 569 */       String text = getLine(content, index).trim();
/* 570 */       if (text.startsWith(needle) || text.endsWith(needle)) {
/*     */         break;
/*     */       }
/* 573 */       offset = index + 1;
/*     */     } 
/*     */     
/* 576 */     return index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int lastIndexOf(String content, String needle, int offset) {
/*     */     int index;
/* 586 */     while ((index = content.lastIndexOf(needle, offset)) != -1) {
/* 587 */       String text = getLine(content, index).trim();
/* 588 */       if (text.startsWith(needle) || text.endsWith(needle)) {
/*     */         break;
/*     */       }
/* 591 */       offset = index - 1;
/*     */     } 
/*     */     
/* 594 */     return index;
/*     */   }
/*     */   
/*     */   private String getLine(String content, int offset) {
/* 598 */     int line = this.rootElement.getElementIndex(offset);
/* 599 */     Element lineElement = this.rootElement.getElement(line);
/* 600 */     int start = lineElement.getStartOffset();
/* 601 */     int end = lineElement.getEndOffset();
/* 602 */     return content.substring(start, end - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isDelimiter(String character) {
/* 609 */     if (Character.isWhitespace(character.charAt(0)) || " \t:|',;\"\\/-=+"
/* 610 */       .indexOf(character) != -1) {
/* 611 */       return true;
/*     */     }
/* 613 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isQuoteDelimiter(String character) {
/* 620 */     String quoteDelimiters = "\"'";
/* 621 */     if (quoteDelimiters.indexOf(character) < 0) {
/* 622 */       return false;
/*     */     }
/* 624 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isKeyword(String token) {
/* 632 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getStartDelimiter() {
/* 639 */     return "|$";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getEndDelimiter() {
/* 646 */     return "$|";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getSingleLineDelimiter() {
/* 653 */     return "$";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getEscapeString(String quoteDelimiter) {
/* 660 */     return "\\" + quoteDelimiter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String addMatchingBrace(int offset) throws BadLocationException {
/* 667 */     StringBuilder whiteSpace = new StringBuilder();
/* 668 */     int line = this.rootElement.getElementIndex(offset);
/* 669 */     int i = this.rootElement.getElement(line).getStartOffset();
/*     */     while (true) {
/* 671 */       String temp = this.doc.getText(i, 1);
/* 672 */       if (temp.equals(" ") || temp.equals("\t")) {
/* 673 */         whiteSpace.append(temp);
/* 674 */         i++;
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/* 679 */     return "{\n" + whiteSpace.toString() + whiteSpace.toString() + "\n" + whiteSpace
/* 680 */       .toString() + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\PlainDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */