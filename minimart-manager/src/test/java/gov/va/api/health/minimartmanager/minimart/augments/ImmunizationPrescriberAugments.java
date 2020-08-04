package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.immunization.DatamartImmunization;
import gov.va.api.health.minimartmanager.minimart.augments.Augmentation.Context;
import java.util.Optional;

public class ImmunizationPrescriberAugments {
  public static void main(String[] args) {
    Augmentation.forResources(DatamartImmunization.class)
        .whenMatching(p -> p.performer().isPresent())
        .transform(ImmunizationPrescriberAugments::removePrescriber)
        .build()
        .rewriteFiles();
  }

  static DatamartImmunization removePrescriber(Context<DatamartImmunization> ctx) {
    ctx.resource().performer(Optional.empty());
    return ctx.resource();
  }
}
