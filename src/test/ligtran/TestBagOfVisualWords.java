package test.ligtran;
import com.github.whym.ligtran.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestBagOfVisualWords {
  @Test public void testBag() {
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
          { 0, 0, 0, 0 },
          { 0, 1, 1, 0 }
        }));
    BagOfVisualWords bag = new BagOfVisualWords(ls, 2, false, -1, 0);
    assertArrayEquals(new int[][]{
        {0, 3, 1, 1, 1, 1, 1, 1},
        {0, 7, 1, 0, 0, 0, 1, 0},
        {0, 6, 1, 0, 1, 0, 1, 0},
      },
      bag.getBags());
    bag = new BagOfVisualWords(ls, 2, true, 0, 0);
    assertArrayEquals(new int[][]{
        {0, 0, 0, 2, 1, 2, 0, 2},
        {0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0},
      },
      bag.getBags());
    bag.cutoff(0);
    assertArrayEquals(new int[][]{
        {0, 2, 1, 2, 2},
        {0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0},
      },
      bag.getBags());
  }
}
