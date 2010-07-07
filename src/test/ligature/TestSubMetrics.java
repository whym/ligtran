package test.ligature;
import com.github.whym.ligature.*;
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
        }), new SubMetrics(m, 2, 1, 2, 2));
  }
}
