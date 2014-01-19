package de.smilix.gaeCalenderGateway.service.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
  
  public int getCountForStatus(RawMailIn.Status status) {
    EntityManager em = EMF.get().createEntityManager();
    try {
      Query query = em.createQuery("select count(r.status) from " + RawMailIn.class.getSimpleName() + " r where status = :status");
      query.setParameter("status", status);
      Long result = (Long) query.getSingleResult();
      return result.intValue();
    } finally {
      em.close();
    }
  }
  
}
