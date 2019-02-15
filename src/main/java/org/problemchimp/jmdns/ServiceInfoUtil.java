package org.problemchimp.jmdns;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.jmdns.ServiceInfo;

/**
 * Utility functions for working with {@link ServiceInfo}.
 */
public final class ServiceInfoUtil {

    private ServiceInfoUtil() {

    }

    /**
     * Passing a {@link ServiceInfo} to an ObjectMapper directly doesn't work
     * because some getXXX methods can throw exceptions; use this instead.
     * @param info
     * @return
     * @throws MalformedURLException 
     */
    public static Map<String, Object> asMap(ServiceInfo info) throws MalformedURLException {
	Map<String, Object> fields = new HashMap<>();
	fields.put("name", info.getName());
	fields.put("type", info.getType());
	fields.put("url", getURL(info));
	return fields;
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

    public static URL getURL(ServiceInfo info, int index, String path) throws MalformedURLException {
	return new URL("http", info.getInetAddresses()[index].getHostName(), info.getPort(), path);
    }

    public static URL getURL(ServiceInfo info) throws MalformedURLException {
	return getURL(info, 0, "/");
    }
}
