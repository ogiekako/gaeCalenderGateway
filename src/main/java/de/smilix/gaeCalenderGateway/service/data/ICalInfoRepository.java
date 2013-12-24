package de.smilix.gaeCalenderGateway.service.data;

import de.smilix.gaeCalenderGateway.model.IcalInfo;

/**
 * @author Holger Cremer
 */
public class ICalInfoRepository extends AbstractRepository<IcalInfo> {

private static ICalInfoRepository instance;
  
  public static ICalInfoRepository get() {
    if (instance == null) {
      instance = new ICalInfoRepository();
    }
    return instance;
  }
  
  private ICalInfoRepository() {
    super(IcalInfo.class);
  }
}
