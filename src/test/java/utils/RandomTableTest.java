package utils;
import static org.junit.Assert.assertArrayEquals;
import main.RandomTable;

import org.junit.Test;

public class RandomTableTest {
  @Test
  public void createTableTest() {
    int size = 5;
    short seed = (short) (size * size);

    int[] testTable = new RandomTable(seed).createTable(size);
    int[] expected = {187, 242, 13, 255, 165};

    assertArrayEquals(expected, testTable);
  }
}
