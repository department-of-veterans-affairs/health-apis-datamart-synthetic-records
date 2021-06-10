package gov.va.api.health.minimartmanager.minimart.augments;

import static com.google.common.base.Preconditions.checkArgument;

import gov.va.api.health.dataquery.service.controller.allergyintolerance.DatamartAllergyIntolerance;
import gov.va.api.health.dataquery.service.controller.appointment.DatamartAppointment;
import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition;
import gov.va.api.health.dataquery.service.controller.medicationorder.DatamartMedicationOrder;
import gov.va.api.health.dataquery.service.controller.observation.DatamartObservation;
import gov.va.api.lighthouse.datamart.DatamartReference;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PractitionerReferenceSuffixAugments {
  static void addIdSuffix(Optional<DatamartReference> maybeRef) {
    addIdSuffix(maybeRef.orElse(null));
  }

  static void addIdSuffix(DatamartReference ref) {
    if (ref == null) {
      return;
    }
    checkArgument("Practitioner".equals(ref.type().orElse(null)));
    checkArgument(ref.reference().isPresent());
    String id = ref.reference().get();
    if (!id.endsWith(":S")) {
      ref.reference(Optional.of(id + ":S"));
    }
  }

  static DatamartAllergyIntolerance addPractitionerIdSuffixToAllergyIntolerance(
      Augmentation.Context<DatamartAllergyIntolerance> ctx) {
    DatamartAllergyIntolerance dm = ctx.resource();
    Optional.ofNullable(dm.notes()).orElse(List.of()).forEach(n -> addIdSuffix(n.practitioner()));
    addIdSuffix(dm.recorder());
    return dm;
  }

  static DatamartAppointment addPractitionerIdSuffixToAppointment(
      Augmentation.Context<DatamartAppointment> ctx) {
    DatamartAppointment dm = ctx.resource();
    Optional.ofNullable(dm.participant()).orElse(List.of()).stream()
        .filter(ref -> "Practitioner".equals(ref.type().orElse(null)))
        .forEach(ref -> addIdSuffix(ref));
    return dm;
  }

  static DatamartCondition addPractitionerIdSuffixToCondition(
      Augmentation.Context<DatamartCondition> ctx) {
    DatamartCondition dm = ctx.resource();
    addIdSuffix(dm.asserter());
    return dm;
  }

  static DatamartMedicationOrder addPractitionerIdSuffixToMedicationOrder(
      Augmentation.Context<DatamartMedicationOrder> ctx) {
    DatamartMedicationOrder dm = ctx.resource();
    addIdSuffix(dm.prescriber());
    return dm;
  }

  static DatamartObservation addPractitionerIdSuffixToObservation(
      Augmentation.Context<DatamartObservation> ctx) {
    DatamartObservation dm = ctx.resource();
    Optional.ofNullable(dm.performer()).orElse(List.of()).stream()
        .filter(ref -> "Practitioner".equals(ref.type().orElse(null)))
        .forEach(ref -> addIdSuffix(ref));
    return dm;
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartAllergyIntolerance.class)
        .whenMatching(Objects::nonNull)
        .transform(PractitionerReferenceSuffixAugments::addPractitionerIdSuffixToAllergyIntolerance)
        .build()
        .rewriteFiles();
    Augmentation.forResources(DatamartAppointment.class)
        .whenMatching(Objects::nonNull)
        .transform(PractitionerReferenceSuffixAugments::addPractitionerIdSuffixToAppointment)
        .build()
        .rewriteFiles();
    Augmentation.forResources(DatamartCondition.class)
        .whenMatching(Objects::nonNull)
        .transform(PractitionerReferenceSuffixAugments::addPractitionerIdSuffixToCondition)
        .build()
        .rewriteFiles();
    Augmentation.forResources(DatamartMedicationOrder.class)
        .whenMatching(Objects::nonNull)
        .transform(PractitionerReferenceSuffixAugments::addPractitionerIdSuffixToMedicationOrder)
        .build()
        .rewriteFiles();
    Augmentation.forResources(DatamartObservation.class)
        .whenMatching(Objects::nonNull)
        .transform(PractitionerReferenceSuffixAugments::addPractitionerIdSuffixToObservation)
        .build()
        .rewriteFiles();
  }
}
