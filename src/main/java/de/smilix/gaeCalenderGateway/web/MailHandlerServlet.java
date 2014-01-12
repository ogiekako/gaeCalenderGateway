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
import de.smilix.gaeCalenderGateway.model.RawMailIn;
import de.smilix.gaeCalenderGateway.service.data.RawMailInRepository;
/**
 * @author Holger Cremer
 */
public class MailHandlerServlet extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(MailHandlerServlet.class.getName());

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

//    System.out.println("URL: " + req.getRequestURI());

//    System.out.println("HEADER: ");
//    Enumeration headerEnum = req.getHeaderNames();
//    while (headerEnum.hasMoreElements()) {
//      String headerName = (String) headerEnum.nextElement();
//      System.out.println(headerName + " -> " + req.getHeader(headerName));
//    }

//    System.out.println("PARAMS:");
//    Enumeration paramEnum = req.getParameterNames();
//    while (paramEnum.hasMoreElements()) {
//      String paramName = (String) paramEnum.nextElement();
//      System.out.println(paramName + " -> " + req.getParameter(paramName));
//    }

//    ServletInputStream inputStream = req.getInputStream();
//    String reqData = Utils.streamToString(inputStream);
//    System.out.println("DATA:");
//    System.out.println(reqData);

    LOG.fine("Got mail");
    
    String rawData = Utils.streamToString(req.getInputStream());
    RawMailIn rawMailIn = new RawMailIn(rawData);
    RawMailInRepository.get().addEntry(rawMailIn);
    
    LOG.fine("Add new queue entry: " + rawMailIn.getId().toString());
    Queue queue = QueueFactory.getDefaultQueue();
    queue.add(withUrl("/tasks/processMailWorker").method(Method.GET).param("id", rawMailIn.getId().toString()));
  }
}
