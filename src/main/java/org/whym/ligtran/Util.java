package org.whym.ligtran;
import java.util.logging.*;
import java.io.*;
import java.util.*;

public class Util {
  private static final Logger logger = Logger.getLogger(Util.class.getPackage().getName());

  public static Logger getLogger() {
    return logger;
  }

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

  public static boolean loadProperties() {
    try {
      return loadProperties(Util.class)
        || loadProperties(new FileInputStream(new File(Util.class.getPackage().getName() + ".properties")));
    } catch ( IOException e ) {
      e.printStackTrace();
    }
    return false;
  }

  public static <T> boolean loadProperties(Class<T> cls) {
    try {
      String name = cls.getName().replace('.','/') + ".properties";
      InputStream is = cls.getClassLoader().getResourceAsStream(name);
      if ( is == null ) {
        logger.warning(name + " not found");
        return false;
      } else {
        logger.info("loading Properies: " + name);
        loadProperties(is);
        return true;
      }
    } catch (IOException e) {
      if (getProperty("verbose", null) != null) {
        e.printStackTrace();
      }
    }
    return false;
  }

  private static boolean loadProperties(InputStream is) throws IOException {
    Properties prop = new Properties();
    prop.load(new InputStreamReader(is));
    for (Map.Entry<Object,Object> ent: prop.entrySet()) {
      if ( getProperty(ent.getKey().toString(), null) == null )
        System.setProperty(ent.getKey().toString(), ent.getValue().toString());
    }
    return true;
  }
}
