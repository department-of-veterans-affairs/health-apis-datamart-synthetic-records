package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.location.DatamartLocation;
import gov.va.api.health.dataquery.service.controller.procedure.DatamartProcedure;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.SneakyThrows;

public class ProcedureLocationAugments {
  private static final List<Optional<DatamartReference>> LOCATION_REFERENCES = locationReferences();

  static DatamartProcedure addLocation(Augmentation.Context<DatamartProcedure> ctx) {
    return ctx.resource().location(ctx.random(LOCATION_REFERENCES));
  }

  /* Will scale if new locations are ever added.
   * Will update if any location is changed. */
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
    Augmentation.forResources(DatamartProcedure.class)
        .whenMatching(Objects::nonNull)
        .transform(ProcedureLocationAugments::addLocation)
        .build()
        .rewriteFiles();
  }

  @SneakyThrows
  private static DatamartLocation toLocation(File file) {
    return JacksonConfig.createMapper().readValue(file, DatamartLocation.class);
  }
}
