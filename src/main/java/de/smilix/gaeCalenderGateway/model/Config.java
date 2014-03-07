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
  /**
   * The maximum size of the content of the incoming mail. In bytes. 
   */
  public static final int MAX_INCOMING_MAIL_SIZE = 12 * 1000; 
  
  /**
   * There is only one config....
   */
  public static Long CONFIG_ID = 1L;
  
  @Id 
  private Long id = CONFIG_ID; 
    
  private String userId;
  
  private String calendarId;
  
  private String contactEmail;
  
  private String senderEmail;

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

  public String getSenderEmail() {
    return senderEmail;
  }

  public void setSenderEmail(String senderEmail) {
    this.senderEmail = senderEmail;
  }

  @Override
  public String toString() {
    return String.format("Config [id=%s, userId=%s, calendarId=%s, contactEmail=%s, senderEmail=%s]", id, userId,
            calendarId, contactEmail, senderEmail);
  }

}
