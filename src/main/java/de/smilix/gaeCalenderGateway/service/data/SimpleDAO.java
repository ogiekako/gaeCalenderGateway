package de.smilix.gaeCalenderGateway.service.data;

import javax.persistence.EntityManager;

public final class SimpleDAO {

    public static final SimpleDAO GET = new SimpleDAO();

    private SimpleDAO() {
        // no instantiation
    }

    public <T> T find(Class<T> clazz, Long id) {
        EntityManager em = EMF.get().createEntityManager();
        try {
            return em.find(clazz, id);
        } finally {
            em.close();
        }
    }

    public void persist(Object object) {
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.persist(object);
        } finally {
            em.close();
        }
    }

    public <T> T merge(T object) {
        EntityManager em = EMF.get().createEntityManager();
        try {
            return em.merge(object);
        } finally {
            em.close();
        }
    }
}
