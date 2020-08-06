package gov.va.api.health.minimartmanager.minimart.augments;

import com.google.common.collect.ImmutableMap;
import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition;
import gov.va.api.health.dataquery.service.controller.condition.DatamartCondition.IcdCode;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.util.Pair;

public class ConditionCodeAugments {

  private static final Map<String, Pair<String, String>> SNOMED_TO_ICD_MAPPINGS =
      new ImmutableMap.Builder<String, Pair<String, String>>()
          .put("109838007", Pair.of("C18.8", "Malignant neoplasm of overlapping sites of colon"))
          .put("195967001", Pair.of("J45.51", "Severe persistent asthma with (acute) exacerbation"))
          .put("236077008", Pair.of("R19.7", "Diarrhea, unspecified"))
          .put(
              "301011002",
              Pair.of("R82.79", "Other abnormal findings on microbiological examination of urine"))
          .put(
              "363406005",
              Pair.of("C26.0", "Malignant neoplasm of intestinal tract, part unspecified"))
          .put("38822007", Pair.of("A56.01", "Chlamydial cystitis and urethritis"))
          .put(
              "444470001",
              Pair.of(
                  "S83.511D",
                  "Sprain of anterior cruciate ligament of right knee, subsequent encounter"))
          .put(
              "6072007",
              Pair.of(
                  "K57.31",
                  "Diverticulosis of large intestine without perforation or abscess with bleeding"))
          .put("90560007", Pair.of("M71.161", "Other infective bursitis, right knee"))
          .put(
              "97331000119101",
              Pair.of(
                  "E11.37X3",
                  "Type 2 diabetes mellitus with diabetic macular edema, resolved following treatment, bilateral"))
          .put("239872002", Pair.of("M87.051", "Idiopathic aseptic necrosis of right femur"))
          .put(
              "284551006",
              Pair.of("S91.312A", "Laceration without foreign body, left foot, initial encounter"))
          .put(
              "444448004",
              Pair.of(
                  "S83.419A",
                  "Sprain of medial collateral ligament of unspecified knee, initial encounter"))
          .put(
              "1501000119109",
              Pair.of(
                  "E11.329",
                  "Type 2 Diabetes Mellitus with Mild Nonproliferative Diabetic Retinopathy without Macular Edema"))
          .put("24079001", Pair.of("L20.81", "Atopic neurodermatitis"))
          .put(
              "263102004",
              Pair.of(
                  "S62.90XD",
                  "Unspecified fracture of unspecified wrist and hand, subsequent encounter for fracture with routine healing"))
          .put(
              "359817006",
              Pair.of(
                  "M84.459A",
                  "Pathological fracture, hip, unspecified, initial encounter for fracture"))
          .put(
              "370247008",
              Pair.of(
                  "S01.119A",
                  "Laceration without foreign body of unspecified eyelid and periocular area, initial encounter"))
          .put(
              "84757009",
              Pair.of(
                  "G40.409",
                  "Other generalized epilepsy and epileptic syndromes, not intractable, without status epilepticus"))
          .put("232353008", Pair.of("J34.9", "Unspecified disorder of nose and nasal sinuses"))
          .put(
              "62106007",
              Pair.of(
                  "S06.300D",
                  "Unspecified focal traumatic brain injury without loss of consciousness, subsequent encounter"))
          .put(
              "16114001",
              Pair.of(
                  "M80.871A",
                  "Other osteoporosis with current pathological fracture, right ankle and foot, initial encounter for fracture"))
          .put("314994000", Pair.of("C61.", "Malignant neoplasm of prostate"))
          .put(
              "65966004",
              Pair.of(
                  "S52.91XD",
                  "Unspecified fracture of right forearm, subsequent encounter for closed fracture with routine healing"))
          .put("75498004", Pair.of("J98.01", "Acute bronchospasm"))
          .put("124171000119105", Pair.of("G44.221", "Chronic tension-type headache, intractable"))
          .put("196416002", Pair.of("K01.1", "Impacted teeth"))
          .put(
              "239873007",
              Pair.of(
                  "M18.11",
                  "Unilateral primary osteoarthritis of first carpometacarpal joint, right hand"))
          .put("399211009", Pair.of("Z86.74", "Personal history of sudden cardiac arrest"))
          .put("428251008", Pair.of("K35.2", "Acute appendicitis with generalized peritonitis"))
          .put(
              "703151001",
              Pair.of(
                  "G40.309",
                  "Generalized idiopathic epilepsy and epileptic syndromes, not intractable, without status epilepticus"))
          .put("410429000", Pair.of("J44.9", "Chronic obstructive pulmonary disease, unspecified"))
          .put(
              "443165006",
              Pair.of(
                  "M80.862G",
                  "Other osteoporosis with current pathological fracture, left lower leg, subsequent encounter for fracture with delayed healing"))
          .put(
              "44465007",
              Pair.of("S93.412S", "Sprain of calcaneofibular ligament of left ankle, sequela"))
          .put(
              "157141000119108",
              Pair.of("E11.22", "Type 2 diabetes mellitus with diabetic chronic kidney disease"))
          .put(
              "47505003",
              Pair.of("Z86.51", "Personal history of combat and operational stress reaction"))
          .put("254632001", Pair.of("C34.01", "Malignant neoplasm of right main bronchus"))
          .put("36971009", Pair.of("J20.8", "Acute bronchitis due to other specified organisms"))
          .put(
              "64859006",
              Pair.of(
                  "M80.071S",
                  "Age-related osteoporosis with current pathological fracture, right ankle and foot, sequela"))
          .put("68496003", Pair.of("K63.5", "Polyp of colon"))
          .put(
              "422034002",
              Pair.of(
                  "E08.319",
                  "Diabetes mellitus due to underlying condition with unspecified diabetic retinopathy without macular edema"))
          .put("65363002", Pair.of("H66.3X3", "Other chronic suppurative otitis media, bilateral"))
          .put(
              "449868002",
              Pair.of(
                  "F17.219",
                  "Nicotine dependence, cigarettes, with unspecified nicotine-induced disorders"))
          .put("26929004", Pair.of("G30.9", "Alzheimer's disease, unspecified"))
          .put("49436004", Pair.of("J96.11", "Chronic respiratory failure with hypoxia"))
          .put("40055000", Pair.of("J01.21", "Acute recurrent ethmoidal sinusitis"))
          .put(
              "55822004",
              Pair.of("E10.8", "Type 1 diabetes mellitus with unspecified complications"))
          .put("162864005", Pair.of("Z68.38", "Body mass index (BMI) 38.0-38.9, adult"))
          .put("431855005", Pair.of("N18.1", "Chronic kidney disease, stage 1"))
          .put("126906006", Pair.of("D40.0", "Neoplasm of uncertain behavior of prostate"))
          .put("38341003", Pair.of("I11.0", "Hypertensive heart disease with heart failure"))
          .put(
              "237602007",
              Pair.of("E11.8", "Type 2 diabetes mellitus with unspecified complications"))
          .put("44054006", Pair.of("E10.9", "Type 1 diabetes mellitus without complications"))
          .put("195662009", Pair.of("J02.8", "Acute pharyngitis due to other specified organisms"))
          .build();

  static DatamartCondition code(Augmentation.Context<DatamartCondition> ctx) {
    if (!ctx.resource().hasSnomedCode()) {
      System.out.println("No snomed code exists for condition.cdwId: " + ctx.resource().cdwId());
      return ctx.resource();
    }
    if (SNOMED_TO_ICD_MAPPINGS.containsKey(ctx.resource().snomed().get().code())) {
      return convertSnomedCodeToIcd(ctx.resource());
    }
    return ctx.resource();
  }

  /* Build a ICD-10-CM code, and remove the SNOMED */
  private static DatamartCondition convertSnomedCodeToIcd(DatamartCondition condition) {
    condition.icd(
        Optional.of(
            IcdCode.builder()
                .version("10")
                .code(SNOMED_TO_ICD_MAPPINGS.get(condition.snomed().get().code()).getFirst())
                .display(SNOMED_TO_ICD_MAPPINGS.get(condition.snomed().get().code()).getSecond())
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
