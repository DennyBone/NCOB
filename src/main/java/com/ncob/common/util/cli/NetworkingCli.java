package com.ncob.common.util.cli;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class NetworkingCli {
    private static final String DEFAULT_HOST = "*"; //localhost
    private static final String DEFAULT_PORT = "61615"; //61616

    private CommandLine commandLine;
    private final String prefix;

    public NetworkingCli(String prefix, String[] args) throws ParseException {
        Options options = new Options();
        if (prefix != null) {
            this.prefix = prefix + ".";
        } else {
            this.prefix = "";
        }
        options.addOption(prefix + "host", false, prefix + "hostname");
        options.addOption(prefix + "port", false, prefix + "port number");

        CommandLineParser commandLineParser = new DefaultParser();
        commandLine = commandLineParser.parse(options, args);
    }

    public String getHost() {
        return getOption("host", DEFAULT_HOST);
    }

    public String getPort() {
        return getOption("port", DEFAULT_PORT);
    }

    private String getOption(String optionKey, String defaultValue) {
        optionKey = prefix + optionKey;
        if (!commandLine.hasOption(optionKey)) {
            log.warn("Using default {} {}", optionKey, defaultValue);
            return defaultValue;
        }
        return commandLine.getOptionValue(optionKey);
    }
}
