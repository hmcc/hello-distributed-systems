package org.problemchimp.jmdns;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Wrapper around {@link JmDNS} which takes care of instantiation and ensures
 * there is only a single instance running.
 */
public final class JmDNSWrapper {

    private static Logger logger = LoggerFactory.getLogger(JmDNSWrapper.class);

    private static JmDNS jmdns;
    private @Autowired Listener listener;
    private @Autowired InetAddress thisAddress;
    private @Autowired ServiceInfo thisService;

    public void init() throws IOException {
	if (jmdns == null) {
	    jmdns = JmDNS.create(thisAddress);
	    jmdns.registerService(thisService);
	    logger.info("Registered service " + ServiceRegistry.stringify(thisService));
	    jmdns.addServiceListener(thisService.getType(), listener);
	}
    }

    public JmDNS getJmDNS() {
	return jmdns;
    }

    public Listener getListener() {
	return listener;
    }

    public void shutdown() {
	logger.debug("Shutting down jmDNS");
	if (jmdns != null) {
	    jmdns.removeServiceListener(listener.getServiceInfo().getType(), listener);
	    jmdns.unregisterAllServices();
	    try {
		jmdns.close();
	    } catch (IOException e) {
		logger.info("Error closing JmDNS: " + e);
	    }
	    jmdns = null;
	}
    }
}
