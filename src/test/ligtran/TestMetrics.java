package test.ligtran;
import com.github.whym.ligtran.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestMetrics {
  @Test public void testRawMetrics() {
    assertEquals(new RawMetrics(new byte[][]{
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 },
          { 0, 1, 1, 0 },
          { 0, 1, 0, 0 }
        }),
      new RawMetrics(new byte[][]{
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 },
          { 0, 1, 1, 0 },
          { 0, 1, 0, 0 }
        }));
    assertEquals(new RawMetrics(new byte[][]{
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 },
          { 0, 0, 0, 0 }
        }),
      new UnigramMetrics(' ', 4));
  }
}