package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition;
import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.practitioner.DatamartPractitioner;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConditionAsserterAugments {
  private static final List<Optional<DatamartReference>> PRACTITIONER_REFERENCES =
      loadPractitioners();

  static DatamartCondition asserterReferences(Augmentation.Context<DatamartCondition> ctx) {
    return ctx.resource()
        .asserter(Optional.ofNullable(ctx.random(PRACTITIONER_REFERENCES).orElse(null)));
  }

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
    Augmentation.forResources(DatamartCondition.class)
        .whenMatching(Objects::nonNull)
        .transform(ConditionAsserterAugments::asserterReferences)
        .build()
        .rewriteFiles();
  }
}
