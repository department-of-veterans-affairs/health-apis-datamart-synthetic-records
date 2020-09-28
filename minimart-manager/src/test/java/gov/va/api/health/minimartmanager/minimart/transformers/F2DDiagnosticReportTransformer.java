package gov.va.api.health.minimartmanager.minimart.transformers;

import static java.util.Arrays.asList;

import gov.va.api.health.dataquery.service.controller.Transformers;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.v1.DatamartDiagnosticReports;
import gov.va.api.health.dstu2.api.elements.Reference;
import gov.va.api.health.dstu2.api.resources.DiagnosticReport;
import gov.va.api.health.minimartmanager.minimart.*;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class F2DDiagnosticReportTransformer {

  FhirToDatamartUtils fauxIds;

  public DatamartDiagnosticReports fhirToDatamart(DiagnosticReport diagnosticReport) {
    return DatamartDiagnosticReports.builder()
        .fullIcn(fauxIds.unmaskByReference(diagnosticReport.subject().reference()))
        .patientName(diagnosticReport.subject().display())
        .reports(reports(diagnosticReport))
        .build();
  }

  public List<DatamartDiagnosticReports.DiagnosticReport> reports(
      DiagnosticReport diagnosticReport) {
    String performer =
        diagnosticReport.performer() != null && diagnosticReport.performer().reference() != null
            ? fauxIds.unmaskByReference(diagnosticReport.performer().reference())
            : null;
    return Transformers.emptyToNull(
        asList(
            DatamartDiagnosticReports.DiagnosticReport.builder()
                .identifier(fauxIds.unmask("DiagnosticReport", diagnosticReport.id()))
                .effectiveDateTime(diagnosticReport.effectiveDateTime())
                .issuedDateTime(diagnosticReport.issued())
                .accessionInstitutionSid(performer)
                .accessionInstitutionName(
                    diagnosticReport.performer() != null
                            && diagnosticReport.performer().display() != null
                        ? diagnosticReport.performer().display()
                        : null)
                .results(results(diagnosticReport.result()))
                .build()));
  }

  private List<DatamartDiagnosticReports.Result> results(List<Reference> results) {
    if (results == null || results.isEmpty()) {
      return null;
    }
    return results.stream()
        .map(
            r ->
                DatamartDiagnosticReports.Result.builder()
                    .result(fauxIds.unmaskByReference(r.reference()))
                    .display(r.display())
                    .build())
        .collect(Collectors.toList());
  }
}
