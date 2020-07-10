#!/usr/bin/env bash

set -e

WORKING_DIR=$(readlink -f $(dirname $0))
cd $WORKING_DIR

main() {
  if [ "$1" == "start" ]; then
    createDatabase
    shift
  fi

  syntheticRecordsBuilder

  loadDatabase $@
}

syntheticRecordsBuilder() {
  cd $WORKING_DIR/docker
  docker build -t vasdvp/health-apis-synthetic-records-builder:local .
  cd $WORKING_DIR
}

createDatabase() {
  # SQL Server Docker Image (You don't have to install anything!!!)
  docker pull mcr.microsoft.com/mssql/server:2017-latest

  # Run the docker image, but make sure its configuration matches the local ones
  # that were set.
  [ -f "environments/local.conf" ] && . environments/local.conf
  [ -z "$FLYWAY_PASSWORD" ] && "Help! I can't seem to find my password(FLYWAY_PASSWORD)!" && exit 1
  docker run \
    --name "dqdb" \
    -e 'ACCEPT_EULA=Y' \
    -e "SA_PASSWORD=$FLYWAY_PASSWORD" \
    -p 1433:1433 \
    -d mcr.microsoft.com/mssql/server:2017-latest

  # Needs time to create SA user
  sleep 10
}

loadDatabase() {
  docker run --rm -it \
    -e ENVIRONMENT="local" \
    -v $(pwd):/root/synthetic-records \
    -v ~/.m2:/root/.m2 \
    --network host \
    vasdvp/health-apis-synthetic-records-builder:local \
    ./root/synthetic-records/build.sh $@
}

main $@
