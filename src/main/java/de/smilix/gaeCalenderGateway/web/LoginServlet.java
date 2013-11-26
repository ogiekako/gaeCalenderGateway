package de.smilix.gaeCalenderGateway.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.appengine.api.users.UserServiceFactory;

import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.service.AuthService;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;

public class LoginServlet extends AbstractAppEngineAuthorizationCodeServlet {
  
  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return AuthService.get().getRedirectUri(req);
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return AuthService.get().newFlow();
  }
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    PrintWriter writer = resp.getWriter();
    writer.println("Calenders:<br><br>");
    
      com.google.api.services.calendar.Calendar client = AuthService.get().loadCalendarClient();
    
      com.google.api.services.calendar.Calendar.CalendarList.List listRequest =
          client.calendarList().list();
      listRequest.setFields("items(id,summary)");
      CalendarList feed = listRequest.execute();

      if (feed.getItems() != null) {
        for (CalendarListEntry entry : feed.getItems()) {
          writer.print("<pre>" + entry.toPrettyString() + "</pre><br>");
        }
      }

      String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
      
      Config config = ConfigurationService.getConfig();
      config.setUserId(userId);
      // todo: use from config
      String calendarId = "kugekl5f03hjsvd8c34t1gg7hg@group.calendar.google.com";
      config.setCalendarId(calendarId);
      
      ConfigurationService.save(config);
      
      writer.println("<pre>Config: " + config + "</pre><br>");
  }
}
