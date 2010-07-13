package com.github.whym.ligature;

public class Util {
  public static int getPropertyInt(String name, int v) {
    String s = System.getProperty(name);
    if ( s != null ) {
      return Integer.parseInt(s);
    } else {
      return v;
    }
  }

  public static double getPropertyDouble(String name, double v) {
    String s = System.getProperty(name);
    if ( s != null ) {
      return Double.parseDouble(s);
    } else {
      return v;
    }
  }
}
