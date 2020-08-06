package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.allergyintolerance.DatamartAllergyIntolerance;
import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.practitioner.DatamartPractitioner;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AllergyIntoleranceRecorderAugments {

  private static final List<Optional<DatamartReference>> PRACTITIONER_REFERENCES =
      loadPractitioners();

  private static List<Optional<DatamartReference>> loadPractitioners() {
    List<Optional<DatamartReference>> p =
        ReferenceLoader.loadReferencesFor(
            DatamartPractitioner.class,
            dm -> {
              String whoDis =
                  dm.name().prefix().orElse("")
                      + " "
                      + dm.name().given()
                      + " "
                      + dm.name().family()
                      + " "
                      + dm.name().suffix().orElse("");
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
    Augmentation.forResources(DatamartAllergyIntolerance.class)
        .whenMatching(Objects::nonNull)
        .transform(AllergyIntoleranceRecorderAugments::recorderReferences)
        .build()
        .rewriteFiles();
  }

  static DatamartAllergyIntolerance recorderReferences(
      Augmentation.Context<DatamartAllergyIntolerance> ctx) {
    return ctx.resource()
        .recorder(Optional.ofNullable(ctx.random(PRACTITIONER_REFERENCES).orElse(null)));
  }
}
