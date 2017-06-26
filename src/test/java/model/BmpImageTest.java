package model;

import org.junit.Assert;
import org.junit.Test;

public class BmpImageTest {

	@Test
	public void testHideAndGetBytes() throws BmpImageException {
		BmpImage bmp = new BmpImage("name", new byte[]{
				0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x20, 0x00, 
				0x00, 0x00, 0x00, 0x0C, 
				0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 
				0x00, 0x00, 0x00, 0x00, 
				0x08, 0x00, 0x00, 0x00, 
				0x2A, 0x34, 0x01, 0x30,
				(byte) 0xFE, (byte) 0xFF, 0x00, 0x5C,
				0x2A, 0x34, 0x01, 0x30,
				(byte) 0xFE, (byte) 0xFF, 0x00, 0x5C}, true);
			BmpImage altered = bmp.hideBytes(new byte[] {(byte) 215, 17});
			Assert.assertArrayEquals(new byte[] {(byte) 215, 17}, altered.getHiddenBytes(2));
	}
}
