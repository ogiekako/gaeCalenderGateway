package de.smilix.gaeCalenderGateway.web.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.users.UserServiceFactory;

import de.smilix.gaeCalenderGateway.common.Utils;
import de.smilix.gaeCalenderGateway.common.Version;
import de.smilix.gaeCalenderGateway.model.CalendarInfo;
import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.model.IcalInfo;
import de.smilix.gaeCalenderGateway.model.RawMailIn.Status;
import de.smilix.gaeCalenderGateway.service.AuthService;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;
import de.smilix.gaeCalenderGateway.service.data.ICalInfoRepository;
import de.smilix.gaeCalenderGateway.service.data.RawMailInRepository;
import de.smilix.gaeCalenderGateway.service.gcal.GoogleCalService;

@Path("/base")
public class BaseResource {

  @GET
  @Path("/stats")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> getStats() {
    Config config = ConfigurationService.getConfig();
    
    Map<String, Object> response = new HashMap<>();
    response.put("rawMailIn_errors", RawMailInRepository.get().getCountForStatus(Status.ERROR));
    response.put("rawMailIn_incoming", RawMailInRepository.get().getCountForStatus(Status.INCOMING));
    response.put("iCalInfo_add_errors", ICalInfoRepository.get().getCountForStatus(IcalInfo.Status.CAL_ERROR));
    response.put("iCalInfo_parsed", ICalInfoRepository.get().getCountForStatus(IcalInfo.Status.PARSED));
    response.put("user_selected", !Utils.isEmpty(config.getUserId()));
    response.put("calendar_selected", !Utils.isEmpty(config.getCalendarId()));
    
    return response;
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
    response.put("version", Version.CURRENT);
    
    return response;
  }
  
}