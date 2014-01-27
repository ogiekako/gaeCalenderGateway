package de.smilix.gaeCalenderGateway.web.rest;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.users.UserServiceFactory;

import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;

@Path("/debug")
public class DebugResource {

  @GET
  @Path("/a")
  @Produces(MediaType.APPLICATION_JSON)
  public Response testA() throws IOException, MessagingException {
    
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    String msgBody = "...";

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("blahholgercremer@gmail.com", "Holger"));
        msg.addRecipient(Message.RecipientType.TO,
                         new InternetAddress("smile@circlelab.de", "testing"));
        msg.setSubject("Testing");
        msg.setText(msgBody);
        Transport.send(msg);

    
    return Response.ok("mail send!").build();
  }
  

  @GET
  @Path("/b")
  @Produces(MediaType.APPLICATION_JSON)
  public Response testb() throws IOException {
    
    Config config = ConfigurationService.getConfig();
    config.setSenderEmail(null);
    ConfigurationService.save(config);
    
    return Response.ok("done").build();
  }
  
}
