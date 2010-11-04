package org.whym.ligtran;

import java.awt.*;

public interface Metrics extends Comparable<Metrics> {
  byte[][] fillArray(byte[][] array);
  byte valueAt(int i, int j);
  int getWidth();
  int getHeight();
  CharSequence getSequence();
}

