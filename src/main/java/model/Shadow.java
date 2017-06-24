package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Shadow {

	private int number;
	private int[] values;
	
	public Shadow(int[] values, int number) {
		super();
		this.values = values;
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public int getAt(int i) {
		return values[i];
	}
	
	public int size() {
		return values.length;
	}
	
	public static List<Shadow> fromArrays(int[][] shadows) {
		return IntStream.range(0, shadows.length).mapToObj(i -> new Shadow(shadows[i], i + 1)).collect(Collectors.toList());
	}
}
