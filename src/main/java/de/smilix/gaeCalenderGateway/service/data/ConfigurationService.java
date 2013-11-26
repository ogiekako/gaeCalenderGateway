package de.smilix.gaeCalenderGateway.service.data;

import de.smilix.gaeCalenderGateway.model.Config;


public class ConfigurationService {
  
  private ConfigurationService() {
    // 
  }

  public static Config getConfig() {
    Config config = SimpleDAO.GET.find(Config.class,
            Config.CONFIG_ID);
    if (config == null) {
      config = new Config();
      SimpleDAO.GET.persist(config);
    }
    return config;
  }

  public static void save(Config config) {
    SimpleDAO.GET.merge(config);
  }
}
