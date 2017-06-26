package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.BmpImage;
import model.BmpImageException;
import model.Shadow;
import utils.ArgsParserException;
import utils.ArgumentParser;
import utils.Operation;

public class Main {

	public static void main(String[] args) {
		ArgumentParser parser = getArgumentParser(args);
		int r = parser.getMinimumShadows();
		int n = parser.getTotalShadows();
		Path secretPath = parser.getSecretPath();

		List<BmpImage> shadowHolders = getShadowHolders(parser.getDir(),
				parser.getOperation() == Operation.DISTRIBUTE ? false : false);
		
		if (shadowHolders.size() < r) {
			System.err.println("Not enough shadows.");
			System.exit(1);
		}
		
		switch (parser.getOperation()) {
		case DISTRIBUTE:
			distribute(secretPath, shadowHolders, r, n);
			break;
		case RETRIEVE:
			retrieve(secretPath, shadowHolders, r);
			break;
		default:
			throw new IllegalStateException("Unknown operation.");
		}
	}

	private static void distribute(Path secretPath, List<BmpImage> shadowHolders, int r, int n) {
		BmpImage secret = readSecretImage(secretPath);
		byte[] secretPixels = Arrays.copyOf(secret.getPixels(),
				(int) (Math.ceil(secret.getImageSize() / r) * r));
		int shadowSize = secretPixels.length / r;
		checkShadowHoldersSize(shadowHolders, shadowSize);
		SecretShareAlgorithm secretShareAlg = new SecretShareAlgorithm();
		byte[][] shadows = secretShareAlg.getShadows(r, n, secretPixels);
		List<BmpImage> realShadows = new ArrayList<>();
		for (int i = 0; i < shadows.length; i++) {
			realShadows.add(shadowHolders.get(i).hideBytes(shadows[i])
					.hideSeed(secretShareAlg.getSeed())
					.hideShadowNumber((short) (i + 1)));
		}
		saveShadows(realShadows);
	}
	
	private static void retrieve(Path secretPath, List<BmpImage> shadowHolders, int r) {
		List<Shadow> shadows = shadowHolders.stream()
				.map(shadow -> {
					byte[] content = shadow.getHiddenBytes(shadow.getImageSize() / 8);
					int number = shadow.getShadowNumber();
					System.out.println(number);
					short seed = shadow.getSeed();
					System.out.println(seed);
					return new Shadow(content, number, seed);
				})
				.collect(Collectors.toList());
		SecretShareAlgorithm secretShareAlg = new SecretShareAlgorithm(shadows.get(0).getSeed());
		byte[] bytes = secretShareAlg.getImage(shadows, r);
		saveSecret(secretPath, bytes);
	}
	
	private static void checkShadowHoldersSize(List<BmpImage> shadowHolders, int shadowSize) {
		List<String> smallImgs = shadowHolders.stream()
				.filter(img -> img.getImageSize() < shadowSize * 8)
				.map(img -> img.getName()).collect(Collectors.toList());
		if (!smallImgs.isEmpty()) {
			System.err
					.println("The following shadow images are too small: "
							+ String.join(", ", smallImgs));
			System.exit(1);
		}
	}
	
	private static List<BmpImage> getShadowHolders(Path dir, boolean reservedShouldBeEmpty) {
		try {
			return Files.list(dir)
					.filter(path -> path.toString().endsWith(".bmp"))
					.map(path -> {
						try {
							return BmpImage.readImage(path, reservedShouldBeEmpty);
						} catch(IOException e) {
							throw new IllegalStateException("Could't open known path.");
						} catch(BmpImageException e) {
							System.err.println(
									"Invalid Bmp image " + path.toString() + ": " + e.getMessage());
							System.exit(1);
							return null;
						}
					})
					.collect(Collectors.toList());
		} catch (IOException e) {
			System.err.println("Error reading from directory.");
			System.exit(1);
			return null;
		}
	}
	
	private static ArgumentParser getArgumentParser(String[] args) {
		ArgumentParser parser = new ArgumentParser();
		try {
			parser.parse(args);
		} catch (ArgsParserException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		return parser;
	}
	
	private static BmpImage readSecretImage(Path path) {
		try {
			return BmpImage.readImage(path, false);
		} catch (BmpImageException e) {
			System.err.println(
					"Invalid Bmp image " + path.toString() + ": " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't open secret image.");
			System.exit(1);
		}
		return null;
	}
	
	private static void saveShadows(List<BmpImage> shadows) {
		shadows.stream().forEach(img -> {
			try {
				img.save();
			} catch (IOException e) {
				System.err.println("Couldn't save shadow image.");
				System.exit(1);
			}});
	}
	
	private static void saveSecret(Path secretPath, byte[] bytes) {
		try {
//			System.out.println(Arrays.toString(bytes));
			new BmpImage(secretPath.toString(), bytes).save();
		} catch (IOException e) {
			System.err.println("Couldn't save secret image.");
			System.exit(1);
//		} catch (BmpImageException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
}
