package com.github.whym.ligature;
import java.util.*;
import java.io.*;

public class NeighbourFinder {
  BagOfVisualWords bags;
  Map<Metrics,Metrics> map;
  public NeighbourFinder(List<Metrics> from_, List<Metrics> to_, int g, double threshold) {
    List<Metrics> coll = new ArrayList<Metrics>();
    coll.addAll(from_);
    coll.addAll(to_);
    int[][] bags = new BagOfVisualWords(coll, g).getBags();
    int[][] from = new int[from_.size()][];
    System.arraycopy(bags, 0, from, 0, from.length);
    int[][] to   = new int[to_.size()][];
    System.arraycopy(bags, from.length, to, 0, to.length);
    this.map = new HashMap<Metrics,Metrics>();
    for ( int i = 0; i < from.length; ++i ) {
      for ( int j = 0; j < to.length; ++j ) {
        double d = distance(from[i], to[j]);
        System.err.println(from_.get(i) + " " + to_.get(j) + " " + d);//!
        if ( d < threshold ) {
          this.map.put(from_.get(i), to_.get(j));
        }
      }
    }
  }
  public Map<Metrics,Metrics> getMappings() {
    return this.map;
  }
  private double distance(int[] xx, int[] yy) {
    double prod = 0;
    double xlen = 0;
    double ylen = 0;
    for ( int i = 0; i < xx.length; ++i ) {
      prod += xx[i] * yy[i];
      xlen += xx[i] * xx[i];
      ylen += yy[i] * yy[i];
    }
    prod *= prod;
    return 1.0 - Math.sqrt(prod / xlen / ylen);
  }


  private static List<Metrics> readMetrics(Reader reader, int size, boolean antialias) throws IOException {
    BufferedReader read = new BufferedReader(reader);
    String line;
    List<Metrics> list = new ArrayList<Metrics>();
    while ( (line = read.readLine()) != null ) {
      list.add(new UnigramMetrics(line, size, antialias));
    }
    return list;
  }

  public static void main(String[] args) throws IOException {
    int size = Util.getPropertyInt("size", 100);
    int grid = Util.getPropertyInt("grid", 4);
    double threshold = Util.getPropertyDouble("threshold", 0.001);
      
    NeighbourFinder finder = new NeighbourFinder
      (readMetrics(new FileReader(args[0]), size, true),
       readMetrics(new FileReader(args[1]), size, true),
       grid, threshold);
    for ( Map.Entry<Metrics,Metrics> p: finder.getMappings().entrySet() ) {
      System.out.print("" +
                       p.getKey().getSequence() + "\t" +
                       p.getValue().getSequence());
    }
  }
}
