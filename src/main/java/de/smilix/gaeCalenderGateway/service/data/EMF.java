package de.smilix.gaeCalenderGateway.service.data;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Holger
 */
public final class EMF {
  
  private static final EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("transactions-optional");

  private EMF() {
  }

  public static EntityManagerFactory get() {
    return emfInstance;
  }
}
