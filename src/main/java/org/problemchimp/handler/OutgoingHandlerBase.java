package org.problemchimp.handler;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jmdns.ServiceInfo;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.problemchimp.App;
import org.problemchimp.http.Endpoint;
import org.problemchimp.jmdns.ServiceInfoUtil;
import org.problemchimp.jmdns.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base implementation of {@link OutgoingHandler} which polls the queue and
 * provides an overridable {@code handleMessage} method.
 */
public abstract class OutgoingHandlerBase<T> implements OutgoingHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(OutgoingHandlerBase.class);

    private Queue<T> outgoing = new ConcurrentLinkedQueue<>();
    private @Autowired ServiceRegistry registry;

    protected abstract void handleMessage(T message);

    /**
     * Read from the queue.
     */
    private void sendAll() {
	T message;
	while ((message = outgoing.poll()) != null) {
	    handleMessage(message);
	}
    }

    protected boolean sendToAddress(URL url, Object message) {
	try {
	    logger.debug("Attempting to send " + message + " to " + url);
	    Response response = ClientBuilder.newClient().target(url.toURI()).request(MediaType.APPLICATION_JSON)
		    .put(Entity.json(message));
	    if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
		logger.debug("Sent " + message + " to " + url);
		return true;
	    } else {
		logger.warn(response.toString());
	    }

	} catch (URISyntaxException e) {
	    logger.warn(e.toString());
	}
	return false;
    }

    protected void sendToService(ServiceInfo info, Object message) {
	boolean sent = false;
	Iterator<URL> urlIterator = ServiceInfoUtil.getURLs(info, Endpoint.LOCAL_PATH);
	while (!sent && urlIterator.hasNext()) {
	    try {
		sent = sendToAddress(urlIterator.next(), message);
	    } catch (NoSuchElementException e) {
		logger.warn(e.toString());
	    }
	}
    }

    protected void sendToAll(Object message) {
	Iterator<ServiceInfo> it = registry.iterator();
	while (it.hasNext()) {
	    ServiceInfo info = it.next();
	    sendToService(info, message);
	}
	sendToService(registry.getThisService(), message);
    }

    @Override
    public void run() {
	logger.info("Running " + this.getClass().getCanonicalName());
	try {
	    while (true) {
		sendAll();
		Thread.sleep(App.SPIN_SPEED);
	    }
	} catch (InterruptedException e) {
	    logger.debug("Interrupted: all done");
	    return;
	}
    }

    @Override
    public void add(T message) {
	outgoing.add(message);
    }
}
