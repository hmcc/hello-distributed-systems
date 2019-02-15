package org.problemchimp.jmdns;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Wrapper around {@link JmDNS} which takes care of instantiation and ensures
 * there is only a single instance running.
 */
public final class JmDNSWrapper {

    private static JmDNSWrapper instance;
    private static Logger logger = LoggerFactory.getLogger(JmDNSWrapper.class);

    private JmDNS jmdns;
    private Listener listener;

    private JmDNSWrapper(InetAddress address, ServiceInfo serviceInfo) throws IOException {
	jmdns = JmDNS.create(address);
	jmdns.registerService(serviceInfo);
	
	logger.info("Registered service " + ServiceRegistry.stringify(serviceInfo));

	listener = new Listener(serviceInfo);
	jmdns.addServiceListener(serviceInfo.getType(), listener);
    }

    public static JmDNSWrapper getInstance(String host, String serviceType, String serviceName, int port)
	    throws IOException {
	checkArguments(host, serviceType, serviceName, port);
	InetAddress address = InetAddress.getByName(host);
	ServiceInfo serviceInfo = ServiceInfo.create(serviceType, serviceName, port, "");
	if (instance == null) {
	    instance = new JmDNSWrapper(address, serviceInfo);
	} else {
	    instance.checkDuplicate(address, serviceInfo);
	}
	return instance;
    }
    
    private static void checkArguments(String host, String serviceType, String serviceName, int port) {
	Assert.hasLength(serviceName, "Service name is required");
	Assert.hasLength(serviceType, "Service type is required");
	Assert.hasLength(host, "Host is required");
	if (!(port > 0 && port < 65535)) {
	   throw new IllegalArgumentException("Port " + port + " is not valid"); 
	}
    }
    
    private void checkDuplicate(InetAddress address, ServiceInfo serviceInfo) throws IOException {
	if (!(jmdns.getInetAddress().equals(address))) {
	    throw new IllegalArgumentException(
		    "Instance already exists with address " + this.listener.getServiceInfo().getInetAddresses());
	}
	if (!serviceInfo.getName().equals(this.listener.getServiceInfo().getName())) {
	    throw new IllegalArgumentException(
		    "Instance already exists with service name " + this.listener.getServiceInfo().getName());
	}
	if (!serviceInfo.getType().equals(this.listener.getServiceInfo().getType())) {
	    throw new IllegalArgumentException(
		    "Instance already exists with service type " + this.listener.getServiceInfo().getType());
	}
	if (serviceInfo.getPort() != this.listener.getServiceInfo().getPort()) {
	    throw new IllegalArgumentException("Instance already exists with port " + this.listener.getServiceInfo().getPort());
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
	    instance = null;
	}
    }
}
