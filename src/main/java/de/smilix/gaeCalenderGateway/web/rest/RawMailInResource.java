package de.smilix.gaeCalenderGateway.web.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.smilix.gaeCalenderGateway.model.RawMailIn;
import de.smilix.gaeCalenderGateway.service.data.RawMailInRepository;

@Path("/rawMailIn")
public class RawMailInResource {

  @GET
  @Path("/list")
  @Produces(MediaType.APPLICATION_JSON)
  public List<RawMailIn> getList() {
    List<RawMailIn> allItems = RawMailInRepository.get().getAll();
    
    for (RawMailIn item : allItems) {
      item.chopRawMail(30);
    }
    return allItems;
  }

  @GET
  @Path("/item/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getItem(@PathParam("id") Long id) {
    RawMailIn find = RawMailInRepository.get().find(id);
    if (find == null) {
      return Response.status(Status.NOT_FOUND).entity("Nothing found for given id.").build();
    }
    return Response.ok(find).build();
  }
  
  @PUT
  @Path("/item/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response setItem(@PathParam("id") Long id, RawMailIn rawMailIn) {
    RawMailIn mail = RawMailInRepository.get().find(id);
    if (mail== null) {
      return Response.status(Status.NOT_FOUND).entity("Nothing found for given id.").build();
    }
    mail.setStatus(rawMailIn.getStatus());
    mail.setRawMail(rawMailIn.getRawMail());
    RawMailInRepository.get().merge(mail);

    return Response.ok().build();
  }
}
