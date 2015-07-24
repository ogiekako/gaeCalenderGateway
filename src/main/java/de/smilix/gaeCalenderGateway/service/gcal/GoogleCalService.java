package de.smilix.gaeCalenderGateway.service.gcal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events.Delete;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import de.smilix.gaeCalenderGateway.common.Version;
import de.smilix.gaeCalenderGateway.model.CalendarInfo;
import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.model.Contact;
import de.smilix.gaeCalenderGateway.model.IcalInfo;
import de.smilix.gaeCalenderGateway.model.IcalInfo.Status;
import de.smilix.gaeCalenderGateway.service.auth.AuthException;
import de.smilix.gaeCalenderGateway.service.auth.AuthService;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;

public class GoogleCalService {

  private static final Logger LOG = Logger.getLogger(GoogleCalService.class.getName());

  private static GoogleCalService instance;

  public static GoogleCalService get() {
    if (instance == null) {
      instance = new GoogleCalService();
    }
    return instance;
  }

  private GoogleCalService() {
  }

  public List<CalendarInfo> getAllCalenders() throws IOException, AuthException {
    Calendar calendarSrv = AuthService.get().loadCalendarClient();
    com.google.api.services.calendar.Calendar.CalendarList.List listRequest = calendarSrv.calendarList().list();
    listRequest.setFields("items(id,summary)");

    CalendarList feed = listRequest.execute();

    List<CalendarInfo> result = new ArrayList<>();

    if (feed.getItems() != null) {
      for (CalendarListEntry entry : feed.getItems()) {
        result.add(new CalendarInfo(entry.getId(), entry.getSummary()));
      }
    }

    return result;
  }

  // https://developers.google.com/google-apps/calendar/v3/reference/events/list?hl=de
  public List<Event> getAllEvents(String calendarId, Date begin, Date end) throws IOException, AuthException {
    Calendar calendarSrv = AuthService.get().loadCalendarClient();
    com.google.api.services.calendar.Calendar.Events.List listRequest = calendarSrv.events().list(calendarId);
    listRequest.setSingleEvents(false);
    //    listRequest.setOrderBy("startTime"); // can't use this with singleEvents false
    listRequest.setTimeMin(new DateTime(begin));
    listRequest.setTimeMax(new DateTime(end));

    Events events = listRequest.execute();
    return events.getItems();
  }

  private void nullCheckWithExcpetion(Object objectToCheck, String excpMessage) throws GCCException {
    if (objectToCheck == null) {
      throw new GCCException("event object is null");
    }
  }

  /**
   * Applies the event to the calendar. This can be 
   * <li>a new event entry
   * <li>an event update
   * <li>a removed event
   *  
   * @param ical
   * @throws IOException
   * @throws AuthException 
   */
  public Status processEvent(IcalInfo ical) throws IOException, AuthException {
    //    checkEventParameter(event);

    Calendar calendarSrv = AuthService.get().loadCalendarClient();

    if (ical.isCancelEvent()) {
      return removeEvent(calendarSrv, ical);
    }

    Event event = new Event();
    event.setICalUID(ical.getuId());
    event.setSummary(ical.getSummary());
    String description = ical.getDescription();
    description += "\n$$Cal: " + Version.CURRENT + "$$";
    event.setDescription(description);
    event.setLocation(ical.getLocation());

    event.setStart(makeTime(ical.getStartTimestamp(), ical.getTzStartOffsetInMinutes()));
    event.setEnd(makeTime(ical.getEndTimestamp(), ical.getTzEndOffsetInMinutes()));

    List<EventAttendee> attendees = new ArrayList<>();
    for (Contact attendee : ical.getAttendees()) {
      EventAttendee ea = new EventAttendee();
      ea.setDisplayName(attendee.getName());
      ea.setEmail(attendee.getEmail());
      attendees.add(ea);
    }
    event.setAttendees(attendees);

    // add attendees as description values, because they don't have an email address
    //        StringBuilder description = new StringBuilder();
    //        for (String attendeeName : event.getAttendees()) {
    //          description.append(attendeeName).append("\n");
    //        }

    Creator creator = new Creator();
    //    creator.setEmail("none@localhost");
    creator.setDisplayName(ical.getOrganizer());
    event.setCreator(creator);

    // reminder
    Reminders reminders = new Reminders();
    EventReminder eventReminder = new EventReminder();
    eventReminder.setMinutes(Config.ALARM_MINUTES);
    eventReminder.setMethod("popup");
    reminders.setOverrides(Arrays.asList(eventReminder));
    reminders.setUseDefault(false); // or you get cannotUseDefaultRemindersAndSpecifyOverride
    event.setReminders(reminders);

    if (!ical.getRecurrence().isEmpty()) {
      event.setRecurrence(ical.getRecurrence());
    }

    // add event 
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine("com.google.api.services.calendar.model.Event: " + event.toPrettyString());
    }

    Status result;
    try {
      result = Status.CAL_ADDED;
      event = calendarSrv.events().insert(ConfigurationService.getConfig().getCalendarId(), event).execute();
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 409 /* duplicate */) {
        result = Status.CAL_UPDATED;
        event = findAndUpdateEvent(calendarSrv, event);
      } else {
        throw e;
      }
    }
    LOG.info("Event to calendar added, id: " + event.getICalUID());

    return result;
  }

  private Status removeEvent(Calendar calendarSrv, IcalInfo ical) throws IOException {
    LOG.info("Remove the event.");
    String calendarId = ConfigurationService.getConfig().getCalendarId();

    Calendar.Events.List listRequest = calendarSrv.events().list(calendarId);
    listRequest.setMaxResults(4);
    listRequest.setICalUID(ical.getuId());
    listRequest.setFields("items/id");
    List<Event> items = listRequest.execute().getItems();
    if (items.size() != 1) {
      throw new IllegalStateException(String.format(
              "Searched for the iCal event by it's uid (%s), but found %d events!", ical.getuId(), items.size()));
    }
    
    Delete deleteRequest = calendarSrv.events().delete(calendarId, items.get(0).getId());
    deleteRequest.setSendNotifications(false);
    deleteRequest.execute();

    return Status.CAL_REMOVED;
  }

  private Event findAndUpdateEvent(Calendar calendarSrv, Event event) throws IOException {
    LOG.info("Event already exists (Goolge API error 409 duplicate), try to find it's eventId and update it.");
    String calendarId = ConfigurationService.getConfig().getCalendarId();
    com.google.api.services.calendar.Calendar.Events.List list = calendarSrv.events().list(calendarId);
    list.setShowDeleted(true);
    list.setICalUID(event.getICalUID());
    List<Event> eventsList = list.execute().getItems();
    if (eventsList.size() != 1) {
      // log all the problematic events 
      for (Event anEvent : eventsList) {
        LOG.info("Found event: " + anEvent.toPrettyString());
      }
      
      throw new IllegalStateException("Tried to get the event for the duplicated event (icalUid: " + event.getICalUID()
              + "), but got this amount of event: " + eventsList.size());
    }

    String eventId = eventsList.get(0).getId();
    event.setSequence(eventsList.get(0).getSequence());
    
    String description = event.getDescription();
    description += "\n$$Cal update: " + Version.CURRENT + "$$";
    event.setDescription(description);
    
    return calendarSrv.events().update(calendarId, eventId, event).execute();
  }

  private EventDateTime makeTime(Long startTimestamp, int timeZoneOffset) {
    EventDateTime time = new EventDateTime();
    time.setTimeZone(Config.TIME_ZONE);
    time.setDateTime(new DateTime(startTimestamp, timeZoneOffset));
    return time;
  }

  public void addTestEvent(IcalInfo event) throws IOException, AuthException {
    Config config = ConfigurationService.getConfig();

    String calendarId = config.getCalendarId();

    Calendar calendarSrv = AuthService.get().loadCalendarClient();

    final long DAY_DELTA = 1000 * 60 * 60 * 24;
    final long DAY_AND_HOUR_DELTA = DAY_DELTA + (1000 * 60 * 60);

    Event newEvent = new Event();
    newEvent.setSummary("sum" + event.getSummary());
    EventDateTime start = new EventDateTime();
    // == 16.12.2013 at 14.26 
    //    start.setDateTime(new DateTime(new Date(113, 11, 16, 14, 26)));
    start.setDateTime(new DateTime(new Date(System.currentTimeMillis() + DAY_DELTA)));

    newEvent.setStart(start);
    EventDateTime stop = new EventDateTime();
    //    stop.setDateTime(new DateTime(new Date(113, 11, 16, 16, 29)));
    stop.setDateTime(new DateTime(new Date(System.currentTimeMillis() + DAY_AND_HOUR_DELTA)));
    newEvent.setEnd(stop);
    EventAttendee ea = new EventAttendee();
    ea.setDisplayName("hans");
    ea.setEmail("lala@circlelab.de");
    newEvent.setAttendees(Arrays.asList(ea));
    Creator creator = new Creator();
    creator.setEmail("hlger@circlelab.de");
    creator.setDisplayName("holger");
    newEvent.setCreator(creator);
    newEvent.setDescription("ich bin eine <b>description</b>...");

    newEvent.setICalUID(RandomStringUtils.randomAlphabetic(10));
    Reminders reminders = new Reminders();
    EventReminder eventReminder = new EventReminder();
    eventReminder.setMinutes(3);
    eventReminder.setMethod("popup");
    reminders.setOverrides(Arrays.asList(eventReminder));
    reminders.setUseDefault(false); // or you get cannotUseDefaultRemindersAndSpecifyOverride
    newEvent.setReminders(reminders);
    newEvent.setLocation("raum 17");
    newEvent = calendarSrv.events().insert(calendarId, newEvent).execute();

    LOG.info("Event added: " + newEvent);
  }
}
