package org.problemchimp;

import javax.annotation.PreDestroy;

import org.problemchimp.jmdns.JmDNSWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;

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
}
