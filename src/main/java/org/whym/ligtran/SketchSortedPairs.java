package org.whym.ligtran;

import java.util.*;

public class SketchSortedPairs<T extends Byteable & Comparable<? extends Byteable>> implements Iterable<Pair<T,T>> {
  private final List<Pair<T,T>> result;
  private final int blockSize;
  private final int error;
  private final List<T> body;
  private final int dim;

  private final BucketArray buckets;
  private final List<Pair<Integer,Integer>> currentClassification;

  protected static class BucketArray implements Iterable<List<Integer>> {
    List<List<Integer>> array;
    List<List<Integer>> result;
    LinkedList<Pair<List<Integer>, Integer>> candidates;
    public BucketArray(int capacity, int bucketnum) {
      this.array = new ArrayList<List<Integer>>();
      this.result = new ArrayList<List<Integer>>();
      this.candidates = new LinkedList<Pair<List<Integer>,Integer>>();
      for ( int i = 0; i < bucketnum; ++i ) {
        this.array.add(new ArrayList<Integer>(capacity));
      }
    }
    public void classify(List<? extends Byteable> body, List<Integer> indices_, int start, int end) {
      //System.err.printf("%s %s %d %d\n", body, indices_, start, end);//!
      this.candidates.clear();
      this.result.clear();
      this.candidates.add(Pair.newInstance(indices_,start));
      while ( this.candidates.size() > 0 ) {
        Pair<List<Integer>,Integer> state = this.candidates.removeFirst();
        List<Integer> indices = state.getFirst();
        int b = state.getSecond();
        for ( List<Integer> x: this.array ) {
          x.clear();
        }
        // classify with radix
        for ( Integer x: indices ) {
          int radix = body.get(x).getBytes()[b];
          if ( radix < 0 ) {
            radix &= 0xFF;
          }
          this.array.get(radix).add(x);
        }
        // reorder indices
        {
          int n = 0;
          for ( List<Integer> a: this.array ) {
            for ( Integer i: a ) {
              indices.set(n, i);
              ++n;
            }
            //System.err.printf("%s %s %s %d %d %d\n", body, candidates, indices, start, b, end);//!
            if ( a.size() >= 2 ) {
              List<Integer> c = indices.subList(n-a.size(), n);
              if ( b == end - 1 ) {
                this.result.add(new ArrayList<Integer>(c));
              } else if ( b < end - 1 ) {
                this.candidates.add(Pair.newInstance(c, b + 1));
              }
            }
          }
        }
      }
      //System.err.printf("result %s\n", this.result); //!
    }
    public Iterator<List<Integer>> iterator() {
      // return a deep copy of result
      List<List<Integer>> ret = new ArrayList<List<Integer>>();
      for ( List<Integer> ls: this.result ) {
        ret.add(new ArrayList<Integer>(ls));
      }
      return ret.iterator();
    }
  }

  public SketchSortedPairs(List<T> x, List<T> y, int blockSize, int error) {
    this(x, y, blockSize, error, 256);
  }
  public SketchSortedPairs(List<T> x, List<T> y, int blockSize, int error, int maxvalue) {
    this.blockSize = blockSize;
    this.error = error;
    this.body = new ArrayList<T>(x);
    this.body.addAll(y);
    this.result = new ArrayList<Pair<T,T>>();
    this.buckets = new BucketArray(this.body.size(), maxvalue);
    this.currentClassification = new ArrayList<Pair<Integer,Integer>>();
    this.dim = this.body.get(0).getBytes().length;
    if ( this.body.size() > 0 ) {
    	multiclassify(new Iterated<Pair<T,T>>(){
    		public void execute(org.whym.ligtran.Pair<T,T> p) {
    			result.add(p);
    		}
    	});
    }
  }

  public Iterator<Pair<T,T>> iterator() {
    return this.result.iterator();
  }

  protected void multiclassify(Iterated<Pair<T,T>> callback) {
    List<Integer> targets = new ArrayList<Integer>();
    for ( int i = 0; i < this.body.size(); ++ i ) {
      targets.add(i);
    }
    List<Integer> masks = new ArrayList<Integer>();
    multiclassifyRecursive(targets, masks, callback);
  }

  private void multiclassifyRecursive(List<Integer> targets, List<Integer> masks, Iterated<Pair<T,T>> callback) {
    //System.err.printf("mc %s %s\n", targets, masks);//!
    if ( masks.size() >= (double)this.dim / this.blockSize - this.error ) {
      for ( int x = 0; x < targets.size(); ++x ) {
        for ( int y = x + 1; y < targets.size(); ++y ) {
          //System.err.println("ret " + targets.get(x) + " " + targets.get(y) + body + error + masks);//!
          int last = 0;
          int max = Collections.max(masks);
          for ( Integer b: masks ) {
            if ( b > last + 1 )  {
              for ( int i = last + 1; i < b; ++i ) {
                Comparator<Integer> comp = getMaskedComparator(i * this.blockSize, (i + 1) * this.blockSize);
                if ( comp.compare(targets.get(x), targets.get(y)) == 0 ) {
                  return;
                }
              }
            }
            last = b;
          }
          callback.execute(Pair.newInstance(body.get(targets.get(x)), body.get(targets.get(y))));
        }
      }
    } else {
      int min = masks.size() > 0 ? masks.get(masks.size() - 1) + 1: 0;
      int max = Math.min(this.error + masks.size(), this.dim / this.blockSize);
      for ( int b = min; b <= max; ++b ) {
        for ( List<Integer> x: this.classify(targets, b) ) {
          if ( x.size() >= 2 ) {
            masks.add(b);
            multiclassifyRecursive(x, masks, callback);
            masks.remove(masks.size() - 1);
          }
        }
      }
    }
  }

  private Comparator<Integer> getMaskedComparator(final int min_, final int max_) {
    final int min = Math.max(0,   min_);
    final int max = Math.min(dim, max_);
    return new Comparator<Integer>(){
      public int compare(Integer x, Integer y) {
        byte[] aa = body.get(x).getBytes();
        byte[] bb = body.get(y).getBytes();
        for ( int i = min; i < max; ++i ) {
          if ( aa[i] != bb[i] ) {
            return aa[i] - bb[i];
          }
        }
        return 0;
      }
    };
  }

  protected Iterable<List<Integer>> classify(List<Integer> indices, final int b) {
    final int min = b * this.blockSize;
    final int max = min + this.blockSize;
    if ( min >= max ) {
      return new ArrayList<List<Integer>>();
    }
    if ( indices.size() < this.dim ) {
      Comparator<Integer> comp = getMaskedComparator(min, max);
      Collections.sort(indices, comp);
      List<List<Integer>> ret = new ArrayList<List<Integer>>();
      List<Integer> cur = new ArrayList<Integer>();
      cur.add(indices.get(0));
      for ( int i = 1; i < indices.size(); ++i ) {
        if ( comp.compare(indices.get(i), indices.get(i - 1)) == 0 ) {
          cur.add(indices.get(i));
        } else {
          if ( cur.size() >= 2 ) {
        	  ret.add(new ArrayList<Integer>(cur));
          }
          cur.clear();
          cur.add(indices.get(i));
        }
      }
     if ( cur.size() >= 2 ) {
    	ret.add(new ArrayList<Integer>(cur));
      }
     return ret;
    } else {
      this.buckets.classify(this.body, indices, min, max);
      return this.buckets;
    }
  }

  protected static List<AbstractByteable> generateItems(Random rand, int num, int dim, int min, int max) {
    List<AbstractByteable> ret = new ArrayList<AbstractByteable>();
    for ( int i = 0; i < num; ++i ) {
      final byte[] array = new byte[dim];
      final List<Byte> boxed = new ArrayList<Byte>();
      for ( int j = 0; j < dim; ++j ) {
        array[j] = (byte)((rand.nextInt() % (max - min)) + min);
        boxed.add(array[j]);
      }
      ret.add(new AbstractByteable(){
          public byte[] getBytes() { return array; }
          public String toString() { return boxed.toString(); }
        });
    }
    return ret;
  }

  protected static void evaluate(Iterable<Pair<AbstractByteable,AbstractByteable>> pairs) {
    long sum = 0;
    int n = 0;
    for ( Pair<AbstractByteable,AbstractByteable> p: pairs ) {
      byte[] a = p.getFirst().getBytes();
      byte[] b = p.getSecond().getBytes();
      int dist = 0;
      for ( int i = 0; i < a.length; ++i ) {
        dist += Math.abs(a[i] - b[i]);
      }
      sum += dist;
      ++n;
    }
    System.out.printf("%s\t%.3f\t%d/%d\n", pairs.getClass().getName(), (double)sum/n, sum, n);
  }

  public static void main(String[] args) throws NumberFormatException {
    // show the time and performance(intra-cluster similarity) of multiclassify with different number of items and dimensions, and compare it with exhaustive numeration
    int num = Util.getPropertyInt("num", 10000);
    int dim = Util.getPropertyInt("dim", 10);
    int block = Util.getPropertyInt("blksize", 2);
    int error = Util.getPropertyInt("error", 1);
    int max = Util.getPropertyInt("max", 4);
    int min = Util.getPropertyInt("min", 1);
    int srand = Util.getPropertyInt("srand", 123);
    Random rand = new Random(srand);
    List<AbstractByteable> ls1 = generateItems(rand, num, dim, min, max);
    List<AbstractByteable> ls2 = generateItems(rand, num, dim, min, max);
    if ( Util.getPropertyBoolean("sketch", false) ) {
      SketchSortedPairs<AbstractByteable> spairs = new SketchSortedPairs<AbstractByteable>(ls1, ls2, block, error);
      evaluate(spairs);
    }
    if ( Util.getPropertyBoolean("all", false) ) {
      AllPairs<AbstractByteable> apairs = new AllPairs<AbstractByteable>(ls1, ls2);
      evaluate(apairs);
    }
  }
}
