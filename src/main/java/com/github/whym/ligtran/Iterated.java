package com.github.whym.ligtran;

public interface Iterated<T> {
  void execute(T x);
  public static interface Pair<T,S> {
    void execute(T x, S y);
  }
}
