package com.github.whym.ligature;

public interface Iterated<T> {
  void execute(T x);
  public static interface Pair<T,S> {
    void execute(T x, S y);
  }
}
