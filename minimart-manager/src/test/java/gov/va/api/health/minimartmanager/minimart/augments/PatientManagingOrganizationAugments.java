package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.datamart.DatamartReference;
import gov.va.api.health.dataquery.service.controller.organization.DatamartOrganization;
import gov.va.api.health.dataquery.service.controller.patient.DatamartPatient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PatientManagingOrganizationAugments {

    private static final List<Optional<DatamartReference>> ORGANIZATION_REFERENCES =
            loadOrganizations();

    static DatamartPatient addManagingOrganization(Augmentation.Context<DatamartPatient> ctx){
        Optional<String> randOrg = ctx.random(ORGANIZATION_REFERENCES).flatMap(DatamartReference::reference);
        return ctx.resource()
                .managingOrganization(
                        Optional.ofNullable(randOrg.isPresent() ? randOrg : null)
                                .orElse(null));
    }

  private static List<Optional<DatamartReference>> loadOrganizations() {
    List<Optional<DatamartReference>> o =
        ReferenceLoader.loadReferencesFor(
            DatamartOrganization.class,
            dm ->
                DatamartReference.builder()
                    .type(Optional.of("Organization"))
                    .reference(Optional.ofNullable(dm.cdwId()))
                    .build());
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
