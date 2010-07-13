package com.github.whym.ligature;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;


//TODO: rename
public class UnigramMetrics extends AbstractMetrics {
  private static BufferedImage bi;
  private static Graphics2D g;
  private CharSequence ch;
  public UnigramMetrics(char c1, int size) {
    this(c1, size, false);
  }
  public UnigramMetrics(char c1, int size, boolean antialias) {
    this(""+c1, size, antialias);
  }
  public UnigramMetrics(CharSequence s, int size, boolean antialias) {
    this.ch = s;
    if (bi == null || g == null || bi.getWidth() != size || bi.getHeight() != size ) {
      bi = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
      g = bi.createGraphics();
      if ( antialias ) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
      }
    }
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
    g.setColor(Color.WHITE);
    int fsize = size;
    String str = s.toString();
    while (true) {
      g.setFont(new Font("serif", Font.PLAIN, fsize));
      if (g.getFontMetrics().getMaxDescent() + g.getFontMetrics().getMaxAscent() <= size
          &&  g.getFontMetrics().stringWidth(str) <= size) {
        break;
      }
      --fsize;
    }
    g.drawString(str, 0, bi.getHeight() - g.getFontMetrics().getMaxDescent());
    this.pixels = new byte[bi.getHeight()][bi.getWidth()];
    for ( int i = 0; i < pixels.length; ++i ) {
      bi.getData().getDataElements(0, i, bi.getWidth(), 1, pixels[i]);
      for ( int j = 0; j < this.pixels[i].length; ++j ) {
      }
    }
  }
  @Override public CharSequence getSequence() {
    return this.ch;
  }

  public static void main(String[] args) {
    System.out.println(new UnigramMetrics(args[0].charAt(0), 5));
  }
}
