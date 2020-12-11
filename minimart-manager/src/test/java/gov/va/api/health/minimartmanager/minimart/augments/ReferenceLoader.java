package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.minimartmanager.minimart.DatamartFilenamePatterns;
import gov.va.api.lighthouse.datamart.DatamartReference;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public class ReferenceLoader {
  @SneakyThrows
  public static <R> List<Optional<DatamartReference>> loadReferencesFor(
      Class<R> resource, Function<R, DatamartReference> function) {
    return Files.walk(Path.of("../datamart"))
        .map(Path::toFile)
        .filter(File::isFile)
        .filter(file -> file.getName().matches(DatamartFilenamePatterns.get().json(resource)))
        .map(f -> mapFromFile(f, resource))
        .map(r -> Optional.ofNullable(function.apply(r)))
        .collect(Collectors.toList());
  }

  @SneakyThrows
  public static <R> R mapFromFile(File file, Class<R> resourceType) {
    return JacksonConfig.createMapper().readValue(file, resourceType);
  }
}
