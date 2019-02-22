package org.problemchimp.handler;

/**
 * Handler interface for incoming messages.
 * 
 * The {@link org.problemchimp.http.Endpoint} class expects an instance of this
 * interface which it can use to add incoming messages to a queue.
 * Implementations of the interface should override the run() method to process
 * the queued messages.
 */
public interface IncomingHandler<T> extends Runnable {

    public void add(T object);
}
