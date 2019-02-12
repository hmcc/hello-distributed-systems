package org.problemchimp.handler;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Unit tests for {@link IncomingEchoHandler}.
 */
public class IncomingEchoHandlerTest {
    
    private IncomingEchoHandler handler = new IncomingEchoHandler();

    @Test
    public void testHandleMessage_null() {
	handler.handleMessage(null);
    }

    @Test
    public void testHandleMessage_blank() {
	handler.handleMessage("");
    }
    
    @Test
    public void testHandleMessage_valid() {
	Map<String, String> hello = new HashMap<>();
	hello.put("hello", "world");
	handler.handleMessage(hello);
    }
    
    @Test
    public void testHandleMessage_invalid() {
	handler.handleMessage("this is not json");
    }
}
