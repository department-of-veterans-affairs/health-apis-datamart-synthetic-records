package gov.va.api.health.minimartmanager.minimart;

import gov.va.api.health.vistafhirquery.service.controller.observation.VitalVuidMappingEntity;
import java.util.function.Consumer;
import javax.persistence.EntityManager;

public class MinimartRowRemover {
  public static Consumer<EntityManager> removeBloodPressure552844LoincCode() {
    return removeEntity(
        VitalVuidMappingEntity.builder()
            .codingSystemId(Short.valueOf("11"))
            .sourceValue("4500634")
            .code("55284-4")
            .uri("http://loinc.org")
            .build());
  }

  public static Consumer<EntityManager> removeEntity(Object entity) {
    return em -> {
      em.remove(em.contains(entity) ? entity : em.merge(entity));
      em.flush();
      em.clear();
    };
  }

  public static Consumer<EntityManager> removeOxygenSaturation594085LoincCode() {
    return removeEntity(
        VitalVuidMappingEntity.builder()
            .codingSystemId(Short.valueOf("11"))
            .sourceValue("4500637")
            .code("59408-5")
            .uri("http://loinc.org")
            .build());
  }
}
