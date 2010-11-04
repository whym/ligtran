package org.whym.ligtran;
import java.util.*;

public class BottomUpClustering implements Clustering {
  private List<List<Integer>> clusters;
  private List<double[]> centroids;
  private List<Boolean> active; // TODO: use BitSet
  private int[][] items;
  private double threshold;
  private double mindist;
  public BottomUpClustering(int[][] items, double threshold) {
    this.items = items;
    this.threshold = threshold;
    this.mindist = Double.MIN_VALUE;
    this.clusters = new ArrayList<List<Integer>>();
    this.centroids = new ArrayList<double[]>();
    this.active = new ArrayList<Boolean>();
    for ( int i = 0; i < items.length; ++i ) {
      List<Integer> ls = new ArrayList<Integer>();
      ls.add(i);
      this.clusters.add(ls);
      double[] c = new double[items[i].length];
      for ( int j = 0; j < c.length; ++j ) {
        c[j] = items[i][j];
      }
      //System.arraycopy(items[i], 0, c, 0, c.length);
      this.centroids.add(c);
      this.active.add(true);
    }
  }
  private double distance(int x, int y) {
    double prod = 0;
    double xlen = 0;
    double ylen = 0;
    double[] xx = this.centroids.get(x);
    double[] yy = this.centroids.get(y);
    for ( int i = 0; i < xx.length; ++i ) {
      prod += xx[i] * yy[i];
      xlen += xx[i] * xx[i];
      ylen += yy[i] * yy[i];
    }
    prod *= prod;
    return 1.0 - Math.sqrt(prod / xlen / ylen);
  }
  @Override public boolean iterate() {
    double min = Double.MAX_VALUE;
    List<int[]> minpairs = new ArrayList<int[]>();
    for ( int i = 0; i < this.centroids.size(); ++i ) {
      for ( int j = 0; j < this.centroids.size(); ++j ) {
        if ( i != j  && this.active.get(i) &&  this.active.get(j) ) {
          double d = distance(i, j);
          if ( d < min ) {
            min = d;
            minpairs.clear();
            minpairs.add(new int[]{i, j});
          } else if ( d == min ) {
            minpairs.add(new int[]{i, j});
          }
        }
      }
    }
    for ( int[] minpair: minpairs ) {
      System.err.println(Arrays.toString(minpair) + min);//!
      if ( !this.converge() && min != Double.MAX_VALUE ) {
        // update centroids
        double[] xx = this.centroids.get(minpair[0]);
        double[] yy = this.centroids.get(minpair[1]);
        double[] c = new double[xx.length];
        for ( int i = 0; i < xx.length; ++i ) {
          c[i] = (xx[i] + yy[i]) / 2;
        }
        this.centroids.add(c);
        
        // update clusters
        List<Integer> ls = new ArrayList<Integer>();
        ls.addAll(this.clusters.get(minpair[0]));
        ls.addAll(this.clusters.get(minpair[1]));
        this.clusters.add(ls);
        
        this.active.set(minpair[0], false);
        this.active.set(minpair[1], false);
        this.active.add(true);
        this.mindist = min;
      }
      return true;
    }
    return false;
  }
  @Override public boolean converge() {
    return this.mindist > this.threshold;
  }
  @Override public Iterator<List<Integer>> iterator() {
    List<List<Integer>> ls = new ArrayList<List<Integer>>();
    int i = 0;
    for ( List<Integer> l: this.clusters ) {
      if ( this.active.get(i) ) {
        ls.add(l);
      }
      ++i;
    }
    return ls.iterator();
  }
  @Override public String toString() {
    StringBuffer buff = new StringBuffer();
    buff.append(this.clusters.toString());
    buff.append(this.active.toString());
    for ( double[] x: this.centroids ) {
      buff.append(Arrays.toString(x));
    }
    return buff.toString();
  }
}
