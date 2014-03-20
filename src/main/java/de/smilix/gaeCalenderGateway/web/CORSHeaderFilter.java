package de.smilix.gaeCalenderGateway.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CORSHeaderFilter implements Filter {

  @Override
  public void destroy() {
    // nothing to do
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
          ServletException {
    
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.addHeader("Access-Control-Allow-Origin", "*");
    
    chain.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // nothing to do
  }
}
