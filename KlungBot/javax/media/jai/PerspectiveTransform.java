/*      */ package javax.media.jai;
/*      */ 
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.NoninvertibleTransformException;
/*      */ import java.awt.geom.Point2D;
/*      */ import java.io.Serializable;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class PerspectiveTransform
/*      */   implements Cloneable, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*      */   private static final double PERSPECTIVE_DIVIDE_EPSILON = 1.0E-10D;
/*      */   double m00;
/*      */   double m01;
/*      */   double m02;
/*      */   double m10;
/*      */   double m11;
/*      */   double m12;
/*      */   double m20;
/*      */   double m21;
/*      */   double m22;
/*      */   
/*      */   public PerspectiveTransform() {
/*   59 */     this.m00 = this.m11 = this.m22 = 1.0D;
/*   60 */     this.m01 = this.m02 = this.m10 = this.m12 = this.m20 = this.m21 = 0.0D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PerspectiveTransform(double[][] matrix) {
/*   70 */     if (matrix == null) {
/*   71 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*   74 */     this.m00 = matrix[0][0];
/*   75 */     this.m01 = matrix[0][1];
/*   76 */     this.m02 = matrix[0][2];
/*   77 */     this.m10 = matrix[1][0];
/*   78 */     this.m11 = matrix[1][1];
/*   79 */     this.m12 = matrix[1][2];
/*   80 */     this.m20 = matrix[2][0];
/*   81 */     this.m21 = matrix[2][1];
/*   82 */     this.m22 = matrix[2][2];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PerspectiveTransform(AffineTransform transform) {
/*   91 */     if (transform == null) {
/*   92 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*   95 */     this.m00 = transform.getScaleX();
/*   96 */     this.m01 = transform.getShearX();
/*   97 */     this.m02 = transform.getTranslateX();
/*   98 */     this.m10 = transform.getShearY();
/*   99 */     this.m11 = transform.getScaleY();
/*  100 */     this.m12 = transform.getTranslateY();
/*  101 */     this.m20 = 0.0D;
/*  102 */     this.m21 = 0.0D;
/*  103 */     this.m22 = 1.0D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void makeAdjoint() {
/*  110 */     double m00p = this.m11 * this.m22 - this.m12 * this.m21;
/*  111 */     double m01p = this.m12 * this.m20 - this.m10 * this.m22;
/*  112 */     double m02p = this.m10 * this.m21 - this.m11 * this.m20;
/*  113 */     double m10p = this.m02 * this.m21 - this.m01 * this.m22;
/*  114 */     double m11p = this.m00 * this.m22 - this.m02 * this.m20;
/*  115 */     double m12p = this.m01 * this.m20 - this.m00 * this.m21;
/*  116 */     double m20p = this.m01 * this.m12 - this.m02 * this.m11;
/*  117 */     double m21p = this.m02 * this.m10 - this.m00 * this.m12;
/*  118 */     double m22p = this.m00 * this.m11 - this.m01 * this.m10;
/*      */ 
/*      */     
/*  121 */     this.m00 = m00p;
/*  122 */     this.m01 = m10p;
/*  123 */     this.m02 = m20p;
/*  124 */     this.m10 = m01p;
/*  125 */     this.m11 = m11p;
/*  126 */     this.m12 = m21p;
/*  127 */     this.m20 = m02p;
/*  128 */     this.m21 = m12p;
/*  129 */     this.m22 = m22p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final void normalize() {
/*  137 */     double invscale = 1.0D / this.m22;
/*  138 */     this.m00 *= invscale;
/*  139 */     this.m01 *= invscale;
/*  140 */     this.m02 *= invscale;
/*  141 */     this.m10 *= invscale;
/*  142 */     this.m11 *= invscale;
/*  143 */     this.m12 *= invscale;
/*  144 */     this.m20 *= invscale;
/*  145 */     this.m21 *= invscale;
/*  146 */     this.m22 = 1.0D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final void getSquareToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, PerspectiveTransform tx) {
/*  154 */     double dx3 = x0 - x1 + x2 - x3;
/*  155 */     double dy3 = y0 - y1 + y2 - y3;
/*      */     
/*  157 */     tx.m22 = 1.0D;
/*      */     
/*  159 */     if (dx3 == 0.0D && dy3 == 0.0D) {
/*  160 */       tx.m00 = x1 - x0;
/*  161 */       tx.m01 = x2 - x1;
/*  162 */       tx.m02 = x0;
/*  163 */       tx.m10 = y1 - y0;
/*  164 */       tx.m11 = y2 - y1;
/*  165 */       tx.m12 = y0;
/*  166 */       tx.m20 = 0.0D;
/*  167 */       tx.m21 = 0.0D;
/*      */     } else {
/*  169 */       double dx1 = x1 - x2;
/*  170 */       double dy1 = y1 - y2;
/*  171 */       double dx2 = x3 - x2;
/*  172 */       double dy2 = y3 - y2;
/*      */       
/*  174 */       double invdet = 1.0D / (dx1 * dy2 - dx2 * dy1);
/*  175 */       tx.m20 = (dx3 * dy2 - dx2 * dy3) * invdet;
/*  176 */       tx.m21 = (dx1 * dy3 - dx3 * dy1) * invdet;
/*  177 */       tx.m00 = x1 - x0 + tx.m20 * x1;
/*  178 */       tx.m01 = x3 - x0 + tx.m21 * x3;
/*  179 */       tx.m02 = x0;
/*  180 */       tx.m10 = y1 - y0 + tx.m20 * y1;
/*  181 */       tx.m11 = y3 - y0 + tx.m21 * y3;
/*  182 */       tx.m12 = y0;
/*      */     } 
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
/*      */   public static PerspectiveTransform getSquareToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
/*  201 */     PerspectiveTransform tx = new PerspectiveTransform();
/*  202 */     getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
/*  203 */     return tx;
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
/*      */   public static PerspectiveTransform getSquareToQuad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
/*  222 */     return getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3);
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
/*      */   public static PerspectiveTransform getQuadToSquare(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
/*  244 */     PerspectiveTransform tx = new PerspectiveTransform();
/*  245 */     getSquareToQuad(x0, y0, x1, y1, x2, y2, x3, y3, tx);
/*  246 */     tx.makeAdjoint();
/*  247 */     return tx;
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
/*      */   public static PerspectiveTransform getQuadToSquare(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
/*  265 */     return getQuadToSquare(x0, y0, x1, y1, x2, y2, x3, y3);
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
/*      */   public static PerspectiveTransform getQuadToQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double x0p, double y0p, double x1p, double y1p, double x2p, double y2p, double x3p, double y3p) {
/*  291 */     PerspectiveTransform tx1 = getQuadToSquare(x0, y0, x1, y1, x2, y2, x3, y3);
/*      */ 
/*      */     
/*  294 */     PerspectiveTransform tx2 = getSquareToQuad(x0p, y0p, x1p, y1p, x2p, y2p, x3p, y3p);
/*      */     
/*  296 */     tx1.concatenate(tx2);
/*  297 */     return tx1;
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
/*      */   public static PerspectiveTransform getQuadToQuad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, float x0p, float y0p, float x1p, float y1p, float x2p, float y2p, float x3p, float y3p) {
/*  320 */     return getQuadToQuad(x0, y0, x1, y1, x2, y2, x3, y3, x0p, y0p, x1p, y1p, x2p, y2p, x3p, y3p);
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
/*      */   public double getDeterminant() {
/*  335 */     return this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) - this.m01 * (this.m10 * this.m22 - this.m12 * this.m20) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
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
/*      */   @Deprecated
/*      */   public double[] getMatrix(double[] flatmatrix) {
/*  355 */     if (flatmatrix == null) {
/*  356 */       flatmatrix = new double[9];
/*      */     }
/*      */     
/*  359 */     flatmatrix[0] = this.m00;
/*  360 */     flatmatrix[1] = this.m01;
/*  361 */     flatmatrix[2] = this.m02;
/*  362 */     flatmatrix[3] = this.m10;
/*  363 */     flatmatrix[4] = this.m11;
/*  364 */     flatmatrix[5] = this.m12;
/*  365 */     flatmatrix[6] = this.m20;
/*  366 */     flatmatrix[7] = this.m21;
/*  367 */     flatmatrix[8] = this.m22;
/*      */     
/*  369 */     return flatmatrix;
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
/*      */   public double[][] getMatrix(double[][] matrix) {
/*  384 */     if (matrix == null) {
/*  385 */       matrix = new double[3][3];
/*      */     }
/*      */     
/*  388 */     matrix[0][0] = this.m00;
/*  389 */     matrix[0][1] = this.m01;
/*  390 */     matrix[0][2] = this.m02;
/*  391 */     matrix[1][0] = this.m10;
/*  392 */     matrix[1][1] = this.m11;
/*  393 */     matrix[1][2] = this.m12;
/*  394 */     matrix[2][0] = this.m20;
/*  395 */     matrix[2][1] = this.m21;
/*  396 */     matrix[2][2] = this.m22;
/*      */     
/*  398 */     return matrix;
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
/*      */   public void translate(double tx, double ty) {
/*  412 */     PerspectiveTransform Tx = new PerspectiveTransform();
/*  413 */     Tx.setToTranslation(tx, ty);
/*  414 */     concatenate(Tx);
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
/*      */   public void rotate(double theta) {
/*  432 */     PerspectiveTransform Tx = new PerspectiveTransform();
/*  433 */     Tx.setToRotation(theta);
/*  434 */     concatenate(Tx);
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
/*      */   public void rotate(double theta, double x, double y) {
/*  453 */     PerspectiveTransform Tx = new PerspectiveTransform();
/*  454 */     Tx.setToRotation(theta, x, y);
/*  455 */     concatenate(Tx);
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
/*      */   public void scale(double sx, double sy) {
/*  472 */     PerspectiveTransform Tx = new PerspectiveTransform();
/*  473 */     Tx.setToScale(sx, sy);
/*  474 */     concatenate(Tx);
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
/*      */   public void shear(double shx, double shy) {
/*  495 */     PerspectiveTransform Tx = new PerspectiveTransform();
/*  496 */     Tx.setToShear(shx, shy);
/*  497 */     concatenate(Tx);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setToIdentity() {
/*  504 */     this.m00 = this.m11 = this.m22 = 1.0D;
/*  505 */     this.m01 = this.m10 = this.m02 = this.m20 = this.m12 = this.m21 = 0.0D;
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
/*      */   public void setToTranslation(double tx, double ty) {
/*  522 */     this.m00 = 1.0D;
/*  523 */     this.m01 = 0.0D;
/*  524 */     this.m02 = tx;
/*  525 */     this.m10 = 0.0D;
/*  526 */     this.m11 = 1.0D;
/*  527 */     this.m12 = ty;
/*  528 */     this.m20 = 0.0D;
/*  529 */     this.m21 = 0.0D;
/*  530 */     this.m22 = 1.0D;
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
/*      */   public void setToRotation(double theta) {
/*  546 */     this.m00 = Math.cos(theta);
/*  547 */     this.m01 = -Math.sin(theta);
/*  548 */     this.m02 = 0.0D;
/*  549 */     this.m10 = -this.m01;
/*  550 */     this.m11 = this.m00;
/*  551 */     this.m12 = 0.0D;
/*  552 */     this.m20 = 0.0D;
/*  553 */     this.m21 = 0.0D;
/*  554 */     this.m22 = 1.0D;
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
/*      */   public void setToRotation(double theta, double x, double y) {
/*  576 */     setToRotation(theta);
/*  577 */     double sin = this.m10;
/*  578 */     double oneMinusCos = 1.0D - this.m00;
/*  579 */     this.m02 = x * oneMinusCos + y * sin;
/*  580 */     this.m12 = y * oneMinusCos - x * sin;
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
/*      */   public void setToScale(double sx, double sy) {
/*  597 */     this.m00 = sx;
/*  598 */     this.m01 = 0.0D;
/*  599 */     this.m02 = 0.0D;
/*  600 */     this.m10 = 0.0D;
/*  601 */     this.m11 = sy;
/*  602 */     this.m12 = 0.0D;
/*  603 */     this.m20 = 0.0D;
/*  604 */     this.m21 = 0.0D;
/*  605 */     this.m22 = 1.0D;
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
/*      */   public void setToShear(double shx, double shy) {
/*  626 */     this.m00 = 1.0D;
/*  627 */     this.m01 = shx;
/*  628 */     this.m02 = 0.0D;
/*  629 */     this.m10 = shy;
/*  630 */     this.m11 = 1.0D;
/*  631 */     this.m12 = 0.0D;
/*  632 */     this.m20 = 0.0D;
/*  633 */     this.m21 = 0.0D;
/*  634 */     this.m22 = 1.0D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTransform(AffineTransform Tx) {
/*  642 */     if (Tx == null) {
/*  643 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*  646 */     this.m00 = Tx.getScaleX();
/*  647 */     this.m01 = Tx.getShearX();
/*  648 */     this.m02 = Tx.getTranslateX();
/*  649 */     this.m10 = Tx.getShearY();
/*  650 */     this.m11 = Tx.getScaleY();
/*  651 */     this.m12 = Tx.getTranslateY();
/*  652 */     this.m20 = 0.0D;
/*  653 */     this.m21 = 0.0D;
/*  654 */     this.m22 = 1.0D;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setTransform(PerspectiveTransform Tx) {
/*  662 */     if (Tx == null) {
/*  663 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*  666 */     this.m00 = Tx.m00;
/*  667 */     this.m01 = Tx.m01;
/*  668 */     this.m02 = Tx.m02;
/*  669 */     this.m10 = Tx.m10;
/*  670 */     this.m11 = Tx.m11;
/*  671 */     this.m12 = Tx.m12;
/*  672 */     this.m20 = Tx.m20;
/*  673 */     this.m21 = Tx.m21;
/*  674 */     this.m22 = Tx.m22;
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
/*      */   public void setTransform(double[][] matrix) {
/*  688 */     if (matrix == null) {
/*  689 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*  692 */     this.m00 = matrix[0][0];
/*  693 */     this.m01 = matrix[0][1];
/*  694 */     this.m02 = matrix[0][2];
/*  695 */     this.m10 = matrix[1][0];
/*  696 */     this.m11 = matrix[1][1];
/*  697 */     this.m12 = matrix[1][2];
/*  698 */     this.m20 = matrix[2][0];
/*  699 */     this.m21 = matrix[2][1];
/*  700 */     this.m22 = matrix[2][2];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void concatenate(AffineTransform Tx) {
/*  708 */     if (Tx == null) {
/*  709 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  714 */     double tx_m00 = Tx.getScaleX();
/*  715 */     double tx_m01 = Tx.getShearX();
/*  716 */     double tx_m02 = Tx.getTranslateX();
/*  717 */     double tx_m10 = Tx.getShearY();
/*  718 */     double tx_m11 = Tx.getScaleY();
/*  719 */     double tx_m12 = Tx.getTranslateY();
/*      */     
/*  721 */     double m00p = this.m00 * tx_m00 + this.m10 * tx_m01 + this.m20 * tx_m02;
/*  722 */     double m01p = this.m01 * tx_m00 + this.m11 * tx_m01 + this.m21 * tx_m02;
/*  723 */     double m02p = this.m02 * tx_m00 + this.m12 * tx_m01 + this.m22 * tx_m02;
/*  724 */     double m10p = this.m00 * tx_m10 + this.m10 * tx_m11 + this.m20 * tx_m12;
/*  725 */     double m11p = this.m01 * tx_m10 + this.m11 * tx_m11 + this.m21 * tx_m12;
/*  726 */     double m12p = this.m02 * tx_m10 + this.m12 * tx_m11 + this.m22 * tx_m12;
/*  727 */     double m20p = this.m20;
/*  728 */     double m21p = this.m21;
/*  729 */     double m22p = this.m22;
/*      */     
/*  731 */     this.m00 = m00p;
/*  732 */     this.m10 = m10p;
/*  733 */     this.m20 = m20p;
/*  734 */     this.m01 = m01p;
/*  735 */     this.m11 = m11p;
/*  736 */     this.m21 = m21p;
/*  737 */     this.m02 = m02p;
/*  738 */     this.m12 = m12p;
/*  739 */     this.m22 = m22p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void concatenate(PerspectiveTransform Tx) {
/*  747 */     if (Tx == null) {
/*  748 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*  751 */     double m00p = this.m00 * Tx.m00 + this.m10 * Tx.m01 + this.m20 * Tx.m02;
/*  752 */     double m10p = this.m00 * Tx.m10 + this.m10 * Tx.m11 + this.m20 * Tx.m12;
/*  753 */     double m20p = this.m00 * Tx.m20 + this.m10 * Tx.m21 + this.m20 * Tx.m22;
/*  754 */     double m01p = this.m01 * Tx.m00 + this.m11 * Tx.m01 + this.m21 * Tx.m02;
/*  755 */     double m11p = this.m01 * Tx.m10 + this.m11 * Tx.m11 + this.m21 * Tx.m12;
/*  756 */     double m21p = this.m01 * Tx.m20 + this.m11 * Tx.m21 + this.m21 * Tx.m22;
/*  757 */     double m02p = this.m02 * Tx.m00 + this.m12 * Tx.m01 + this.m22 * Tx.m02;
/*  758 */     double m12p = this.m02 * Tx.m10 + this.m12 * Tx.m11 + this.m22 * Tx.m12;
/*  759 */     double m22p = this.m02 * Tx.m20 + this.m12 * Tx.m21 + this.m22 * Tx.m22;
/*      */     
/*  761 */     this.m00 = m00p;
/*  762 */     this.m10 = m10p;
/*  763 */     this.m20 = m20p;
/*  764 */     this.m01 = m01p;
/*  765 */     this.m11 = m11p;
/*  766 */     this.m21 = m21p;
/*  767 */     this.m02 = m02p;
/*  768 */     this.m12 = m12p;
/*  769 */     this.m22 = m22p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void preConcatenate(AffineTransform Tx) {
/*  777 */     if (Tx == null) {
/*  778 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  783 */     double tx_m00 = Tx.getScaleX();
/*  784 */     double tx_m01 = Tx.getShearX();
/*  785 */     double tx_m02 = Tx.getTranslateX();
/*  786 */     double tx_m10 = Tx.getShearY();
/*  787 */     double tx_m11 = Tx.getScaleY();
/*  788 */     double tx_m12 = Tx.getTranslateY();
/*      */     
/*  790 */     double m00p = tx_m00 * this.m00 + tx_m10 * this.m01;
/*  791 */     double m01p = tx_m01 * this.m00 + tx_m11 * this.m01;
/*  792 */     double m02p = tx_m02 * this.m00 + tx_m12 * this.m01 + this.m02;
/*  793 */     double m10p = tx_m00 * this.m10 + tx_m10 * this.m11;
/*  794 */     double m11p = tx_m01 * this.m10 + tx_m11 * this.m11;
/*  795 */     double m12p = tx_m02 * this.m10 + tx_m12 * this.m11 + this.m12;
/*  796 */     double m20p = tx_m00 * this.m20 + tx_m10 * this.m21;
/*  797 */     double m21p = tx_m01 * this.m20 + tx_m11 * this.m21;
/*  798 */     double m22p = tx_m02 * this.m20 + tx_m12 * this.m21 + this.m22;
/*      */     
/*  800 */     this.m00 = m00p;
/*  801 */     this.m10 = m10p;
/*  802 */     this.m20 = m20p;
/*  803 */     this.m01 = m01p;
/*  804 */     this.m11 = m11p;
/*  805 */     this.m21 = m21p;
/*  806 */     this.m02 = m02p;
/*  807 */     this.m12 = m12p;
/*  808 */     this.m22 = m22p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void preConcatenate(PerspectiveTransform Tx) {
/*  816 */     if (Tx == null) {
/*  817 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*  820 */     double m00p = Tx.m00 * this.m00 + Tx.m10 * this.m01 + Tx.m20 * this.m02;
/*  821 */     double m10p = Tx.m00 * this.m10 + Tx.m10 * this.m11 + Tx.m20 * this.m12;
/*  822 */     double m20p = Tx.m00 * this.m20 + Tx.m10 * this.m21 + Tx.m20 * this.m22;
/*  823 */     double m01p = Tx.m01 * this.m00 + Tx.m11 * this.m01 + Tx.m21 * this.m02;
/*  824 */     double m11p = Tx.m01 * this.m10 + Tx.m11 * this.m11 + Tx.m21 * this.m12;
/*  825 */     double m21p = Tx.m01 * this.m20 + Tx.m11 * this.m21 + Tx.m21 * this.m22;
/*  826 */     double m02p = Tx.m02 * this.m00 + Tx.m12 * this.m01 + Tx.m22 * this.m02;
/*  827 */     double m12p = Tx.m02 * this.m10 + Tx.m12 * this.m11 + Tx.m22 * this.m12;
/*  828 */     double m22p = Tx.m02 * this.m20 + Tx.m12 * this.m21 + Tx.m22 * this.m22;
/*      */     
/*  830 */     this.m00 = m00p;
/*  831 */     this.m10 = m10p;
/*  832 */     this.m20 = m20p;
/*  833 */     this.m01 = m01p;
/*  834 */     this.m11 = m11p;
/*  835 */     this.m21 = m21p;
/*  836 */     this.m02 = m02p;
/*  837 */     this.m12 = m12p;
/*  838 */     this.m22 = m22p;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PerspectiveTransform createInverse() throws NoninvertibleTransformException {
/*  849 */     PerspectiveTransform tx = (PerspectiveTransform)clone();
/*  850 */     tx.makeAdjoint();
/*  851 */     if (Math.abs(tx.m22) < 1.0E-10D) {
/*  852 */       throw new NoninvertibleTransformException(JaiI18N.getString("PerspectiveTransform0"));
/*      */     }
/*  854 */     tx.normalize();
/*  855 */     return tx;
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
/*      */   public PerspectiveTransform createAdjoint() {
/*  874 */     PerspectiveTransform tx = (PerspectiveTransform)clone();
/*  875 */     tx.makeAdjoint();
/*  876 */     return tx;
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
/*      */   public Point2D transform(Point2D ptSrc, Point2D ptDst) {
/*  892 */     if (ptSrc == null) {
/*  893 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*  896 */     if (ptDst == null) {
/*  897 */       if (ptSrc instanceof Point2D.Double) {
/*  898 */         ptDst = new Point2D.Double();
/*      */       } else {
/*  900 */         ptDst = new Point2D.Float();
/*      */       } 
/*      */     }
/*      */     
/*  904 */     double x = ptSrc.getX();
/*  905 */     double y = ptSrc.getY();
/*  906 */     double w = this.m20 * x + this.m21 * y + this.m22;
/*  907 */     ptDst.setLocation((this.m00 * x + this.m01 * y + this.m02) / w, (this.m10 * x + this.m11 * y + this.m12) / w);
/*      */ 
/*      */     
/*  910 */     return ptDst;
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
/*      */   public void transform(Point2D[] ptSrc, int srcOff, Point2D[] ptDst, int dstOff, int numPts) {
/*  930 */     if (ptSrc == null || ptDst == null) {
/*  931 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*  934 */     while (numPts-- > 0) {
/*      */       
/*  936 */       Point2D src = ptSrc[srcOff++];
/*  937 */       Point2D dst = ptDst[dstOff++];
/*  938 */       if (dst == null) {
/*  939 */         if (src instanceof Point2D.Double) {
/*  940 */           dst = new Point2D.Double();
/*      */         } else {
/*  942 */           dst = new Point2D.Float();
/*      */         } 
/*  944 */         ptDst[dstOff - 1] = dst;
/*      */       } 
/*      */       
/*  947 */       double x = src.getX();
/*  948 */       double y = src.getY();
/*  949 */       double w = this.m20 * x + this.m21 * y + this.m22;
/*      */       
/*  951 */       if (w == 0.0D) {
/*  952 */         dst.setLocation(x, y); continue;
/*      */       } 
/*  954 */       dst.setLocation((this.m00 * x + this.m01 * y + this.m02) / w, (this.m10 * x + this.m11 * y + this.m12) / w);
/*      */     } 
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
/*      */   public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
/*  978 */     if (srcPts == null) {
/*  979 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/*  982 */     if (dstPts == null) {
/*  983 */       dstPts = new float[numPts * 2 + dstOff];
/*      */     }
/*      */     
/*  986 */     while (numPts-- > 0) {
/*  987 */       float x = srcPts[srcOff++];
/*  988 */       float y = srcPts[srcOff++];
/*  989 */       double w = this.m20 * x + this.m21 * y + this.m22;
/*      */       
/*  991 */       if (w == 0.0D) {
/*  992 */         dstPts[dstOff++] = x;
/*  993 */         dstPts[dstOff++] = y; continue;
/*      */       } 
/*  995 */       dstPts[dstOff++] = (float)((this.m00 * x + this.m01 * y + this.m02) / w);
/*  996 */       dstPts[dstOff++] = (float)((this.m10 * x + this.m11 * y + this.m12) / w);
/*      */     } 
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
/*      */   public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
/* 1019 */     if (srcPts == null) {
/* 1020 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/* 1023 */     if (dstPts == null) {
/* 1024 */       dstPts = new double[numPts * 2 + dstOff];
/*      */     }
/*      */     
/* 1027 */     while (numPts-- > 0) {
/* 1028 */       double x = srcPts[srcOff++];
/* 1029 */       double y = srcPts[srcOff++];
/* 1030 */       double w = this.m20 * x + this.m21 * y + this.m22;
/*      */       
/* 1032 */       if (w == 0.0D) {
/* 1033 */         dstPts[dstOff++] = x;
/* 1034 */         dstPts[dstOff++] = y; continue;
/*      */       } 
/* 1036 */       dstPts[dstOff++] = (this.m00 * x + this.m01 * y + this.m02) / w;
/* 1037 */       dstPts[dstOff++] = (this.m10 * x + this.m11 * y + this.m12) / w;
/*      */     } 
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
/*      */   public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
/* 1061 */     if (srcPts == null) {
/* 1062 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/* 1065 */     if (dstPts == null) {
/* 1066 */       dstPts = new double[numPts * 2 + dstOff];
/*      */     }
/*      */     
/* 1069 */     while (numPts-- > 0) {
/* 1070 */       float x = srcPts[srcOff++];
/* 1071 */       float y = srcPts[srcOff++];
/* 1072 */       double w = this.m20 * x + this.m21 * y + this.m22;
/*      */       
/* 1074 */       if (w == 0.0D) {
/* 1075 */         dstPts[dstOff++] = x;
/* 1076 */         dstPts[dstOff++] = y; continue;
/*      */       } 
/* 1078 */       dstPts[dstOff++] = (this.m00 * x + this.m01 * y + this.m02) / w;
/* 1079 */       dstPts[dstOff++] = (this.m10 * x + this.m11 * y + this.m12) / w;
/*      */     } 
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
/*      */   public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
/* 1103 */     if (srcPts == null) {
/* 1104 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/* 1107 */     if (dstPts == null) {
/* 1108 */       dstPts = new float[numPts * 2 + dstOff];
/*      */     }
/*      */     
/* 1111 */     while (numPts-- > 0) {
/* 1112 */       double x = srcPts[srcOff++];
/* 1113 */       double y = srcPts[srcOff++];
/* 1114 */       double w = this.m20 * x + this.m21 * y + this.m22;
/*      */       
/* 1116 */       if (w == 0.0D) {
/* 1117 */         dstPts[dstOff++] = (float)x;
/* 1118 */         dstPts[dstOff++] = (float)y; continue;
/*      */       } 
/* 1120 */       dstPts[dstOff++] = (float)((this.m00 * x + this.m01 * y + this.m02) / w);
/* 1121 */       dstPts[dstOff++] = (float)((this.m10 * x + this.m11 * y + this.m12) / w);
/*      */     } 
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
/*      */   public Point2D inverseTransform(Point2D ptSrc, Point2D ptDst) throws NoninvertibleTransformException {
/* 1142 */     if (ptSrc == null) {
/* 1143 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/* 1146 */     if (ptDst == null) {
/* 1147 */       if (ptSrc instanceof Point2D.Double) {
/* 1148 */         ptDst = new Point2D.Double();
/*      */       } else {
/* 1150 */         ptDst = new Point2D.Float();
/*      */       } 
/*      */     }
/*      */     
/* 1154 */     double x = ptSrc.getX();
/* 1155 */     double y = ptSrc.getY();
/*      */     
/* 1157 */     double tmp_x = (this.m11 * this.m22 - this.m12 * this.m21) * x + (this.m02 * this.m21 - this.m01 * this.m22) * y + this.m01 * this.m12 - this.m02 * this.m11;
/*      */ 
/*      */     
/* 1160 */     double tmp_y = (this.m12 * this.m20 - this.m10 * this.m22) * x + (this.m00 * this.m22 - this.m02 * this.m20) * y + this.m02 * this.m10 - this.m00 * this.m12;
/*      */ 
/*      */     
/* 1163 */     double w = (this.m10 * this.m21 - this.m11 * this.m20) * x + (this.m01 * this.m20 - this.m00 * this.m21) * y + this.m00 * this.m11 - this.m01 * this.m10;
/*      */ 
/*      */ 
/*      */     
/* 1167 */     double wabs = w;
/* 1168 */     if (w < 0.0D) {
/* 1169 */       wabs = -w;
/*      */     }
/* 1171 */     if (wabs < 1.0E-10D) {
/* 1172 */       throw new NoninvertibleTransformException(
/*      */           
/* 1174 */           JaiI18N.getString("PerspectiveTransform1"));
/*      */     }
/*      */     
/* 1177 */     ptDst.setLocation(tmp_x / w, tmp_y / w);
/*      */     
/* 1179 */     return ptDst;
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
/*      */   public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws NoninvertibleTransformException {
/* 1205 */     if (srcPts == null) {
/* 1206 */       throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
/*      */     }
/*      */     
/* 1209 */     if (dstPts == null) {
/* 1210 */       dstPts = new double[numPts * 2 + dstOff];
/*      */     }
/*      */     
/* 1213 */     while (numPts-- > 0) {
/* 1214 */       double x = srcPts[srcOff++];
/* 1215 */       double y = srcPts[srcOff++];
/*      */       
/* 1217 */       double tmp_x = (this.m11 * this.m22 - this.m12 * this.m21) * x + (this.m02 * this.m21 - this.m01 * this.m22) * y + this.m01 * this.m12 - this.m02 * this.m11;
/*      */ 
/*      */       
/* 1220 */       double tmp_y = (this.m12 * this.m20 - this.m10 * this.m22) * x + (this.m00 * this.m22 - this.m02 * this.m20) * y + this.m02 * this.m10 - this.m00 * this.m12;
/*      */ 
/*      */       
/* 1223 */       double w = (this.m10 * this.m21 - this.m11 * this.m20) * x + (this.m01 * this.m20 - this.m00 * this.m21) * y + this.m00 * this.m11 - this.m01 * this.m10;
/*      */ 
/*      */ 
/*      */       
/* 1227 */       double wabs = w;
/* 1228 */       if (w < 0.0D) {
/* 1229 */         wabs = -w;
/*      */       }
/* 1231 */       if (wabs < 1.0E-10D) {
/* 1232 */         throw new NoninvertibleTransformException(
/* 1233 */             JaiI18N.getString("PerspectiveTransform1"));
/*      */       }
/*      */       
/* 1236 */       dstPts[dstOff++] = tmp_x / w;
/* 1237 */       dstPts[dstOff++] = tmp_y / w;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1246 */     StringBuffer sb = new StringBuffer();
/* 1247 */     sb.append("Perspective transform matrix\n");
/* 1248 */     sb.append(this.m00);
/* 1249 */     sb.append("\t");
/* 1250 */     sb.append(this.m01);
/* 1251 */     sb.append("\t");
/* 1252 */     sb.append(this.m02);
/* 1253 */     sb.append("\n");
/* 1254 */     sb.append(this.m10);
/* 1255 */     sb.append("\t");
/* 1256 */     sb.append(this.m11);
/* 1257 */     sb.append("\t");
/* 1258 */     sb.append(this.m12);
/* 1259 */     sb.append("\n");
/* 1260 */     sb.append(this.m20);
/* 1261 */     sb.append("\t");
/* 1262 */     sb.append(this.m21);
/* 1263 */     sb.append("\t");
/* 1264 */     sb.append(this.m22);
/* 1265 */     sb.append("\n");
/* 1266 */     return new String(sb);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isIdentity() {
/* 1274 */     return (this.m01 == 0.0D && this.m02 == 0.0D && this.m10 == 0.0D && this.m12 == 0.0D && this.m20 == 0.0D && this.m21 == 0.0D && this.m22 != 0.0D && this.m00 / this.m22 == 1.0D && this.m11 / this.m22 == 1.0D);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object clone() {
/*      */     try {
/* 1286 */       return super.clone();
/* 1287 */     } catch (CloneNotSupportedException e) {
/*      */       
/* 1289 */       throw new InternalError();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object obj) {
/* 1301 */     if (!(obj instanceof PerspectiveTransform)) {
/* 1302 */       return false;
/*      */     }
/*      */     
/* 1305 */     PerspectiveTransform a = (PerspectiveTransform)obj;
/*      */     
/* 1307 */     return (this.m00 == a.m00 && this.m10 == a.m10 && this.m20 == a.m20 && this.m01 == a.m01 && this.m11 == a.m11 && this.m21 == a.m21 && this.m02 == a.m02 && this.m12 == a.m12 && this.m22 == a.m22);
/*      */   }
/*      */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\javax\media\jai\PerspectiveTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */