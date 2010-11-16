package org.whym.ligtran;
import java.util.*;
import java.io.*;
import java.awt.Font;
import java.util.logging.Logger;

public class NeighbourFinder {
  private static final Logger logger = Util.getLogger();

  public static class Item extends AbstractByteable {
    
    public final Metrics metrics;
    public final int[] vector;
    public final Byteable bytes;
    public Item(Metrics met, int[] a, ByteableFactory facto) {
      this.metrics = met;
      this.vector = a;
      this.bytes = facto.getByteable(a);
    }
    public byte[] getBytes() {
      return bytes.getBytes();
    }
  }

  public static class Param {
    public static enum Mode {
      ALL_PAIRS, SKETCHSORTED_PAIRS
    }
    public final int gridSize;
    public final double threshold;
    public final int bogCutOff;
    public final double bogOOVWeight;
    public final Mode pairs;
    public final boolean bogTfidf;
    public final int intSize;
    public final int blockSize;
    public final int error;
    public final int bits;
    public final boolean approximate;
    public final int seed;
    public final int center;
    public Param(int gridsize, double threshold, int cutoff, boolean tfidf, double oov, String mode) {
      this(gridsize, threshold, cutoff, tfidf, oov, mode, 32, 32, false, 0, 0, 2, 20);
    }
    public Param(int gridsize, double threshold, int cutoff, boolean tfidf, double oov, String mode, int intsize, int bits,  boolean approximate, int seed, int center, int blocksize, int error) {
      this.gridSize = gridsize;
      this.threshold = threshold;
      this.bogCutOff = cutoff;
      this.bogOOVWeight = oov;
      this.pairs = Mode.valueOf(mode);
      this.intSize = intsize;
      this.bits = bits;
      this.error = error;
      this.bogTfidf = tfidf;
      this.blockSize = blocksize;
      this.seed = seed;
      this.approximate = approximate;
      this.center = center;
    }
    @Override public String toString() {
      return
        "grid: " + gridSize + ", " +
        "threshold: " + threshold + ", " +
        "cutoff: " + bogCutOff + ", " +
        "oovweight: " + bogOOVWeight + ", " +
        "pairs: " + pairs + ", " +
        "tfidf: " + bogTfidf + ", " +
        "intsize: " + intSize + ", " +
        "approximate: " + approximate + ", " +
        "bits: " + bits + ", " +
        "seed: " + seed + ", " +
        "center: " + center + ", " +
        "error: " + error
        ;
    }
  }

  BagOfVisualWords bags;
  Map<Set<Metrics>, Double> map;
  public NeighbourFinder(List<Metrics> from_, List<Metrics> to_, Param param, Iterated<Pair<Set<Metrics>, Double>> it) {
    logger.info("param: " + param);
    BagOfVisualWords bag = new BagOfVisualWords(from_, param.gridSize, param.bogTfidf, param.bogCutOff, param.bogOOVWeight);
    logger.info("number of bags: " + bag.getBags()[0].length);//!
    bag.cutoff(param.bogCutOff);                         // TODO: different cutoff for freq and weighted score
    logger.info("after cutoff:   " + bag.getBags()[0].length);//!
    int dim = bag.getBags()[0].length;

    ByteableFactory facto;
    if ( param.approximate ) {
      facto = new ApproximateByteableFactory(dim, param.bits, new Random(param.seed), param.center);
    } else {
      facto = new FixedSizeByteableFactory(param.intSize, param.bits / dim);
    }

    List<Item> from = new ArrayList<Item>();
    Set<Metrics> fromset = new HashSet<Metrics>();
    {
      int i = 0;
      for ( int[] a: bag.getBags() ) {
        from.add(new Item(from_.get(i), a, facto));
        fromset.add(from_.get(i));
        ++i;
      }
    }
    List<Item> to = new ArrayList<Item>();
    Set<Metrics> toset = new HashSet<Metrics>();
    for ( Metrics x: to_ ) {
      to.add(new Item(x, bag.getBag(x, param.gridSize), facto));
      toset.add(x);
    }
    logger.info("feature vector size:   " + to.get(0).getBytes().length);//!
    logger.info("feature vector:   " + to.get(0));//!
    this.map = new HashMap<Set<Metrics>, Double>();
    for ( Pair<Item,Item> p: Param.Mode.ALL_PAIRS.equals(param.pairs)
            ? new AllPairs<Item>(from, to)
            : new SketchSortedPairs<Item>(from, to, param.blockSize, param.error) ) {
      //for ( Pair<Item,Item> p: new AllPairs<Item>(from, to) ) {
      double d = distance(p.getFirst().vector, p.getSecond().vector);
      if ( d < param.threshold ) {
        Set<Metrics> s = new TreeSet<Metrics>();
        if ( fromset.contains(p.getFirst().metrics) && toset.contains(p.getSecond().metrics)  ||
             toset.contains(p.getFirst().metrics) && fromset.contains(p.getSecond().metrics) ) {
          s.add(p.getFirst().metrics);
          s.add(p.getSecond().metrics);
          it.execute(Pair.newInstance(s, d));
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
    Util.loadProperties();
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
    String mode = Util.getProperty("pairs", "ALL_PAIRS");
    int intsize = Util.getPropertyInt("intsize", 16);
    int bits = Util.getPropertyInt("bits", 32);
    boolean approx = Util.getPropertyBoolean("approx", false);
    int seed = Util.getPropertyInt("seed", 2);
    int center = Util.getPropertyInt("center", 1);
    int blocksize = Util.getPropertyInt("blocksize", 4);
    int error = Util.getPropertyInt("error", 200);
    if ( fpath != null ) {
      font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fpath));
    }
    List<Metrics> ls1 = readMetrics(new FileReader(args[0]), size, antialias, square, font, min, max1);
    List<Metrics> ls2 = readMetrics(new FileReader(args[1]), size, antialias, square, font, min, max2);
    logger.info(String.format("%s: %d, %s: %d\n", args[0], ls1.size(), args[1], ls2.size()));
    NeighbourFinder finder = new NeighbourFinder
      (ls1, ls2,
       new NeighbourFinder.Param(grid, threshold, cutoff, tfidf, oov, mode, intsize, bits, approx, seed, center, blocksize, error),
       new Iterated<Pair<Set<Metrics>, Double>>() {
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
