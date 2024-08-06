# Getting Started

### Reference Documentation for Sensors Watch Project

The objective of this project is to provide a reliable architecture to consume UPD
events from temperature and humidity sensors, centralize it and to notify exceeds thresholds.

The project was divided into three pieces:

* Monitoring Service - Contains all details of monitoring and alerting.
* Warehouse Service - Gather temperature and humidity events and put it on Kafka Topic.
* Measurement Schema - The temperature and humidity events schema.

## Schema

![alt text](doc/schema-small.png "Architecture")

## Solution

![alt text](doc/measurement-solution.drawio.png "Architecture")

### Apache Kafka Topics

* **temperature-measurements-data** - Contains temperature measurements.
* **humidity-measurements-data** - Contains humidity measurements.
* **malformed-measurements-data** - Contains malformed temperature and humidity payloads.
* **humidity-measurements-alert-data** - Contains high temperature alerts.
* **temperature-measurements-alert-data**- Contains high humidity alerts.

All topics above have a DLT (Death Letter Topic) that receive unprocessed messages.  
The DTL topic has the suffix -dlt, for example: **humidity-measurements-data-dlt**.

## Test Coverage

### Warehouse Service

![alt text](doc/warehouse-site/img.png "Warehouse test coverage")

[Link to Site](doc/warehouse-site/jacoco/index.html)

### Monitoring Service

![alt text](doc/monitoring-site/img.png "Monitoring test coverage")

[Link to Site](doc/monitoring-site/jacoco/index.html)

# Running

## Requirements

* Require Docker and Docker Compose plugin
* Apache maven (Run Unit ant Integration Tests)
* JDK 21 (Run Unit ant Integration Tests)

## Run the solution

````shell
docker compose up --build --force-recreate
````

Having problems? The command below can help, **but it will delete all your containers**.

````shell
docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)
````

### Send a Temperature Event

````shell
echo "sensor_id=temp1;value=36" | nc -u localhost 3344
````

### Send a Humidity Event

````shell
echo "sensor_id=hum1;value=51" | nc -u localhost 3345
````

## Run tests

### Install the schema

````shell
cd measurement-schema && mvn clean install && cd -
````

### Run tests on warehouse-service

````shell
cd warehouse-service &&  mvn clean verify && docker compose down -v && cd -
````

### Run tests on monitoring-service

````shell
cd monitoring-service &&  mvn clean verify && docker compose down -v && cd -
````