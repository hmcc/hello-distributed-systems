package org.problemchimp.handler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.problemchimp.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of {@link IncomingHandler} which polls the queue and
 * provides an overridable {@code handleMessage} method.
 */
public abstract class IncomingHandlerBase<T> implements IncomingHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(IncomingHandlerBase.class);

    private Queue<T> incoming = new ConcurrentLinkedQueue<>();
    
    protected abstract void handleMessage(T message);

    /**
     * Read from the queue.
     */
    protected void readAll() {
	T message;
	while ((message = incoming.poll()) != null) {
	    logger.debug("Handling message " + message);
	    handleMessage(message);
	}
    }

    @Override
    public void run() {
	logger.info("Running " + this.getClass().getCanonicalName());
	try {
	    while (true) {
		readAll();
		Thread.sleep(App.SPIN_SPEED);
	    }
	} catch (InterruptedException e) {
	    logger.debug("Interrupted: all done");
	    return;
	}
    }

    @Override
    public void add(T message) {
	incoming.add(message);
    }
}
