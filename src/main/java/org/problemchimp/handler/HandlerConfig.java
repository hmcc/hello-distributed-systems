package org.problemchimp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * Configuration class for message handlers.
 * Provides 2 default handlers:
 * 1. An incoming handler that echos everything it receives to stdout
 * 2. An outgoing handler that sends everything to all other connected instances
 */
@Configuration
public class HandlerConfig {

    private static final Logger logger = LoggerFactory.getLogger(HandlerConfig.class);

    @Bean
    @ConditionalOnProperty(name = "org.problemchimp.handler", havingValue = "default", matchIfMissing = true)
    public IncomingHandler<?> defaultIncomingHandler() {
	IncomingHandler<?> toReturn = new IncomingEchoHandler();
	logger.debug("org.problemchimp.handler not set, defaulting to " + toReturn.getClass());
	return toReturn;
    }

    @Bean
    @ConditionalOnProperty(name = "org.problemchimp.handler", havingValue = "default", matchIfMissing = true)
    public OutgoingHandler<?> defaultOutgoingHandler() {
	OutgoingHandler<?> toReturn = new OutgoingSendAllHandler();
	logger.debug("org.problemchimp.handler not set, defaulting to " + toReturn.getClass());
	return toReturn;
    }

    @Bean
    public CommandLineRunner schedulingRunner(TaskExecutor executor, IncomingHandler<?> incoming,
	    OutgoingHandler<?> outgoing) {
	return new CommandLineRunner() {
	    public void run(String... args) throws Exception {
		executor.execute(incoming);
		executor.execute(outgoing);
	    }
	};
    }

    @Bean
    public TaskExecutor taskExecutor() {
	return new SimpleAsyncTaskExecutor();
    }
}
