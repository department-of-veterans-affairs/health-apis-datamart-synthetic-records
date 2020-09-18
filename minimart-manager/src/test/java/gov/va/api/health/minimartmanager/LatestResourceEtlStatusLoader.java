package gov.va.api.health.minimartmanager;

import java.text.MessageFormat;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class LatestResourceEtlStatusLoader {
  private final ThreadLocal<EntityManager> LOCAL_ENTITY_MANAGER = new ThreadLocal<>();

  @NonNull private EntityManagerFactory entityManagerFactory;
  // "SELECT row FROM app.Latest_Resource_ETL_Status WHERE row.ResourceName LIKE :resourceName"

  private boolean checkExists(String resource, EntityManager entityManager) {
    Query q =
        entityManager
            .createNativeQuery(
                "SELECT ResourceName FROM dq.app.Latest_Resource_ETL_Status WHERE ResourceName = ?1")
            .setParameter(1, resource);

    if (q.getResultList().size() > 0) {
      return true;
    }
    return false;
  }

  private EntityManager getEntityManager() {
    if (LOCAL_ENTITY_MANAGER.get() == null) {
      LOCAL_ENTITY_MANAGER.set(entityManagerFactory.createEntityManager());
      LOCAL_ENTITY_MANAGER.get().getTransaction().begin();
    }
    return LOCAL_ENTITY_MANAGER.get();
  }

  public void insertIntoEtlTable(String resource) {
    EntityManager entityManager = getEntityManager();
    boolean exists = checkExists(resource, entityManager);
    java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
    String query;
    if (exists) {
      query =
          MessageFormat.format(
              "UPDATE [dq].[app].[Latest_Resource_ETL_Status] SET EndDateTimeUTC = {} WHERE ResourceName = {}",
              now.toString(),
              resource);
    } else {
      query =
          MessageFormat.format(
              "INSERT INTO [dq].[app].[Latest_Resource_ETL_Status] VALUES ({}, {})", resource, now.toString());
    }
    entityManager.getTransaction().begin();
    entityManager.createNativeQuery(query);
    entityManager.getTransaction().commit();
    entityManager.close();
    LOCAL_ENTITY_MANAGER.remove();
  }
}
