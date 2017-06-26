package main;

import java.util.Random;

public class RandomTable {
	
	private static int NUMBER_BOUND = 256;
	
	private final short seed;

	public RandomTable(short seed) {
		this.seed = seed;
	}

	public int[] createTable(int size) {
		Random rnd = new Random(seed);
		int[] table = new int[size];

		for (int i = 0; i < size; i++) {
			table[i] = rnd.nextInt(NUMBER_BOUND);
		}

		return table;
	}
}
