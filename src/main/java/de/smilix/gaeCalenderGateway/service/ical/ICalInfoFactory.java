package de.smilix.gaeCalenderGateway.service.ical;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.util.CompatibilityHints;
import de.smilix.gaeCalenderGateway.common.Version;
import de.smilix.gaeCalenderGateway.model.Contact;
import de.smilix.gaeCalenderGateway.model.IcalInfo;

/**
 * @author Holger Cremer
 */
public class ICalInfoFactory {

  private static final String MAILTO_PREFIX = "mailto:";

  private static Logger LOG = Logger.getLogger(ICalInfoFactory.class.getName());

  private static ICalInfoFactory instance;

  public static ICalInfoFactory get() {
    if (instance == null) {
      instance = new ICalInfoFactory();
    }
    return instance;
  }

  private ICalInfoFactory() {
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
    CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
  }

  /**
   * Creates a {@link ICalInfosDB} object from the passed plain ical data.
   * 
   * @param iCalData
   * @return
   * @throws InvalidFormatException on any underlying exception or invalid/missing data.
   * @throws NoSuchAlgorithmException 
   */
  public IcalInfo create(String iCalData) throws InvalidFormatException, NoSuchAlgorithmException {
    if (iCalData == null) {
      throw new NullPointerException("The iCalData must not be null.");
    }

    CalendarBuilder builder = new CalendarBuilder();
    Calendar calendar;
    try {
      calendar = builder.build(new StringReader(iCalData));
    } catch (IOException excp) {
      throw new InvalidFormatException("IO problems during  parsing: " + excp.getMessage(), excp);
    } catch (ParserException excp) {
      throw new InvalidFormatException("Can't parse the ical data: " + excp.getMessage(), excp);
    }

    VEvent vEvent = (VEvent) calendar.getComponent(VEvent.VEVENT);
    if (vEvent == null) {
      throw new InvalidFormatException("No VEVENT entry found.");
    }

    IcalInfo info = new IcalInfo();

    info.setuId(vEvent.getUid().getValue());
    info.setSummary(getValueOrDefault(vEvent.getSummary(), "~~No summary~~"));
    if (vEvent.getStartDate() == null) {
      throw new InvalidFormatException("Start date is missing.");
    }
    info.setStartTimestamp(vEvent.getStartDate().getDate().getTime());
    if (vEvent.getEndDate() == null) {
      throw new InvalidFormatException("End date is missing.");
    }
    info.setEndTimestamp(vEvent.getEndDate().getDate().getTime());
    info.setLocation(getValueOrDefault(vEvent.getLocation(), ""));
    info.setOrganizer(getParameterValueOrDefault(vEvent.getOrganizer(), Cn.CN, "~~No organizer CN~~"));

    PropertyList attendees = vEvent.getProperties(Attendee.ATTENDEE);
    List<Contact> attendeeList = new ArrayList<>(attendees.size());
    for (Object property : attendees) {
      Attendee attendee = (Attendee) property;
      attendeeList.add(createContact(attendee));
    }
    info.setAttendees(attendeeList);

    String description = getValueOrDefault(vEvent.getDescription(), "--No description--");
    description += "\n$$Parsed: " + Version.CURRENT + "$$";
    info.setDescription(description);

    parseReccurence(vEvent, info);
    boolean cancelEvent = "CANCELLED".equals(vEvent.getStatus().getValue());
    info.setCancelEvent(cancelEvent);

    LOG.fine("Created ICal info: " + info.toShortSummary());
    LOG.finer("Description: " + info.getDescription());

    return info;
  }

  private Contact createContact(Attendee attendee) {
    String email = attendee.getValue();
    if (email.toLowerCase().startsWith(MAILTO_PREFIX)) {
      email = email.substring(MAILTO_PREFIX.length());
    }
    String name = email;
    
    Parameter cnParam = attendee.getParameter("CN");
    if (cnParam != null) {
      name = cnParam.getValue();
    }
    
    return new Contact(name, email);
  }

  private void parseReccurence(VEvent vEvent, IcalInfo info) {
    List<String> rules = new ArrayList<>();
    PropertyList properties = vEvent.getProperties(Property.RRULE);
    for (Object prop : properties) {
      RRule rrule = (RRule) prop;
      //      Recur rcur = rrule.getR..
      rules.add(rrule.toString());
    }

    if (!rules.isEmpty()) {
      LOG.fine("Recurrences: " + rules);
    }
    info.setRecurrence(rules);

    final DtStart start = (DtStart) vEvent.getProperty(Property.DTSTART);
    final DateProperty end = (DateProperty) vEvent.getProperty(Property.DTEND);
    info.setTzStartOffsetInMinutes(start.getTimeZone().getRawOffset() / 1000 / 60 / 60);
    info.setTzEndOffsetInMinutes(end.getTimeZone().getRawOffset() / 1000 / 60 / 60);
  }

  // creates a real unique id for the given event using a md5 hash on the whole event
  private String generateUId(VEvent event) throws NoSuchAlgorithmException {
    MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
    digest.update(event.toString().getBytes());
    byte[] hash = digest.digest();

    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < hash.length; i++) {
      hexString.append(Integer.toHexString(0xFF & hash[i]));
    }
    return hexString.toString();
  }

  private String getValueOrDefault(Content content, String defaultValue) {
    if (content == null) {
      return defaultValue;
    } else {
      return content.getValue();
    }
  }

  private String getParameterValueOrDefault(Property property, String parameterName, String defaultValue) {
    if (property == null) {
      return defaultValue;
    }
    return getValueOrDefault(property.getParameter(parameterName), defaultValue);
  }
}
