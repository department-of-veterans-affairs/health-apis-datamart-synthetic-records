package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.organization.DatamartOrganization;
import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientManagingOrganizationAugments {

  private static final List<Optional<String>> ORGANIZATION_REFERENCES = loadOrganizations();

  static DatamartPatient addManagingOrganization(Augmentation.Context<DatamartPatient> ctx) {
    return ctx.resource().managingOrganization(ctx.random(ORGANIZATION_REFERENCES));
  }

  private static List<Optional<String>> loadOrganizations() {
    List<Optional<String>> o =
        ReferenceLoader.loadReferencesFor(
                DatamartOrganization.class,
                dm ->
                    DatamartReference.builder().reference(Optional.ofNullable(dm.cdwId())).build())
            .stream()
            .map(d -> d.flatMap(DatamartReference::reference))
            .collect(Collectors.toList());
    o.add(Optional.empty());
    return o;
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartPatient.class)
        .whenMatching(Objects::nonNull)
        .transform(PatientManagingOrganizationAugments::addManagingOrganization)
        .build()
        .rewriteFiles();
  }
}
