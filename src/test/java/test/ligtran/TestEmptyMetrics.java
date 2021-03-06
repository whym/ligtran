package test.ligtran;
import org.whym.ligtran.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestEmptyMetrics {
  @Test public void testSubMetrics() {
    Metrics m = new RawMetrics(new byte[][]{
        { 0, 0, 0, 0 },
        { 0, 0, 0, 0 },
        { 0, 1, 1, 0 },
        { 0, 1, 0, 0 }
      });
    assertNotSame(EmptyMetrics.getInstance(), m);
    assertNotSame(m, EmptyMetrics.getInstance());
    m = new RawMetrics(new byte[][]{{}});
    assertEquals(EmptyMetrics.getInstance(), m);
  }
}
