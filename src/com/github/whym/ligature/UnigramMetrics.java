package com.github.whym.ligature;

import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

public class UnigramMetrics implements Metrics {
  private byte[][] pixels;
  public UnigramMetrics(char c1, int size) {
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
  
  @Override public byte[][] fillArray(byte[][] array) {
    for (int a = 0; a < this.pixels.length; a++) {
      System.arraycopy(this.pixels[a], 0, array[a], 0, this.pixels[a].length);
		}
    return array;
  }

  @Override public String toString() {
    StringBuffer buff = new StringBuffer();
    for ( int i = 0; i < this.pixels.length; ++i ) {
      for ( int j = 0; j < this.pixels[i].length; ++j ) {
        buff.append(this.pixels[i][j] != 0 ? this.pixels[i][j] > 0 ? '+' : '#' : ' ');
      }
      buff.append("\n");
    }
    return buff.toString();
  }

  public static void main(String[] args) {
    System.out.println(new UnigramMetrics(args[0].charAt(0), 5));
  }
}
