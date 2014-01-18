package de.smilix.gaeCalenderGateway.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import de.smilix.gaeCalenderGateway.service.gcal.GoogleCalService;

@Path("/ical")
public class IcalInfoResource {

  private static final Logger LOG = Logger.getLogger(IcalInfoResource.class.getName());

  @GET
  @Path("/list")
  @Produces(MediaType.APPLICATION_JSON)
  public List<IcalInfo> getList() {
    List<IcalInfo> list = ICalInfoRepository.get().getAll("tsCreated desc");
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
    info.setRecurrence(icalInfo.getRecurrence());

    ICalInfoRepository.get().merge(info);

    return Response.ok().build();
  }

  @PUT
  @Path("/addToCalendar")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response addToCalendar(List<Long> icalInfoIdList) {
    LOG.fine("Try to add events into Google calendar: " + icalInfoIdList);

    AddToCalResponse response = new AddToCalResponse();
    ICalInfoRepository repo = ICalInfoRepository.get();

    for (Long id : icalInfoIdList) {
      IcalInfo iCalInfos = repo.find(id);
      if (iCalInfos == null) {
        response.getErrors().put(id, "Can't find the event with id: " + id);
        continue;
      }
      try {
        GoogleCalService.get().addEvent(iCalInfos);
        iCalInfos.setStatus(IcalInfo.Status.ADD_SUCCESS);
        LOG.info("New calendar entry added: " + iCalInfos.getId());
        response.getSuccess().add(id);
      } catch (Exception e) {
        iCalInfos.setStatus(IcalInfo.Status.ADD_ERROR);
        LOG.log(Level.SEVERE, "Error during google calender connection.", e);
        response.getErrors().put(id, "Error adding to calendar: " + e.getMessage());
      } finally {
        repo.merge(iCalInfos);
      }
    }
    
    return Response.ok(response).build();
  }
  
  private static class AddToCalResponse {
    private Map<Long, String> errors = new HashMap<>();
    private List<Long> success = new ArrayList<>();

    public Map<Long, String> getErrors() {
      return errors;
    }
    public List<Long> getSuccess() {
      return success;
    }
  }

}
