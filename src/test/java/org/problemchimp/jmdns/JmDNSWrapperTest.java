package org.problemchimp.jmdns;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Test;
import org.problemchimp.App;

/**
 * Unit tests for {@link Listener}.
 */
public class JmDNSWrapperTest {

    public static final String SERVICE_NAME = "test";

    @Test(expected = IllegalArgumentException.class)
    public void testGetInstance_nullHost() throws IOException {
	JmDNSWrapper.getInstance(null, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInstance_nullServiceType() throws IOException {
	JmDNSWrapper.getInstance(App.HOSTNAME, null, SERVICE_NAME, App.DEFAULT_PORT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInstance_nullServiceName() throws IOException {
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, null, App.DEFAULT_PORT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetInstance_invalidPort() throws IOException {
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, -1);
    }

    @Test
    public void testGetInstance_valid() throws IOException {
	JmDNSWrapper jmdns = JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
	assertEquals(App.HOSTNAME, jmdns.getJmDNS().getInetAddress().getHostName());
    }
    
    /**
     * A second call to getInstance() with a different hostname is not allowed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstance_twice_differentHost() throws IOException {
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
	JmDNSWrapper.getInstance("www.google.com", App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
    }

    /**
     * A second call to getInstance() with a different type is not allowed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstance_twice_differentType() throws IOException {
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE + 2, SERVICE_NAME, App.DEFAULT_PORT);
    }

    /**
     * A second call to getInstance() with a different name is not allowed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstance_twice_differentName() throws IOException {
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME + 2, App.DEFAULT_PORT);
    }

    /**
     * A second call to getInstance() with a different port is not allowed.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstance_twice_differentPort() throws IOException {
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
	JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT + 2);
    }

    /**
     * Duplicate calls to getInstance() should return the same object.
     */
    @Test
    public void testGetInstance_twice() throws IOException {
	JmDNSWrapper instance1 = JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
	JmDNSWrapper instance2 = JmDNSWrapper.getInstance(App.HOSTNAME, App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT);
	assertEquals(instance1, instance2);
    }

    @After
    public void tearDown() {
	try {
	    Field instanceField = JmDNSWrapper.class.getDeclaredField("instance");
	    instanceField.setAccessible(true);
	    instanceField.set(null, null);
	} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
	    e.printStackTrace();
	}
    }
}
