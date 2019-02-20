package org.problemchimp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.util.SocketUtils;

/**
 * Parses the command line args and starts the application.
 */
public class AppStarter {
    
    public static final int DEFAULT_PORT = 5000;
    public static final String PORT_PROPERTY = "server.port";
    public static final int PORT_RANGE = 10;

    private static Logger logger = LoggerFactory.getLogger(AppStarter.class);

    private ApplicationArguments args;

    protected AppStarter(String[] args) {
	// Parse the args manually.
	// We can't get at the parsed args from Spring until after the class is
	// constructed, and by that time it's too late to set the port.
	this.args = new DefaultApplicationArguments(args);
    }

    public static String getOrDefault(ApplicationArguments args, String optionName, String defaultVal) {
	if (args.getOptionNames().contains(optionName)) {
	    return args.getOptionValues(optionName).iterator().next();
	} else {
	    return defaultVal;
	}
    }

    public static int getOrDefault(ApplicationArguments args, String optionName, int defaultVal) {
	try {
	    return Integer.parseInt(getOrDefault(args, optionName, ""));
	} catch (NumberFormatException e) {
	    return defaultVal;
	}
    }
    
    private int findAvailablePort(int minPort, int maxPort) {
	int port = SocketUtils.findAvailableTcpPort(minPort, maxPort);
	logger.info("Server port set to {}.", port);
	return port;
    }

    protected int[] getPorts() {
	int minPort = getOrDefault(args, "minPort", -1);
	int maxPort = getOrDefault(args, "maxPort", -1);

	// both ports invalid
	if ((minPort < 1 || minPort > 0xffff) && (maxPort < 1 || maxPort > 0xffff)
		|| (maxPort > 1 && minPort > maxPort)) {
	    minPort = AppStarter.DEFAULT_PORT;
	    maxPort = minPort + AppStarter.PORT_RANGE;

	    // min is valid
	} else if (minPort >= 1 && minPort <= 0xffff && maxPort < 1) {
	    maxPort = minPort + AppStarter.PORT_RANGE;
	    if (maxPort > 0xFFFF) {
		maxPort = 0xFFFF;
	    }

	    // max is valid
	} else if (maxPort >= 1 && maxPort <= 0xffff && minPort < 1) {
	    minPort = maxPort - AppStarter.PORT_RANGE;
	    if (minPort < 1) {
		minPort = 1;
	    }
	}
	return new int[] { minPort, maxPort };
    }
    
    protected void start() {
	int[] ports = getPorts();

	// Find a port within range that's available and set the server.port
	// property so Spring will pick it up.
	int port = findAvailablePort(ports[0], ports[1]);
	System.setProperty(PORT_PROPERTY, String.valueOf(port));

	SpringApplication.run(App.class, args.getSourceArgs());
    }

    public static void main(String[] args) {
	new AppStarter(args).start();
    }
}
