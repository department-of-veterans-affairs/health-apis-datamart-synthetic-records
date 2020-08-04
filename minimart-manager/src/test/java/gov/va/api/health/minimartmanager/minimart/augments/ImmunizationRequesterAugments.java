package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.immunization.DatamartImmunization;
import gov.va.api.health.minimartmanager.minimart.augments.Augmentation.Context;
import java.util.Optional;

public class ImmunizationRequesterAugments {
  static DatamartImmunization removeRequester(Context<DatamartImmunization> ctx) {
    ctx.resource().requester(Optional.empty());
    return ctx.resource();
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartImmunization.class)
        .whenMatching(p -> p.requester().isPresent())
        .transform(ImmunizationRequesterAugments::removeRequester)
        .build()
        .rewriteFiles();
  }
}
