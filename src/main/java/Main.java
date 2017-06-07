import utils.ArgumentParser;

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
  }
}
