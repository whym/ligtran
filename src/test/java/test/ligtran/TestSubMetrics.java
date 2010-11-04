package test.ligtran;
import com.github.whym.ligtran.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestSubMetrics {
  @Test public void testSubMetrics() {
    Metrics m = new RawMetrics(new byte[][]{
        { 0, 0, 0, 0 },
        { 0, 0, 0, 0 },
        { 0, 1, 1, 0 },
        { 0, 1, 0, 0 }
      });
    assertEquals(new RawMetrics(new byte[][]{
          { 0, 0 },
          { 0, 0 }
        }), new SubMetrics(m, 0, 0, 2, 2));
    assertEquals(new RawMetrics(new byte[][]{
          { 1, 1 },
          { 1, 0 },
        }), new SubMetrics(m, 1, 2, 2, 2));
  }
  @Test public void testSubMetricsShallow() {
    AbstractMetrics m = new RawMetrics(new byte[][]{
        { 0, 0, 0, 0 },
        { 0, 0, 0, 0 },
        { 0, 1, 1, 0 },
        { 0, 1, 0, 0 }
      });
    assertEquals(new RawMetrics(new byte[][]{
          { 0, 0 },
          { 0, 0 }
        }), new SubMetrics.Shallow(m, 0, 0, 2, 2));
    assertEquals(new RawMetrics(new byte[][]{
          { 1, 1 },
          { 1, 0 },
        }), new SubMetrics.Shallow(m, 1, 2, 2, 2));
  }
}
