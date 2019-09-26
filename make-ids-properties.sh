#!/usr/bin/env bash

SQL_FILE="$1"
OUTPUT_FILE='identity-service.properties'

echo 'resource+uuid=identifier' > $OUTPUT_FILE

grep 'INSERT INTO' $1 \
  | sed 's/(/\n(/g' \
  | awk -F ',' '{print $6"+"$4"="$2}' \
  | tr -d "_');" \
  >> $OUTPUT_FILE
