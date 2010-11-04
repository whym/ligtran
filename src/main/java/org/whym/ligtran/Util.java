package org.whym.ligtran;

public class Util {
  public static String getProperty(String name, String v) {
    String s = System.getProperty(name);
    if ( s != null ) {
      return s;
    } else {
      return v;
    }
  }

  public static boolean getPropertyBoolean(String name, boolean v) {
    String s = System.getProperty(name);
    if ( s != null ) {
      return Boolean.parseBoolean(s);
    } else {
      return v;
    }
  }

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
