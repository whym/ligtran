package com.github.whym.ligature;
import java.util.*;

public interface Clustering extends Iterable<List<Integer>> {
  boolean iterate();
  boolean converge();
}
