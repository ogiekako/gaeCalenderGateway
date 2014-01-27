package de.smilix.gaeCalenderGateway.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.service.AuthService;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;

/**
 * @author holger
 */
public class LoginServlet extends AbstractAppEngineAuthorizationCodeServlet {
  
  private static final Logger LOG = Logger.getLogger(LoginServlet.class.getName());
  
  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return AuthService.get().getRedirectUri(req);
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return AuthService.get().newFlow();
  }
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    User currentUser = UserServiceFactory.getUserService().getCurrentUser();
    
    Config config = ConfigurationService.getConfig();
    config.setUserId(currentUser.getUserId());
    config.setSenderEmail(currentUser.getEmail());
    LOG.info(String.format("Setting userId '%s' and email '%s'.", currentUser.getUserId(), currentUser.getEmail()));
    ConfigurationService.save(config);
    
    resp.sendRedirect("/app/");
  }
}
