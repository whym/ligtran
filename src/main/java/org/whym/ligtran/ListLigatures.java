package org.whym.ligtran;

import java.util.*;
import java.text.*;
import java.awt.Font;
import java.io.*;
import static java.text.Normalizer.Form.*;

public class ListLigatures {
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
      if ( c != '\n'  &&  Character.getType(c) != Character.CONTROL && !Character.isMirrored(c) ) {
        String s = "" + c;
        String norm = Normalizer.normalize(s, NFKC);
        if ( norm.length() > s.length()
             && !norm.contains("\u0308")) {
          UnigramMetrics m = new UnigramMetrics(s, size, false, true, font);
          if ( min < m.getBlackness() && m.getBlackness() < max ) {
            System.out.printf("%s %s\n", norm, s);
            for ( Normalizer.Form f: new Normalizer.Form[]{NFC, NFD, NFKD} ) {
              String norm2 = Normalizer.normalize(s, f);
              if ( !norm2.equals(norm) && norm2.length() > s.length() ) {
                System.out.printf("%s %s\n", norm2, s);
              }
            }
          }
        }
      }
    }
  }
}