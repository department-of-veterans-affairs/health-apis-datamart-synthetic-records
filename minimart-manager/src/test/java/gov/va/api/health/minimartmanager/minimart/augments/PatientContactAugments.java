package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient;
import gov.va.api.health.minimartmanager.minimart.augments.Augmentation.Context;
import java.util.List;

public class PatientContactAugments {
  static DatamartPatient addContact(Context<DatamartPatient> ctx) {
    System.out.println(ctx.resource().toString());
    ctx.resource().contact(List.of(DatamartPatient.Contact.builder().name("HE MAN").build()));
    return ctx.resource();
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartPatient.class)
        .whenMatching(p -> p.fullIcn().equals("17"))
        .transform(PatientContactAugments::addContact)
        .build()
        .rewriteFiles();
  }
}
