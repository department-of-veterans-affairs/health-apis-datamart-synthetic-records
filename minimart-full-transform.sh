#!/usr/bin/env bash

#
# Uses mitre-minimart-maker.sh to transform all resources for a given patient to datamart
#

MANAGED_RESOURCES="AllergyIntolerance Condition DiagnosticReport Immunization Medication MedicationOrder MedicationStatement Observation Patient Procedure"

SD=$(readlink -f $(dirname $0))
FHIR_RESOURCES=$(readlink -f ../health-apis-data-query/data-query-tests)

TRANSFORM_FAILURES="$SD/transformFailures.txt"
[ -f $TRANSFORM_FAILURES ] && rm $TRANSFORM_FAILURES

cd "$SD"

determineChangedPatients() {
local listPatients=${1:-true}
# Create files listing time since epoch and patient id for fhir and datamart
local fhirRecords='/tmp/fhir.tmp'
local dmRecords='/tmp/dm.tmp'

# fhir records
stat -c "%Y %n" fhir/* | sed 's/fhir\/lab-crawl-//' > $fhirRecords

# datamart records
[ $(ls "$SD/datamart/" | wc -l) -eq 0 ] || stat -c "%Y %n" datamart/* | sed 's/datamart\/dm-records-//' > $dmRecords

while read fhir
do
  local fhirChangedTime="$(echo $fhir | cut -d ' ' -f1)"
  local fhirPatientId="$(echo $fhir | cut -d ' ' -f2)"

  # If patient is new it will always be considered changed
  [ ! -d "$SD/datamart/dm-records-$fhirPatientId" ] && HAS_CHANGED+=("$fhirPatientId") && continue

  while read dm
  do
    local dmPatientId="$(echo $dm | cut -d ' ' -f2)"

    # Keep Going Until the Patient IDs match
    [ "$fhirPatientId" == "$dmPatientId" ] || continue

    local dmChangedTime="$(echo $dm | cut -d ' ' -f1)"

    # Uses time since epoch so we can just do a quick value compare
    if [ $dmChangedTime -lt $fhirChangedTime ]
    then
      HAS_CHANGED+=("$fhirPatientId")
    fi
  done < $dmRecords
done < $fhirRecords

echo "${#HAS_CHANGED[@]} Patient Records have changed since last use..."
[ "$listPatients" == "true" ] && echo ${HAS_CHANGED[@]} | sed 's/ /\n/g'
}

transformDatamartForPatient() {
  [ -z "$1" ] && usage "Patient is a required parameter for transforming by patient..." && exit 1
  TRANSFORM_FAILED='false'
  cd "$FHIR_RESOURCES"
  PATIENT="$1"

  for resource in $MANAGED_RESOURCES
  do
    echo "Transforming To Datamart for Resource: $resource"
    ./mitre-minimart-maker.sh transformToDatamart -d "$SD/fhir/lab-crawl-$PATIENT" -r "$resource" --compile "${compile:-true}"
    [ $? != 0 ] && TRANSFORM_FAILED='true' && echo "Failed to transform $resource for patient $PATIENT" >> $TRANSFORM_FAILURES
    compile='false'
  done

  # SINGLE_LADY is a workaround for invalid patients
  # It allows the patient to be transformed if being done one patient at a time
  if [ "$TRANSFORM_FAILED" == 'false' ] || [ "${SINGLE_LADY:-false}" == 'true' ]
  then
    echo "Copying files to Synthetic Records Repo..."

    [ ! -d "$SD/datamart/dm-records-$PATIENT/" ] && mkdir "$SD/datamart/dm-records-$PATIENT/"

    cp $FHIR_RESOURCES/target/fhir-to-datamart-samples/* "$SD/datamart/dm-records-$PATIENT/"

    [ "${SINGLE_LADY:-false}" == 'true' ] \
      && echo "Safe transform of resources has been skipped... Double check your records to make sure there were no failures..."

  else
    echo "Failed to transform resource(s)..."
  fi

  rm -r "$FHIR_RESOURCES/target/fhir-to-datamart-samples/"

  cd "$SD"
}

createInvalidPatientsCsv() {
  BAD_GUYS='/tmp/im-the-bad-guy.tmp'
  [ -f $BAD_GUYS ] && rm $BAD_GUYS

  echo "Creating Invalid Patients file in: $BAD_GUYS"

  local fhirFiles="$SD/fhir"
  for file in $(ls $fhirFiles)
  do
    [ $(grep OUTCOME $fhirFiles/$file/* | grep -c -i INVALID) -gt 0 ] && echo $(echo "$file" | cut -d '-' -f3) >> $BAD_GUYS
  done
}

transformDatamartAllPatients() {
  createInvalidPatientsCsv
  determineChangedPatients 'false'
  for patient in ${HAS_CHANGED[@]}
  do
    local isValid=$(grep -x -c $patient $BAD_GUYS)
    if [ $isValid -gt 0 ]
    then
      echo "Patient ($patient) is invalid. One or more responses were invalid." >> $TRANSFORM_FAILURES
      continue
    fi

    echo -e "\n\n\nTransforming managed resources for patient: $patient ...\n"
    transformDatamartForPatient "$patient"
  done

  [ -f $TRANSFORM_FAILURES ] && cat $TRANSFORM_FAILURES
}

# =========================================================

usage() {
cat <<EOF
==========
  Commands:
    transformAllPatients
      Transform all patients changed to datamart
    transformByPatient <patient-id>
      Transform a single patient to datamart for all resources
    listChangedPatients <list-patients-boolean>
      List all changed patients (true lists changed ids, false gives a count)
      Default: true

  Example:
    transformAllPatients
    transformByPatient 1010101010V666666
    listChangedPatients false

==========
$1
EOF
}

# =========================================================

COMMAND="$1"

case $COMMAND in
  transformAllPatients) transformDatamartAllPatients;;
  transformByPatient) SINGLE_LADY='true' && transformDatamartForPatient "$2";;
  listChangedPatients) determineChangedPatients "$2";;
  createInvalidPatientsCsv) createInvalidPatientsCsv;;
  *) usage "Invalid Command: $COMMAND";;
esac
