package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.diagnosticreport.DatamartDiagnosticReports;
import gov.va.api.health.dataquery.service.controller.organization.DatamartOrganization;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OrganizationReferenceAugments {
  private static final List<Optional<DatamartReference>> ORGANIZATION_REFERENCES =
      loadOrganizations();

  static DatamartDiagnosticReports addOrganizationReferencesToDiagnosticReport(
      Augmentation.Context<DatamartDiagnosticReports> ctx) {
    ctx.resource()
        .reports()
        .forEach(
            r -> {
              DatamartReference org = ctx.random(ORGANIZATION_REFERENCES).orElse(null);
              if (org != null) {
                r.accessionInstitutionSid(org.reference().orElse(null));
                r.accessionInstitutionName(org.display().orElse(null));
              } else {
                r.accessionInstitutionSid(null);
                r.accessionInstitutionName(null);
              }
            });
    return ctx.resource();
  }

  private static List<Optional<DatamartReference>> loadOrganizations() {
    List<Optional<DatamartReference>> o =
        ReferenceLoader.loadReferencesFor(
            DatamartOrganization.class,
            dm ->
                DatamartReference.builder()
                    .type(Optional.of("Organization"))
                    .reference(Optional.ofNullable(dm.cdwId()))
                    .display(Optional.ofNullable(dm.name()))
                    .build());
    o.add(Optional.empty());
    return o;
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartDiagnosticReports.class)
        .whenMatching(Objects::nonNull)
        .transform(OrganizationReferenceAugments::addOrganizationReferencesToDiagnosticReport)
        .build()
        .rewriteFiles();
    /* See ObservationPerformerAugments for more. */
  }
}
