package de.smilix.gaeCalenderGateway.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.appengine.api.users.UserServiceFactory;

import de.smilix.gaeCalenderGateway.common.Utils;
import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.model.RawMailIn;
import de.smilix.gaeCalenderGateway.service.AuthService;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;
import de.smilix.gaeCalenderGateway.service.data.RawMailInRepository;

public class TestServlet extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(TestServlet.class.getName());
  
  /** Global instance of the HTTP transport. */
  static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();

  /** Global instance of the JSON factory. */
  static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

//  private static final Logger log = LoggerFactory
//          .getLogger(TestServlet.class);

  @Override
  protected void doGet(HttpServletRequest request,
          HttpServletResponse response) throws ServletException, IOException {
    try {

      if (request.getParameter("list") != null) {
        testCalendar(response);
      } else if (request.getParameter("userId") != null) {
        printUserId(response);
      }
    } catch (Exception e) {
      
      LOG.log(Level.SEVERE, "Error during test: " + e.getMessage(), e);
    }

  }

  private void printUserId(HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    PrintWriter writer = resp.getWriter();
    
    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
    writer.println("UserId: " + userId);
}

  private void testCalendar(HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    PrintWriter writer = resp.getWriter();
    writer.println("Events: <br><br>");

    Config config = ConfigurationService.getConfig();
    String userId = config.getUserId();
    String calendarId = config.getCalendarId();
    
    LOG.info("Using config: " + config);

    Credential credential = AuthService.get().newFlow().loadCredential(userId);
    if (credential == null) {
      LOG.severe("credential is null!");
      writer.print("<pre>Credential is null!</pre><br>");
      return;
    }
//    System.out.println("access token: " + credential.getAccessToken());

    Calendar calendarSrv = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
    //    com.google.api.services.calendar.model.Calendar calendar = calendarSrv.calendars().get(calendarId).execute();
    //    System.out.println("got cal: " + calendar);


    Events allEvents = calendarSrv.events().list(calendarId).execute();
    for (Event event : allEvents.getItems()) {
      writer.print("<pre>" + event.toPrettyString() + "</pre><br>");
    }

    //    System.out.println("create new event");
    //    Event newEvent = new Event();
    //    newEvent.setSummary("summ4");
    //    EventDateTime start = new EventDateTime();
    //    // == 16.12.2013 at 14.26 
    //    start.setDateTime(new DateTime(new Date(113, 11, 16, 14, 26)));
    //    newEvent.setStart(start);
    //    EventDateTime stop = new EventDateTime();
    //    stop.setDateTime(new DateTime(new Date(113, 11, 16, 16, 29)));
    //    newEvent.setEnd(stop);
    //    EventAttendee ea = new EventAttendee();
    //    ea.setDisplayName("hans");
    //    ea.setEmail("lala@circlelab.de");
    //    newEvent.setAttendees(Arrays.asList(ea));
    //    Creator creator = new Creator();
    //    creator.setEmail("hlger@circlelab.de");
    //    creator.setDisplayName("holger");
    //    newEvent.setCreator(creator);
    //    newEvent.setDescription("ich bin eine <b>description</b>...");
    //    newEvent.setICalUID("sdfklsdo340345345sdflk");
    //    Reminders reminders = new Reminders();
    //    EventReminder eventReminder = new EventReminder();
    //    eventReminder.setMinutes(3);
    //    eventReminder.setMethod("popup");
    //    reminders.setOverrides(Arrays.asList(eventReminder));
    //    reminders.setUseDefault(false); // or you get cannotUseDefaultRemindersAndSpecifyOverride
    //    newEvent.setReminders(reminders);
    //    newEvent.setLocation("raum 17");
    //    newEvent = calendarSrv.events().insert(calendarId, newEvent).execute();
    //    System.out.println("Event added: " + newEvent);

  }

  private void testRepo() {
    for (RawMailIn m : RawMailInRepository.get().getAll()) {
      System.out.println(m);
      System.out.println(Utils.timestampToFormattedDate(m.getRecieved()));
      System.out.println(m.getStatus());
    }
  }

  private void testConfig() {
    Config config = ConfigurationService.getConfig();
    //    config.setCalendarId("neu");
    //    ConfigurationService.save(config);

    //    ConfigRepository cr = new ConfigRepository();
    //    Config config = cr.getConfig();
    //    config.setCalendarId("my id");
    //    config.setUserId(userId);
    //    cr.updateConfig(config);

    System.out.println(config);
  }

  private void list(com.google.api.services.calendar.Calendar.CalendarList.List listRequest) throws IOException {
    CalendarList feed = listRequest.execute();
    if (feed.getItems() != null) {
      for (CalendarListEntry entry : feed.getItems()) {
        System.out.println(entry);
      }
    }
  }


  /**
   * Forwards request and response to given path. Handles any exceptions
   * caused by forward target by printing them to logger.
   * 
   * @param request 
   * @param response
   * @param path 
   */
//  protected void forward(HttpServletRequest request,
//          HttpServletResponse response, String path) {
//    try {
//      RequestDispatcher rd = request.getRequestDispatcher(path);
//      rd.forward(request, response);
//    } catch (Throwable tr) {
//      if (log.isErrorEnabled()) {
//        log.error("Cought Exception: " + tr.getMessage());
//        log.debug("StackTrace:", tr);
//      }
//    }
//  }
}
