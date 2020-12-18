package gov.va.api.health.minimartmanager.minimart.transformers;

import gov.va.api.health.dataquery.service.controller.diagnosticreport.DatamartDiagnosticReport;
import gov.va.api.health.dstu2.api.elements.Reference;
import gov.va.api.health.dstu2.api.resources.DiagnosticReport;
import gov.va.api.health.minimartmanager.minimart.FhirToDatamartUtils;
import gov.va.api.lighthouse.datamart.DatamartReference;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class F2DDiagnosticReportTransformer {

  FhirToDatamartUtils fauxIds;

  public DatamartDiagnosticReport fhirToDatamart(DiagnosticReport diagnosticReport) {

    return DatamartDiagnosticReport.builder()
        .cdwId(fauxIds.unmaskByReference(diagnosticReport.id()))
        .patient(
            DatamartReference.builder()
                .type(Optional.of("Patient"))
                .reference(Optional.of(diagnosticReport.subject().reference()))
                .display(Optional.ofNullable(diagnosticReport.subject().display()))
                .build())
        .orders(toDatamartReferenceList(diagnosticReport.request(), "DiagnosticOrder"))
        .results(toDatamartReferenceList(diagnosticReport.result(), "Observation"))
        .build();
  }

  private List<DatamartReference> toDatamartReferenceList(
      List<Reference> referenceList, String resource) {
    if (referenceList == null || referenceList.isEmpty()) {
      return null;
    }
    return referenceList.stream()
        .map(
            r ->
                DatamartReference.builder()
                    .type(Optional.of(resource))
                    .reference(Optional.of(r.reference()))
                    .display(Optional.ofNullable(r.display()))
                    .build())
        .collect(Collectors.toList());
  }
}
