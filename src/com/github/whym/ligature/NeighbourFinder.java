package com.github.whym.ligature;
import java.util.*;
import java.io.*;
import java.awt.Font;

public class NeighbourFinder {
  BagOfVisualWords bags;
  Map<Set<Metrics>, Double> map;
  public NeighbourFinder(List<Metrics> from_, List<Metrics> to_, int g, double threshold, int cutoff) {
    List<Metrics> coll = new ArrayList<Metrics>();
    coll.addAll(from_);
    coll.addAll(to_);
    BagOfVisualWords bag = new BagOfVisualWords(coll, g, true, cutoff);
    System.err.println(bag.getBags()[0].length);//!
    bag.cutoff(cutoff);
    System.err.println(bag.getBags()[0].length);//!
    int[][] bags = bag.getBags();
    int[][] from = new int[from_.size()][];
    System.arraycopy(bags, 0, from, 0, from.length);
    int[][] to   = new int[to_.size()][];
    System.arraycopy(bags, from.length, to, 0, to.length);
    this.map = new HashMap<Set<Metrics>, Double>();
    for ( int i = 0; i < from.length; ++i ) {
      for ( int j = 0; j < to.length; ++j ) {
        double d = distance(from[i], to[j]);
        if ( d < threshold ) {
          Set<Metrics> s = new HashSet<Metrics>();
          s.add(from_.get(i));
          s.add(to_.get(j));
          this.map.put(s, d);
        }
      }
    }
  }
  public List<Set<Metrics>> getMappings() {
    List<Set<Metrics>> ls = new ArrayList<Set<Metrics>>(this.map.keySet());
    Collections.sort(ls, new Comparator<Set<Metrics>>(){
        public int compare(Set<Metrics> a, Set<Metrics> b) {
          double d = map.get(a) - map.get(b);
          if ( Math.abs(d) == 0 ) {
            return 0;
          } else if ( d < 0 ) {
            return -1;
          } else {
            return 1;
          }
        }
      });
    return ls;
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


  private static List<Metrics> readMetrics(Reader reader, int size, boolean antialias, Font font, double min, double max) throws IOException {
    BufferedReader read = new BufferedReader(reader);
    String line;
    List<Metrics> list = new ArrayList<Metrics>();
    while ( (line = read.readLine()) != null ) {
      line = line.trim();
      int p = line.indexOf(' ');
      if ( p < 0 ) p = line.length();
      line = line.substring(0, p);
      if ( line.length() == 0 ) continue;
      UnigramMetrics m = new UnigramMetrics(line, size, antialias, font);
      if ( min < m.getBlackness() && m.getBlackness() < max ) {
        list.add(m);
      } else {
        System.err.printf("skip \'%s\' %s\n", m.getSequence(), Arrays.toString(m.getSequence().toString().getBytes()));
      }
    }
    return list;
  }

  public static void main(String[] args) throws java.awt.FontFormatException, IOException {
    int size = Util.getPropertyInt("size", 100);
    int grid = Util.getPropertyInt("grid", 4);
    int cutoff = Util.getPropertyInt("cutoff", 4);
    double threshold = Util.getPropertyDouble("threshold", 0.001);
    double min = Util.getPropertyDouble("min", 0.01);
    double max1 = Util.getPropertyDouble("max1", 0.7);
    double max2 = Util.getPropertyDouble("max2", 0.5);
    Font font = new Font("serif", Font.PLAIN, size);
    String fpath = Util.getProperty("font", null);
    if ( fpath != null ) {
      font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fpath));
    }
    List<Metrics> ls1 = readMetrics(new FileReader(args[0]), size, false, font, min, max1);
    List<Metrics> ls2 = readMetrics(new FileReader(args[1]), size, false, font, min, max2);
    System.err.printf("%s: %d, %s: %d\n", args[0], ls1.size(), args[1], ls2.size());
    NeighbourFinder finder = new NeighbourFinder(ls1, ls2, grid, threshold, cutoff);
    for ( Set<Metrics> s: finder.getMappings() ) {
      for ( Metrics m: s ) {
        System.out.print(m.getSequence());
        System.out.print('\t');
      }
      System.out.println("");
    }
  }
}
