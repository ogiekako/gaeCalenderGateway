package de.smilix.gaeCalenderGateway.service.gcal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtStart;

import org.apache.commons.lang.RandomStringUtils;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
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
import de.smilix.gaeCalenderGateway.model.IcalInfo;
import de.smilix.gaeCalenderGateway.service.AuthService;
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

  public List<CalendarInfo> getAllCalenders() throws IOException {
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
  public List<Event> getAllEvents(String calendarId, Date since) throws IOException {
    Calendar calendarSrv = AuthService.get().loadCalendarClient();
    com.google.api.services.calendar.Calendar.Events.List listRequest = calendarSrv.events().list(calendarId);
    listRequest.setSingleEvents(false);
//    listRequest.setOrderBy("startTime"); // can't use this with singleEvents false
    listRequest.setTimeMin(new DateTime(since));
    //    listRequest.setTimeMin(new DateTime("2013-09-23T16:00:00+02:00"));

    Events events = listRequest.execute();
    return events.getItems();
  }

  private void checkEventParameter(IcalInfo event) throws GCCException {
    nullCheckWithExcpetion(event, "event object is null");
    nullCheckWithExcpetion(event.getStartTimestamp(), "start time is null");
    nullCheckWithExcpetion(event.getEndTimestamp(), "end time is null");
    // todo: more checks
  }

  private void nullCheckWithExcpetion(Object objectToCheck, String excpMessage) throws GCCException {
    if (objectToCheck == null) {
      throw new GCCException("event object is null");
    }
  }

  public void addEvent(IcalInfo ical) throws IOException {
    //    checkEventParameter(event);

    Config config = ConfigurationService.getConfig();
    String calendarId = config.getCalendarId();
    Calendar calendarSrv = AuthService.get().loadCalendarClient();

    Event event = new Event();
    event.setICalUID(ical.getuId());
    event.setSummary(ical.getSummary());
    String description = ical.getDescription(); 
    description += "\n$$CalAdd: " + Version.CURRENT + "$$";
    event.setDescription(description);
    event.setLocation(ical.getLocation());

    event.setStart(makeTime(ical.getStartTimestamp(), ical.getTzStartOffsetInMinutes()));
    event.setEnd(makeTime(ical.getEndTimestamp(), ical.getTzEndOffsetInMinutes()));

    List<EventAttendee> attendees = new ArrayList<>();
    for (String attendeeName : ical.getAttendees()) {
      EventAttendee ea = new EventAttendee();
      ea.setDisplayName(attendeeName);
      ea.setEmail("none@localhost");
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

    try {
      event = calendarSrv.events().insert(calendarId, event).execute();
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 409 /* duplicate */) {
        event = findAndUpdateEvent(calendarSrv, event);
      } else {
        throw e;
      }
    }
    LOG.info("Event to calendar added, id: " + event.getICalUID());


    // OLD copy & paste

    // add attendees as description values, because they don't have an email address
    //    for (String attendeeName : event.getAttendees()) {
    //      EventWho attendee = new EventWho();
    //      attendee.setValueString(attendeeName);
    //      attendee.setRel(Rel.EVENT_ATTENDEE);
    //      myEntry.addParticipant(attendee);
    //    }
    //    StringBuilder description = new StringBuilder();
    //    for (String attendeeName : event.getAttendees()) {
    //      description.append(attendeeName).append("\n");
    //    }
    //    description.append("------------- DESCRIPTION -------------\n").append(event.getDescription());
    //    myEntry.setContent(new PlainTextConstruct(description.toString()));


    //    String rRule = 
    //    "RRULE:FREQ=WEEKLY;COUNT=5;INTERVAL=2;BYDAY=WE;WKST=SU\r\n"+
    //    "DTSTART;TZID=W. Europe Standard Time:20120328T100000\r\n"+
    //    "DTEND;TZID=W. Europe Standard Time:20120328T121500";
    //    Recurrence recur = new Recurrence();
    //    recur.setValue(rRule);
    //    myEntry.setRecurrence(recur);

  }

  private Event findAndUpdateEvent(Calendar calendarSrv, Event event) throws IOException {
    LOG.info("Event already exists (Goolge API error 409 duplicate), try to find it's eventId and update it.");
    String calendarId = ConfigurationService.getConfig().getCalendarId();
    com.google.api.services.calendar.Calendar.Events.List list = calendarSrv.events().list(calendarId);
    list.setShowDeleted(true);
    list.setICalUID(event.getICalUID());
    List<Event> eventsList = list.execute().getItems();
    if (eventsList.size() != 1) {
      throw new IllegalStateException("Tried to get the event for the duplicated event (icalUid: " + event.getICalUID() +"), but got this amount of event: " + eventsList.size());
    }
    
    String eventId = eventsList.get(0).getId();
    event.setSequence(eventsList.get(0).getSequence());
    return calendarSrv.events().update(calendarId, eventId, event).execute();
  }

  private EventDateTime makeTime(Long startTimestamp, int timeZoneOffset) {
    EventDateTime time = new EventDateTime();
    time.setTimeZone(Config.TIME_ZONE);
    time.setDateTime(new DateTime(startTimestamp, timeZoneOffset));
    return time;
  }

  public void addTestEvent(IcalInfo event) throws IOException {
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
