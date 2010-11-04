package org.whym.ligtran;

public class RawMetrics extends AbstractMetrics {
  public RawMetrics(byte[][] a) {
    this.pixels = a;
  }
  public CharSequence getSequence() {
    return this.toString();
  }
}
