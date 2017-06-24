package main;
import model.BmpImage;
import model.Shadow;
import utils.ArgumentParser;

import java.io.IOException;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    ArgumentParser parser = new ArgumentParser();
    boolean parsedCorrectly = parser.parse(args);
    if (!parsedCorrectly) {
      System.out.println("Parse error.");
      return;
    }

    switch (parser.getOperation()) {
      case DISTRIBUTE:
        System.out.println("DISTRIBUTE");
        System.out.println(parser.getTotalShadows());
        break;
      case RETRIEVE:
        System.out.println("RETRIEVE");
        break;
      default:
        break;
    }
    System.out.println(parser.getMinimumShadows());
    System.out.println(parser.getDir().toString());
    System.out.println(parser.getSecretFile().toString());

    try {
      BmpImage secret = BmpImage.readImage(parser.getSecretFile());
      int[][] shadows = SecretShare.generateShadowsFromRegularImage(parser.getMinimumShadows(), parser.getTotalShadows(), secret);
      int[] originalPixels = SecretShare.randomize(secret.getPixels());
      int[] recoveredPixels = SecretShare.getRandomizedImage(Shadow.fromArrays(shadows), parser.getMinimumShadows());
      for (int i = 0; i < originalPixels.length; i++) {
        if (originalPixels[i] != recoveredPixels[i]) {
          System.out.println(String.format("Fallo pixel %d, original: %d, recovered: %d", i, originalPixels[i], recoveredPixels[i]));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
