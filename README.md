# health-apis-datamart-synthetic-records

This repository acts as storage for synthetic data via JSON documents. It also provides tools for building reproducable (MITRE) databases filled with synthetic data.

### Creating a Local Synthetic Records Database

##### Prerequisites

1. Docker
2. [Flyway](https://flywaydb.org/download/)


##### Populating the Database

1. Download Flyway and unzip it in the root of this repository
2. Use `./run-local.sh start` to create the database (as a docker image) and populate it


##### Repopulating the Database

An existing database can be repopulated through one of two methods:
1. Update
    * `./run-local.sh`
    * Updating the database will update the already existing records with any changes/new data.
2. Clean
    * `./run-local.sh clean`
    * Cleaning will drop the cuurently existing tables and do a fresh load of the data.