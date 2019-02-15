package org.problemchimp.jmdns;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jmdns.ServiceInfo;

/**
 * Registry of currently available services.
 */
public final class ServiceRegistry {

    private Set<ServiceInfo> services = ConcurrentHashMap.newKeySet();

    public boolean add(ServiceInfo service) {
	return services.add(service);
    }

    public ServiceInfo lookup(String serviceName) {
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

    public void clear() {
	services.clear();
    }

    public int size() {
	return services.size();
    }

    public static String stringify(ServiceInfo info) {
	if (info == null) {
	    return null;
	}
	final StringBuilder sb = new StringBuilder();
	sb.append(info.getQualifiedName());
	InetAddress[] addresses = info.getInetAddresses();
	for (InetAddress address : addresses) {
	    if (address != null) {
		sb.append(" ").append(address).append(':').append(info.getPort());
	    }
	}
	return sb.toString();
    }
}
