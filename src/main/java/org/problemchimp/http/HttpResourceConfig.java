package org.problemchimp.http;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Jersey config for the HTTP endpoint.
 */
@Component
public class HttpResourceConfig extends ResourceConfig implements InitializingBean {

    @Autowired private Endpoint<?> endpoint;

    public void afterPropertiesSet() {
	register(endpoint.getClass());
    }
}
