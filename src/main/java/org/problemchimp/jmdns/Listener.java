package org.problemchimp.jmdns;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jmDNS service listener for the application which maintains a registry of
 * currently available services.
 */
public final class Listener implements ServiceListener {

    private static Logger logger = LoggerFactory.getLogger(Listener.class);

    private ServiceRegistry registry = ServiceRegistry.getInstance();
    private ServiceInfo serviceInfo;

    public Listener(ServiceInfo serviceInfo) {
	this.serviceInfo = serviceInfo;
    }
    
    protected ServiceInfo getServiceInfo() {
	return serviceInfo;
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
	ServiceInfo info = event.getInfo();
	logger.trace("Service added: " + ServiceRegistry.stringify(info));
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
	ServiceInfo info = event.getInfo();
	logger.trace("Service removed: " + ServiceRegistry.stringify(info));
	if (info.equals(serviceInfo)) {
	    logger.debug("Service is this service: " + info.getName());
	} else {
	    logger.info("Removing service: " + ServiceRegistry.stringify(info));
	    registry.remove(info);
	}
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
	ServiceInfo info = event.getInfo();
	logger.trace("Service resolved: " + ServiceRegistry.stringify(info));
	if (info.equals(serviceInfo)) {
	    logger.debug("Service is this service: " + info.getName());
	} else {
	    logger.info("Found new service: " + ServiceRegistry.stringify(info));
	    registry.add(info);
	}
    }
}
