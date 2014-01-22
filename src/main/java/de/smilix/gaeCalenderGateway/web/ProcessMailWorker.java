package de.smilix.gaeCalenderGateway.web;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.smilix.gaeCalenderGateway.common.Utils;
import de.smilix.gaeCalenderGateway.model.IcalInfo;
import de.smilix.gaeCalenderGateway.model.RawMailIn;
import de.smilix.gaeCalenderGateway.model.RawMailIn.Status;
import de.smilix.gaeCalenderGateway.service.data.ICalInfoRepository;
import de.smilix.gaeCalenderGateway.service.data.RawMailInRepository;
import de.smilix.gaeCalenderGateway.service.gcal.GoogleCalService;
import de.smilix.gaeCalenderGateway.service.ical.ICalInfoFactory;
import de.smilix.gaeCalenderGateway.service.mail.MailParserMime4j;

public class ProcessMailWorker extends HttpServlet {

  private static final Logger LOG = Logger.getLogger(ProcessMailWorker.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    LOG.fine("Process id: " + id);

    RawMailInRepository r = RawMailInRepository.get();
    RawMailIn rawMail = r.find(Long.parseLong(id));
    if (rawMail == null) {
      LOG.severe("Can't find a rawMail for id: " + id);
      return;
    }

    IcalInfo iCalInfos;
    ICalInfoRepository iCalInfoRepository;
    try {
      MailParserMime4j parser = new MailParserMime4j();
      String plainTextCalendarData = parser.parse(rawMail.getRawMail());

      if (Utils.isEmpty(plainTextCalendarData)) {
        LOG.severe("Mail did'nt match, see logs.");
        rawMail.setStatus(Status.ERROR);
        return;
      }
      iCalInfos = ICalInfoFactory.get().create(plainTextCalendarData);

      iCalInfoRepository = ICalInfoRepository.get();
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

    LOG.fine("Try to add the parsed event into Google calendar: " + iCalInfos.getId());
    try {
      IcalInfo.Status result = GoogleCalService.get().processEvent(iCalInfos);
      iCalInfos.setStatus(result);
      LOG.info("New calendar entry added: " + iCalInfos.getId());
    } catch (Exception e) {
      iCalInfos.setStatus(IcalInfo.Status.CAL_ERROR);
      LOG.log(Level.SEVERE, "Error during google calender connection.", e);
      return;
    } finally {
      iCalInfoRepository.merge(iCalInfos);
    }
  }
}
