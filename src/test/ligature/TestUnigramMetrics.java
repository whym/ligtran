package test.ligature;
import com.github.whym.ligature.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestUnigramMetrics {
  @Test public void test0() {
    Metrics m = new UnigramMetrics('-', 3);
    byte[][] expect = new byte[][]{
      {0,  0, 0},
      {-1, 0, 0},
      {0,  0, 0},
    };
    byte[][] actual = new byte[3][3];
    m.fillArray(actual);
    assertArrayEquals(expect, actual);
  }
}
