package utils;

import java.util.Random;

public class RandomTable {
  private static int NUMBER_BOUND = 256;

  public static int[] createTable(int size, long seed) {
    Random rnd = new Random();
    rnd.setSeed(seed);

    int[] table = new int[size];

    for (int i = 0; i < size ; i++) {
      table[i] = rnd.nextInt(NUMBER_BOUND);
    }

    return table;
  }
}
