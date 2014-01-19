package de.smilix.gaeCalenderGateway.service.data;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.smilix.gaeCalenderGateway.model.IcalInfo;
import de.smilix.gaeCalenderGateway.model.RawMailIn;

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
  
  public int getCountForStatus(IcalInfo.Status status) {
    EntityManager em = EMF.get().createEntityManager();
    try {
      Query query = em.createQuery("select count(i.status) from " + IcalInfo.class.getSimpleName() + " i where status = :status");
      query.setParameter("status", status);
      Long result = (Long) query.getSingleResult();
      return result.intValue();
    } finally {
      em.close();
    }
  }
}
