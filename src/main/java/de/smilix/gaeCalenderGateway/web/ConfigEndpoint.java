package de.smilix.gaeCalenderGateway.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;

public class ConfigEndpoint extends HttpServlet{

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
    Config config = ConfigurationService.getConfig();
    
//    JsonFactory factory = new JsonFactory();
//    JsonGenerator gen = factory.createGenerator((OutputStream)null);
  }
}
