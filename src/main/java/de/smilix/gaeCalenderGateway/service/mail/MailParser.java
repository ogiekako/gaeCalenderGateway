package de.smilix.gaeCalenderGateway.service.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import de.smilix.gaeCalenderGateway.common.Utils;

/**
 * @author Holger Cremer
 */
public class MailParser {

  private static final Logger LOG = Logger.getLogger(MailParser.class.getName());

  public MimeMessage createFromString(String mail) throws MessagingException {
    Session session = Session.getDefaultInstance(new Properties(), null);
    MimeMessage message =
            new MimeMessage(session, new ByteArrayInputStream(mail.getBytes()));

    return message;
  }

  public String parse(MimeMessage message) throws MessagingException, IOException {

    if (message == null) {
      LOG.warning("message is null");
      return null;
    }

    String data = null;
    if (message.getFrom() == null) {
      printNotMatchedMail(message, "no 'from'");
      return null;
    }
    final String from = message.getFrom()[0].toString();

    MimeMultipart multipartContent = null;

    LOG.fine(String.format("'%s', %s", message.getSubject(), from));
    if (message.isMimeType("message/rfc822")) {
      MimeMessage content = (MimeMessage) message.getContent();
      if (content.isMimeType("multipart/alternative")) {
        multipartContent = (MimeMultipart) content.getContent();
      } else {
        printNotMatchedMail(message, "Unexpected mime type for content: " + content.getContentType());
      }
    } else if (message.isMimeType("multipart/alternative")) {
      multipartContent = (MimeMultipart) message.getContent();
    }


    if (multipartContent != null) {
      for (int i = 0; i < multipartContent.getCount(); i++) {
        BodyPart bodyPart = multipartContent.getBodyPart(i);
        LOG.fine("Multipart: " + bodyPart.getContentType() + " / " + bodyPart.getDescription() + " / "
                + bodyPart.getDisposition() + " / " + bodyPart.getFileName());
        if (bodyPart.isMimeType("text/calendar")) {
          InputStream stream = bodyPart.getInputStream();
          String meetingICS = Utils.streamToString(stream);
          Utils.closeQuietly(stream);
          data = meetingICS;
        }
      }
    } else {
      printNotMatchedMail(message, "Unexpected mail mimetype.");
    }

    if (data == null) {
      printNotMatchedMail(message, "No calender attachment found.");
    }

    return data;
  }

  public void printNotMatchedMail(Message message, String noMatchReason) throws MessagingException, IOException {
    LOG.warning(noMatchReason);
    StringBuilder d = new StringBuilder();
    d.append("\tFrom: ").append(message.getFrom() != null ? message.getFrom()[0].toString() : "null");
    d.append("\n\tRecipient: ");
    if (message.getAllRecipients() != null) {
      for (Address recipient : message.getAllRecipients()) {
        d.append(recipient).append(" ");
      }
    }
    d.append("\n\tSubject: ").append(message.getSubject());
    d.append("\n\tMimeType: ").append(message.getContentType());
    if (message.isMimeType("message/rfc822")) {
      MimeMessage content = (MimeMessage) message.getContent();
      d.append("\n\tContent type: ").append(content.getContentType());
      if (content.isMimeType("multipart/alternative")) {
        MimeMultipart multipartContent = (MimeMultipart) content.getContent();
        for (int i = 0; i < multipartContent.getCount(); i++) {
          // show all content information
          BodyPart bodyPart = multipartContent.getBodyPart(i);
          d.append("\n\tContent multipart: " + bodyPart.getContentType() + " / " + bodyPart.getDescription() + " / "
                  + bodyPart.getDisposition() + " / " + bodyPart.getFileName());
        }
      }
    } else {
      d.append("\n\tcontent class: ").append(message.getContent().getClass());
    }
    LOG.info("Message details: \n" + d);
  }
}
