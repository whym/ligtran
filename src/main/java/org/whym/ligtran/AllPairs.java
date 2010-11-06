package org.whym.ligtran;

import java.util.*;

public class AllPairs<T> implements Iterable<Pair<T,T>> {
  final T[] x;
  final T[] y;
  public AllPairs(T[] x, T[] y) {
    this.x = x;
    this.y = y;
  }

  public Iterator<Pair<T,T>> iterator() {
    return new Iterator<Pair<T,T>>() {
      int i = 0;
      int j = 0;

      public boolean hasNext() {
        return i < x.length && j < y.length;
      }
        
      public Pair<T,T> next() {
        Pair<T,T> p = Pair.newInstance(x[i], y[j]);
        ++j;
        if ( j == y.length ) {
          ++i;
          j = 0;
        }
        return p;
      }
        
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
