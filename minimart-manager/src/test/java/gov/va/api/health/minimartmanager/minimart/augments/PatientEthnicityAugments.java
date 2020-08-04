package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient;
import java.util.List;

public class PatientEthnicityAugments {

  private static final List<DatamartPatient.Ethnicity> DATAMART_ETHNICITIES =
      List.of(
          DatamartPatient.Ethnicity.builder()
              .display("*Missing*")
              .abbrev("*")
              .hl7("*Missing*")
              .build(),
          DatamartPatient.Ethnicity.builder()
              .display("*Unknown at this time*")
              .abbrev("*")
              .hl7("*Unknown at this time*")
              .build(),
          DatamartPatient.Ethnicity.builder()
              .display("DECLINED TO ANSWER")
              .abbrev("D")
              .hl7("0000-0")
              .build(),
          DatamartPatient.Ethnicity.builder()
              .display("HISPANIC OR LATINO")
              .abbrev("H")
              .hl7("2135-2")
              .build(),
          DatamartPatient.Ethnicity.builder()
              .display("NOT HISPANIC OR LATINO")
              .abbrev("N")
              .hl7("2186-5")
              .build(),
          DatamartPatient.Ethnicity.builder()
              .display("UNKNOWN BY PATIENT")
              .abbrev("U")
              .hl7("9999-4")
              .build());

  static DatamartPatient addEthnicity(Augmentation.Context<DatamartPatient> ctx) {
    return ctx.resource().ethnicity(ctx.random(DATAMART_ETHNICITIES));
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartPatient.class)
        .whenMatching(p -> p.ethnicity() == null)
        .transform(PatientEthnicityAugments::addEthnicity)
        .build()
        .rewriteFiles();
  }
}
