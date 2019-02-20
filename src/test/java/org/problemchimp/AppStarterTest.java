package org.problemchimp;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link App}.
 */
public class AppStarterTest {

    private String[] emptyArgs = {};

    @Before
    public void setUp() {
	System.setProperty(App.PORT_PROPERTY, "5000");
    }

    @Test
    public void testGetPorts_defaultBoth() {
	int ports[] = new AppStarter(emptyArgs).getPorts();
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_bothSmall() {
	String[] args = { "--minPort=" + Integer.toString(0), "--maxPort=" + Integer.toString(0) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_bothBig() {
	String[] args = { "--minPort=" + Integer.toString(0x10000), "--maxPort=" + Integer.toString(0x10000) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_oneBigOneSmall() {
	String[] args = { "--minPort=" + Integer.toString(0), "--maxPort=" + Integer.toString(0x10000) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_minPortValid() {
	int minPort = 1234;
	String[] args = { "--minPort=" + Integer.toString(minPort) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(minPort, ports[0]);
	assertEquals(minPort + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_minPortValidButBig() {
	for (int minPort = 0xffff; minPort >= 0xffff - 10; minPort--) {
	    String[] args = { "--minPort=" + Integer.toString(minPort) };
	    int ports[] = new AppStarter(args).getPorts();
	    assertEquals(minPort, ports[0]);
	    assertEquals(0xffff, ports[1]);
	}
	int minPort = 0xffff - 11;
	String[] args = { "--minPort=" + Integer.toString(minPort) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(minPort, ports[0]);
	assertEquals(0xffff - 1, ports[1]);
    }

    @Test
    public void testGetPorts_minPortInvalid() {
	String[] args = { "--minPort=invalid" };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_maxPortValid() {
	int maxPort = 1234;
	String[] args = { "--maxPort=" + Integer.toString(maxPort) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(maxPort - App.PORT_RANGE, ports[0]);
	assertEquals(maxPort, ports[1]);
    }

    @Test
    public void testGetPorts_maxPortValidButSmall() {
	for (int maxPort = 1; maxPort <= 11; maxPort++) {
	    String[] args = { "--maxPort=" + Integer.toString(maxPort) };
	    int ports[] = new AppStarter(args).getPorts();
	    assertEquals(1, ports[0]);
	    assertEquals(maxPort, ports[1]);
	}
	int maxPort = 12;
	String[] args = { "--maxPort=" + Integer.toString(maxPort) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(2, ports[0]);
	assertEquals(maxPort, ports[1]);
    }

    @Test
    public void testGetPorts_maxPortInvalid() {
	String[] args = { "--maxPort=invalid" };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }

    @Test
    public void testGetPorts_bothPortsValid() {
	int minPort = 1;
	int maxPort = 1;
	String[] args = { "--minPort=" + Integer.toString(minPort), "--maxPort=" + Integer.toString(maxPort) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(minPort, ports[0]);
	assertEquals(maxPort, ports[1]);
    }

    @Test
    public void testGetPorts_wrongWayRound() {
	int minPort = 4010;
	int maxPort = 4000;
	String[] args = { "--minPort=" + Integer.toString(minPort), "--maxPort=" + Integer.toString(maxPort) };
	int ports[] = new AppStarter(args).getPorts();
	assertEquals(App.DEFAULT_PORT, ports[0]);
	assertEquals(App.DEFAULT_PORT + App.PORT_RANGE, ports[1]);
    }
}
