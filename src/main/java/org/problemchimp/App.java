package org.problemchimp;

import java.util.UUID;

import javax.annotation.PreDestroy;

import org.problemchimp.jmdns.JmDNSWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
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
    public static final String SERVICE_TYPE = "_hello-distributed-systems._http._tcp.local.";
    public static final long SPIN_SPEED = 0;

    private static Logger logger = LoggerFactory.getLogger(App.class);

    private JmDNSWrapper jmdns;
    private String serviceName = UUID.randomUUID().toString();
    
    public App(String...args) {
	jmdns = null;
	try {
	    jmdns = JmDNSWrapper.getInstance(
		    HOSTNAME,
		    SERVICE_TYPE,
		    serviceName, 
		    Integer.parseInt(System.getProperty(PORT_PROPERTY)));

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

    protected static int getPort(String[] args) {
	int port = DEFAULT_PORT;
	if (args.length >= 1) {
	    try {
		port = Integer.parseInt(args[0]);
	    } catch (NumberFormatException e) {
		logger.warn("Invalid port " + args[0] + ": ignoring");
	    }
	}
	return port;
    }

    public static void main(String[] args) {
	int minPort = getPort(args);
	int port = findAvailablePort(minPort, minPort + PORT_RANGE);
	System.setProperty(PORT_PROPERTY, String.valueOf(port));
	SpringApplication.run(App.class, args);
    }
}
