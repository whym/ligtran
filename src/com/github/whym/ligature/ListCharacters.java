package com.github.whym.ligature;

import java.util.*;

public class ListCharacters {
  public static void main(String[] args) {
    for ( char c = Character.MIN_VALUE + 1; c < Character.MAX_VALUE; ++c ) {
      if ( c != '\n'  &&  Character.getType(c) != Character.CONTROL && !Character.isMirrored(c) ) {
        System.out.println("" + c + " " + (int)c);
      }
    }
  }
}