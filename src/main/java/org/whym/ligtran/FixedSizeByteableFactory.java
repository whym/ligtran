package org.whym.ligtran;
import java.util.*;

public class FixedSizeByteableFactory implements ByteableFactory {
  private final int intSize;
  private final int wordSize;

  public FixedSizeByteableFactory(int intSize, int wordSize) {
    this.intSize = intSize;
    this.wordSize = wordSize;
  }

  public static byte[] int2word(int[]src, int intsize, int wordsize) {
    int srcLength = src.length;
    byte[]dst = new byte[srcLength * (intsize / wordsize)];
    int mask = (1 << wordsize) - 1;
    for (int i=0; i < srcLength; ++i) {
      int x = src[i];
      int n = i * (intsize / wordsize);
      int j;
      for ( j = 0; j < (intsize / wordsize); ++j ) {
          dst[n + j] = (byte) ((x >>> (j * wordsize)) & mask);
      }
    }
    return dst;
  }

  public AbstractByteable getByteable(int[] vector) {
    final byte[] bytes = int2word(vector, this.intSize, this.wordSize);
    return new AbstractByteable() {
      public byte[] getBytes() { return bytes; }
    };
  }
}
