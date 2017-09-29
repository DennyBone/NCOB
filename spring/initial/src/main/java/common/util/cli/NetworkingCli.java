package common.util.cli;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkingCli {
    private static final Logger logger = LoggerFactory.getLogger(NetworkingCli.class);
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "61616";

    private CommandLineParser commandLineParser = new DefaultParser();
    private CommandLine commandLine;
    private final String prefix;

    public NetworkingCli(String prefix, String[] args) throws ParseException {
        Options options = new Options();
        if (prefix != null) {
            this.prefix = prefix + ".";
        } else {
            this.prefix = "";
        }
        options.addOption(prefix + "targetHost", false, prefix + "target hostname");
        options.addOption(prefix + "targetPort", false, prefix + "target port number");
        options.addOption(prefix + "bindingHost", false, prefix + "binding hostname");
        options.addOption(prefix + "bindingPort", false, prefix + "binding port number");

        commandLine = commandLineParser.parse(options, args);
    }

    public String getTargetHost() {
        return getOption("targetHost", DEFAULT_HOST);
    }

    public String getTargetPort() {
        return getOption("targetPort", DEFAULT_PORT);
    }

    public String getBindingHost() {
        return getOption("bindingHost", DEFAULT_HOST);
    }

    public String getBindingPort() {
        return getOption("bindingPort", DEFAULT_PORT);
    }

    private String getOption(String optionKey, String defaultValue) {
        optionKey = prefix + optionKey;
        if (!commandLine.hasOption(optionKey)) {
            logger.warn("Using default {} {}", optionKey, defaultValue);
            return defaultValue;
        }
        return commandLine.getOptionValue(optionKey);
    }
}
