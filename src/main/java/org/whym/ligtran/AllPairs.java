package org.whym.ligtran;

import java.util.*;

public class AllPairs<T> implements Iterable<Pair<T,T>> {
  final List<T> x;
  final List<T> y;
  public AllPairs(T[] x, T[] y) {
    this.x = Arrays.asList(x);
    this.y = Arrays.asList(y);
  }
  public AllPairs(List<T> x, List<T> y) {
    this.x = x;
    this.y = y;
  }
  
  public Iterator<Pair<T,T>> iterator() {
    return new Iterator<Pair<T,T>>() {
      int i = 0;
      int j = 0;
      
      public boolean hasNext() {
        return i < x.size() && j < y.size();
      }
      
      public Pair<T,T> next() {
        Pair<T,T> p = Pair.newInstance(x.get(i), y.get(j));
        ++j;
        if ( j == y.size() ) {
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
