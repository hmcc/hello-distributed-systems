package org.problemchimp;

import java.util.UUID;

import javax.annotation.PreDestroy;

import org.problemchimp.jmdns.JmDNSWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.util.SocketUtils;

/**
 * Skeleton application for distributed systems using Spring Boot and Jersey.
 */
@SpringBootApplication
public class App extends SpringBootServletInitializer {

    public static final int DEFAULT_PORT = 5000;
    public static final String HOSTNAME = "localhost";
    public static final String PORT_PROPERTY = "server.port";
    public static final int PORT_RANGE = 10;
    public static final String SERVICE_TYPE = "_hello-distributed-systems._tcp.local.";
    public static final long SPIN_SPEED = 0;

    private static Logger logger = LoggerFactory.getLogger(App.class);

    private @Autowired JmDNSWrapper jmdns;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
	logger.debug("Initialising jmDNS");
	try {
	    jmdns.init();

	} catch (Exception e) {
	    logger.error(e.toString(), e);
	    destroy();
	}
    }

    @PreDestroy
    public void destroy() {
	if (jmdns != null) {
	    jmdns.shutdown();
	}
    }

    private static int findAvailablePort(int minPort, int maxPort) {
	int port = SocketUtils.findAvailableTcpPort(minPort, maxPort);
	logger.info("Server port set to {}.", port);
	return port;
    }

    public static String getOrDefault(ApplicationArguments args, String optionName, String defaultVal) {
	if (args.getOptionNames().contains(optionName)) {
	    return args.getOptionValues(optionName).iterator().next();
	} else {
	    return defaultVal;
	}
    }

    private static int getOrDefault(ApplicationArguments args, String optionName, int defaultVal) {
	try {
	    return Integer.parseInt(getOrDefault(args, optionName, ""));
	} catch (NumberFormatException e) {
	    return defaultVal;
	}
    }

    protected static int[] getPorts(ApplicationArguments appArgs) {
	int minPort = getOrDefault(appArgs, "minPort", -1);
	int maxPort = getOrDefault(appArgs, "maxPort", -1);

	// both ports invalid
	if ((minPort < 1 || minPort > 0xffff) && (maxPort < 1 || maxPort > 0xffff)
		|| (maxPort > 1 && minPort > maxPort)) {
	    minPort = App.DEFAULT_PORT;
	    maxPort = minPort + App.PORT_RANGE;

	    // min is valid
	} else if (minPort >= 1 && minPort <= 0xffff && maxPort < 1) {
	    maxPort = minPort + App.PORT_RANGE;
	    if (maxPort > 0xFFFF) {
		maxPort = 0xFFFF;
	    }

	    // max is valid
	} else if (maxPort >= 1 && maxPort <= 0xffff && minPort < 1) {
	    minPort = maxPort - App.PORT_RANGE;
	    if (minPort < 1) {
		minPort = 1;
	    }
	}
	return new int[] { minPort, maxPort };
    }

    protected static String getServiceName(ApplicationArguments appArgs) {
	return getOrDefault(appArgs, "serviceName", UUID.randomUUID().toString());
    }

    public static void main(String[] args) {
	// Parse the args manually.
	// We can't get at the parsed args from Spring until after the class is
	// constructed, and by that time it's too late to set the port.
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	int[] ports = getPorts(appArgs);

	// Find a port within range that's available and set the server.port
	// property so Spring will pick it up.
	int port = findAvailablePort(ports[0], ports[1]);
	System.setProperty(PORT_PROPERTY, String.valueOf(port));

	SpringApplication.run(App.class, args);
    }
}
