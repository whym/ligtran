package test.ligature;
import com.github.whym.ligature.*;
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
    BagOfVisualWords bag = new BagOfVisualWords(ls, 2);
    assertArrayEquals(new int[][]{
        {3, 1, 1, 1, 1, 1, 1},
        {7, 1, 0, 1, 0, 0, 0}},
      bag.getBags());
  }
}
