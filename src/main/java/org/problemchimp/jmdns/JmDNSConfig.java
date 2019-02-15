package org.problemchimp.jmdns;

import java.net.InetAddress;
import java.util.UUID;

import javax.jmdns.ServiceInfo;

import org.problemchimp.App;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean definitions for JmDNS
 */
@Configuration
public class JmDNSConfig {
    
    public static final String HOSTNAME = "localhost";
    public static final String SERVICE_TYPE = "_hello-distributed-systems._tcp.local.";
    
    @Bean
    public InetAddress thisAddress() {
	try {
	    return InetAddress.getByName(HOSTNAME);
	} catch (Exception e) {
	    throw new IllegalStateException("Hostname " + HOSTNAME + " is not valid!", e);
	}
    }
    
    @Bean
    public ServiceInfo thisService(ApplicationArguments appArgs) {
	String serviceName = App.getOrDefault(appArgs, "serviceName", UUID.randomUUID().toString());
	int port;
	try {
	    port = Integer.parseInt(System.getProperty(App.PORT_PROPERTY));
	} catch (Exception e) {
	    throw new IllegalStateException("System property " + App.PORT_PROPERTY + " not set!", e);
	}
	return ServiceInfo.create(SERVICE_TYPE, serviceName, port, "");
    }
    
    @Bean
    public JmDNSWrapper jmdns() {
	return new JmDNSWrapper();
    }
    
    @Bean
    public Listener listener() {
	return new Listener();
    }
    
    @Bean
    public ServiceRegistry registry() {
	return new ServiceRegistry();
    }
}
