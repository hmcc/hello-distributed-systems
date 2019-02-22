package org.problemchimp.jmdns;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jmdns.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for working with {@link ServiceInfo}.
 */
public final class ServiceInfoUtil {

    private static final Logger logger = LoggerFactory.getLogger(ServiceInfoUtil.class);

    private ServiceInfoUtil() {

    }

    /**
     * Passing a {@link ServiceInfo} to an ObjectMapper directly doesn't work
     * because some getXXX methods can throw exceptions; use this instead.
     *
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

    public static Iterator<URL> getURLs(ServiceInfo info, String path) {
	return new Iterator<URL>() {
	    private int currentIndex = 0;

	    @Override
	    public boolean hasNext() {
		return currentIndex < info.getInetAddresses().length;
	    }

	    @Override
	    public URL next() {
		if (!(hasNext())) {
		    throw new NoSuchElementException();
		}
		try {
		    return new URL("http", info.getInetAddresses()[currentIndex++].getHostName(), info.getPort(), path);
		} catch (MalformedURLException e) {
		    logger.warn(e.toString());
		    return next();
		}
	    }
	};
    }

    public static Iterator<URL> getURLs(ServiceInfo info) {
	return getURLs(info, "/");
    }

    public static URL getURL(ServiceInfo info) throws MalformedURLException {
	return getURLs(info).next();
    }
}
