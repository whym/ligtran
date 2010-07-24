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
  private double blackness;
  public UnigramMetrics(char c1, int size) {
    this(c1, size, false);
  }
  public UnigramMetrics(char c1, int size, boolean antialias) {
    this(""+c1, size, antialias, new Font("serif", Font.PLAIN, size));
  }
  public UnigramMetrics(CharSequence s, int size, boolean antialias, Font font) {
    this.ch = s;
    if (bi == null || g == null || bi.getWidth() != size || bi.getHeight() != size ) {
      bi = new BufferedImage(size * s.length(), size, BufferedImage.TYPE_BYTE_GRAY);
      g = bi.createGraphics();
      if ( antialias ) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
      }
    }
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
    g.setColor(Color.WHITE);
    float fsize = size;
    String str = s.toString();
    g.setFont(font);
    while (true) {
      g.setFont(g.getFont().deriveFont(fsize));
      if (g.getFontMetrics().getMaxDescent() + g.getFontMetrics().getMaxAscent() <= bi.getHeight()
          &&  g.getFontMetrics().stringWidth(str) <= bi.getWidth()) {
        break;
      }
      --fsize;
    }
    g.drawString(str, 0, bi.getHeight() - g.getFontMetrics().getMaxDescent());
    this.pixels = new byte[bi.getHeight()][bi.getWidth()];
    this.blackness = 0.0;
    for ( int i = 0; i < pixels.length; ++i ) {
      bi.getData().getDataElements(0, i, bi.getWidth(), 1, pixels[i]);
      for ( int j = 0; j < this.pixels[i].length; ++j ) {
        this.blackness += 0x00FF & (int)this.pixels[i][j];
      }
    }
    this.blackness /= size * size * 0xFF;
  }

  public double getBlackness() {
    return this.blackness;
  }

  @Override public CharSequence getSequence() {
    return this.ch;
  }

  public static void main(String[] args) {
    int size = Util.getPropertyInt("size", 5);
    Font font = Font.decode(Util.getProperty("font", "serif 10"));
    System.out.println(new UnigramMetrics(args[0], size, false, font));
  }
}
