package org.problemchimp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation of {@link IncomingHandler} that echoes messages to
 * stdout.
 */
public final class IncomingEchoHandler extends IncomingHandlerBase {

    private static final Logger logger = LoggerFactory.getLogger(IncomingEchoHandler.class);

    private ObjectMapper mapper = new ObjectMapper();
    
    protected void handleMessage(Object message) {
	try {
	    System.out.println(mapper.writeValueAsString(message));
	} catch (JsonProcessingException e) {
	    logger.warn(e.toString());
	    System.out.println(message);
	}
    }
}
