package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.observation.DatamartObservation;
import gov.va.api.lighthouse.datamart.DatamartCoding;
import java.util.Optional;

public class ObservationBloodPressureLoincCodeAugments {
  private static final Optional<DatamartObservation.CodeableConcept> NEW_BLOOD_PRESSURE_LOINC =
      Optional.of(
          DatamartObservation.CodeableConcept.builder()
              .coding(
                  Optional.of(
                      DatamartCoding.builder()
                          .system(Optional.of("http://loinc.org"))
                          .code(Optional.of("85354-9"))
                          .display(Optional.of("Blood pressure systolic and diastolic"))
                          .build()))
              .build());

  public static void main(String[] args) {
    Augmentation.forResources(DatamartObservation.class)
        .whenMatching(p -> "55284-4".equals(p.code().get().coding().get().code().get()))
        .transform(ObservationBloodPressureLoincCodeAugments::replaceBloodPressureLoincCode)
        .build()
        .rewriteFiles();
  }

  static DatamartObservation replaceBloodPressureLoincCode(
      Augmentation.Context<DatamartObservation> ctx) {
    return ctx.resource().code(NEW_BLOOD_PRESSURE_LOINC);
  }
}
