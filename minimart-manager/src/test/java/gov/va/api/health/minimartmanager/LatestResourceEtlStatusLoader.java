package gov.va.api.health.minimartmanager;

import gov.va.api.health.dataquery.service.controller.etlstatus.LatestResourceEtlStatusEntity;
import java.time.Instant;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class LatestResourceEtlStatusLoader {

  private boolean checkExists(String resource, EntityManager entityManager) {
    return entityManager.find(LatestResourceEtlStatusEntity.class, resource) != null;
  }

  public void insertIntoEtlTable(String resource, EntityManager entityManager) {

    boolean exists = checkExists(resource, entityManager);
    Instant now = Instant.now();
    LatestResourceEtlStatusEntity statusEntity =
        LatestResourceEtlStatusEntity.builder().resourceName(resource).endDateTime(now).build();
    if (!exists) {
      entityManager.persist(statusEntity);
    } else {
      entityManager.merge(statusEntity);
    }
  }
}
