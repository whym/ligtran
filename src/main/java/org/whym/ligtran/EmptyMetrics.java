package org.whym.ligtran;

public class EmptyMetrics extends AbstractMetrics {
  private static EmptyMetrics singleton = new EmptyMetrics();
  private EmptyMetrics() {
    this.pixels = new byte[][]{new byte[]{}};
  }
  public CharSequence getSequence() {
    return "[E]";
  }
  public static Metrics getInstance() {
    return singleton;
  }
}
