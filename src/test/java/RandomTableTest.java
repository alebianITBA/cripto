import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import utils.RandomTable;

public class RandomTableTest {
  @Test
  public void createTableTest() {
    int size = 5;
    long seed = size * size;

    int[] testTable = RandomTable.createTable(size, seed);
    int[] expected = {187, 242, 13, 255, 165};

    assertArrayEquals(expected, testTable);
  }
}
