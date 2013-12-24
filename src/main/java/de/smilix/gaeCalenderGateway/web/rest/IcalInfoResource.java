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

import de.smilix.gaeCalenderGateway.model.IcalInfo;
import de.smilix.gaeCalenderGateway.service.data.ICalInfoRepository;

@Path("/ical")
public class IcalInfoResource {

  @GET
  @Path("/list")
  @Produces(MediaType.APPLICATION_JSON)
  public List<IcalInfo> getList() {
    List<IcalInfo> list = ICalInfoRepository.get().getAll();
    for (IcalInfo icalInfo : list) {
      icalInfo.chopDescription(30);
    }
    return list;
  }

  @GET
  @Path("/item/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getItem(@PathParam("id") Long id) {
    IcalInfo find = ICalInfoRepository.get().find(id);
    if (find == null) {
      return Response.status(Status.NOT_FOUND).entity("Nothing found for given id.").build();
    }
    return Response.ok(find).build();
  }
  
  
  @PUT
  @Path("/item/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response setItem(@PathParam("id") Long id, IcalInfo icalInfo) {
    IcalInfo info = ICalInfoRepository.get().find(id);
    if (info == null) {
      return Response.status(Status.NOT_FOUND).entity("Nothing found for given id.").build();
    }

    info.setuId(icalInfo.getuId());
    info.setSummary(icalInfo.getSummary());
    info.setStartTimestamp(icalInfo.getStartTimestamp());
    info.setEndTimestamp(icalInfo.getEndTimestamp());
    info.setLocation(icalInfo.getLocation());
    info.setAttendees(icalInfo.getAttendees());
    info.setDescription(icalInfo.getDescription());
    info.setStatus(icalInfo.getStatus());
    
    ICalInfoRepository.get().merge(info);
    
    return Response.ok().build();
  }

}
