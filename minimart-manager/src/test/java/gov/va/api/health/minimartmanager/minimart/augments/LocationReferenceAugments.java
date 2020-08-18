package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.immunization.DatamartImmunization;
import gov.va.api.health.dataquery.service.controller.location.DatamartLocation;
import gov.va.api.health.dataquery.service.controller.procedure.DatamartProcedure;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.SneakyThrows;

public class LocationReferenceAugments {
  private static final List<Optional<DatamartReference>> LOCATION_REFERENCES = locationReferences();

  static DatamartImmunization addLocationToImmunization(
      Augmentation.Context<DatamartImmunization> ctx) {
    return ctx.resource().location(ctx.random(LOCATION_REFERENCES));
  }

  static DatamartProcedure addLocationToProcedure(Augmentation.Context<DatamartProcedure> ctx) {
    return ctx.resource().location(ctx.random(LOCATION_REFERENCES));
  }

  @SneakyThrows
  private static List<Optional<DatamartReference>> locationReferences() {
    List<Optional<DatamartReference>> references =
        ReferenceLoader.loadReferencesFor(
            DatamartLocation.class,
            loc ->
                DatamartReference.builder()
                    .type(Optional.of("Location"))
                    .reference(Optional.ofNullable(loc.cdwId()))
                    .display(Optional.ofNullable(loc.name()))
                    .build());
    // has an equal chance to be one of three locations or null
    references.add(Optional.empty());
    return references;
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartImmunization.class)
        .whenMatching(Objects::nonNull)
        .transform(LocationReferenceAugments::addLocationToImmunization)
        .build()
        .rewriteFiles();
    Augmentation.forResources(DatamartProcedure.class)
        .whenMatching(Objects::nonNull)
        .transform(LocationReferenceAugments::addLocationToProcedure)
        .build()
        .rewriteFiles();
  }
}
