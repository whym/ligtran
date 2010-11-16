package test.ligtran;
import org.whym.ligtran.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestNeighbourFinder {
  @Test public void testItem() {
    NeighbourFinder.Item item = new NeighbourFinder.Item(EmptyMetrics.getInstance(), new int[]{7, 9}, new FixedSizeByteableFactory(8, 2));
    System.err.println(item);//!
    assertArrayEquals(new byte[]{
        3, 1, 0, 0,
        1, 2, 0, 0,
      }, item.getBytes());
  }
  @Test public void testNeighbourFinder() {
    List<Metrics> ls = new ArrayList<Metrics>();
    List<Metrics> ls2 = new ArrayList<Metrics>();
    ls.add(new RawMetrics(new byte[][]{
          { 1, 1, 1, 1 },
          { 1, 0, 1, 1 },
          { 1, 0, 0, 1 },
          { 1, 0, 0, 1 }
        }));
    ls.add(new RawMetrics(new byte[][]{
          { 1, 1, 1, 1 },
          { 1, 1, 1, 1 },
          { 1, 1, 1, 1 },
          { 1, 0, 1, 1 }
        }));
    ls2.add(new RawMetrics(new byte[][]{
          { 1, 1, 1, 1 },
          { 1, 0, 0, 1 },
          { 1, 0, 0, 1 },
          { 1, 0, 0, 1 }
        }));
    List<Set<Metrics>> expect = new ArrayList<Set<Metrics>>();
    expect.add(new HashSet<Metrics>(Arrays.<Metrics>asList(new Metrics[]{ls.get(0), ls2.get(0)})));
    assertEquals(expect,
                 new NeighbourFinder(ls, ls2, new NeighbourFinder.Param(2, 0.9, 0, true, 0, "ALL_PAIRS"),
                                     new Iterated<Pair<Set<Metrics>, Double>>(){public void execute(Pair<Set<Metrics>, Double> p){}}).getMappings());
  }
}
