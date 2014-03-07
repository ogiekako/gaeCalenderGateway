package de.smilix.gaeCalenderGateway.common;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.lang.Validate;

/**
 * @author Holger Cremer
 */
public final class Utils {
  private Utils() {
  };

  // not thread safe !
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

  public static String timestampToFormattedDate(Long timestamp) {
    if (timestamp == null) {
      return null;
    }
    return DATE_FORMAT.format(new Date(timestamp));
  }
  
  public static void logException(Logger logger, String message, Throwable t) {
    logger.warning(message);
    logger.warning(excpToString(t));
  }

  public static String excpToString(Throwable t) {
    StringWriter output = new StringWriter();
    t.printStackTrace(new PrintWriter(output, false));
    return output.toString();
  }

  public static String streamToString(InputStream stream) throws IOException {
    StringBuilder string = new StringBuilder();
    byte[] buffer = new byte[2048];
    for (int l; (l = stream.read(buffer)) != -1;) {
      string.append(new String(buffer, 0, l));
    }
    return string.toString();
  }
  
  public static String streamToString(InputStream stream, int maxLength) throws IOException {
    Validate.isTrue(maxLength >= 0, "Invalid max length range. Must >= 0.");
    StringBuilder string = new StringBuilder();
    byte[] buffer = new byte[2048];
    int byteCounter = 0;
    for (int l; (l = stream.read(buffer)) != -1;) {
      byteCounter += l;
      if (byteCounter < maxLength) {
        string.append(new String(buffer, 0, l));
      } else {
        int remainder = maxLength - byteCounter + l;
        string.append(new String(buffer, 0, remainder));
        break;
      }
    }
    return string.toString();
  }

  public static void closeQuietly(Closeable closeMe) {
    if (closeMe != null) {
      try {
        closeMe.close();
      } catch (IOException excp) {
        // do nothing
      }
    }
  }

  public static Throwable getRootCause(final Throwable t) {
    Throwable root = t;
    for (Throwable cause; (cause = root.getCause()) != null;) {
      root = cause;
    }
    return root;
  }
  
  public static boolean isEmpty(String str) {
    if (str == null) {
      return true;
    }
    return str.isEmpty();
  }
}
