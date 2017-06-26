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
	private int size;
	private int width;
	private int height;
	private int offset;
	private int imageSize;
	private final boolean reservedShouldBeEmpty;
	
	public BmpImage(String name, byte[] pixels, boolean useWH, BmpImage shadow) {
		this.name = name;
		this.reservedShouldBeEmpty = false;
		byte[] header;
		if (useWH) {
			header = new byte[1078];
			System.arraycopy(shadow.getByteArray(), 0, header, 0, 54);
			this.width = shadow.getHiddenWidth();
			this.height = shadow.getHiddenHeight();
			this.size = 1078 + pixels.length;
			this.offset = 1078;
			ByteBuffer sizeBuffer = ByteBuffer.allocate(4).putInt(Integer.reverseBytes(size));
			ByteBuffer offsetBuffer = ByteBuffer.allocate(4).putInt(Integer.reverseBytes(offset));
			ByteBuffer headerSizeBuffer = ByteBuffer.allocate(4).putInt(Integer.reverseBytes(40));	
			ByteBuffer widthBuffer = ByteBuffer.allocate(4).putInt(Integer.reverseBytes(width));	
			ByteBuffer heightBuffer = ByteBuffer.allocate(4).putInt(Integer.reverseBytes(height));	
			byte[] palette = getColorPalette();
			System.arraycopy(sizeBuffer.array(), 0, header, 2, 4);
			System.arraycopy(offsetBuffer.array(), 0, header, 10, 4);
			System.arraycopy(headerSizeBuffer.array(), 0, header, 14, 4);
			System.arraycopy(widthBuffer.array(), 0, header, 18, 4);
			System.arraycopy(heightBuffer.array(), 0, header, 22, 4);
			System.arraycopy(palette, 0, header, 54, palette.length);
		} else {
			header = new byte[shadow.getOffset()];
			header = Arrays.copyOfRange(shadow.getByteArray(), 0, shadow.getOffset());
		}
		this.arr = new byte[header.length + pixels.length];
		System.arraycopy(header, 0, arr, 0, header.length);
		System.arraycopy(pixels, 0, arr, header.length, pixels.length);
	}
	
	public BmpImage(String name, byte[] arr, boolean reservedShouldBeEmpty) throws BmpImageException {
		this.name = name;
		this.arr = arr;
		this.reservedShouldBeEmpty = reservedShouldBeEmpty;
		int headerSize = getInt(arr, 14, 18);
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
		initVariables();
	}
	
	private void initVariables() {
		this.size = getInt(arr, 2, 6);
		this.offset = getInt(arr, 10, 14);
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
				byte bitValue = (byte) ((bytes[i] & (1 << (8 - b - 1))) >> (8 - b - 1));
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
				bytes[i] |= bitValue << (8 - b - 1);
			}
		}
		return bytes;
	}

	public BmpImage hideSeed(short seed) {
		return hideShort(seed, 6);
	}
	
	public short getSeed() {
		return getShort(arr, 6, 8);
	}
	
	public BmpImage hideShadowNumber(short k) {
		return hideShort(k, 8);
	}
	
	public short getShadowNumber() {
		return getShort(arr, 8, 10);
	}
	
	public BmpImage hideWidthNumber(int width) {
		return hideInt(width, 38);
	}
	
	public int getHiddenWidth() {
		return getInt(arr, 38, 42);
	}
	
	public BmpImage hideHeightNumber(int height) {
		return hideInt(height, 42);
	}
	
	public int getHiddenHeight() {
		return getInt(arr, 42, 46);
	}
	
	private BmpImage hideInt(int v, int start) {
		byte[] arrCopy = Arrays.copyOf(arr, arr.length);
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(Integer.reverseBytes(v));
		System.arraycopy(buffer.array(), 0, arrCopy, start, 4);
		try {
			return new BmpImage(this.name, arrCopy, false);
		} catch (BmpImageException e) {
			throw new IllegalStateException(
					"Cloned BMP should be legal BMP image: " + e.getMessage());
		}
	}
	
	private BmpImage hideShort(short v, int start) {
		byte[] arrCopy = Arrays.copyOf(arr, arr.length);
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putShort(Short.reverseBytes(v));
		System.arraycopy(buffer.array(), 0, arrCopy, start, 2);
		try {
			return new BmpImage(this.name, arrCopy, false);
		} catch (BmpImageException e) {
			throw new IllegalStateException(
					"Cloned BMP should be legal BMP image: " + e.getMessage());
		}
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
		Files.write(Paths.get(name), this.arr, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
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
	
	
	private static byte[] getColorPalette() {
		byte[] palette = new byte[1024];
		for (int i = 0; i < 256; i++) {
			byte b = (byte) i;
			System.arraycopy(new byte[]{b, b, b, 0}, 0, palette, i * 4, 4);
		}
		return palette;
	}
}
