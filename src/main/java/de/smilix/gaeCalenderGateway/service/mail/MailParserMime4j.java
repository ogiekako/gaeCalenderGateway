package de.smilix.gaeCalenderGateway.service.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;

import de.smilix.gaeCalenderGateway.common.Utils;

/**
 * @author Holger Cremer
 */
public class MailParserMime4j {

  private static final Logger LOG = Logger.getLogger(MailParserMime4j.class.getName());
  private static final String TEXT_CALENDAR_MIMETYPE = "text/calendar";

  public String parse(String text) throws IOException  {
    return parse(new ByteArrayInputStream(text.getBytes()));
  }
  
  public String parse(InputStream inputStream) throws IOException  {

    DefaultMessageBuilder mb = new DefaultMessageBuilder();
    Message message = mb.parseMessage(inputStream);

    if (message.getFrom() == null) {
      printNotMatchedMail(message, "no 'from'");
      return null;
    }

    final String from = message.getFrom().get(0).toString();
    LOG.fine(String.format("Parsing mail '%s', %s", message.getSubject(), from));

    String result = findCalendarEntry(message);
    if (result == null) {
      printNotMatchedMail(message, "no calendar entry found");
    }
    
    return result;
  }


  /**
   * Searches recursively all mimeparts and returns the first entry with the {@link #TEXT_CALENDAR_MIMETYPE} type. 
   * @param entity
   * @return the first matching entry or <code>null</code> for no result
   * @throws IOException
   */
  private String findCalendarEntry(Entity entity) throws IOException {
    if (TEXT_CALENDAR_MIMETYPE.equals(entity.getMimeType())) {
      TextBody textBody = (TextBody) entity.getBody();
      return Utils.streamToString(textBody.getInputStream());
    }

    if (entity.isMultipart()) {
      Multipart multipart = (Multipart) entity.getBody();
      for (Entity part : multipart.getBodyParts()) {
        String result = findCalendarEntry(part);
        if (result != null) {
          return result;
        }
      }
    }

    return null;
  }


  private void printNotMatchedMail(Message message, String noMatchReason) {
    LOG.warning(noMatchReason);
    StringBuilder b = new StringBuilder();
    b.append("\n\tFrom: ").append(message.getFrom() != null ? message.getFrom().toString() : "!no from!");
    b.append("\n\tTo: ").append(message.getTo() != null ? message.getTo().toString() : "!no to!");
    b.append("\n\tSubject: ").append(message.getSubject());
    b.append("\n\tParts: \n");
    printMimetypeRecurisve(b, message, 1);

    LOG.info("Message details:" + b);
  }

  private void printMimetypeRecurisve(StringBuilder b, Entity entity, int depth) {
    for (int i = 0; i < depth; i++) {
      b.append("\t");
    }
    b.append("type: ").append(entity.getMimeType()).append(", filename: ").append(entity.getFilename()).append("\n");
    if (!entity.isMultipart()) {
      return;
    }

    Multipart multipart = (Multipart) entity.getBody();
    for (Entity part : multipart.getBodyParts()) {
      printMimetypeRecurisve(b, part, depth + 1);
    }
  }
}
