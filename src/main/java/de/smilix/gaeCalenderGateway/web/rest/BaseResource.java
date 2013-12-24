package de.smilix.gaeCalenderGateway.web.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.users.UserServiceFactory;

import de.smilix.gaeCalenderGateway.model.CalendarInfo;
import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.service.AuthService;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;
import de.smilix.gaeCalenderGateway.service.gcal.GoogleCalService;

@Path("/base")
public class BaseResource {

  @GET
  @Path("/config")
  @Produces(MediaType.APPLICATION_JSON)
  public Config getConfig() {
    return ConfigurationService.getConfig();
  }
  
  @PUT
  @Path("/config/calendar")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void setCalendarId(CalendarIdParam param) {
    ConfigurationService.getConfig().setCalendarId(param.getId());
  }

  @GET
  @Path("/calendars")
  @Produces(MediaType.APPLICATION_JSON)
  public List<CalendarInfo> getCalenders() throws IOException {
    return GoogleCalService.get().getAllCalenders();
  }
  
  @GET
  @Path("/credentials")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> credentialStatus() throws IOException {
    boolean hasClientCredentials = AuthService.get().hasClientCredentials();
    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();

    Map<String, Object> response = new HashMap<>();
    response.put("valid", hasClientCredentials);
    response.put("currentUserId", userId);
    
    return response;
  }
  
  
  public static class CalendarIdParam {
    private String id;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }
}