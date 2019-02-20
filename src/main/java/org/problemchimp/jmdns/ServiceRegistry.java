package org.problemchimp.jmdns;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.ServiceInfo;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Registry of currently available services.
 */
public final class ServiceRegistry {

    private Set<ServiceInfo> services = ConcurrentHashMap.newKeySet();
    @Autowired ServiceInfo thisService;

    public boolean add(ServiceInfo service) {
	return services.add(service);
    }

    public void clear() {
	services.clear();
    }
    
    public ServiceInfo find(String serviceName) {
	if (thisService.getName().equals(serviceName)) {
	    return thisService;
	}
	for (ServiceInfo info : services) {
	    if (info != null && info.getName().equals(serviceName)) {
		return info;
	    }
	}
	return null;
    }
    
    public Iterator<ServiceInfo> iterator() {
	return services.iterator();
    }
    
    public boolean remove(ServiceInfo service) {
	return services.remove(service);
    }

    public int size() {
	return services.size();
    }
}
