package org.problemchimp.jmdns;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.impl.JmDNSImpl;
import javax.jmdns.impl.ServiceEventImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.problemchimp.App;

/**
 * Unit tests for {@link Listener}.
 */
public class ListenerTest {
    
    public static final String SERVICE_NAME = "test";
    
    private JmDNS jmdns;
    private ServiceInfo info;
    private ServiceRegistry registry = ServiceRegistry.getInstance();
    
    @Before
    public void setUp() throws IOException {
	jmdns = JmDNS.create(InetAddress.getLocalHost());
	info = ServiceInfo.create(App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT, "");
    }
    
    @Test
    public void testConstructor_valid() throws IOException {
	Listener listener = new Listener(info);
	assertEquals(SERVICE_NAME, listener.getServiceInfo().getName());
	assertEquals(App.SERVICE_TYPE, listener.getServiceInfo().getType());
	assertEquals(App.DEFAULT_PORT, listener.getServiceInfo().getPort());
    }
    
    @Test
    public void testServiceResolved_valid() throws IOException {
	// Create a listener with the usual name, and a service event with a different one
	Listener listener = new Listener(info);
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
	// Create a listener with the usual name, and a service event with the same info
	Listener listener = new Listener(info);
	ServiceEvent event = new ServiceEventImpl((JmDNSImpl) jmdns, null, null, listener.getServiceInfo());
	
	// The new info is not added to the registry
	listener.serviceResolved(event);
	assertEquals(0, registry.size());
    }
    
    /**
     * Resolving the same service twice will only add it to the registry once
     * @throws IOException
     */
    @Test
    public void testServiceResolved_twice() throws IOException {
	// Create a listener with the usual name, and a service event with a different one
	Listener listener = new Listener(info);
	ServiceInfo info2 = ServiceInfo.create(App.SERVICE_TYPE, SERVICE_NAME + 2, App.DEFAULT_PORT, "");
	ServiceEvent event = new ServiceEventImpl((JmDNSImpl) jmdns, null, null, info2);
	
	// The new info is added to the registry only once
	listener.serviceResolved(event);
	listener.serviceResolved(event);
	assertEquals(1, registry.size());
    }
    
    @Test
    public void testServiceRemoved_valid() throws IOException {
	// Create a listener with the usual name, and a service event with a different one
	Listener listener = new Listener(info);
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
	// Create a listener with the usual name, and a service event with the same info
	Listener listener = new Listener(info);
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
	// Create a listener with the usual name, and a service event with a different one
	Listener listener = new Listener(info);
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
}
