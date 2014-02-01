package de.smilix.gaeCalenderGateway.web.rest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.api.services.calendar.model.Event;

import de.smilix.gaeCalenderGateway.service.auth.AuthException;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;
import de.smilix.gaeCalenderGateway.service.gcal.GoogleCalService;

@Path("/debug")
public class DebugResource {

  @GET
  @Path("/listEvents")
  @Produces(MediaType.APPLICATION_JSON)
  public Response readEvents(@QueryParam(value = "begin") long beginTs, @QueryParam(value = "end") long endTs) throws IOException, AuthException {
    
    String calendarId = ConfigurationService.getConfig().getCalendarId();
    List<Event> allEvents = GoogleCalService.get().getAllEvents(calendarId, new Date(beginTs), new Date(endTs));
    
    return Response.ok(allEvents).build();
  }  
}
