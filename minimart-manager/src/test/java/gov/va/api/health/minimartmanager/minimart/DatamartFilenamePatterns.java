package gov.va.api.health.minimartmanager.minimart;

import gov.va.api.health.dataquery.service.controller.allergyintolerance.DatamartAllergyIntolerance;
import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.DatamartDiagnosticReports;
import gov.va.api.health.dataquery.service.controller.immunization.DatamartImmunization;
import gov.va.api.health.dataquery.service.controller.location.DatamartLocation;
import gov.va.api.health.dataquery.service.controller.medication.DatamartMedication;
import gov.va.api.health.dataquery.service.controller.medicationorder.DatamartMedicationOrder;
import gov.va.api.health.dataquery.service.controller.medicationstatement.DatamartMedicationStatement;
import gov.va.api.health.dataquery.service.controller.observation.DatamartObservation;
import gov.va.api.health.dataquery.service.controller.organization.DatamartOrganization;
import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient;
import gov.va.api.health.dataquery.service.controller.practitioner.DatamartPractitioner;
import gov.va.api.health.dataquery.service.controller.procedure.DatamartProcedure;
import gov.va.api.health.fallrisk.service.controller.DatamartFallRisk;
import java.util.HashMap;
import java.util.Map;

public class DatamartFilenamePatterns {
  private static final DatamartFilenamePatterns INSTANCE = new DatamartFilenamePatterns();

  private final Map<Class<?>, String> jsonFileRegex;

  private final Map<Class<?>, String> ndjsonFileRegex;

  private DatamartFilenamePatterns() {
    ndjsonFileRegex = new HashMap<>();
    ndjsonFileRegex.put(DatamartFallRisk.class, "^dmFalRis.*ndjson$");
    jsonFileRegex = new HashMap<>();
    jsonFileRegex.put(DatamartAllergyIntolerance.class, "^dmAllInt.*json$");
    jsonFileRegex.put(DatamartCondition.class, "^dmCon.*json$");
    // Diagnostic Report files are currently using the v1 Datamart objects
    jsonFileRegex.put(DatamartDiagnosticReports.class, "^dmDiaRep.*json$");
    jsonFileRegex.put(DatamartFallRisk.class, "^dmFalRis.*json$");
    jsonFileRegex.put(DatamartImmunization.class, "^dmImm.*json$");
    jsonFileRegex.put(DatamartLocation.class, "^dmLoc.*json$");
    jsonFileRegex.put(DatamartMedication.class, "^dmMed(?!Sta|Ord).*json$");
    jsonFileRegex.put(DatamartMedicationOrder.class, "^dmMedOrd.*json$");
    jsonFileRegex.put(DatamartMedicationStatement.class, "^dmMedSta.*json$");
    jsonFileRegex.put(DatamartObservation.class, "^dmObs.*json$");
    jsonFileRegex.put(DatamartOrganization.class, "^dmOrg.*json$");
    jsonFileRegex.put(DatamartPatient.class, "^dmPat.*json$");
    jsonFileRegex.put(DatamartPractitioner.class, "^dmPra.*json$");
    jsonFileRegex.put(DatamartProcedure.class, "^dmPro.*json$");
  }

  public static DatamartFilenamePatterns get() {
    return INSTANCE;
  }

  private String getOrDie(Map<Class<?>, String> registry, Class<?> datamartResource) {
    var pattern = registry.get(datamartResource);
    if (pattern == null) {
      throw new IllegalArgumentException(datamartResource.getName());
    }
    return pattern;
  }

  public String json(Class<?> datamartResource) {
    return getOrDie(jsonFileRegex, datamartResource);
  }

  public String ndjson(Class<?> datamartResource) {
    return getOrDie(ndjsonFileRegex, datamartResource);
  }
}
