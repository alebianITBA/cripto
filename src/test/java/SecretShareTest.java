import static org.junit.Assert.assertArrayEquals;
import main.SecretShare;
import model.Shadow;

import org.junit.Test;

import java.util.stream.IntStream;

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

    IntStream.range(0, shadows.length).forEach(i -> assertArrayEquals(shadows[i], test[i]));
  }
  
  @Test
  public void generateShadowsWith256() {
    int[][] test = SecretShare.generateShadows(8, 8, randomizedComplexImage);

    IntStream.range(0, shadowsComplex.length).forEach(i -> assertArrayEquals(shadowsComplex[i], test[i]));
  }
  
  @Test
  public void decipher() {
    assertArrayEquals(
      SecretShare.getRandomizedImage(
        Shadow.fromArrays(SecretShare.generateShadows(
         8, 8, randomizedImage)), 8),
          randomizedImage);
  }
}
