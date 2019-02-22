package org.problemchimp.handler;

/**
 * Default implementation of {@link OutgoingHandler} that echoes messages to all
 * connected clients.
 */
public class OutgoingSendAllHandler extends OutgoingHandlerBase<Object> {

    protected void handleMessage(Object message) {
	sendToAll(message);
    }
}
