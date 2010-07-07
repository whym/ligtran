package test.ligature;
import com.github.whym.ligature.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestUnigramMetrics {
  @Test public void testUnigramMetrics() {
    Metrics m = new UnigramMetrics('-', 3, false);
    byte[][] expect = new byte[][]{
      {0,  0, 0},
      {-1, 0, 0},
      {0,  0, 0},
    };
    byte[][] actual = new byte[3][3];
    m.fillArray(actual);
    assertEquals(new RawMetrics(expect).toString(), m.toString());
    assertArrayEquals(expect, actual);
    assertEquals(new RawMetrics(expect), m);
  }
}
