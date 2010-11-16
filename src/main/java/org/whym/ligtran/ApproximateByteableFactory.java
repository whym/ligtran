package org.whym.ligtran;
import java.util.*;

public class ApproximateByteableFactory implements ByteableFactory{
  private final double[][] projection;
  private final int orig_dim;
  private final int bits;
  private final int center;
  public ApproximateByteableFactory(int orig_dim, int bits, Random random, int center) {
    this.orig_dim = orig_dim;
    this.bits = bits;
    this.center = center;
    this.projection = new double[orig_dim][];
    double nonzero_prob = 1.0 / Math.sqrt(orig_dim);
    double coeff = Math.sqrt(Math.sqrt(orig_dim));
    for ( int i = 0; i < orig_dim; ++i ) {
      this.projection[i] = new double[bits];
      for ( int j = 0; j < bits; ++j ) {  
        double v = 0;
        double r = random.nextDouble();
        if ( r < nonzero_prob ) {
          if ( r < nonzero_prob / 2 ) {
            v = -coeff;
          } else {
            v = coeff;
          }
        }
        this.projection[i][j] = v;
      }
    }
  }
  public AbstractByteable getByteable(int[] vector) {
    final byte[] bytes = new byte[this.bits];
    for ( int j = 0; j < bits; ++j ) {  
      double s = 0;
      for ( int i = 0; i < orig_dim; ++i ) {
        s += this.projection[i][j] * vector[i];
      }
      bytes[j] = (byte)(s > 0 ? 1 : 0);
    }
    return new AbstractByteable() {
      public byte[] getBytes() { return bytes; }
    };
  }
}
