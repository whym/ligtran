package com.github.whym.ligature;
import java.util.*;

public class BagOfVisualWords {
  private static Set<Metrics> stopWords = new HashSet<Metrics>();
  public static void addStopWord(Metrics m) {
    stopWords.add(m);
  }

  private Map<Metrics, Integer> codeIndex;
  private List<Metrics> codes;
  private int[][] bags;
  public BagOfVisualWords(List<Metrics> metricses, int g) {
    this(metricses, g, true, 0);
  }
  public BagOfVisualWords(List<Metrics> metricses, int g, boolean tfidf, int cutoff) {
    this.codeIndex = new HashMap<Metrics, Integer>();
    this.codes = new ArrayList<Metrics>();
    List<Map<Metrics, Integer>> freqs = new ArrayList<Map<Metrics, Integer>>();
    Map<Metrics, Integer> dfreqs = new HashMap<Metrics, Integer>();
    int n = 0;
    for ( Metrics m: metricses ) {
      Map<Metrics, Integer> fq = new TreeMap<Metrics, Integer>();
      for ( int i = 0; i + g <= m.getWidth(); ++i ) {
        for ( int j = 0; j + g <= m.getHeight(); ++j ) {
          Metrics s = new SubMetrics(m, i, j, g, g);
          Integer c;
          if ( (c = fq.get(s)) == null ) {
            c = 0;
            if ( !stopWords.contains(s) && this.codeIndex.get(s) == null ) {
              this.codeIndex.put(s, this.codes.size());
              this.codes.add(s);
            }
          }
          fq.put(s, c + 1);
        }
      }
      List<Metrics> rm = new ArrayList<Metrics>();
      for ( Metrics x: fq.keySet() ) {
        if ( fq.get(x) <= cutoff ) {
          rm.add(x);
        }
        Integer df = dfreqs.get(x);
        if ( df == null ) df = 0;
        dfreqs.put(x, df + 1);
      }
      for ( Metrics x: rm ) {
        fq.remove(x);
      }
      freqs.add(fq);
      ++n;
    }
    //System.err.println(freqs);
    //System.err.println(dfreqs);
    this.bags = new int[freqs.size()][this.codes.size()];
    for ( int i = 0; i < this.bags.length; ++i ) {
      for ( int j = 0; j < this.bags[i].length; ++j ) {
        Integer tf = freqs.get(i).get(this.codes.get(j));
        if ( tf == null ) {
          tf = 0;
        }
        if ( tfidf ) {
          this.bags[i][j] = (int)(0.5 + tf * Math.log((double)metricses.size() / dfreqs.get(this.codes.get(j))) / Math.log(2));
        } else {
          this.bags[i][j] = tf;
        }
      }
      //System.err.println(Arrays.toString(this.bags[i]));
    }
  }
  public void cutoff(int threshold) {
    List<Integer> ls = new ArrayList<Integer>();
    int dim = this.bags[0].length;
    for ( int j = 0; j < dim; ++j ) {
      boolean allzero = true;
      for ( int i = 0; i < this.bags.length; ++i ) {
        if ( this.bags[i][j] > threshold ) {
          allzero = false;
          break;
        }
      }
      if ( !allzero ) {
        ls.add(j);
      }
    }
    if ( ls.size() > 0 ) {
      for ( int i = 0; i < this.bags.length; ++i ) {
        int[] a = new int [ls.size()];
        int n = 0;
        for ( int j: ls) {
          a[n] = this.bags[i][j];
          n++;
        }
        this.bags[i] = a;
      }
      //TODO update codeIndex
    }
  }
  public List<Metrics> getCodeBook() {
    return codes;
  }
  public int[][] getBags() {
    return bags;
  }
}
