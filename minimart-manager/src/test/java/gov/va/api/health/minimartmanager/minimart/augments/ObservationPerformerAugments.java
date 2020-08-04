package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.observation.DatamartObservation;
import gov.va.api.health.dataquery.service.controller.organization.DatamartOrganization;
import gov.va.api.health.dataquery.service.controller.practitioner.DatamartPractitioner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ObservationPerformerAugments {
  private static final List<Optional<DatamartReference>> ORGANIZATION_REFERENCES =
      loadOrganizations();

  private static final List<Optional<DatamartReference>> PRACTITIONER_REFERENCES =
      loadPractitioners();

  private static List<Optional<DatamartReference>> loadOrganizations() {
    List<Optional<DatamartReference>> o =
        ReferenceLoader.loadReferencesFor(
            DatamartOrganization.class,
            dm ->
                DatamartReference.builder()
                    .type(Optional.of("Organization"))
                    .reference(Optional.ofNullable(dm.cdwId()))
                    .display(Optional.ofNullable(dm.name()))
                    .build());
    o.add(Optional.empty());
    return o;
  }

  private static List<Optional<DatamartReference>> loadPractitioners() {
    List<Optional<DatamartReference>> p =
        ReferenceLoader.loadReferencesFor(
            DatamartPractitioner.class,
            dm -> {
              String whoDis =
                  dm.name().prefix()
                      + " "
                      + dm.name().given()
                      + " "
                      + dm.name().family()
                      + " "
                      + dm.name().suffix();
              return DatamartReference.builder()
                  .type(Optional.of("Practitioner"))
                  .reference(Optional.ofNullable(dm.cdwId()))
                  .display(Optional.of(whoDis))
                  .build();
            });
    p.add(Optional.empty());
    return p;
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartObservation.class)
        .whenMatching(Objects::nonNull)
        .transform(ObservationPerformerAugments::performerReferences)
        .build()
        .rewriteFiles();
  }

  static DatamartObservation performerReferences(Augmentation.Context<DatamartObservation> ctx) {
    List<DatamartReference> performer = new ArrayList<>(2);
    ctx.random(ORGANIZATION_REFERENCES).ifPresent(performer::add);
    ctx.random(PRACTITIONER_REFERENCES).ifPresent(performer::add);
    return ctx.resource().performer(performer.isEmpty() ? null : performer);
  }
}
