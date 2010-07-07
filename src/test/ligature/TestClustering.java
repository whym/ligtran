package test.ligature;
import com.github.whym.ligature.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestClustering {
  @Test public void testMetricsClusterer() {
    List<Metrics> ls = new ArrayList<Metrics>();
    ls.add(new RawMetrics(new byte[][]{
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 },
          { 0, 1, 1, 0 },
          { 0, 1, 0, 0 }
        }));
    ls.add(new RawMetrics(new byte[][]{
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 },
          { 0, 1, 0, 0 }
        }));
    ls.add(new RawMetrics(new byte[][]{
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 },
          { 0, 1, 1, 0 },
          { 0, 1, 0, 0 }
        }));
    assertEquals(new HashSet<Set<Metrics>>
                 (Arrays.<Set<Metrics>>asList(new Set[]{
                     new HashSet<Metrics>(Arrays.<Metrics>asList(new Metrics[]{ls.get(1)})),
                     new HashSet<Metrics>(Arrays.<Metrics>asList(new Metrics[]{ls.get(0), ls.get(2)})),
                 }                   
                   )), new MetricsClusterer(ls, 2, 0.01).getClusters());
  }
  @Test public void testBottomUpClustering() {
    int[][] items = new int[][]{
      {0, 10, 0},
      {20, 10, 1},
      {0, 10, 0},
      {30, 10, 1},
      {100, 0, 0},
    };
    Clustering c = new BottomUpClustering(items, 0.01);
    while ( !c.converge() && c.iterate() ) {
      System.err.println(c);
    }
    List<List<Integer>> actual = new ArrayList<List<Integer>>();
    for ( List<Integer> ls: c ) {
      actual.add(ls);
    }
    assertEquals(Arrays.asList(new Object[]{
          Arrays.asList(new Integer[] {4}),
          Arrays.asList(new Integer[] {0, 2}),
          Arrays.asList(new Integer[] {3, 1}),
        }), actual);
  }
}
