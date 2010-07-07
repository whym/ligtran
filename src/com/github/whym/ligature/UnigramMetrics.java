package com.github.whym.ligature;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

public class UnigramMetrics extends AbstractMetrics {
  private char ch;
  public UnigramMetrics(char c1, int size) {
    this.ch = c1;
    final BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D g = bi.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);
    int fsize = size;
    String str = "" + c1;
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
    return "" + ch;
  }

  public static void main(String[] args) {
    System.out.println(new UnigramMetrics(args[0].charAt(0), 5));
  }
}
