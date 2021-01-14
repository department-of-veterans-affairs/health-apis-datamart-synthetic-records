package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.organization.DatamartOrganization;

import java.util.Optional;

public class OrganizationFacilityAugments {

    static DatamartOrganization addFacility(Augmentation.Context<DatamartOrganization> ctx) {
        return ctx.resource().facilityId(Optional.of(DatamartOrganization.FacilityId.builder()
                .stationNumber(ctx.resource().stationIdentifier().get())
                .type(DatamartOrganization.FacilityId.FacilityType.HEALTH)
                .build()));
    }

    public static void main(String[] args) {
        Augmentation.forResources(DatamartOrganization.class)
                .whenMatching(p -> p.stationIdentifier().get().length() == 3)
                .transform(OrganizationFacilityAugments::addFacility)
                .build()
                .rewriteFiles();
    }
}
