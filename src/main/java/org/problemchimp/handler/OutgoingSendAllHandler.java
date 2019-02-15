package org.problemchimp.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import javax.jmdns.ServiceInfo;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.problemchimp.http.Endpoint;
import org.problemchimp.jmdns.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Default implementation of {@link OutgoingHandler} that echoes messages to all
 * connected clients.
 */
public final class OutgoingSendAllHandler extends OutgoingHandlerBase {

    private static final Logger logger = LoggerFactory.getLogger(OutgoingSendAllHandler.class);

    private @Autowired ServiceRegistry registry;

    private boolean sendToAddress(String host, int port, Object message) {
	try {
	    URL url = new URL("http", host, port, Endpoint.LOCAL_PATH);
	    logger.debug("Attempting to send " + message + " to " + url);
	    Response response = ClientBuilder.newClient()
		    .target(url.toURI())
		    .request(MediaType.APPLICATION_JSON)
		    .put(Entity.json(message));
	    if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
		logger.debug("Sent " + message + " to " + url);
		return true;
	    } else {
		logger.warn(response.toString());
	    }
	    
	} catch (IOException | URISyntaxException e) {
	    logger.warn(e.toString());
	}
	return false;
    }

    protected void handleMessage(Object message) {
	Iterator<ServiceInfo> it = registry.iterator();
	while (it.hasNext()) {
	    ServiceInfo info = it.next();
	    boolean sent = false;
	    for (int i = 0; i < info.getHostAddresses().length && !sent; i++) {
		sent = sendToAddress(info.getHostAddresses()[i], info.getPort(), message);
	    }
	}
    }
}
