package utils;

import model.BmpImage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BmpReader {

	public static void main(String[] args) {
		try {
			new BmpReader().readImage("resources/lena512.bmp");
			System.out.println();
			new BmpReader().readImage("resources/boats.bmp");
			System.out.println();
			new BmpReader().readImage("resources/girlface.bmp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String> fromDirectory(final String directoryName) {
		final File directory = new File(directoryName);
		final File[] list = directory.listFiles();

		Objects.requireNonNull(list);

		return Arrays.stream(list)
				.filter(file -> file.getName().endsWith("bmp"))
				.map(File::getName)
				.collect(Collectors.toList());
	}
	
	public void readImage(String imgName) throws IOException {
		File file = new File(imgName);
		byte[] arr = Files.readAllBytes(file.toPath());
		BmpImage img = new BmpImage(arr);
		System.out.println(img);
	}
	
	@SuppressWarnings("unused")
	private void print(byte[] arr) {
		for (byte b: arr) {
//			System.out.println(String.format("%d", b));
			System.out.println(String.format("%02X ", b));
		}
	}
}
