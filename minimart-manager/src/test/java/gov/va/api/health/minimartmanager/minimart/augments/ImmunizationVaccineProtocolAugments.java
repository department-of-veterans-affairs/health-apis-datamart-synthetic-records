package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.immunization.DatamartImmunization;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ImmunizationVaccineProtocolAugments {
  private static final List<Optional<DatamartImmunization.VaccinationProtocols>> VACC_PROTOCOLS =
      List.of(vp("Series 1", 2), vp("Series 1", 1), vp("Booster", 1), Optional.empty());

  static DatamartImmunization addVaccineProtocol(Augmentation.Context<DatamartImmunization> ctx) {
    ctx.resource().vaccinationProtocols(ctx.random(VACC_PROTOCOLS));
    return ctx.resource();
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartImmunization.class)
        .whenMatching(Objects::nonNull)
        .transform(ImmunizationVaccineProtocolAugments::addVaccineProtocol)
        .build()
        .rewriteFiles();
  }

  private static Optional<DatamartImmunization.VaccinationProtocols> vp(
      String series, Integer seriesDoses) {
    return Optional.of(
        DatamartImmunization.VaccinationProtocols.builder()
            .seriesDoses(Optional.of(seriesDoses))
            .series(Optional.of(series))
            .build());
  }
}
