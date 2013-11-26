package de.smilix.gaeCalenderGateway.service.gcal;

/**
 * @author Holger Cremer
 */
public class GCCException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -4011545412983979081L;

  /**
   * @param message
   * @param cause
   */
  public GCCException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public GCCException(String message) {
    super(message);
  }

}
