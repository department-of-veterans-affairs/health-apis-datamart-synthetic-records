package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.allergyintolerance.DatamartAllergyIntolerance;
import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition;
import gov.va.api.health.dataquery.service.controller.medicationorder.DatamartMedicationOrder;
import gov.va.api.health.dataquery.service.controller.practitioner.DatamartPractitioner;
import gov.va.api.lighthouse.datamart.DatamartReference;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PractitionerReferenceAugments {
  private static final List<Optional<DatamartReference>> PRACTITIONER_REFERENCES =
      loadPractitioners();

  static DatamartAllergyIntolerance addPractitionerToAllergyIntolerance(
      Augmentation.Context<DatamartAllergyIntolerance> ctx) {
    List<DatamartAllergyIntolerance.Note> note = ctx.resource().notes();
    if (note != null && !note.isEmpty()) {
      note.forEach(
          n -> {
            n.practitioner(ctx.random(PRACTITIONER_REFERENCES));
          });
    }
    return ctx.resource().recorder(ctx.random(PRACTITIONER_REFERENCES));
  }

  static DatamartCondition addPractitionerToCondition(Augmentation.Context<DatamartCondition> ctx) {
    return ctx.resource().asserter(ctx.random(PRACTITIONER_REFERENCES));
  }

  static DatamartMedicationOrder addPractitionerToMedicationOrder(
      Augmentation.Context<DatamartMedicationOrder> ctx) {
    return ctx.resource().prescriber(ctx.random(PRACTITIONER_REFERENCES).orElse(null));
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
    Augmentation.forResources(DatamartAllergyIntolerance.class)
        .whenMatching(Objects::nonNull)
        .transform(PractitionerReferenceAugments::addPractitionerToAllergyIntolerance)
        .build()
        .rewriteFiles();
    Augmentation.forResources(DatamartCondition.class)
        .whenMatching(Objects::nonNull)
        .transform(PractitionerReferenceAugments::addPractitionerToCondition)
        .build()
        .rewriteFiles();
    // See ImmunizationPrescriberAugments
    // See ImmunizationRequesterAugments
    Augmentation.forResources(DatamartMedicationOrder.class)
        .whenMatching(Objects::nonNull)
        .transform(PractitionerReferenceAugments::addPractitionerToMedicationOrder)
        .build()
        .rewriteFiles();
    // See ObservationPerformerAugments
  }
}
