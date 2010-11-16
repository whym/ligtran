package test.ligtran;
import org.whym.ligtran.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestApproximateByteableFactory {
  @Test public void test0() {
    ApproximateByteableFactory facto = new ApproximateByteableFactory(3, 12, new Random(123), 0);
    assertEquals(12, facto.getByteable(new int[]{10,-1,2}).getBytes().length);
    assertArrayEquals(facto.getByteable(new int[]{ 10, -1, 2}).getBytes(),
                      facto.getByteable(new int[]{ 10, -1, 2}).getBytes());
    assertNotSame(Arrays.toString(facto.getByteable(new int[]{ 10, -1, 2}).getBytes()),
                  Arrays.toString(facto.getByteable(new int[]{  0, -1, 2}).getBytes()));
  }
}
