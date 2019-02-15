package org.problemchimp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;

/**
 * Unit tests for {@link App}.
 */
public class AppTest {

    private String[] emptyArgs = {};

    @Before
    public void setUp() {
	System.setProperty(App.PORT_PROPERTY, "5000");
    }

    @Test
    public void testGetServiceName_default() {
	ApplicationArguments appArgs = new DefaultApplicationArguments(emptyArgs);
	assertNotNull(App.getServiceName(appArgs));
    }

    @Test
    public void testGetServiceName_valid() {
	String serviceName = "Alice";
	String[] args = { "--serviceName=" + serviceName };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	assertEquals(serviceName, App.getServiceName(appArgs));
    }

    @Test
    public void testGetPorts_defaultBoth() {
	ApplicationArguments appArgs = new DefaultApplicationArguments(emptyArgs);
	int ports[] = App.getPorts(appArgs);
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_minPortValid() {
	int minPort = 1234;
	String[] args = { "--minPort=" + Integer.toString(minPort) };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	int ports[] = App.getPorts(appArgs);
	assertEquals(minPort, ports[0]);
	assertEquals(minPort + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_minPortInvalid() {
	String[] args = { "--minPort=invalid" };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	int ports[] = App.getPorts(appArgs);
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_maxPortValid() {
	int maxPort = 1234;
	String[] args = { "--maxPort=" + Integer.toString(maxPort) };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	int ports[] = App.getPorts(appArgs);
	assertEquals(maxPort - App.PORT_RANGE, ports[0]);
	assertEquals(maxPort, ports[1]);
    }

    @Test
    public void testGetPorts_maxPortValidButSmall() {
	for (int maxPort = 1; maxPort <= 11; maxPort++) {
	    String[] args = { "--maxPort=" + Integer.toString(maxPort) };
	    ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	    int ports[] = App.getPorts(appArgs);
	    assertEquals(1, ports[0]);
	    assertEquals(maxPort, ports[1]);
	}
	int maxPort = 12;
	String[] args = { "--maxPort=" + Integer.toString(maxPort) };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	int ports[] = App.getPorts(appArgs);
	assertEquals(2, ports[0]);
	assertEquals(maxPort, ports[1]);
    }

    @Test
    public void testGetPorts_maxPortInvalid() {
	String[] args = { "--maxPort=invalid" };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	int ports[] = App.getPorts(appArgs);
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_bothPortsValid() {
	int minPort = 1;
	int maxPort = 1;
	String[] args = { "--minPort=" + Integer.toString(minPort), "--maxPort=" + Integer.toString(maxPort) };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	int ports[] = App.getPorts(appArgs);
	assertEquals(minPort, ports[0]);
	assertEquals(maxPort, ports[1]);
    }

    @Test
    public void testGetPorts_wrongWayRound() {
	int minPort = 4010;
	int maxPort = 4000;
	String[] args = { "--minPort=" + Integer.toString(minPort), "--maxPort=" + Integer.toString(maxPort) };
	ApplicationArguments appArgs = new DefaultApplicationArguments(args);
	int ports[] = App.getPorts(appArgs);
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }
}
