package de.smilix.gaeCalenderGateway.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity 
public class Config {
  
  /**
   * There is only one config....
   */
  public static Long CONFIG_ID = 1L;          
  
  @Id 
  private Long id = CONFIG_ID; 
    
  private String userId; 
  private String calendarId;

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

  @Override
  public String toString() {
    return String.format("Config [id=%s, userId=%s, calendarId=%s]", id, userId, calendarId);
  }
  
}
