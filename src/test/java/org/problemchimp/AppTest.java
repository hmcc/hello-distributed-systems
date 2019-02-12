package org.problemchimp;

import org.junit.Test;

/**
 * Unit tests for {@link App}.
 */
public class AppTest {

    @Test
    public void testGetPort_default() {
	assert App.getPort(new String[] {}) == App.DEFAULT_PORT;
    }

    @Test
    public void testGetPort_valid() {
	int port = 1234;
	assert App.getPort(new String[] { Integer.toString(port) }) == port;
    }

    @Test
    public void testGetPort_invalid() {
	assert App.getPort(new String[] { "not valid" }) == App.DEFAULT_PORT;
    }
}
