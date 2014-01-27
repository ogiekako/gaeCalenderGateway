package de.smilix.gaeCalenderGateway.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;
import de.smilix.gaeCalenderGateway.service.mail.SendMail;

@Path("/config")
public class ConfigRessource {

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Config getConfig() {
    return ConfigurationService.getConfig();
  }

  @PUT
  @Path("/calendar")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void setCalendarId(CalendarIdParam param) {
    Config config = ConfigurationService.getConfig();
    config.setCalendarId(param.getId());
    ConfigurationService.save(config);
  }

  @PUT
  @Path("/contactEmail")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public void setContactEmail(ContactMailParam param) {
    Config config = ConfigurationService.getConfig();
    config.setContactEmail(param.getContactEmail());
    ConfigurationService.save(config);
  }

  @GET
  @Path("/testMail")
  @Produces(MediaType.APPLICATION_JSON)
  public Response testMailConfiguration() {
    new SendMail().sendErrorMail("A test mail. If you can read this email body, your server can send emails to you.");
    return Response.ok().build();
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

  public static class ContactMailParam {
    private String contactEmail;

    public String getContactEmail() {
      return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
      this.contactEmail = contactEmail;
    }
  }
}
