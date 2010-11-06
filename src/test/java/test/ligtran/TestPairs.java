package test.ligtran;
import org.whym.ligtran.*;
import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;

public class TestPairs {
  @Test public void testAllPairs() {
    Integer[] x = {10, 20};
    Integer[] y = {1, 2};
    List<Pair<Integer,Integer>> expect = new ArrayList<Pair<Integer,Integer>>();
    expect.add(Pair.newInstance(10, 1));
    expect.add(Pair.newInstance(10, 2));
    expect.add(Pair.newInstance(20, 1));
    expect.add(Pair.newInstance(20, 2));
    List<Pair<Integer,Integer>> actual = new ArrayList<Pair<Integer,Integer>>();
    for ( Pair<Integer,Integer> p: new AllPairs<Integer>(x,y) ) {
      actual.add(p);
    }
    assertEquals(expect, actual);
  }
}
