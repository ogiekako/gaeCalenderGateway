package de.smilix.gaeCalenderGateway.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Text;

/**
 * @author holger
 *
 */
@Entity
public class RawMailIn extends DatastoreObject implements Serializable {

  public enum Status {
    INCOMING, PROCESSED, ERROR; 
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private Text rawMail;
  private Long recieved = System.currentTimeMillis();
  private Status status = Status.INCOMING;
  
  
  public RawMailIn(String rawMail) {
    this.rawMail = new Text(rawMail);
  }
  
  public Text getRawMail() {
    return rawMail;
  }

  public void setRawMail(Text rawMail) {
    this.rawMail = rawMail;
  }

  public Long getRecieved() {
    return recieved;
  }
  

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return String.format("RawMailIn [id=%s, rawMail=%s, recieved=%s, status=%s]", id, rawMail, recieved, status);
  }
}
