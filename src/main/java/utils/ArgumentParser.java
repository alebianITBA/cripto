package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
		distributeOptions.addOption(Option.builder().argName(DISTRIBUTE_ARG)
				.longOpt(DISTRIBUTE_ARG).desc("Execute image distribution.")
				.hasArg(false).required(true).build());

		distributeOptions.addOption(Option.builder().argName(TOTAL_SHADOWS_ARG)
				.longOpt(TOTAL_SHADOWS_ARG)
				.desc("Total N number of shadows in a (K, N) scheme.").hasArg()
				.required(false).build());

		retrieveOptions.addOption(Option.builder().argName(RETRIEVE_ARG)
				.longOpt(RETRIEVE_ARG).desc("Execute image distribution.")
				.hasArg(false).required(true).build());
	}

	private void addCommonOptions(Options options) {
		options.addOption(Option.builder().argName(SECRET_ARG)
				.longOpt(SECRET_ARG).desc("BMP image to hide or retrieve.")
				.hasArg().required(true).build());

		options.addOption(Option.builder().argName(MINIMUM_SHADOWS_ARG)
				.longOpt(MINIMUM_SHADOWS_ARG)
				.desc("Minimum K number of shadows in a (K, N) scheme.")
				.hasArg().required(true).build());

		options.addOption(Option.builder().argName(DIR_ARG).longOpt(DIR_ARG)
				.desc("Directory to work on.").hasArg().required(false).build());
	}

	public Operation getOperation() {
		return this.operation;
	}

	public Integer getMinimumShadows() {
		return minimumShadows;
	}

	public Path getSecretPath() {
		return secretFile;
	}

	public Integer getTotalShadows() {
		return totalShadows;
	}

	public Path getDir() {
		return dir;
	}

	public void parse(String[] args) throws ArgsParserException {
		List<String> argsList = Arrays.asList(args);
		if (argsList.contains("-d") && argsList.contains("-r")) {
			throw new ArgsParserException(
					"Only one of -d or -r should be specified.");
		} else if (!argsList.contains("-d") && !argsList.contains("-r")) {
			throw new ArgsParserException("Either -d or -r should be specified.");
		} else if (argsList.contains("-d")) {
			this.operation = Operation.DISTRIBUTE;
			parseDistributionArgs(args);
		} else {
			this.operation = Operation.RETRIEVE;
			parseRetrieveArgs(args);
		}
	}

	private void parseDistributionArgs(String[] args) throws ArgsParserException {
		CommandLine cmd;
		try {
			cmd = new DefaultParser().parse(this.distributeOptions,
					args);			
			setDir(cmd);
			if (cmd.hasOption(TOTAL_SHADOWS_ARG)) {
				this.totalShadows = Integer.valueOf(cmd
						.getOptionValue(TOTAL_SHADOWS_ARG));
			} else {
				this.totalShadows = countImagesOnDir();
			}
			this.secretFile = Paths.get(cmd.getOptionValue(SECRET_ARG));
			setMinimumShadows(cmd);
			checkArgs(cmd);
		} catch (ParseException e) {
			throw new ArgsParserException(e.getMessage());
		}
	}
	
	private void parseRetrieveArgs(String[] args) throws ArgsParserException {
		CommandLine cmd;
		try {
			cmd = new DefaultParser().parse(this.retrieveOptions, args);
			this.secretFile = Paths.get(cmd.getOptionValue(SECRET_ARG));
			setMinimumShadows(cmd);
			setDir(cmd);	
			this.totalShadows = countImagesOnDir();
			checkArgs(cmd);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private boolean checkArgs(CommandLine cmd) throws ArgsParserException {
		if (totalShadows > Short.MAX_VALUE - 1) {
			throw new ArgsParserException(
					"n should be lower than or equal to " + (Short.MAX_VALUE - 1));
		}
		if (minimumShadows > totalShadows || minimumShadows <= 0
				|| totalShadows <= 0) {
			throw new ArgsParserException(
					"k <= n, n > 0, k > 0 requirement not met.");
		}
		return true;
	}

	private void setMinimumShadows(CommandLine cmd) {
		this.minimumShadows = Integer.valueOf(cmd
				.getOptionValue(MINIMUM_SHADOWS_ARG));
	}
	
	private void setDir(CommandLine cmd) {
		if (cmd.hasOption(DIR_ARG)) {
			this.dir = Paths.get(cmd.getOptionValue(DIR_ARG));
		} else {
			this.dir = Paths.get("").toAbsolutePath();
		}
	}
	
	private int countImagesOnDir() throws ArgsParserException {
		try {
			return (int) Files.list(this.dir).filter(f -> f.getFileName().toString().endsWith(".bmp")).count();
		} catch (IOException e) {
			throw new ArgsParserException("Error when reading from dir: " + this.dir);
		}
	}
}
