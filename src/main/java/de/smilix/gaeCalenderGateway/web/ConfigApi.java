package de.smilix.gaeCalenderGateway.web;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

import de.smilix.gaeCalenderGateway.model.Config;
import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;


@Api(
        name = "config",
        version = "v1")
public class ConfigApi {


  @ApiMethod(name = "config.get", httpMethod =
          ApiMethod.HttpMethod.GET, path = "getc")
  public Config getConfig() {
    Config config = ConfigurationService.getConfig();

    return config;
  }


}
