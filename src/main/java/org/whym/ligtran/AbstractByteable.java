package org.whym.ligtran;

public abstract class AbstractByteable implements Byteable, Comparable<Byteable> {
  @Override public abstract byte[] getBytes();
  public int compareTo(Byteable x) {
    byte[] a = this.getBytes();
    byte[] b = x.getBytes();
    if ( a.length != b.length ) {
      return a.length - b.length;
    }
    for ( int i = 0; i < a.length; ++i ) {
      if ( a[i] != b[i] ) {
        return a[i] - b[i];
      }
    }
    return 0;
  }
  @Override public int hashCode() {
    byte[] a = this.getBytes();
    int c = 511;
    for ( int i = 0; i < a.length; ++i ) {
      c = (c + a[i] * 213) & 0xFFFF;
    }
    return c;
  }
  @Override public boolean equals(Object o) {
    if ( o instanceof Byteable ) {
      Byteable a = (Byteable)o;
      return this.compareTo(a) == 0;
    } else {
      return false;
    }
  }
}
