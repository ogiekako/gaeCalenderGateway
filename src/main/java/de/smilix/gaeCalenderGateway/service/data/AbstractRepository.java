package de.smilix.gaeCalenderGateway.service.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public abstract class AbstractRepository<E> {

  private Class<E> type;

  protected AbstractRepository(Class<E> type) {
    this.type = type;
  }

  public void addEntry(E entry) {
    SimpleDAO.GET.persist(entry);
  }

  public E merge(E entry) {
    return SimpleDAO.GET.merge(entry);
  }

  public List<E> getAll() {
    EntityManager em = EMF.get().createEntityManager();
    try {
      Query query = em.createQuery("select from " + this.type.getName());
      List<E> resultList = query.getResultList();
      resultList.size();
      return resultList;
    } finally {
      em.close();
    }
  }

  public E find(Long id) {
    EntityManager em = EMF.get().createEntityManager();
    try {
      return em.find(this.type, id);
    } finally {
      em.close();
    }
  }
}
