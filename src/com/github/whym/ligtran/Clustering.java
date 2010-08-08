package com.github.whym.ligtran;
import java.util.*;

public interface Clustering extends Iterable<List<Integer>> {
  boolean iterate();
  boolean converge();
}
