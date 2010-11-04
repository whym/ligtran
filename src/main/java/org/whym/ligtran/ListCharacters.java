package org.whym.ligtran;

import java.util.*;
import java.awt.Font;
import java.io.*;
import java.text.*;
import static java.text.Normalizer.Form.*;

public class ListCharacters {
  public static void main(String[] args) throws Exception {
    int size = Util.getPropertyInt("size", 100);
    double min = Util.getPropertyDouble("min", 0.01);
    double max = Util.getPropertyDouble("max", 0.9);
    Font font = new Font("serif", Font.PLAIN, size);
    String fpath = Util.getProperty("font", null);
    if ( fpath != null ) {
      font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fpath));
    }

    for ( char c = Character.MIN_VALUE + 1; c < Character.MAX_VALUE; ++c ) {
      int type = Character.getType(c);
      if ( type != Character.CONTROL &&
           type != Character.FORMAT &&
           type != Character.PRIVATE_USE &&
           type != Character.SURROGATE &&
           type != Character.UNASSIGNED &&
           !Character.isMirrored(c) &&
           !Character.isSpaceChar(c) ) {
        String s = "" + c;
        if ( Normalizer.normalize(s, NFKC).contains("\u0308") ) continue; //TODO: adhoc
        UnigramMetrics m = new UnigramMetrics(s, size, false, true, font);
        if ( min < m.getBlackness() && m.getBlackness() < max ) {
          System.out.println("" + c + " " + (int)c);
        }
      }
    }
  }
}