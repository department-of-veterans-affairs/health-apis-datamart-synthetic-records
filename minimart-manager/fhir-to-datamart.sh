#!/usr/bin/env bash



REPO="$(cd $(dirname $0)/../ && pwd)"

transformFhirToDatamart() {
  COMPILE=${COMPILE:-true}
  local workingDir="$REPO/datamart-exporter"
  [ -z "$DIRECTORY" ] && usage "Directory is a Required Option." && exit 1
  [ -z "$RESOURCE_TYPE" ] && usage "Resource Type is a Required Option." && exit 1
  [ "$COMPILE" == 'true' ] && mvn -f "$workingDir" test-compile
  mvn -f "$workingDir" \
    -P'!standard' \
    -Pmitre-minimart-maker \
    generate-resources \
    -DresourceType="$RESOURCE_TYPE" \
    -DinputDirectory="$DIRECTORY" \
    -DidsConfig=${CONFIG_FILE:-"identity-service.properties"}
}

usage() {
cat <<EOF
---
Commands:
  transformToDatamart <directory-to-read-files-from> <resource-name>
    Takes all files for the given resource in the directory and transforms them to datamart schema
---
Options:
  -d|--directory) Use to specify the directory files are located in for a transform or dbPush
  -r|--resource) Use to specify the resource to transform or push to db
  -f|--config) Config file used either specify location of properties for different actions
  -p|--compile) Boolean, Maven will run a test-compile before performing operation
  -h|--help) I need an adult!!!
---
Examples:
  transformToDatamart -d "$(pwd)/data-query-tests/target" -r AllergyIntolerance -f config.properties
---
$1
EOF
}

ARGS=$(getopt -n $(basename ${0}) \
    -l "help,directory:,resource:,config:,compile:" \
    -o "hd:r:f:p:" -- "$@")
[ $? != 0 ] && usage
eval set -- "$ARGS"
while true
do
  case "$1" in
    -d|--directory) DIRECTORY="$2";;
    -f|--config) CONFIG_FILE="$2";;
    -r|--resource) RESOURCE_TYPE="$2";;
    -p|--compile) COMPILE="$2";;
    -h|--help) usage && exit 0;;
    --) shift;break;;
  esac
  shift;
done

COMMAND=$1

echo "Working from directory: $REPO"

case $COMMAND in
  transformToDatamart) transformFhirToDatamart;;
  *) usage "Invalid Command: $COMMAND" && exit 1 ;;
esac
