package org.whym.ligtran;
import java.util.*;
import java.io.*;
import java.awt.Font;
import java.util.logging.Logger;

public class NeighbourFinder {
  private static final Logger logger = Util.getLogger();

  BagOfVisualWords bags;
  Map<Set<Metrics>, Double> map;
  public NeighbourFinder(List<Metrics> from_, List<Metrics> to_, int g, double threshold, int cutoff, boolean tfidf, double oovweight, Iterated<Pair<Set<Metrics>, Double>> it) {
    BagOfVisualWords bag = new BagOfVisualWords(from_, g, tfidf, cutoff, oovweight);
    logger.info("number of bags: " + bag.getBags()[0].length);//!
    bag.cutoff(cutoff);                         // TODO: different cutoff for freq and weighted score
    logger.info("after cutoff:   " + bag.getBags()[0].length);//!
    List<Pair<Integer,int[]>> from = convert(bag.getBags());
    List<Pair<Integer,int[]>> to = new ArrayList<Pair<Integer,int[]>>();
    for ( int i = 0; i < to_.size(); ++i ) {
      to.add(Pair.newInstance(i, bag.getBag(to_.get(i), g)));
    }
    this.map = new HashMap<Set<Metrics>, Double>();
    for ( Pair<Pair<Integer,int[]>,Pair<Integer,int[]>> p: new AllPairs<Pair<Integer,int[]>>(from, to) ) {
      double d = distance(p.getFirst().getSecond(), p.getSecond().getSecond());
      if ( d < threshold ) {
        Set<Metrics> s = new TreeSet<Metrics>();
        s.add(from_.get(p.getFirst().getFirst()));
        s.add(to_.get(p.getSecond().getFirst()));
        it.execute(Pair.newInstance(s, d));
        this.map.put(s, d);
      }
    }
  }

  private static List<Pair<Integer,int[]>> convert(int[][] array) {
    List<Pair<Integer,int[]>> ret = new ArrayList<Pair<Integer,int[]>>();
    int i = 0;
    for ( int[] a: array ) {
      ret.add(Pair.newInstance(i, a));
      ++i;
    }
    return ret;
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
  private static double distance(int[] xx, int[] yy) {
    assert(xx.length == yy.length);
    int prod = 0;
    int xlen = 0;
    int ylen = 0;
    for ( int i = 0; i < xx.length; ++i ) {
      prod += xx[i] * yy[i];
      xlen += xx[i] * xx[i];
      ylen += yy[i] * yy[i];
    }
    prod *= prod;
    //System.err.println(Arrays.toString(xx) + Arrays.toString(yy) + Arrays.toString(new int[]{prod, xlen, ylen}));//!
    if ( prod <= 0 || xlen*ylen <= 0 ) return 1.0;
    //if ( Math.abs(prod) < 1E-7 || Math.abs(xlen*ylen) < 1E-7 ) return 1.0;
    return 1.0 - Math.sqrt((double)(prod) / xlen / ylen);
  }


  private static List<Metrics> readMetrics(Reader reader, int size, boolean antialias, boolean square, Font font, double min, double max) throws IOException {
    BufferedReader read = new BufferedReader(reader);
    String line;
    List<Metrics> list = new ArrayList<Metrics>();
    while ( (line = read.readLine()) != null ) {
      line = line.trim();
      int p = line.indexOf(' ');
      if ( p < 0 ) p = line.length();
      line = line.substring(0, p);
      if ( line.length() == 0 ) continue;
      UnigramMetrics m = new UnigramMetrics(line, size, antialias, square, font);
      if ( min < m.getBlackness() && m.getBlackness() < max ) {
        list.add(m);
      } else {
        System.err.printf("skip \'%s\' %s\n", m.getSequence(), Arrays.toString(m.getSequence().toString().getBytes()));
      }
    }
    return list;
  }

  public static void main(String[] args) throws java.awt.FontFormatException, IOException {
    logger.info(String.format("start"));
    int size = Util.getPropertyInt("size", 100);
    int grid = Util.getPropertyInt("grid", 4);
    int cutoff = Util.getPropertyInt("cutoff", 4);
    double threshold = Util.getPropertyDouble("threshold", 0.001);
    double min = Util.getPropertyDouble("min", 0.01);
    double max1 = Util.getPropertyDouble("max1", 0.7);
    double max2 = Util.getPropertyDouble("max2", 0.5);
    double oov = Util.getPropertyDouble("oov", 0.01);
    boolean antialias = Util.getPropertyBoolean("antialias", false);
    boolean square  = Util.getPropertyBoolean("square", true);
    boolean tfidf = Util.getPropertyBoolean("tfidf", true);
    Font font = new Font("serif", Font.PLAIN, size);
    String fpath = Util.getProperty("font", null);
    if ( fpath != null ) {
      font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fpath));
    }
    List<Metrics> ls1 = readMetrics(new FileReader(args[0]), size, antialias, square, font, min, max1);
    List<Metrics> ls2 = readMetrics(new FileReader(args[1]), size, antialias, square, font, min, max2);
    logger.info(String.format("%s: %d, %s: %d\n", args[0], ls1.size(), args[1], ls2.size()));
    NeighbourFinder finder = new NeighbourFinder(ls1, ls2, grid, threshold, cutoff, tfidf, oov, new Iterated<Pair<Set<Metrics>, Double>>() {
        public void execute(Pair<Set<Metrics>, Double> p) {
          Set<Metrics> s = p.getFirst();
          double d = p.getSecond();
          if ( s.size() > 1 && d > 0 ) {//TODO: adhoc fix for d=0
            for ( Metrics m: s ) {
              System.out.print(m.getSequence());
              System.out.print('\t');
            }
            System.out.println("\t" + d);
          }
        }
      });
  }
}
