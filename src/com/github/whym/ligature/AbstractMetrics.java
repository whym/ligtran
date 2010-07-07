package com.github.whym.ligature;

import java.util.*;

public abstract class AbstractMetrics implements Metrics {
  protected byte[][] pixels;

  @Override public int getWidth() {
    return this.pixels.length;
  }

  @Override public int getHeight() {
    return this.pixels[0].length;
  }

  @Override public boolean equals(Object o) {
    byte[][] pixels;
    if ( o instanceof AbstractMetrics ) {
      pixels = ((AbstractMetrics)o).pixels;
    } else if ( o instanceof Metrics ) {
      Metrics m = (Metrics)o;
      pixels = new byte[m.getWidth()][m.getHeight()];
      m.fillArray(pixels);
    } else {
      return false;
    }
    for ( int i = 0; i < this.pixels.length; ++i ) {
      if ( !Arrays.equals(this.pixels[i], pixels[i]) ) {
        return false;
      }
    }
    return true;
  }

  @Override public int hashCode() {
    int c = 7;
    for ( int i = 0; i < this.pixels.length; ++i ) {
      for ( int j = 0; j < this.pixels[i].length; ++j ) {
        c =  (c * 3) & 0xFFFF;
        c += 17;
      }
    }
    return c;
  }

  @Override public int compareTo(Metrics o) {
    byte[][] pixels;
    if ( o instanceof AbstractMetrics ) {
      pixels = ((AbstractMetrics)o).pixels;
    } else {
      Metrics m = (Metrics)o;
      pixels = new byte[m.getWidth()][m.getHeight()];
      m.fillArray(pixels);
    }
    for ( int i = 0; i < this.pixels.length; ++i ) {
      if ( i >= pixels.length ) {
        return -1;
      }
      for ( int j = 0; j < this.pixels[i].length; ++j ) {
        if ( i >= pixels[i].length ) {
          return -1;
        }
        int c = this.pixels[i][j] - pixels[i][j];
        if ( c != 0 ) {
          return c;
        }
      }
    }
    return 0;
  }
  
  @Override public byte[][] fillArray(byte[][] array) {
    for (int a = 0; a < this.pixels.length; a++) {
      System.arraycopy(this.pixels[a], 0, array[a], 0, this.pixels[a].length);
		}
    return array;
  }

  @Override public String toString() {
    StringBuffer buff = new StringBuffer();
    for ( int i = 0; i < this.pixels.length; ++i ) {
      for ( int j = 0; j < this.pixels[i].length; ++j ) {
        buff.append(this.pixels[i][j] != 0 ? this.pixels[i][j] > 0 ? '+' : '#' : '.');
      }
      buff.append("\n");
    }
    return buff.toString();
  }
}
