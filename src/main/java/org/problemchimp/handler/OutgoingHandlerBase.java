package org.problemchimp.handler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.problemchimp.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of {@link OutgoingHandler} which polls the queue and
 * provides an overridable {@code handleMessage} method.
 */
public abstract class OutgoingHandlerBase implements OutgoingHandler {

    private static final Logger logger = LoggerFactory.getLogger(OutgoingHandlerBase.class);

    private Queue<Object> outgoing = new ConcurrentLinkedQueue<>();

    protected abstract void handleMessage(Object message);

    /**
     * Read from the queue.
     */
    private void sendAll() {
	Object message;
	while ((message = outgoing.poll()) != null) {
	    handleMessage(message);
	}
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
    public void add(Object message) {
	outgoing.add(message);
    }
}
