package main;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Implementation of the Kuang-Shyr Wu and Tsung-Ming Lo (r, n) secret image share algorithm
 * This class has all the tools needed to execute the algorithm
 */
public class SecretShare {
  /**
   * Method that applies xor to all the bytes of the image with a given table
   * It is expected that table and image are of the same size
   *
   * @param image O image of the paper
   * @param table R table of the paper
   */
  public static int MODULO = 257;

  public static int[] randomize(int[] image, int[] table) throws IllegalArgumentException {
    if (image.length != table.length) {
      throw new IllegalArgumentException("Image size and table size do not match");
    }

    int[] randomized = new int[image.length];
    IntStream.range(0, image.length).forEach(i -> randomized[i] = image[i] ^ table[i]);
    return randomized;
  }

  /**
   * Method that creates the n secret shadows
   * Q size must be a multiple of r
   *
   * @param r
   * @param n
   * @param randomized Q randomized image of the paper
   */
  public static int[][] generateShadows(int r, int n, int[] randomized) {
    if ((randomized.length / r) * r != randomized.length) {
      throw new IllegalArgumentException("Q image must be multiple of r.");
    }
    int shadowSize = randomized.length / r;
    int [][] shadows = new int[n][shadowSize];
    int processedIdx = 0;

    // We use j in this for to represent the j-th section like the paper
    for (int j = 0; j < shadowSize; j++) {
      int[] section = calculateSection(randomized, processedIdx, processedIdx + r);

      boolean aFjIs256 = true;
      
      while (aFjIs256) {
        aFjIs256 = false;
        for (int i = 0; i < n && !aFjIs256; i++) {
          int fn = evaluate(section, i + 1);
          if (fn == MODULO - 1) {
            aFjIs256 = true;
            updateCoeffs(section);
          } else {
            // Assign pixel generated to the j-th pixel of the n shadow images
            shadows[i][j] = fn;
          }
        }
      }

      processedIdx += r;
    }

    return shadows;
  }

  private static int[] calculateSection(final int[] array, int from, int to) {
    return Arrays.copyOfRange(array, from, to);
  }

  private static int evaluate(int[] coefficients, int x) {
    int value = 0;
    for (int i = 0; i < coefficients.length; i++) {
      value += coefficients[i] * Math.pow(x, i);
    }
    return value % MODULO;
  }
  
  private static void updateCoeffs(int[] coefficients) {
    for (int i = 0; i < coefficients.length; i++) {
      if (coefficients[i] != 0) {
        coefficients[i]--;
        break;
      }
    }
  }
}
