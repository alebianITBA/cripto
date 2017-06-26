package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Shadow {

	private byte[] values;
	private int number;
	private short seed;
	
	public Shadow(byte[] values, int number, short seed) {
		super();
		this.values = values;
		this.number = number;
		this.seed = seed;
	}

	public int getNumber() {
		return number;
	}
	
	public short getSeed() {
		return seed;
	}

	public int getAt(int i) {
		return values[i];
	}
	
	public int size() {
		return values.length;
	}
	
	public static List<Shadow> fromArrays(byte[][] shadows) {
		return IntStream.range(0, shadows.length)
				.mapToObj(i -> new Shadow(shadows[i], i + 1, (short) 0))
				.collect(Collectors.toList());
	}
}
