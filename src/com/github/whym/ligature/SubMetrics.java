package com.github.whym.ligature;

public class SubMetrics extends AbstractMetrics {

  public static class Shallow implements Metrics {
    private final AbstractMetrics parent;
    private final String seq;
    private final int offx;
    private final int offy;
    private final int width;
    private final int height;
    public Shallow(AbstractMetrics m, int x, int y, int w, int h) {
      this.parent = m;
      this.offx = x;
      this.offy = y;
      this.width  = w;
      this.height = h;
      this.seq = m.getSequence().toString() + "#(" + x + "+" + w + "," + y + "+" + h + ")";
    }
    
    
    @Override public int getWidth() {
      return this.width;
    }
    
    @Override public int getHeight() {
      return this.height;
    }
    
    @Override public boolean equals(Object o) {
      if ( o instanceof Metrics ) {
        Metrics m = (Metrics)o;
        return this.compareTo(m) == 0;
      }
      return false;
    }

    @Override public byte valueAt(int i, int j) {
      return this.parent.pixels[this.offy+i][this.offx+j];
    }
    
    @Override public int hashCode() {
      int c = 7;
      for ( int i = 0; i < this.height; ++i ) {
        for ( int j = 0; j < this.width; ++j ) {
          c =  (c * 3 * this.valueAt(i,j)) & 0xFFFF;
          c += 17;
        }
      }
      return c;
    }
    
    @Override public int compareTo(Metrics o) {
      Metrics m = (Metrics)o;
      for ( int i = 0; i < this.height; ++i ) {
        if ( i >= m.getHeight() ) {
          return -1;
        }
        for ( int j = 0; j < this.width; ++j ) {
          if ( i >= m.getWidth() ) {
            return -1;
          }
          int c = this.valueAt(i,j) - m.valueAt(i, j);
          if ( c != 0 ) {
            return c;
          }
        }
      }
      return 0;
    }
    
    @Override public byte[][] fillArray(byte[][] array) {
      for (int a = 0; a < this.height; a++) {
        System.arraycopy(this.parent.pixels[this.offy+a], this.offx, array[a], 0, this.width);
      }
      return array;
    }
    
    @Override public String toString() {
      StringBuffer buff = new StringBuffer();
      for ( int i = 0; i < this.height; ++i ) {
        for ( int j = 0; j < this.width; ++j ) {
          buff.append(this.valueAt(i,j) != 0 ? this.valueAt(i,j) > 0 ? '+' : '#' : '.');
        }
        buff.append("\n");
      }
      return buff.toString();
    }
    
    public CharSequence getSequence() {
      return seq;
    }
  }

  private String seq;
  public SubMetrics(Metrics m, int x, int y, int w, int h) {
    byte[][] array = new byte[m.getWidth()][m.getHeight()];
    m.fillArray(array);
    this.pixels = new byte[w][h];
    for ( int i = 0; i < w; ++i ) {
      System.arraycopy(array[y + i], x, this.pixels[i], 0, h);
    }
    this.seq = m.getSequence().toString() + "#(" + x + "+" + w + "," + y + "+" + h + ")";
  }

  public CharSequence getSequence() {
    return seq;
  }
}
