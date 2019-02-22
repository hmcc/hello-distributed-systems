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

/**
 * HTTP endpoint for the application.
 */
@Path("/")
public abstract class Endpoint<T> {

    public static String LOCAL_PATH = "/local";

    @Autowired IncomingHandler<T> incoming;
    @Autowired OutgoingHandler<T> outgoing;
    @Autowired ServiceRegistry registry;

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
	ServiceInfo info = registry.find(serviceName);
	if (info == null) {
	    return Response.noContent().build();
	} else {
	    return Response.ok().entity(ServiceInfoUtil.asMap(info)).build();
	}
    }

    @PUT
    @Path("/local")
    @Consumes(MediaType.APPLICATION_JSON)
    public void receive(T message) {
	incoming.add(message);
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public void send(T message) {
	outgoing.add(message);
    }
}
