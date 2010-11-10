package test.ligtran;
import org.whym.ligtran.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestSketchSortedPairs {
  private static class MyBytes extends AbstractByteable {
    byte[] array;
    public MyBytes(int ... args) {
      this.array = new byte[args.length];
      for ( int i = 0; i < args.length; ++i ) {
        this.array[i] = (byte)args[i];
      }
    }
    public byte[] getBytes() { return array; }
    public String toString() {
      StringBuffer buff = new StringBuffer();
      for ( byte b: this.array ) {
        buff.append(String.format("%02X", b));
      }
      return buff.toString();
    }
  }
  private static class MyPairs extends SketchSortedPairs<MyBytes> {
    public MyPairs(List<MyBytes> x, List<MyBytes> y, int blockSize, int error) {
      super(x, y, blockSize, error);
    }

    public static void testClassify() {
      List<MyBytes> x = Arrays.asList(new MyBytes[]{new MyBytes(0, 1, 2),
                                                    new MyBytes(0, 1, 1)});
      List<MyBytes> y = Arrays.asList(new MyBytes[]{new MyBytes(1, 1, 2),
                                                    new MyBytes(0, 1, 3)});
      List<Integer> indices = Arrays.asList(new Integer[]{0,1,2,3});
      List<List<Integer>> expect = new ArrayList<List<Integer>>();
      MyPairs sk;

      expect.clear();
      expect.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
      sk = new MyPairs(x, y, 1, 0);
      assertEquals(expect, collect(sk.classify(indices, 1)));

      expect.clear();
      expect.add(Arrays.asList(new Integer[]{0, 1, 3}));
      sk = new MyPairs(x, y, 2, 0);
      assertEquals(expect, collect(sk.classify(indices, 0)));

      expect.clear();
      sk = new MyPairs(x, y, 3, 0);
      assertEquals(expect, collect(sk.classify(indices, 0)));
    }

    private static class Accumulator implements Iterated<Pair<MyBytes,MyBytes>>{
      Collection<Pair<MyBytes,MyBytes>> acc;
      public Accumulator(Collection<Pair<MyBytes,MyBytes>> ls) {
        this.acc = ls;
      }
      public void execute(Pair<MyBytes,MyBytes> p) {
        this.acc.add(p);
      }
    }

    public static void testMulticlassify() {
      List<MyBytes> x = Arrays.asList(new MyBytes[]{new MyBytes(0, 1, 2, 0, 0),
                                                    new MyBytes(1, 1, 2, 0, 0),
                                                    new MyBytes(0, 0, 1, 0, 0)});
      List<MyBytes> y = Arrays.asList(new MyBytes[]{new MyBytes(1, 1, 2, 0, 0),
                                                    new MyBytes(1, 1, 1, 0, 0),
                                                    new MyBytes(0, 1, 3, 1, 0)});
      Set<Pair<MyBytes,MyBytes>> expect = new HashSet<Pair<MyBytes,MyBytes>>();
      final Set<Pair<MyBytes,MyBytes>> actual = new HashSet<Pair<MyBytes,MyBytes>>();
      final List<Pair<MyBytes,MyBytes>> actualAll = new ArrayList<Pair<MyBytes,MyBytes>>();
      MyPairs sk;

      actual.clear();
      sk = new MyPairs(x, y, 1, 0);
      sk.multiclassify(new Accumulator(actual));
      expect.clear();
      expect.add(new Pair<MyBytes,MyBytes>(x.get(1), y.get(0)));
      assertEquals(expect, actual);

      actual.clear();
      sk = new MyPairs(x, y, 1, 1);
      sk.multiclassify(new Accumulator(actual));
      expect.clear();
      expect.add(new Pair<MyBytes,MyBytes>(x.get(0), x.get(1)));
      expect.add(new Pair<MyBytes,MyBytes>(y.get(1), x.get(1)));
      expect.add(new Pair<MyBytes,MyBytes>(x.get(1), y.get(0)));
      assertEquals(expect, actual);
      
      actualAll.clear();
      sk.multiclassify(new Accumulator(actualAll));
      assertEquals(6, actualAll.size());
    }
  }
  private static<T> List<T> collect(Iterable<T> it) {
    List<T> ret = new ArrayList<T>();
    for ( T x: it ) {
      ret.add(x);
    }
    return ret;
  }


  @Test public void testClassify() {
    MyPairs.testClassify();
  }
  @Test public void testMulticlassify() {
    MyPairs.testMulticlassify();
  }
}
