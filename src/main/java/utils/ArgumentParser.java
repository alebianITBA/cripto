package utils;

import interfaces.Operation;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


import java.nio.file.Path;
import java.nio.file.Paths;

public class ArgumentParser {
  private static final String DISTRIBUTE_ARG = "d";
  private static final String RETRIEVE_ARG = "r";
  private static final String SECRET_ARG = "secret";
  private static final String MINIMUM_SHADOWS_ARG = "k";
  private static final String TOTAL_SHADOWS_ARG = "n";
  private static final String DIR_ARG = "dir";

  private Options distributeOptions;
  private Options retrieveOptions;

  private Operation operation;
  private Integer minimumShadows;
  private Integer totalShadows;
  private Path secretFile;
  private Path dir;

  public ArgumentParser() {
    createOptions();
    addSpecificOptions();
    addCommonOptions(this.distributeOptions);
    addCommonOptions(this.retrieveOptions);
  }

  private void createOptions() {
    this.distributeOptions = new Options();
    this.retrieveOptions = new Options();
  }

  private void addSpecificOptions() {
    distributeOptions.addOption(Option.builder()
        .argName(DISTRIBUTE_ARG)
        .longOpt(DISTRIBUTE_ARG)
        .desc("Execute image distribution.")
        .hasArg(false)
        .required(true)
        .build());

    distributeOptions.addOption(Option.builder()
        .argName(TOTAL_SHADOWS_ARG)
        .longOpt(TOTAL_SHADOWS_ARG)
        .desc("Total N number of shadows in a (K, N) scheme.")
        .hasArg(true)
        .required(false)
        .build());

    retrieveOptions.addOption(Option.builder()
        .argName(RETRIEVE_ARG)
        .longOpt(RETRIEVE_ARG)
        .desc("Execute image distribution.")
        .hasArg(false)
        .required(true)
        .build());
  }

  private void addCommonOptions(Options options) {
    options.addOption(Option.builder()
        .argName(SECRET_ARG)
        .longOpt(SECRET_ARG)
        .desc("BMP image to hide or retrieve.")
        .hasArg()
        .required(true)
        .build());

    options.addOption(Option.builder()
        .argName(MINIMUM_SHADOWS_ARG)
        .longOpt(MINIMUM_SHADOWS_ARG)
        .desc("Minimum K number of shadows in a (K, N) scheme.")
        .hasArg()
        .required(true)
        .build());

    options.addOption(Option.builder()
        .argName(DIR_ARG)
        .longOpt(DIR_ARG)
        .desc("Minimum number of shadows.")
        .hasArg()
        .required(false)
        .build());
  }

  public Operation getOperation() {
    return this.operation;
  }

  public Integer getMinimumShadows() {
    return minimumShadows;
  }

  public Path getSecretFile() {
    return secretFile;
  }

  public Integer getTotalShadows() {
    return totalShadows;
  }

  public Path getDir() {
    return dir;
  }

  public boolean parse(String[] args) {
    boolean parsed;
    CommandLineParser distributeParser = new DefaultParser();
    CommandLineParser retrieveParser = new DefaultParser();

    try {
      // Parse distribute arguments
      CommandLine distributeCmd = distributeParser.parse(this.distributeOptions, args);
      this.operation = Operation.DISTRIBUTE;
      if (distributeCmd.hasOption(TOTAL_SHADOWS_ARG)) {
        this.totalShadows = Integer.valueOf(distributeCmd.getOptionValue(TOTAL_SHADOWS_ARG));
      }
      parsed = checkArgs(distributeCmd);
    } catch (ParseException distributeException) {

      try {
        // Parse retrieve arguments
        CommandLine retrieveCmd = retrieveParser.parse(this.retrieveOptions, args);
        parsed = checkArgs(retrieveCmd);
        this.operation = Operation.RETRIEVE;
      } catch (ParseException retrieveException) {
        parsed = false;
      }
    }

    return parsed;
  }

  private boolean checkArgs(final CommandLine cmd) throws ParseException {
    this.secretFile = Paths.get(cmd.getOptionValue(SECRET_ARG));
    this.minimumShadows = Integer.valueOf(cmd.getOptionValue(MINIMUM_SHADOWS_ARG));

    if (minimumShadows > totalShadows || minimumShadows <= 0 || totalShadows <= 0) {
      throw new ParseException("");
    }

    if (cmd.hasOption(DIR_ARG)) {
      this.dir = Paths.get(cmd.getOptionValue(DIR_ARG));
    } else {
      this.dir = Paths.get("").toAbsolutePath();
    }

    return true;
  }
}
