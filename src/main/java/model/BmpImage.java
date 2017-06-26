package model;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BmpImage {
	
	private final String name;
	private final byte[] arr;
	private final int size;
	private final int width;
	private final int height;
	private final int offset;
	private final int imageSize;
	private final boolean reservedShouldBeEmpty;
	
	private static byte[] header;

	public BmpImage(String name, byte[] pixels) {
//		int headerSize = 40;
//		this.name = name;
//		this.imageSize = pixels.length;
//		this.reservedShouldBeEmpty = true;
//		this.size = 1078 + pixels.length;
//		ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
//		sizeBuffer.putInt(Integer.reverseBytes(size));
//		this.offset = 1078;
//		ByteBuffer offsetBuffer = ByteBuffer.allocate(4);
//		offsetBuffer.putInt(Integer.reverseBytes(offset));
//		ByteBuffer headerSizeBuffer = ByteBuffer.allocate(4);
//		headerSizeBuffer.putInt(Integer.reverseBytes(headerSize));	
//		this.width = 300;
//		ByteBuffer widthBuffer = ByteBuffer.allocate(4);
//		widthBuffer.putInt(Integer.reverseBytes(width));	
//		this.height = 300;
//		ByteBuffer heightBuffer = ByteBuffer.allocate(4);
//		heightBuffer.putInt(Integer.reverseBytes(height));	
//		ByteBuffer bitsPerPixelBuffer = ByteBuffer.allocate(2);
//		bitsPerPixelBuffer.putShort(Short.reverseBytes((short) 8));	
//		this.arr = new byte[headerSize + pixels.length];
//		System.arraycopy(new byte[]{'B', 'M'}, 0, arr, 0, 2);
//		System.arraycopy(sizeBuffer.array(), 0, arr, 2, 4);
//		System.arraycopy(offsetBuffer.array(), 0, arr, 10, 4);
//		System.arraycopy(headerSizeBuffer.array(), 0, arr, 14, 4);
//		System.arraycopy(widthBuffer.array(), 0, arr, 18, 4);
//		System.arraycopy(heightBuffer.array(), 0, arr, 22, 4);
//		System.arraycopy(bitsPerPixelBuffer.array(), 0, arr, 28, 2);
//		System.arraycopy(pixels, 0, arr, headerSize, pixels.length);
//		System.out.println(Arrays.toString(Arrays.copyOf(arr, headerSize)));
//		System.out.println(getInt(arr, 2, 6));
		this.height = 0;
		this.width = 0;
		this.imageSize = 0;
		this.name = name;
		this.offset = 0;
		this.reservedShouldBeEmpty = false;
		this.size = 0;
		this.arr = new byte[BmpImage.header.length + pixels.length];
		System.arraycopy(BmpImage.header, 0, arr, 0, BmpImage.header.length);
		System.arraycopy(pixels, 0, arr, BmpImage.header.length, pixels.length);
	}
	
	public BmpImage(String name, byte[] arr, boolean reservedShouldBeEmpty) throws BmpImageException {
		this.name = name;
		this.arr = arr;
		this.reservedShouldBeEmpty = reservedShouldBeEmpty;
		int headerSize = getInt(arr, 14, 18);
		System.out.println(Arrays.toString(Arrays.copyOf(arr, 54)));
		if (headerSize <= 12) {
			throw new BmpImageException("Header size too small: " + headerSize);
		}
		int bitsPerPixel = getShort(arr, 28, 30);
		if (bitsPerPixel != 8) {
			throw new BmpImageException("Pixels aren't 1 byte.");
		}
		if (reservedShouldBeEmpty && getInt(arr, 6, 10) != 0) {
			throw new BmpImageException("Reserved bytes are already used.");
		}
		this.size = getInt(arr, 2, 6);
		this.offset = getInt(arr, 10, 14);
		BmpImage.header = Arrays.copyOfRange(arr, 0, offset);
		this.width = getInt(arr, 18, 22);
		this.height = getInt(arr, 22, 26);
		this.imageSize = this.size - this.offset;
	}

	public static BmpImage readImage(final Path imagePath, boolean reservedShouldBeEmpty)
			throws BmpImageException, IOException {
		byte[] arr = Files.readAllBytes(imagePath);
		return new BmpImage(imagePath.toString(), arr, reservedShouldBeEmpty);
	}

	public static List<BmpImage> fromDirectory(final String directoryName, boolean reservedShouldBeEmpty)
			throws IOException {
		final File directory = new File(directoryName);
		final File[] list = directory.listFiles();

		Objects.requireNonNull(list);

		return Arrays
				.stream(list)
				.filter(file -> file.getName().endsWith("bmp"))
				.map(File::getName)
				.map(Paths::get)
				.map(fileName -> {
					try {
						return BmpImage.readImage(fileName, reservedShouldBeEmpty);
					} catch (Exception e) {
						throw new IllegalStateException(
								"Cloned BMP should be legal BMP image.");
					}
				}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public BmpImage hideBytes(byte[] bytes) {
		byte[] arrCopy = Arrays.copyOf(arr, arr.length);
		for (int i = 0; i < bytes.length; i++) {
			for (int b = 0; b < 8; b++) {
				byte bitValue = (byte) ((bytes[i] & (1 << b)) >> b);
				arrCopy[this.offset + i * 8 + b] &= (0xFF - 1);
				arrCopy[this.offset + i * 8 + b] |= bitValue;
			}
		}
		try {
			return new BmpImage(this.name, arrCopy, this.reservedShouldBeEmpty);
		} catch (BmpImageException e) {
			throw new IllegalStateException(
					"Cloned BMP should be legal BMP image: " + e.getMessage());
		}
	}

	public byte[] getHiddenBytes(int length) {
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			for (int b = 0; b < 8; b++) {
				byte bitValue = (byte) (arr[this.offset + i * 8 + b] & 1);
				bytes[i] |= bitValue << b;
			}
		}
		return bytes;
	}

	public BmpImage hideSeed(short seed) {
		byte[] arrCopy = Arrays.copyOf(arr, arr.length);
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort(Short.reverseBytes(seed));
		System.arraycopy(buffer.array(), 0, arrCopy, 6, 2);
		try {
			return new BmpImage(this.name, arrCopy, false);
		} catch (BmpImageException e) {
			throw new IllegalStateException(
					"Cloned BMP should be legal BMP image: " + e.getMessage());
		}
	}
	
	public short getSeed() {
		return getShort(arr, 6, 8);
	}
	
	public BmpImage hideShadowNumber(short k) {
		byte[] arrCopy = Arrays.copyOf(arr, arr.length);
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort(Short.reverseBytes(k));
		System.arraycopy(buffer.array(), 0, arrCopy, 8, 2);
		try {
			return new BmpImage("shadows/" + this.name, arrCopy, false);
		} catch (BmpImageException e) {
			throw new IllegalStateException(
					"Cloned BMP should be legal BMP image: " + e.getMessage());
		}
	}
	
	public short getShadowNumber() {
		return getShort(arr, 8, 10);
	}
	
	private int getInt(byte[] arr, int start, int end) {
		return Integer.reverseBytes(ByteBuffer.wrap(
				Arrays.copyOfRange(arr, start, end)).getInt());
	}

	private short getShort(byte[] arr, int start, int end) {
		return Short.reverseBytes(ByteBuffer.wrap(
				Arrays.copyOfRange(arr, start, end)).getShort());
	}

	public byte[] getByteArray() {
		return arr;
	}
	
	public byte[] getPixels() {
		return Arrays.copyOfRange(arr, getOffset(), arr.length);
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return size;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getOffset() {
		return offset;
	}

	public int getImageSize() {
		return imageSize;
	}
	
	public void save() throws IOException {
		Files.write(Paths.get(name), this.arr,  StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}

	public BmpImage clone() {
		try {
			return new BmpImage(this.name + "_copy", Arrays.copyOf(arr, arr.length), reservedShouldBeEmpty);
		} catch (BmpImageException e) {
			throw new IllegalStateException(
					"Cloned BMP should be legal BMP image.");
		}
	}

	@Override
	public String toString() {
		return "BmpImage [size=" + size + ", width=" + width + ", height="
				+ height + ", offset=" + offset + "]";
	}
}
