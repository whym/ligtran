package com.github.whym.ligtran;
import java.util.*;
import java.io.*;

public class MetricsClusterer {
  BagOfVisualWords bags;
  Clustering cluster;
  List<Metrics> metrics;
  public MetricsClusterer(List<Metrics> metrics, int g, double threshold) {
    this.metrics = metrics;
    this.bags = new BagOfVisualWords(metrics, g, false, 0, 1);
    this.cluster = new BottomUpClustering(this.bags.getBags(), threshold);
    while ( !this.cluster.converge() && this.cluster.iterate() ) {
    }
  }
  public Set<Set<Metrics>> getClusters() {
    Set<Set<Metrics>> ret = new HashSet<Set<Metrics>>();
    for ( List<Integer> ls: this.cluster ) {
      Set<Metrics> s = new HashSet<Metrics>();
      for ( int i: ls ) {
        s.add(this.metrics.get(i));
      }
      ret.add(s);
    }
    return ret;
  }

  public static void main(String[] args) throws IOException {
    int size = Util.getPropertyInt("size", 100);
    int grid = Util.getPropertyInt("grid", 4);
    double threshold = Util.getPropertyDouble("threshold", 0.001);

    {
      byte[][] a = new byte[grid][grid];
      BagOfVisualWords.addStopWord(new RawMetrics(a));
    }
      
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String line;
    List<Metrics> list = new ArrayList<Metrics>();
    while ( (line = reader.readLine()) != null ) {
      if ( line.length() >= 1 ) {
        list.add(new UnigramMetrics(line.charAt(0), size));
      }
    }
    MetricsClusterer cluster = new MetricsClusterer(list, grid, threshold);
    for ( Set<Metrics> s: cluster.getClusters() ) {
      for ( Metrics m: s ) {
        System.out.print(m.getSequence());
        System.out.print('\t');
      }
      System.out.println("");
    }
  }
}
