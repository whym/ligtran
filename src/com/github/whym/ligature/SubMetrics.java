package com.github.whym.ligature;

public class SubMetrics extends AbstractMetrics {
  // TODO: コピーしない表現にしてメモリを節約
  private String seq;
  public SubMetrics(Metrics m, int x, int y, int w, int h) {
    byte[][] array = new byte[m.getWidth()][m.getHeight()];
    m.fillArray(array);
    this.pixels = new byte[w][h];
    for ( int i = 0; i < w; ++i ) {
      System.arraycopy(array[x + i], y, this.pixels[i], 0, h);
    }
    this.seq = m.getSequence().toString() + "#(" + x + "+" + w + "," + y + "+" + h + ")";
  }

  public CharSequence getSequence() {
    return seq;
  }
}
