package main;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import model.Shadow;
import utils.LinearModularEquationSolver;

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
  
  public static int[] getRandomizedImage(List<Shadow> originalShadows, int r) {
	  if (originalShadows.size() < r) {
		  throw new IllegalArgumentException("Not enough shadows.");
	  }
	  List<Shadow> shadows = originalShadows.subList(0, r);
	  int[] image = new int[shadows.get(0).size() * r];
	  for (int i = 0; i < shadows.get(0).size(); i++) {
		  int[][] matrix = new int[shadows.size()][r + 1];
		  for (int j = 0; j < shadows.size(); j++) {
			  final Shadow shadow = shadows.get(j);
			  int[] coeffs = IntStream.range(0, r).map(k -> (int) Math.pow(shadow.getNumber(), k)).toArray();
			  coeffs = Arrays.copyOf(coeffs, coeffs.length + 1);
			  coeffs[coeffs.length -1] = shadow.getAt(i);
			  matrix[j] = coeffs;
		  }
		  Arrays.stream(matrix).forEach(row -> System.out.println(Arrays.toString(row)));
		  System.out.println();
		  int[] answer = LinearModularEquationSolver.solve(matrix, MODULO);
		  System.arraycopy(answer, 0, image, i * r, answer.length);
	  }
	  return image;
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
