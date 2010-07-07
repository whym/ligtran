package com.github.whym.ligature;

import java.awt.*;

public interface Metrics extends Comparable<Metrics> {
  byte[][] fillArray(byte[][] array);
  int getWidth();
  int getHeight();
  CharSequence getSequence();
}

