package gov.va.api.health.minimartmanager.minimart.transformation;

import static java.util.stream.Collectors.toList;

import gov.va.api.health.dataquery.service.controller.diagnosticreport.DatamartDiagnosticReport;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.v1.DatamartDiagnosticReports;
import gov.va.api.lighthouse.datamart.DatamartReference;
import java.util.Objects;
import java.util.Optional;

public class DiagnosticReportToV2Transformation {

  static DatamartDiagnosticReport convert(Transformer.Context<DatamartDiagnosticReports> ctx) {
    DatamartDiagnosticReports.DiagnosticReport report = ctx.resource().reports().get(0);
    DatamartReference patient =
        DatamartReference.builder()
            .type(Optional.of("Patient"))
            .reference(Optional.of(ctx.resource().fullIcn()))
            .display(Optional.ofNullable(ctx.resource().patientName()))
            .build();
    DatamartReference accessionInstitution =
        report.accessionInstitutionSid() == null
            ? null
            : DatamartReference.builder()
                .type(Optional.of("Organization"))
                .reference(Optional.of(report.accessionInstitutionSid()))
                .display(Optional.ofNullable(report.accessionInstitutionName()))
                .build();
    DatamartReference verifyingStaff =
        DatamartReference.builder()
            .display(Optional.ofNullable(report.verifyingStaffName()))
            .reference(Optional.ofNullable(report.verifyingStaffSid()))
            .build();
    DatamartReference topography =
        DatamartReference.builder()
            .display(Optional.ofNullable(report.topographyName()))
            .reference(Optional.ofNullable(report.topographySid()))
            .build();
    DatamartReference visit =
        DatamartReference.builder()
            .display(Optional.ofNullable(report.visitCategory()))
            .reference(Optional.ofNullable(report.visitSid()))
            .build();
    return DatamartDiagnosticReport.builder()
        .cdwId(report.identifier())
        .patient(patient)
        .sta3n(report.sta3n())
        .effectiveDateTime(report.effectiveDateTime())
        .issuedDateTime(report.issuedDateTime())
        .accessionInstitution(Optional.ofNullable(accessionInstitution))
        .verifyingStaff(Optional.ofNullable(verifyingStaff))
        .topography(Optional.ofNullable(topography))
        .visit(Optional.ofNullable(visit))
        .orders(
            report.orders().stream()
                .map(
                    o ->
                        DatamartReference.builder()
                            .type(Optional.of("DiagnosticOrder"))
                            .reference(Optional.of(o.sid()))
                            .display(Optional.ofNullable(o.display()))
                            .build())
                .collect(toList()))
        .results(
            report.results().stream()
                .map(
                    r ->
                        DatamartReference.builder()
                            .type(Optional.of("Observation"))
                            .reference(Optional.of(r.result()))
                            .display(Optional.ofNullable(r.display()))
                            .build())
                .collect(toList()))
        .reportStatus(report.reportStatus())
        .build();
  }

  public static void main(String[] args) {
    Transformer.forResources(DatamartDiagnosticReports.class)
        .whenMatching(Objects::nonNull)
        .transform(DiagnosticReportToV2Transformation::convert)
        .build()
        .rewriteFiles();
  }
}
