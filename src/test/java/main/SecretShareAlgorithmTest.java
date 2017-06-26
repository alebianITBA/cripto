package main;

import static org.junit.Assert.assertArrayEquals;

import java.util.stream.IntStream;

import model.Shadow;

import org.junit.Test;

public class SecretShareAlgorithmTest {
	int[] table = { 187, 242, 13, 255, 165 };
	byte[] image = { 1, 1, 1, 1, 1 };

	byte[] randomizedImage = { 
			1, 2, 3, 4, 5, 6, 7, 8, 
			1, 2, 3, 4, 5, 6, 7, 8,
			1, 2, 3, 4, 5, 6, 7, 8, 
			1, 2, 3, 4, 5, 6, 7, 8, 
			1, 2, 3, 4, 5, 6, 7, 8, 
			1, 2, 3, 4, 5, 6, 7, 8, 
			1, 2, 3, 4, 5, 6, 7, 8,
			1, 2, 3, 4, 5, 6, 7, 8, };

	byte[][] shadows = { 
			{  36 , 36 , 36 , 36 , 36 , 36 , 36 , 36 },
			{ (byte) 251, (byte) 251, (byte) 251, (byte) 251, (byte) 251, (byte) 251, (byte) 251, (byte) 251 },
			{ (byte) 189, (byte) 189, (byte) 189, (byte) 189, (byte) 189, (byte) 189, (byte) 189, (byte) 189 },
			{ (byte) 174, (byte) 174, (byte) 174, (byte) 174, (byte) 174, (byte) 174, (byte) 174, (byte) 174 },
			{ (byte) 228, (byte) 228, (byte) 228, (byte) 228, (byte) 228, (byte) 228, (byte) 228, (byte) 228 },
			{  86 , 86 , 86 , 86 , 86 , 86 , 86 , 86 },
			{  23 , 23 , 23 , 23 , 23 , 23 , 23 , 23 },
			{ (byte) 151, (byte) 151, (byte) 151, (byte) 151, (byte) 151, (byte) 151, (byte) 151, (byte) 151 } 
		};

	byte[] randomizedComplexImage = { 19, 20, 21, 22, 23, 24, 25, 26, };

	byte[][] shadowsComplex = { { (byte) 179 }, { (byte) 214 }, { 118 }, { (byte) 173 }, { (byte) 155 },
			{ 3 }, { 121 }, { (byte) 255 } };

	@Test
	public void randomizeTest() {
		SecretShareAlgorithm alg = new SecretShareAlgorithm((short) 25);
		byte[] test = alg.xorWithTable(image);
		byte[] expected = { (byte) 186, (byte) 243, 12, (byte) 254, (byte) 164 };

		assertArrayEquals(expected, test);
	}

	@Test
	public void generateShadows() {
		byte[][] test = new SecretShareAlgorithm().getShadowsFromRand(8, 8,
				randomizedImage);

		IntStream.range(0, shadows.length).forEach(
				i -> assertArrayEquals(shadows[i], test[i]));
	}

	@Test
	public void generateShadowsWith256() {
		byte[][] test = new SecretShareAlgorithm().getShadowsFromRand(8, 8,
				randomizedComplexImage);

		IntStream.range(0, shadowsComplex.length).forEach(
				i -> assertArrayEquals(shadowsComplex[i], test[i]));
	}

	@Test
	public void decipher() {
		assertArrayEquals(randomizedImage, new SecretShareAlgorithm().getRandomizedImage(Shadow
				.fromArrays(new SecretShareAlgorithm().getShadowsFromRand(8, 8,
						randomizedImage)), 8));
	}
}
