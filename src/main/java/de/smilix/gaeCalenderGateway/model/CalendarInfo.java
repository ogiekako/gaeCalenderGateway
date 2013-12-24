package de.smilix.gaeCalenderGateway.model;


/**
 * Some very basic information about a Google Calendar. 
 */
public class CalendarInfo {

  private String id;
  
  private String summary;

  public CalendarInfo(String id, String summary) {
    this.id = id;
    this.summary = summary;
  }

  public String getId() {
    return id;
  }

  public String getSummary() {
    return summary;
  }
}
