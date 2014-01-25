package de.smilix.gaeCalenderGateway.web.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/debug")
public class DebugResource {

  @GET
  @Path("/a")
  @Produces(MediaType.APPLICATION_JSON)
  public Response testA() throws IOException {
    
    return Response.ok().build();
  }
  

  @GET
  @Path("/b")
  @Produces(MediaType.APPLICATION_JSON)
  public Response testb() throws IOException {
    
    return Response.ok().build();
  }
  
}
