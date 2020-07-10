#!/usr/bin/env bash

WORKING_DIR=$(readlink -f $(dirname $0))
cd $WORKING_DIR

main() {

  if [ "$1" == "start" ]; then
    createDatabase
    shift
  fi

  loadDatabase $@
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

  sleep 5
}

loadDatabase() {
  docker run --rm -it \
    -e ENVIRONMENT="local" \
    -v $(pwd):/root/synthetic-records \
    -v ~/.m2:/root/.m2 \
    --network host \
    vasdvp/health-apis-synthetic-records-builder:latest \
    ./root/synthetic-records/build.sh $@
}

main $@