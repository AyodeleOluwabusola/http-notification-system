package com.seamfix.bioweb.microservices.notification.rest;

import com.seamfix.bioweb.microservices.notification.listener.message.MessageBody;
import com.seamfix.bioweb.microservices.notification.listener.jms.Producer;
import com.seamfix.bioweb.microservices.notification.payload.PublishMessageResponse;
import com.seamfix.bioweb.microservices.notification.payload.SubscribeRequest;
import com.seamfix.bioweb.microservices.notification.payload.SubscribeResponse;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;

@Path("/")
public class Endpoints {

    private static final Logger log = Logger.getLogger(Endpoints.class.getName());

    @Context UriInfo uriInfo;

    @PostConstruct
    public void init() {
        log.info(new StringBuilder("request URI: ").append(uriInfo.getRequestUri()).toString());
    }

    @POST
    @PermitAll
    @Path("/ping")
    @Produces("text/plain")
    public Response doGet() {
        return Response.ok("Services is Up!").build();
    }

    @POST
    @PermitAll
    @Path("/subscribe/{topic}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSub(@PathParam(value = "topic") String topic, SubscribeRequest request ) {
        Producer producer = new Producer(request, topic);
        SubscribeResponse responseData = producer.createSubscription();
        return Response.status(Response.Status.OK).entity(responseData).build();
    }

    @POST
    @PermitAll
    @Path("/publish/{topic}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publishMessage(@PathParam(value = "topic") String topic, MessageBody message) {
        Producer producer = new Producer(topic);
        PublishMessageResponse responseData = producer.sendMessageToSubscribers(message);
        return Response.status(Response.Status.OK).entity(responseData).build();
    }
}
