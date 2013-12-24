package de.smilix.gaeCalenderGateway.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.smilix.gaeCalenderGateway.model.IcalInfo;
import de.smilix.gaeCalenderGateway.model.RawMailIn;
import de.smilix.gaeCalenderGateway.model.RawMailIn.Status;
import de.smilix.gaeCalenderGateway.service.data.ICalInfoRepository;
import de.smilix.gaeCalenderGateway.service.data.RawMailInRepository;
import de.smilix.gaeCalenderGateway.service.gcal.GoogleCalService;
import de.smilix.gaeCalenderGateway.service.ical.ICalInfoFactory;
import de.smilix.gaeCalenderGateway.service.mail.MailParser;

public class ProcessMailWorker extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(ProcessMailWorker.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    Session session = Session.getDefaultInstance(new Properties(), null);

    RawMailInRepository r = RawMailInRepository.get();
    RawMailIn rawMail = r.find(Long.parseLong(id));
    if (rawMail == null) {
      LOG.severe("Can't find a rawMail for id: " + id);
      return;
    }
    
    IcalInfo iCalInfos;
    try {
      MimeMessage message =
              new MimeMessage(session, new ByteArrayInputStream(rawMail.getRawMail().getBytes()));
      MailParser parser = new MailParser();
      String mail = parser.parse(message);

      iCalInfos = ICalInfoFactory.get().create(mail);

      ICalInfoRepository iCalInfoRepository = ICalInfoRepository.get();
      iCalInfoRepository.addEntry(iCalInfos);

      rawMail.setStatus(Status.PROCESSED);
      LOG.info("New cal entry parsed: " + iCalInfos.toShortSummary());

    } catch (Exception e) {
      rawMail.setStatus(Status.ERROR);
      LOG.log(Level.SEVERE, "Error during iCalInfo creation.", e);
      return;
    } finally {
      // always save the new status
      r.merge(rawMail);
    }
    
    try {
      GoogleCalService.get().addEvent(iCalInfos);
    } catch (Exception e) {
      // TODO: set status to icalinfo
      LOG.log(Level.SEVERE, "Error during google calender connection.", e);
    }
  }
}
