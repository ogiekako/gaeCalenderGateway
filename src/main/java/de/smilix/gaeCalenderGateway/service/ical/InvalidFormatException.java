package de.smilix.gaeCalenderGateway.service.ical;

/**
 * @author Holger Cremer
 */
public class InvalidFormatException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 2135505245841771302L;

  /**
   * @param message
   * @param cause
   */
  public InvalidFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public InvalidFormatException(String message) {
    super(message);
  }

}
