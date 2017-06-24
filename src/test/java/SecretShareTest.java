import static org.junit.Assert.assertArrayEquals;

import main.SecretShare;
import org.junit.Test;

public class SecretShareTest {
  int[] table = {187, 242, 13, 255, 165};
  int[] image = {1, 1, 1, 1, 1};

  int[] randomizedImage = {
      1, 2, 3, 4, 5, 6, 7, 8,
      1, 2, 3, 4, 5, 6, 7, 8,
      1, 2, 3, 4, 5, 6, 7, 8,
      1, 2, 3, 4, 5, 6, 7, 8,
      1, 2, 3, 4, 5, 6, 7, 8,
      1, 2, 3, 4, 5, 6, 7, 8,
      1, 2, 3, 4, 5, 6, 7, 8,
      1, 2, 3, 4, 5, 6, 7, 8,
  };
  
  int[][] shadows = {
	  { 36,  36,  36,  36,  36,  36,  36,  36},
	  {251, 251, 251, 251, 251, 251, 251, 251},
	  {189, 189, 189, 189, 189, 189, 189, 189},
	  {174, 174, 174, 174, 174, 174, 174, 174},
	  {228, 228, 228, 228, 228, 228, 228, 228},
	  { 86,  86,  86,  86,  86,  86,  86,  86},
	  { 23,  23,  23,  23,  23,  23,  23,  23},
	  {151, 151, 151, 151, 151, 151, 151, 151}
  };
  
  int[] randomizedComplexImage = {
	  19, 20, 21, 22, 23, 24, 25, 26,
  };
  
  int[][] shadowsComplex = {
     {179},
     {214},
     {118},
     {173},
     {155},
     { 3 },
     {121},
     {255}
  };

  @Test
  public void randomizeTest() {
    int[] test = SecretShare.randomize(image, table);
    int[] expected = {186, 243, 12, 254, 164};

    assertArrayEquals(expected, test);
  }

  @Test
  public void generateShadows() {
    int[][] test = SecretShare.generateShadows(8, 8, randomizedImage);

    assertArrayEquals(shadows[0], test[0]);
    assertArrayEquals(shadows[1], test[1]);
    assertArrayEquals(shadows[2], test[2]);
    assertArrayEquals(shadows[3], test[3]);
    assertArrayEquals(shadows[4], test[4]);
    assertArrayEquals(shadows[5], test[5]);
    assertArrayEquals(shadows[6], test[6]);
    assertArrayEquals(shadows[7], test[7]);
  }
  
  @Test
  public void generateShadowsWith256() {
    int[][] test = SecretShare.generateShadows(8, 8, randomizedComplexImage);

    assertArrayEquals(shadowsComplex[0], test[0]);
    assertArrayEquals(shadowsComplex[1], test[1]);
    assertArrayEquals(shadowsComplex[2], test[2]);
    assertArrayEquals(shadowsComplex[3], test[3]);
    assertArrayEquals(shadowsComplex[4], test[4]);
    assertArrayEquals(shadowsComplex[5], test[5]);
    assertArrayEquals(shadowsComplex[6], test[6]);
    assertArrayEquals(shadowsComplex[7], test[7]);
  }
}
