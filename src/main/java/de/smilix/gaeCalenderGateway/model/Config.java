package de.smilix.gaeCalenderGateway.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity 
public class Config {
  
  /**
   * The timezone used for the recurrent events (Google require this). 
   */
  public static final String TIME_ZONE = "Europe/Berlin";
  public static final int ALARM_MINUTES = 3;
  public static final String EVENT_CANCEL_PREFIX = "";
  
  /**
   * There is only one config....
   */
  public static Long CONFIG_ID = 1L;
  
  @Id 
  private Long id = CONFIG_ID; 
    
  private String userId;
  
  private String calendarId;
  
  private String contactEmail;

  public String getUserId() { 
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getCalendarId() {
    return calendarId;
  }

  public void setCalendarId(String calendarId) { 
    this.calendarId = calendarId; 
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  @Override
  public String toString() {
    return String.format("Config [id=%s, userId=%s, calendarId=%s, contactEmail=%s]", id, userId, calendarId,
            contactEmail);
  }

}
