package gov.va.api.health.minimartmanager;

import gov.va.api.health.dataquery.service.controller.allergyintolerance.AllergyIntoleranceEntity;
import gov.va.api.health.dataquery.service.controller.condition.ConditionEntity;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.DiagnosticReportCrossEntity;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.DiagnosticReportEntity;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.DiagnosticReportsEntity;
import gov.va.api.health.dataquery.service.controller.immunization.ImmunizationEntity;
import gov.va.api.health.dataquery.service.controller.location.LocationEntity;
import gov.va.api.health.dataquery.service.controller.medication.MedicationEntity;
import gov.va.api.health.dataquery.service.controller.medicationorder.MedicationOrderEntity;
import gov.va.api.health.dataquery.service.controller.medicationstatement.MedicationStatementEntity;
import gov.va.api.health.dataquery.service.controller.observation.ObservationEntity;
import gov.va.api.health.dataquery.service.controller.organization.OrganizationEntity;
import gov.va.api.health.dataquery.service.controller.patient.PatientEntityV2;
import gov.va.api.health.dataquery.service.controller.practitioner.PractitionerEntity;
import gov.va.api.health.dataquery.service.controller.procedure.ProcedureEntity;
import gov.va.api.health.fallrisk.service.controller.FallRiskEntity;
import gov.va.api.health.minimartmanager.minimart.MitreMinimartMaker;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Populate a database with datamart style data. Pull in the datamart entity models from Data-Query,
 * Fall-Risk, etc...
 *
 * <p>The push to database is now integrated into the maven test lifecycle. This push requires a few
 * system properties to be provided:
 *
 * <p>(REQUIRED) DIRECTORY_TO_IMPORT : path to the datamart data to be loaded.
 *
 * <p>(OPTIONAL) PATIENT : restrict db load to a specific patient; defaults to all.
 *
 * <p>(REQUIRED) CONFIG_FILE : database connection configuration.
 *
 * <p>The synthetic-data jenkins job will automatically set and launch this test during database
 * population.
 */
@Slf4j
public class PopulateDb {
  /** Resources to load. */
  private static List<String> resources =
      List.of(
          "AllergyIntolerance",
          "Condition",
          "DiagnosticReport",
          "FallRisk",
          "Immunization",
          "Location",
          "Medication",
          "MedicationOrder",
          "MedicationStatement",
          "Observation",
          "Organization",
          "Patient",
          "Practitioner",
          "Procedure");

  private final List<Class<?>> MANAGED_CLASSES =
      Arrays.asList(
          AllergyIntoleranceEntity.class,
          ConditionEntity.class,
          DiagnosticReportCrossEntity.class,
          DiagnosticReportEntity.class,
          DiagnosticReportsEntity.class,
          FallRiskEntity.class,
          ImmunizationEntity.class,
          LocationEntity.class,
          MedicationOrderEntity.class,
          MedicationEntity.class,
          MedicationStatementEntity.class,
          ObservationEntity.class,
          OrganizationEntity.class,
          PatientEntityV2.class,
          PractitionerEntity.class,
          ProcedureEntity.class);

  private String importDirectoryPath;

  private String configFilePath;

  /** Time to rock'n'roll. */
  @Test
  void pushToDb() throws Exception {

    EntityManagerFactory entityManagerFactory;

    if (configFilePath == null || configFilePath.isBlank()) {
      log.info("No config file was specified... Defaulting to local h2 database...");
      entityManagerFactory = new LocalH2("./target/minimart", MANAGED_CLASSES).get();
    } else {
      entityManagerFactory = new ExternalDb(configFilePath, MANAGED_CLASSES).get();
    }

    LatestResourceEtlStatusLoader latestResourceEtlStatusLoader =
        LatestResourceEtlStatusLoader.create(entityManagerFactory);

    // per resource, push the datamart records found in the import directory to the database.
    for (String resource : resources) {
      log.info(
          "Pushing to database with RESOURCE: {}, IMPORT DIRECTORY: {}, AND CONFIG FILE: {}",
          resource,
          importDirectoryPath,
          configFilePath);
      MitreMinimartMaker.sync(importDirectoryPath, resource, entityManagerFactory);

      latestResourceEtlStatusLoader.insertIntoEtlTable(resource);
    }
    log.info("DONE");
  }

  /** Lets do some input validation before attempting a push to the db. */
  @BeforeEach
  public void setup() {
    // Load the import data directory. If not provided, fail.
    importDirectoryPath = System.getProperty("import.directory");
    if (StringUtils.isBlank(importDirectoryPath)) {
      throw new IllegalArgumentException("import.directory not specified");
    }
    // If targeting a patient, use their import data sub-directory.
    // Otherwise, default to all patients.
    String chosenPatient = System.getProperty("patient");
    if (StringUtils.isNotBlank(chosenPatient)) {
      importDirectoryPath = importDirectoryPath + "/dm-records-" + chosenPatient;
    } else {
      log.info("No patient specifed, defaulting to all patients.");
    }
    // Load the config file path. If not provided, fail.
    configFilePath = System.getProperty("config.file");
    if (StringUtils.isBlank(configFilePath)) {
      throw new IllegalArgumentException("CONFIG_FILE not specified.");
    }
  }
}
