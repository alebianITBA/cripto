package main;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import model.Shadow;
import utils.LinearModularEquationSolver;

/**
 * Implementation of the Kuang-Shyr Wu and Tsung-Ming Lo (r, n) secret image
 * share algorithm This class has all the tools needed to execute the algorithm
 */
public class SecretShareAlgorithm {

	private static int MODULO = 257;

	private final short seed;
	private final RandomTable randomTable;

	public SecretShareAlgorithm() {
		this((short) new Random().nextInt());
	}

	public SecretShareAlgorithm(short seed) {
		this.seed = seed;
		this.randomTable = new RandomTable(seed);
	}

	public byte[][] getShadows(int r, int n, byte[] pixels) {
		return getShadowsFromRand(r, n, xorWithTable(pixels));
	}

	public byte[] getImage(List<Shadow> originalShadows, int r) {
		return xorWithTable(getRandomizedImage(originalShadows, r));
	}
	
	public byte[] getRandomizedImage(List<Shadow> originalShadows, int r) {
		List<Shadow> shadows = originalShadows.subList(0, r);
		int size = originalShadows.stream().mapToInt(shadow -> shadow.size()).min().getAsInt();
		byte[] image = new byte[size * r];
		for (int i = 0; i < size; i++) {
			int[][] matrix = new int[r][r + 1];
			for (int j = 0; j < r; j++) {
				final Shadow shadow = shadows.get(j);
				int[] coeffs = IntStream.range(0, r)
						.map(k -> (int) Math.pow(shadow.getNumber(), k))
						.toArray();
				coeffs = Arrays.copyOf(coeffs, coeffs.length + 1);
				coeffs[coeffs.length - 1] = shadow.getAt(i) & 0xFF; // Change to unsigned int
				matrix[j] = coeffs;
			}
			int[] answer = LinearModularEquationSolver.solve(matrix, MODULO);
			byte[] byteAnswer = new byte[answer.length];
			for (int k = 0; k < answer.length; k++) {
				byteAnswer[k] = (byte) answer[k];
			}
			System.arraycopy(byteAnswer, 0, image, i * r, byteAnswer.length);
		}
		return image;
	}

	/**
	 * Method that creates the n secret shadows
	 */
	byte[][] getShadowsFromRand(int r, int n, byte[] randomized) {
		int shadowSize = randomized.length / r;
		byte[][] shadows = new byte[n][shadowSize];
		int processedIdx = 0;

		// We use j in this for to represent the j-th section like the paper
		for (int j = 0; j < shadowSize; j++) {
			byte[] section = Arrays.copyOfRange(randomized, processedIdx,
					processedIdx + r);

			boolean aFjIs256 = true;

			while (aFjIs256) {
				aFjIs256 = false;
				for (int i = 0; i < n && !aFjIs256; i++) {
					int fn = evaluate(section, i + 1);
					if (fn == MODULO - 1) {
						aFjIs256 = true;
						updateCoeffs(section);
					} else {
						// Assign pixel generated to the j-th pixel of the n
						// shadow images
						shadows[i][j] = (byte) fn;
					}
				}
			}

			processedIdx += r;
		}

		return shadows;
	}

	/**
	 * Method that applies xor to all the bytes of the image with a given table
	 */
	byte[] xorWithTable(byte[] image) throws IllegalArgumentException {
		byte[] randomized = new byte[image.length];
		int[] table = randomTable.createTable(image.length);
		IntStream.range(0, image.length).forEach(
				i -> randomized[i] = (byte) (image[i] ^ table[i]));
		return randomized;
	}

	private static int evaluate(byte[] coefficients, int x) {
		int value = 0;
		for (int i = 0; i < coefficients.length; i++) {
			value += coefficients[i] * Math.pow(x, i);
		}
		return value % MODULO;
	}

	private static void updateCoeffs(byte[] coefficients) {
		for (int i = 0; i < coefficients.length; i++) {
			if (coefficients[i] != 0) {
				coefficients[i]--;
				break;
			}
		}
	}
	
	public short getSeed() {
		return seed;
	}
}
