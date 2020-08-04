package gov.va.api.health.minimartmanager.minimart.augments;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.minimartmanager.minimart.DatamartFilenamePatterns;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;

@Value
@AllArgsConstructor
@Builder
class Augmentation<T> {
  final ObjectMapper mapper = JacksonConfig.createMapper();

  Class<T> datamartResourceType;

  Predicate<T> whenMatching;

  Function<Context<T>, T> transform;

  Configuration configuration;

  static <R> AugmentationBuilder<R> forResources(Class<R> datamartResourceType) {
    return Augmentation.<R>builder()
        .datamartResourceType(datamartResourceType)
        .configuration(Configuration.fromSystemProperties());
  }

  @SneakyThrows
  private void rewrite(Transformation<T> transformation) {
    System.out.println("Rewriting " + transformation.context().path());
    mapper.writeValue(transformation.context().path().toFile(), transformation.result());
  }

  @SneakyThrows
  void rewriteFiles() {
    if (!configuration.datamartDirectory().toFile().exists()) {
      throw new FileNotFoundException(configuration.datamartDirectory().toFile().getAbsolutePath());
    }
    var filenamePattern =
        Pattern.compile(DatamartFilenamePatterns.get().json(datamartResourceType));
    AtomicInteger count = new AtomicInteger(0);
    Files.find(
            configuration.datamartDirectory(),
            10,
            (f, a) -> filenamePattern.matcher(f.getFileName().toString()).matches())
        .parallel()
        .map(path -> toContext(count, path))
        .filter(ctx -> whenMatching().test(ctx.resource()))
        .map(ctx -> Transformation.<T>builder().context(ctx).result(transform.apply(ctx)).build())
        .forEach(t -> rewrite(t));
  }

  @SneakyThrows
  private Context<T> toContext(AtomicInteger count, Path path) {
    return Context.<T>builder()
        .count(count.incrementAndGet())
        .path(path)
        .resource(mapper.readValue(path.toFile(), datamartResourceType))
        .random(new SecureRandom())
        .build();
  }

  @Value
  @Builder
  static class Configuration {
    Path datamartDirectory;

    static Configuration fromSystemProperties() {
      return Configuration.builder().datamartDirectory(Path.of("../datamart")).build();
    }
  }

  @Value
  @Builder
  static class Context<T> {
    T resource;

    int count;

    Path path;

    Random random;

    <T> T random(List<T> choices) {
      return choices.get(random.nextInt(choices.size()));
    }
  }

  @Value
  @Builder
  static class Transformation<T> {
    Context<T> context;

    T result;
  }
}
