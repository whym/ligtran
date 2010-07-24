package com.github.whym.ligature;

import java.util.*;

public class ListCharacters {
  public static void main(String[] args) {
    for ( char c = Character.MIN_VALUE + 1; c < Character.MAX_VALUE; ++c ) {
      int type = Character.getType(c);
      if ( type != Character.CONTROL &&
           type != Character.FORMAT &&
           type != Character.PRIVATE_USE &&
           type != Character.SURROGATE &&
           type != Character.UNASSIGNED &&
           !Character.isMirrored(c) &&
           !Character.isSpaceChar(c) ) {
        System.out.println("" + c + " " + (int)c);
      }
    }
  }
}