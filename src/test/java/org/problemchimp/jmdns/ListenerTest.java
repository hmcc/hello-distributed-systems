package org.problemchimp.jmdns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.impl.JmDNSImpl;
import javax.jmdns.impl.ServiceEventImpl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.problemchimp.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests for {@link Listener}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JmDNSConfig.class })
@EnableConfigurationProperties
public class ListenerTest {

    public static final String SERVICE_NAME = "test";

    private static JmDNS jmdns;
    private @Autowired Listener listener;
    private @Autowired ServiceRegistry registry;

    @BeforeClass
    public static void setUp() throws IOException {
	System.setProperty(App.PORT_PROPERTY, Integer.toString(App.DEFAULT_PORT));
	jmdns = JmDNS.create(InetAddress.getLocalHost());
    }

    @Test
    public void testConstructor_valid() throws IOException {
	assertNotNull(listener.getServiceInfo().getName());
	assertEquals(App.SERVICE_TYPE, listener.getServiceInfo().getType());
	assertEquals(App.DEFAULT_PORT, listener.getServiceInfo().getPort());
    }

    @Test
    public void testServiceResolved_valid() throws IOException {
	// Create a service event with a different one
	ServiceInfo info2 = ServiceInfo.create(App.SERVICE_TYPE, SERVICE_NAME + 2, App.DEFAULT_PORT, "");
	ServiceEvent event = new ServiceEventImpl((JmDNSImpl) jmdns, null, null, info2);

	// The new info is added to the registry
	listener.serviceResolved(event);
	assertEquals(1, registry.size());
	ServiceInfo retrievedInfo = registry.iterator().next();
	assertEquals(retrievedInfo, info2);
    }

    @Test
    public void testServiceResolved_duplicate() throws IOException {
	// Create a service event with the same info
	ServiceEvent event = new ServiceEventImpl((JmDNSImpl) jmdns, null, null, listener.getServiceInfo());

	// The new info is not added to the registry
	listener.serviceResolved(event);
	assertEquals(0, registry.size());
    }

    /**
     * Resolving the same service twice will only add it to the registry once
     * 
     * @throws IOException
     */
    @Test
    public void testServiceResolved_twice() throws IOException {
	// Create a service event with a different one
	ServiceInfo info2 = ServiceInfo.create(App.SERVICE_TYPE, SERVICE_NAME + 2, App.DEFAULT_PORT, "");
	ServiceEvent event = new ServiceEventImpl((JmDNSImpl) jmdns, null, null, info2);

	// The new info is added to the registry only once
	listener.serviceResolved(event);
	listener.serviceResolved(event);
	assertEquals(1, registry.size());
    }

    @Test
    public void testServiceRemoved_valid() throws IOException {
	// Create a service event with a different one
	ServiceInfo info2 = ServiceInfo.create(App.SERVICE_TYPE, SERVICE_NAME + 2, App.DEFAULT_PORT, "");
	ServiceEvent event = new ServiceEventImpl((JmDNSImpl) jmdns, null, null, info2);

	// Add the event to the registry manually
	registry.add(info2);
	assertEquals(1, registry.size());

	// The info is removed from the registry
	listener.serviceRemoved(event);
	assertEquals(0, registry.size());
    }

    @Test
    public void testServiceRemoved_duplicate() throws IOException {
	// Create a service event with the same info
	ServiceEvent event = new ServiceEventImpl((JmDNSImpl) jmdns, null, null, listener.getServiceInfo());

	// Add the event to the registry manually
	registry.add(listener.getServiceInfo());
	assertEquals(1, registry.size());

	// The info is not removed from the registry
	listener.serviceRemoved(event);
	assertEquals(1, registry.size());
    }

    /**
     * Removing a non-existent service does nothing
     */
    @Test
    public void testServiceRemoved_notThere() throws IOException {
	ServiceInfo info2 = ServiceInfo.create(App.SERVICE_TYPE, SERVICE_NAME + 2, App.DEFAULT_PORT, "");
	ServiceEvent event = new ServiceEventImpl((JmDNSImpl) jmdns, null, null, info2);

	// Nothing happens on removal
	assertEquals(0, registry.size());
	listener.serviceRemoved(event);
	assertEquals(0, registry.size());
    }

    @After
    public void tearDown() {
	registry.clear();
    }

    @AfterClass
    public static void tearDownClass() {
	System.clearProperty(App.PORT_PROPERTY);
    }
}
