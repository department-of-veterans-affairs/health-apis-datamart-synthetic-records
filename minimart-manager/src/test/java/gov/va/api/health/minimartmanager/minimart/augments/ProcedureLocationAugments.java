package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.location.DatamartLocation;
import gov.va.api.health.dataquery.service.controller.procedure.DatamartProcedure;
import gov.va.api.health.minimartmanager.minimart.DatamartFilenamePatterns;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public class ProcedureLocationAugments {
  private static final List<DatamartReference> LOCATION_REFERENCES = locationReferences();

  static DatamartProcedure addLocation(Augmentation.Context<DatamartProcedure> ctx) {
    DatamartReference r;
    var indexOf = ctx.random().nextInt(LOCATION_REFERENCES.size());
    r = LOCATION_REFERENCES.get(indexOf);
    ctx.resource().location(Optional.ofNullable(r));
    return ctx.resource();
  }

  /* Will scale if new locations are ever added.
   * Will update if any location is changed. */
  @SneakyThrows
  private static List<DatamartReference> locationReferences() {
    List<DatamartReference> references =
        Files.walk(Path.of("../datamart"))
            .map(Path::toFile)
            .filter(File::isFile)
            .filter(
                file ->
                    file.getName()
                        .matches(DatamartFilenamePatterns.get().json(DatamartLocation.class)))
            .map(ProcedureLocationAugments::toLocation)
            .map(
                loc ->
                    DatamartReference.builder()
                        .type(Optional.of("Location"))
                        .reference(Optional.ofNullable(loc.cdwId()))
                        .display(Optional.ofNullable(loc.name()))
                        .build())
            .collect(Collectors.toList());
    // has an equal chance to be one of three locations or null
    references.add(null);
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
