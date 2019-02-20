package org.problemchimp.jmdns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.jmdns.ServiceInfo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.problemchimp.App;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

/**
 * Unit tests for {@link JmDNSConfig}.
 *
 */
public class JmDNSConfigTest {
    
    private String[] emptyArgs = {};
    
    @BeforeClass
    public static void setUp() throws IOException {
	System.setProperty(App.PORT_PROPERTY, Integer.toString(App.DEFAULT_PORT));
    }

    @Test
    public void testGetServiceName_default() {
	ApplicationArguments appArgs = new DefaultApplicationArguments(emptyArgs);
	ServiceInfo info = new JmDNSConfig().thisService(appArgs);
	assertNotNull(info.getName());
    }

    @Test
    public void testGetServiceName_valid() {
	String serviceName = "Alice";
	String[] args = { "--serviceName=" + serviceName };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	ServiceInfo info = new JmDNSConfig().thisService(appArgs);
	assertEquals(serviceName, info.getName());
    }
    
    @AfterClass
    public static void tearDownClass() throws IOException {
	System.clearProperty(App.PORT_PROPERTY);
    }
}
