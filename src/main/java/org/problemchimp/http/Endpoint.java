package org.problemchimp.http;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.problemchimp.handler.IncomingHandler;
import org.problemchimp.handler.OutgoingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HTTP endpoint for the application.
 */
@Service
@Path("/")
public class Endpoint {
    
    public static String LOCAL_PATH = "/local";
    
    @Autowired
    IncomingHandler incoming;
    
    @Autowired
    OutgoingHandler outgoing;
    
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
	return "I'm fine!";
    }
    
    @PUT
    @Path("/local")
    @Consumes(MediaType.APPLICATION_JSON)
    public void receive(Object message) {
	incoming.add(message);
    }
    
    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public void send(Object message) {
	outgoing.add(message);
    }
}
