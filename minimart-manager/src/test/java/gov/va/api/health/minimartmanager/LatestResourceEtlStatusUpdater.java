package gov.va.api.health.minimartmanager;

import gov.va.api.health.dataquery.service.controller.etlstatus.LatestResourceEtlStatusEntity;
import java.time.Instant;
import javax.persistence.EntityManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
public class LatestResourceEtlStatusUpdater {

  @NonNull private EntityManager entityManager;

  public void updateEtlTable(String resource) {

    boolean exists = entityManager.find(LatestResourceEtlStatusEntity.class, resource) != null;
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
