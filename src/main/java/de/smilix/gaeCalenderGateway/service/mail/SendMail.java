package de.smilix.gaeCalenderGateway.service.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.smilix.gaeCalenderGateway.common.Utils;
import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;

public class SendMail {

  private static final Logger LOG = Logger.getLogger(SendMail.class.getName());

  public void sendErrorMail(String errorText) {
    Config config = ConfigurationService.getConfig();
    String contactEmail = config.getContactEmail();

    if (Utils.isEmpty(contactEmail)) {
      LOG.fine("Sending NO error email!");
      return;
    }

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    Message msg = new MimeMessage(session);
    try {
      msg.setFrom(new InternetAddress(config.getSenderEmail(), "gaeCalendarGateway"));
      msg.addRecipient(Message.RecipientType.TO,
              new InternetAddress(contactEmail));
      msg.setSubject("Error in gaeCalendarGateway");
      msg.setText(errorText);
      Transport.send(msg);
      
      LOG.info("Error email send to " + contactEmail);
    } catch (UnsupportedEncodingException | MessagingException e) {
      LOG.log(Level.SEVERE, "Error while sending error mail: " + e.getMessage(), e);
    }

  }
}
