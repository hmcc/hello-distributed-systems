package org.problemchimp.http;

import java.net.MalformedURLException;

import javax.jmdns.ServiceInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.problemchimp.handler.IncomingHandler;
import org.problemchimp.handler.OutgoingHandler;
import org.problemchimp.jmdns.ServiceInfoUtil;
import org.problemchimp.jmdns.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * HTTP endpoint for the application.
 */
@Service
@Path("/")
public class Endpoint {

    public static String LOCAL_PATH = "/local";

    @Autowired IncomingHandler incoming;
    @Autowired OutgoingHandler outgoing;
    @Autowired ServiceRegistry registry;
    @Autowired ServiceInfo thisService;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
	return "I'm fine!";
    }

    @GET
    @Path("/service/{service}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getService(@PathParam("service") String serviceName) throws MalformedURLException {
	ServiceInfo found = null;
	if (thisService.getName().equals(serviceName)) {
	    found = thisService;
	} else {
	    found = registry.find(serviceName);
	}
	if (found == null) {
	    return Response.noContent().build();
	} else {
	    return Response.ok().entity(ServiceInfoUtil.asMap(found)).build();
	}
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
