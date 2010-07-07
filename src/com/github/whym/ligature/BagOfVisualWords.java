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
    this.codeIndex = new HashMap<Metrics, Integer>();
    this.codes = new ArrayList<Metrics>();
    List<Map<Metrics, Integer>> freqs = new ArrayList<Map<Metrics, Integer>>();
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
      freqs.add(fq);
      ++n;
    }
    //System.err.println(freqs);
    this.bags = new int[freqs.size()][this.codes.size()];
    for ( int i = 0; i < this.bags.length; ++i ) {
      for ( int j = 0; j < this.bags[i].length; ++j ) {
        Integer tf = freqs.get(i).get(this.codes.get(j));
        if ( tf == null ) {
          tf = 0;
        }
        this.bags[i][j] = tf;
      }
      //System.err.println(Arrays.toString(this.bags[i]));
    }
  }
  public List<Metrics> getCodeBook() {
    return codes;
  }
  public int[][] getBags() {
    return bags;
  }
}
