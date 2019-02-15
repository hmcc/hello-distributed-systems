package org.problemchimp.jmdns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import javax.jmdns.ServiceInfo;

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
public class ServiceRegistryTest {

    public static final String SERVICE_NAME = "test";

    private @Autowired ServiceRegistry registry;

    @BeforeClass
    public static void setUp() throws IOException {
	System.setProperty(App.PORT_PROPERTY, Integer.toString(App.DEFAULT_PORT));
    }

    @Test
    public void testFind_empty() {
	assertNull(registry.find(SERVICE_NAME));
    }

    @Test
    public void testFind_valid() {
	ServiceInfo info = ServiceInfo.create(App.SERVICE_TYPE, SERVICE_NAME, App.DEFAULT_PORT, "");
	registry.add(info);
	assertEquals(info, registry.find(SERVICE_NAME));
    }

    @Test
    public void testFind_notFound() {
	ServiceInfo info = ServiceInfo.create(App.SERVICE_TYPE, "not found", App.DEFAULT_PORT, "");
	registry.add(info);
	assertNull(registry.find(SERVICE_NAME));
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
