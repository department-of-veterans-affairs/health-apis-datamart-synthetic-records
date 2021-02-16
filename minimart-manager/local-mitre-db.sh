#!/usr/bin/env bash

#
# This will export data from Mitre and replace the contents of the local
# ./src/test/resources/mitre database. It expects config/lab.properties to contain
# standard spring database configuration the source Mitre database.
#
exportLocalMitreDb() {
  local includedTypes="$1"
  cd $(dirname $0)

  LAB_PROPERTIES=config/lab.properties

  [ ! -f "$LAB_PROPERTIES" ] && echo "Missing $LAB_PROPERTIES" && exit 1

  for p in spring.datasource.username spring.datasource.password spring.datasource.url
  do
    ! grep -q "^$p=" $LAB_PROPERTIES && echo "Missing $p in $LAB_PROPERTIES" && exit 1
  done

  [ -f $LOCAL_DB.mv.db ] && rm -v $LOCAL_DB.*

  mvn \
    -P'!standard' \
    -Pmitre-export \
    test-compile \
    -DconfigFile=config/lab.properties \
    -DoutputFile=$LOCAL_DB \
    -DexportPatients=32000225,43000199,17,23,1017283180V801730 \
    -Dexporter.included-types="$includedTypes" \
    -Dorg.jboss.logging.provider=jdk \
    -Djava.util.logging.config.file=nope

  cat<<EOF
You new H2 database is available.
$LOCAL_DB.mv.db
EOF
}

openLocalMitreDb() {
  local db="${1:-}"
  [ -z "${db:-}" ] && db=${LOCAL_DB}
  java -jar ~/.m2/repository/com/h2database/h2/1.4.200/h2-1.4.200.jar -url jdbc:h2:${db} -user sa -password sa
}

# ============================================================

usage() {
  cat <<EOF
$0 <command>

Commands:
  export-fall-risk   Make a copy of db in a local h2 database
  export-data-query  Make a copy of db in a local h2 database
  open               Open the local mitre database

Local database: $LOCAL_DB

EOF
exit
}


BASE_DIR=$(dirname $(readlink -f $0))
LOCAL_DB=$BASE_DIR/target/mitre

case $1 in
  export-fall-risk) exportLocalMitreDb "FallRisk";;
  export-data-query) exportLocalMitreDb "AllergyIntolerance,Appointment,Condition,Datamart,Device,DiagnosticReportCross,DiagnosticReports,DiagnosticReport,Immunization,LatestResourceEtlStatus,Location,Medication,MedicationOrder,MedicationStatement,Observation,Organization,PatientV2,Practitioner,Procedure";;
  open) openLocalMitreDb "${2:-}";;
  *) usage;;
esac
