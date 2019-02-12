package org.problemchimp.http;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * Jersey config for the HTTP endpoint.
 */
@Component
public class Config extends ResourceConfig {

    public Config() {
	registerEndpoints();
    }
    
    protected void registerEndpoints() {
	register(Endpoint.class);
    }
}
