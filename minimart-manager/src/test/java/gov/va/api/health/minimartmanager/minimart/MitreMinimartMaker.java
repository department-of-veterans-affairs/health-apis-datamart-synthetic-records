package gov.va.api.health.minimartmanager.minimart;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.dataquery.service.controller.allergyintolerance.AllergyIntoleranceEntity;
import gov.va.api.health.dataquery.service.controller.allergyintolerance.DatamartAllergyIntolerance;
import gov.va.api.health.dataquery.service.controller.condition.ConditionEntity;
import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition;
import gov.va.api.health.dataquery.service.controller.device.DatamartDevice;
import gov.va.api.health.dataquery.service.controller.device.DeviceEntity;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.DatamartDiagnosticReport;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.DiagnosticReportEntity;
import gov.va.api.health.dataquery.service.controller.etlstatus.LatestResourceEtlStatusEntity;
import gov.va.api.health.dataquery.service.controller.immunization.DatamartImmunization;
import gov.va.api.health.dataquery.service.controller.immunization.ImmunizationEntity;
import gov.va.api.health.dataquery.service.controller.location.DatamartLocation;
import gov.va.api.health.dataquery.service.controller.location.LocationEntity;
import gov.va.api.health.dataquery.service.controller.medication.DatamartMedication;
import gov.va.api.health.dataquery.service.controller.medication.MedicationEntity;
import gov.va.api.health.dataquery.service.controller.medicationorder.DatamartMedicationOrder;
import gov.va.api.health.dataquery.service.controller.medicationorder.MedicationOrderEntity;
import gov.va.api.health.dataquery.service.controller.medicationstatement.DatamartMedicationStatement;
import gov.va.api.health.dataquery.service.controller.medicationstatement.MedicationStatementEntity;
import gov.va.api.health.dataquery.service.controller.observation.DatamartObservation;
import gov.va.api.health.dataquery.service.controller.observation.ObservationEntity;
import gov.va.api.health.dataquery.service.controller.organization.DatamartOrganization;
import gov.va.api.health.dataquery.service.controller.organization.OrganizationEntity;
import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient;
import gov.va.api.health.dataquery.service.controller.patient.PatientEntityV2;
import gov.va.api.health.dataquery.service.controller.practitioner.DatamartPractitioner;
import gov.va.api.health.dataquery.service.controller.practitioner.PractitionerEntity;
import gov.va.api.health.dataquery.service.controller.procedure.DatamartProcedure;
import gov.va.api.health.dataquery.service.controller.procedure.ProcedureEntity;
import gov.va.api.health.fallrisk.service.controller.DatamartFallRisk;
import gov.va.api.health.fallrisk.service.controller.FallRiskEntity;
import gov.va.api.health.minimartmanager.ExternalDb;
import gov.va.api.health.minimartmanager.LatestResourceEtlStatusUpdater;
import gov.va.api.health.minimartmanager.LocalH2;
import gov.va.api.lighthouse.datamart.DatamartEntity;
import gov.va.api.lighthouse.datamart.DatamartReference;
import gov.va.api.lighthouse.datamart.HasReplaceableId;
import gov.va.api.lighthouse.scheduling.service.controller.appointment.AppointmentEntity;
import gov.va.api.lighthouse.scheduling.service.controller.appointment.DatamartAppointment;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MitreMinimartMaker {
  private static final List<Class<?>> MANAGED_CLASSES =
      Arrays.asList(
          AllergyIntoleranceEntity.class,
          AppointmentEntity.class,
          ConditionEntity.class,
          DeviceEntity.class,
          DiagnosticReportEntity.class,
          FallRiskEntity.class,
          ImmunizationEntity.class,
          LatestResourceEtlStatusEntity.class,
          LocationEntity.class,
          MedicationOrderEntity.class,
          MedicationEntity.class,
          MedicationStatementEntity.class,
          ObservationEntity.class,
          OrganizationEntity.class,
          PatientEntityV2.class,
          PractitionerEntity.class,
          ProcedureEntity.class);

  private final ThreadLocal<EntityManager> LOCAL_ENTITY_MANAGER = new ThreadLocal<>();

  // Based on the assumption that every appointment has a single patient participant
  private final Function<DatamartAppointment, AppointmentEntity> toAppointmentEntity =
      (dm) ->
          AppointmentEntity.builder()
              .cdwId(dm.cdwId())
              .icn(
                  dm.participant().stream()
                      .filter(p -> "PATIENT".equalsIgnoreCase(p.actor().type().orElse(null)))
                      .map(p -> patientIcn(p.actor()))
                      .collect(Collectors.toList())
                      .get(0))
              .lastUpdated(Instant.now())
              .payload(datamartToString(dm))
              .build();

  private final Function<DatamartDevice, DeviceEntity> toDeviceEntity =
      (dm) ->
          DeviceEntity.builder()
              .cdwId(dm.cdwId())
              .icn(dm.patient().reference().orElse(null))
              .lastUpdated(Instant.now())
              .payload(datamartToString(dm))
              .build();

  private final Function<DatamartPatient, PatientEntityV2> toPatientEntity =
      (dm) ->
          PatientEntityV2.builder()
              .icn(dm.fullIcn())
              .fullName(dm.name())
              .lastName(dm.lastName())
              .firstName(dm.firstName())
              .birthDate(Instant.parse(dm.birthDateTime()))
              .gender(dm.gender())
              .ssn(dm.ssn())
              .managingOrganization(dm.managingOrganization().orElse(null))
              .payload(datamartToString(dm))
              .build();

  private Function<DatamartDiagnosticReport, DiagnosticReportEntity> toDiagnosticReportEntity =
      (dm) ->
          DiagnosticReportEntity.builder()
              .cdwId(dm.cdwId())
              .icn(dm.patient().reference().orElse(null))
              .category("CH")
              .code("panel")
              .dateUtc(Instant.parse(dm.issuedDateTime()))
              .lastUpdated(null)
              .payload(datamartToString(dm))
              .build();

  private int totalRecords;

  private String resourceToSync;

  private EntityManagerFactory entityManagerFactory;

  private List<EntityManager> entityManagers;

  private AtomicInteger addedCount = new AtomicInteger(0);

  private MitreMinimartMaker(String resourceToSync, String configFile) {
    this.resourceToSync = resourceToSync;
    if (configFile == null || configFile.isBlank()) {
      log.info("No config file was specified... Defaulting to local h2 database...");
      entityManagerFactory = new LocalH2("./target/minimart", MANAGED_CLASSES).get();
    } else {
      entityManagerFactory = new ExternalDb(configFile, MANAGED_CLASSES).get();
    }
    entityManagers = Collections.synchronizedList(new ArrayList<>());
  }

  public static void sync(String directory, String resourceToSync, String configFile) {
    MitreMinimartMaker mmm = new MitreMinimartMaker(resourceToSync, configFile);
    log.info("Syncing {} files in {} to db", mmm.resourceToSync, directory);
    mmm.pushToDatabaseByResourceType(directory);
    log.info("{} sync complete", mmm.resourceToSync);
  }

  @SneakyThrows
  private String datamartToString(HasReplaceableId e) {
    return JacksonConfig.createMapper().writeValueAsString(e);
  }

  @SneakyThrows
  private <R extends HasReplaceableId> R fileToDatamart(File f, Class<R> objectType) {
    return JacksonConfig.createMapper().readValue(f, objectType);
  }

  @SneakyThrows
  private String fileToString(File file) {
    return new String(Files.readAllBytes(Paths.get(file.getPath())));
  }

  @SneakyThrows
  private Stream<File> findUniqueFiles(File dmDirectory, String filePattern) {
    List<File> files =
        Files.walk(dmDirectory.toPath())
            .map(Path::toFile)
            .filter(File::isFile)
            .filter(f -> f.getName().matches(filePattern))
            .collect(toList());
    Set<String> fileNames = new HashSet<>();
    List<File> uniqueFiles = new ArrayList<>();
    for (File file : files) {
      if (fileNames.add(file.getName())) {
        uniqueFiles.add(file);
      }
    }
    log.info("{} unique files found", uniqueFiles.size());
    totalRecords = uniqueFiles.size();
    return uniqueFiles.stream();
  }

  private EntityManager getEntityManager() {
    if (LOCAL_ENTITY_MANAGER.get() == null) {
      LOCAL_ENTITY_MANAGER.set(entityManagerFactory.createEntityManager());
      LOCAL_ENTITY_MANAGER.get().getTransaction().begin();
      entityManagers.add(LOCAL_ENTITY_MANAGER.get());
    }
    return LOCAL_ENTITY_MANAGER.get();
  }

  @SneakyThrows
  private void insertByAllergyIntolerance(File file) {
    DatamartAllergyIntolerance dm =
        JacksonConfig.createMapper().readValue(file, DatamartAllergyIntolerance.class);
    AllergyIntoleranceEntity entity =
        AllergyIntoleranceEntity.builder()
            .cdwId(dm.cdwId())
            .icn(patientIcn(dm.patient()))
            .payload(fileToString(file))
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertByCondition(File file) {
    DatamartCondition dm = JacksonConfig.createMapper().readValue(file, DatamartCondition.class);
    ConditionEntity entity =
        ConditionEntity.builder()
            .cdwId(dm.cdwId())
            .icn(patientIcn(dm.patient()))
            .category(jsonValue(dm.category()))
            .clinicalStatus(jsonValue(dm.clinicalStatus()))
            .payload(fileToString(file))
            .build();
    save(entity);
  }

  private void insertByFallRisk(File file) {
    insertByFallRiskPayload(fileToString(file));
  }

  @SneakyThrows
  private void insertByFallRiskNd(File file) {
    Files.lines(file.toPath()).forEach(this::insertByFallRiskPayload);
  }

  @SneakyThrows
  private void insertByFallRiskPayload(String payload) {
    DatamartFallRisk dm = JacksonConfig.createMapper().readValue(payload, DatamartFallRisk.class);
    String cdwId = dm.cdwId();
    FallRiskEntity entity =
        FallRiskEntity.builder()
            .admitDateTime(dm.admitDateTime())
            .currentSpecialty(dm.admitSpecialty())
            .attendingProvider(dm.attendingProvider())
            .cdwId(dm.cdwId())
            .currentWard(dm.currentWard())
            .lastFour(dm.lastFour())
            .morseAdmitDateTime(dm.morseAdmitDateTime())
            .morseAdmitScore(dm.morseAdmitScore())
            .morseCategory(dm.morseCategory())
            .patientFullIcn(dm.patientFullIcn())
            .patientName(dm.patientName())
            .roomBed(dm.roomBed())
            .station(dm.station())
            .stationName(dm.stationName().get())
            .payload(payload)
            .build();
    save(entity, cdwId);
  }

  @SneakyThrows
  private void insertByImmunization(File file) {
    DatamartImmunization dm =
        JacksonConfig.createMapper().readValue(file, DatamartImmunization.class);
    ImmunizationEntity entity =
        ImmunizationEntity.builder()
            .cdwId(dm.cdwId())
            .icn(patientIcn(dm.patient()))
            .payload(fileToString(file))
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertByLocation(File file) {
    DatamartLocation dm = JacksonConfig.createMapper().readValue(file, DatamartLocation.class);
    LocationEntity entity =
        LocationEntity.builder()
            .cdwId(dm.cdwId())
            .name(dm.name())
            .street(dm.address().line1())
            .city(dm.address().city())
            .state(dm.address().state())
            .postalCode(dm.address().postalCode())
            .payload(fileToString(file))
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertByMedication(File file) {
    DatamartMedication dm = JacksonConfig.createMapper().readValue(file, DatamartMedication.class);
    MedicationEntity entity =
        MedicationEntity.builder().cdwId(dm.cdwId()).payload(fileToString(file)).build();
    save(entity);
  }

  @SneakyThrows
  private void insertByMedicationOrder(File file) {
    DatamartMedicationOrder dm =
        JacksonConfig.createMapper().readValue(file, DatamartMedicationOrder.class);
    MedicationOrderEntity entity =
        MedicationOrderEntity.builder()
            .cdwId(dm.cdwId())
            .icn(patientIcn(dm.patient()))
            .payload(fileToString(file))
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertByMedicationStatement(File file) {
    DatamartMedicationStatement dm =
        JacksonConfig.createMapper().readValue(file, DatamartMedicationStatement.class);
    MedicationStatementEntity entity =
        MedicationStatementEntity.builder()
            .cdwId(dm.cdwId())
            .icn(patientIcn(dm.patient()))
            .payload(fileToString(file))
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertByObservation(File file) {
    DatamartObservation dm =
        JacksonConfig.createMapper().readValue(file, DatamartObservation.class);
    ObservationEntity entity =
        ObservationEntity.builder()
            .cdwId(dm.cdwId())
            .icn(dm.subject().isPresent() ? patientIcn(dm.subject().get()) : null)
            .payload(fileToString(file))
            .category(jsonValue(dm.category()))
            .code(
                dm.code().isPresent() && dm.code().get().coding().isPresent()
                    ? dm.code().get().coding().get().code().orElse(null)
                    : null)
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertByOrganization(File file) {
    DatamartOrganization dm =
        JacksonConfig.createMapper().readValue(file, DatamartOrganization.class);
    OrganizationEntity entity =
        OrganizationEntity.builder()
            .cdwId(dm.cdwId())
            .npi(dm.npi().orElse(null))
            .payload(fileToString(file))
            .street(
                trimToNull(
                    trimToEmpty(dm.address().line1()) + " " + trimToEmpty(dm.address().line2())))
            .name(dm.name())
            .city(dm.address().city())
            .state(dm.address().state())
            .postalCode(dm.address().postalCode())
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertByPractitioner(File file) {
    DatamartPractitioner dm =
        JacksonConfig.createMapper().readValue(file, DatamartPractitioner.class);
    PractitionerEntity entity =
        PractitionerEntity.builder()
            .cdwId(dm.cdwId())
            .npi(dm.npi().orElse(null))
            .familyName(dm.name().family())
            .givenName(dm.name().given())
            .payload(fileToString(file))
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertByProcedure(File file) {
    DatamartProcedure dm = JacksonConfig.createMapper().readValue(file, DatamartProcedure.class);
    Long performedOnEpoch =
        dm.performedDateTime().isPresent() ? dm.performedDateTime().get().toEpochMilli() : null;
    ProcedureEntity entity =
        ProcedureEntity.builder()
            .cdwId(dm.cdwId())
            .icn(patientIcn(dm.patient()))
            .performedOnEpochTime(performedOnEpoch)
            .payload(fileToString(file))
            .build();
    save(entity);
  }

  @SneakyThrows
  private void insertResourceByPattern(
      File dmDirectory, String filePattern, Consumer<File> fileWriter) {
    findUniqueFiles(dmDirectory, filePattern).parallel().forEach(fileWriter);
  }

  @SneakyThrows
  <E extends Enum<E>> String jsonValue(E e) {
    JsonProperty jsonProperty = e.getClass().getField(e.name()).getAnnotation(JsonProperty.class);
    if (jsonProperty != null && jsonProperty.value() != null) {
      return jsonProperty.value();
    }
    return e.toString();
  }

  private String patientIcn(DatamartReference dm) {
    if (dm != null && dm.reference().isPresent()) {
      return dm.reference().get().replaceAll("http.*/fhir/v0/dstu2/Patient/", "");
    }
    return null;
  }

  private void pushToDatabaseByResourceType(String directory) {
    File dmDirectory = new File(directory);
    DatabaseLoader loader = new DatabaseLoader(dmDirectory);
    if (dmDirectory.listFiles() == null) {
      log.error("No files in directory {}", directory);
      throw new RuntimeException("No files found in directory: " + directory);
    }
    switch (resourceToSync) {
      case "AllergyIntolerance":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartAllergyIntolerance.class),
            this::insertByAllergyIntolerance);
        break;
      case "Appointment":
        loader.insertResourceByType(DatamartAppointment.class, toAppointmentEntity);
        break;
      case "Condition":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartCondition.class),
            this::insertByCondition);
        break;
      case "Device":
        loader.insertResourceByType(DatamartDevice.class, toDeviceEntity);
        break;
      case "DiagnosticReport":
        loader.insertResourceByType(DatamartDiagnosticReport.class, toDiagnosticReportEntity);
        break;
      case "FallRisk":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartFallRisk.class),
            this::insertByFallRisk);
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().ndjson(DatamartFallRisk.class),
            this::insertByFallRiskNd);
        break;
      case "Immunization":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartImmunization.class),
            this::insertByImmunization);
        break;
      case "Location":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartLocation.class),
            this::insertByLocation);
        break;
      case "Medication":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartMedication.class),
            this::insertByMedication);
        break;
      case "MedicationOrder":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartMedicationOrder.class),
            this::insertByMedicationOrder);
        break;
      case "MedicationStatement":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartMedicationStatement.class),
            this::insertByMedicationStatement);
        break;
      case "Observation":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartObservation.class),
            this::insertByObservation);
        break;
      case "Organization":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartOrganization.class),
            this::insertByOrganization);
        break;
      case "Patient":
        loader.insertResourceByType(DatamartPatient.class, toPatientEntity);
        break;
      case "Practitioner":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartPractitioner.class),
            this::insertByPractitioner);
        break;
      case "Procedure":
        insertResourceByPattern(
            dmDirectory,
            DatamartFilenamePatterns.get().json(DatamartProcedure.class),
            this::insertByProcedure);
        break;
      default:
        throw new RuntimeException("Couldnt determine resource type for file: " + resourceToSync);
    }
    LatestResourceEtlStatusUpdater.create(getEntityManager()).updateEtlTable(resourceToSync);
    /*
     * Commit and clean up the transactions for the entity managers from
     * the various threads.
     */
    for (EntityManager entityManager : entityManagers) {
      entityManager.getTransaction().commit();
      entityManager.close();
      // HACK
      LOCAL_ENTITY_MANAGER.remove();
    }
    entityManagers.clear();
    log.info("Added {} {} entities", addedCount.get(), resourceToSync);
  }

  private <T extends DatamartEntity> void save(T entity) {
    EntityManager entityManager = getEntityManager();
    boolean exists = entityManager.find(entity.getClass(), entity.cdwId()) != null;
    updateOrAddEntity(exists, entityManager, entity);
  }

  private <T> void save(T entity, String identifier) {
    EntityManager entityManager = getEntityManager();
    boolean exists = entityManager.find(entity.getClass(), identifier) != null;
    updateOrAddEntity(exists, entityManager, entity);
  }

  private <T> void updateOrAddEntity(boolean exists, EntityManager entityManager, T entity) {
    if (!exists) {
      entityManager.persist(entity);
    } else {
      entityManager.merge(entity);
    }
    addedCount.incrementAndGet();
    if ((totalRecords - addedCount.get() != 0) && (totalRecords - addedCount.get()) % 10000 == 0) {
      log.info("{} files remaining", totalRecords - addedCount.get());
    }
    entityManager.flush();
    entityManager.clear();
  }

  private class DatabaseLoader {
    File datamartDirectory;

    DatabaseLoader(File datamartDirectory) {
      this.datamartDirectory = datamartDirectory;
    }

    public <DM extends HasReplaceableId, E extends DatamartEntity> void insertResourceByType(
        Class<DM> resourceType, Function<DM, E> toDatamartEntity) {
      findUniqueFiles(datamartDirectory, DatamartFilenamePatterns.get().json(resourceType))
          .parallel()
          .forEach(
              f -> {
                DM dm = fileToDatamart(f, resourceType);
                DatamartEntity entity = toDatamartEntity.apply(dm);
                save(entity);
              });
    }
  }
}
