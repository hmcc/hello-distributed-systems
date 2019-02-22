package org.problemchimp.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for HTTP endpoints. Provides a default endpoint that
 * supports PUT messages, GET health and GET service information.
 */
@Configuration
public class HttpConfig {

    private static final Logger logger = LoggerFactory.getLogger(HttpConfig.class);

    @Bean
    @ConditionalOnProperty(name = "org.problemchimp.handler", havingValue = "default", matchIfMissing = true)
    public Endpoint<?> defaultEndpoint() {
	Endpoint<?> toReturn = new HelloEndpoint();
	logger.debug("org.problemchimp.handler not set, defaulting to " + toReturn.getClass());
	return toReturn;
    }
}
