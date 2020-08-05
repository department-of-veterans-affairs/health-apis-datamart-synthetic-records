package gov.va.api.health.minimartmanager.minimart.augments;

import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition;
import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition.IcdCode;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ConditionCodeAugments {

  private static final Map<String, String> SNOMED_TO_ICD_MAPPINGS = loadSnomedToIcdMappings();

  // TODO: Load a Mappings File for SNOMED -> ICD-10-CM
  private static Map<String, String> loadSnomedToIcdMappings() {
    return null;
  }

  static DatamartCondition code(Augmentation.Context<DatamartCondition> ctx) {
    /* Only even patient ID's will become ICD-10-CM. Odd ID's will stay SNOMED. */
    if (Integer.parseInt(ctx.resource().patient().reference().get()) % 2 == 0) {
      return convertSnomedCodeToIcd(ctx.resource());
    }

    return ctx.resource();
  }

  /* Build a ICD-10-CM code, and remove the SNOMED */
  private static DatamartCondition convertSnomedCodeToIcd(DatamartCondition condition) {
    if (condition.snomed().isEmpty()) {
      System.out.println("No snomed to convert for condition cdwId: " + condition.cdwId());
      return condition;
    }
    condition.icd(
        Optional.of(
            IcdCode.builder()
                .version("10")
                .code(SNOMED_TO_ICD_MAPPINGS.get(condition.snomed().get().code()))
                .display(condition.snomed().get().display())
                .build()));
    condition.snomed(Optional.empty());
    return condition;
  }

  public static void main(String[] args) {
    Augmentation.forResources(DatamartCondition.class)
        .whenMatching(Objects::nonNull)
        .transform(ConditionCodeAugments::code)
        .build()
        .rewriteFiles();
  }
}
