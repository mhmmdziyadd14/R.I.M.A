/*    */ package com.klungbot.app;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import javax.swing.text.BadLocationException;
/*    */ import javax.swing.text.MutableAttributeSet;
/*    */ import javax.swing.text.SimpleAttributeSet;
/*    */ import javax.swing.text.StyleConstants;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DoremiDocument
/*    */   extends PlainDocument
/*    */ {
/*    */   public DoremiDocument() {
/* 18 */     MutableAttributeSet s = new SimpleAttributeSet();
/* 19 */     StyleConstants.setItalic(s, false);
/* 20 */     StyleConstants.setBold(s, false);
/* 21 */     this.lineStyles[0] = s;
/*    */     
/* 23 */     s = new SimpleAttributeSet();
/* 24 */     StyleConstants.setFontFamily(s, "Courier New");
/* 25 */     StyleConstants.setFontSize(s, 14);
/* 26 */     this.lineStyles[4] = s;
/*    */     
/* 28 */     s = new SimpleAttributeSet();
/* 29 */     StyleConstants.setFontFamily(s, "Courier New");
/* 30 */     StyleConstants.setFontSize(s, 12);
/* 31 */     StyleConstants.setForeground(s, Color.GRAY);
/* 32 */     StyleConstants.setItalic(s, true);
/* 33 */     this.lineStyles[1] = s;
/*    */     
/* 35 */     s = new SimpleAttributeSet();
/* 36 */     StyleConstants.setFontFamily(s, "Courier New");
/* 37 */     StyleConstants.setFontSize(s, 16);
/* 38 */     this.lineStyles[3] = s;
/*    */     
/* 40 */     this.voiceAttr = new SimpleAttributeSet();
/* 41 */     StyleConstants.setFontFamily(this.voiceAttr, "Doremi");
/* 42 */     StyleConstants.setFontSize(this.voiceAttr, 16);
/* 43 */     StyleConstants.setItalic(this.voiceAttr, false);
/* 44 */     StyleConstants.setBold(this.voiceAttr, false);
/* 45 */     this.lineStyles[2] = this.voiceAttr;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void applyHighlighting(String content, int line) throws BadLocationException {
/* 53 */     int startOffset = this.rootElement.getElement(line).getStartOffset();
/* 54 */     int endOffset = this.rootElement.getElement(line).getEndOffset() - 1;
/* 55 */     int lineLength = endOffset - startOffset;
/* 56 */     int contentLength = content.length();
/* 57 */     if (endOffset >= contentLength) {
/* 58 */       endOffset = contentLength - 1;
/*    */     }
/* 60 */     int ltype = getLineType(content, startOffset, endOffset);
/* 61 */     this.doc.setCharacterAttributes(startOffset, lineLength, this.lineStyles[ltype], false);
/* 62 */     switch (ltype) { case 2:
/* 63 */         applyVoiceLine(content, startOffset, endOffset); break;
/* 64 */       case 3: applyWordLine(content, startOffset, endOffset); break;
/* 65 */       case 4: applyHeaderLine(content, startOffset, endOffset);
/*    */         break; }
/*    */   
/*    */   }
/*    */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\app\DoremiDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */