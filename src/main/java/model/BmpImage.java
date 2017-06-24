package model;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This class is mutable, so be careful and remember to clone when necessary.
 */
public class BmpImage {

	private byte[] arr;
	private int size;
	private int width;
	private int height;
	private int offset;
	
	public BmpImage(byte[] arr) {
		this.arr = arr;
		int headerSize = getInt(arr, 14, 18);
		if (headerSize <= 12) {
			throw new IllegalArgumentException("Invalid BMP. Header size too small.");
		}
		int bitsPerPixel = getShort(arr, 28, 30);
		if (bitsPerPixel != 8) {
			throw new IllegalArgumentException("Invalid BMP. Pixels aren't 1 byte.");
		}
		if (getShort(arr, 6, 8) != 0) {
			throw new IllegalArgumentException("Invalid BMP. Reserved bytes are already used.");
		}
		this.size = getInt(arr, 2, 6);
		this.offset = getInt(arr, 10, 14);
		this.width = getInt(arr, 18, 22);
		this.height = getInt(arr, 22, 26);
	}
	
	/**
	 * This WILL change the object.
	 */
	public void setByte(int index, int value) {
		arr[index] = (byte) value;
	}
	
	public int getByte(int index) {
		return arr[index] & 0xFF;
	}
	
	private int getInt(byte[] arr, int start, int end) {
		return Integer.reverseBytes(ByteBuffer.wrap(Arrays.copyOfRange(arr, start, end)).getInt());
	}
	
	private int getShort(byte[] arr, int start, int end) {
		return Short.reverseBytes(ByteBuffer.wrap(Arrays.copyOfRange(arr, start, end)).getShort());
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
	
	public BmpImage clone() {
		return new BmpImage(Arrays.copyOf(arr, arr.length));
	}

	@Override
	public String toString() {
		return "BmpImage [size=" + size + ", width=" + width + ", height=" + height
				+ ", offset=" + offset + "]";
	}
}
