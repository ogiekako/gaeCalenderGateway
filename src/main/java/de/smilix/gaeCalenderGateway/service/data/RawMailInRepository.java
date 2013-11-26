package de.smilix.gaeCalenderGateway.service.data;

import de.smilix.gaeCalenderGateway.model.RawMailIn;


public class RawMailInRepository extends AbstractRepository<RawMailIn>{

  private static RawMailInRepository instance;
  
  public static RawMailInRepository get() {
    if (instance == null) {
      instance = new RawMailInRepository();
    }
    return instance;
  }
  
  private RawMailInRepository() {
    super(RawMailIn.class);
  }
  
  
}
