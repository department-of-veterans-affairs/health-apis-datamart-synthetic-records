#!/usr/bin/env bash

cd $(readlink -f $(dirname $0))

export ENVIRONMENT=local
if [ "$1" == "clean" ]
then
  ./build.sh clean
  exit
# If we start we don't need to clean, nothing is in there to begin with.
elif [ "$1" == "start" ]
then
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
fi
./build.sh
