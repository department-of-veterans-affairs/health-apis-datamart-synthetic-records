#!/usr/bin/env bash

cd $(readlink -f $(dirname $0))
NAME=vasdvp/health-apis-synthetic-records-builder:latest

set -e
docker build --no-cache -t $NAME .

if [ "${1:-}" == "push" ]; then docker push $NAME; fi
