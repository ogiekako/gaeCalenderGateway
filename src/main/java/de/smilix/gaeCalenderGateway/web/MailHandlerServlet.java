package de.smilix.gaeCalenderGateway.web;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

import de.smilix.gaeCalenderGateway.common.Utils;
import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.model.RawMailIn;
import de.smilix.gaeCalenderGateway.service.data.RawMailInRepository;
/**
 * @author Holger Cremer
 */
public class MailHandlerServlet extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(MailHandlerServlet.class.getName());

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    int contentLength = req.getContentLength();
    LOG.fine("Got mail, len: " + contentLength);
    
    if (contentLength > Config.MAX_INCOMING_MAIL_SIZE) {
      LOG.warning("The mail is bigger than the allowed value. The mail will be trimmed to the max size. Maybe the resulting mail can't be parsed.");
    }
    String rawData = Utils.streamToString(req.getInputStream(), Config.MAX_INCOMING_MAIL_SIZE);
    RawMailIn rawMailIn = new RawMailIn(rawData);
    RawMailInRepository.get().addEntry(rawMailIn);
    
    LOG.fine("Add new queue entry: " + rawMailIn.getId().toString());
    Queue queue = QueueFactory.getDefaultQueue();
    queue.add(withUrl("/tasks/processMailWorker").method(Method.GET).param("id", rawMailIn.getId().toString()));
  }
}
