# Getting Started

### Reference Documentation for Sensors Watch Project

The objective of this project is to provide a reliable and observable architecture to consume UPD 
events from temperature and humidity sensors, centralize it and to notify exceed thresholds. 

The project was divided into three pieces:
* Monitoring Service - Contains all details of monitoring and alerting.
* Warehouse Service - Gather temperature and humidity events and put it on Kafka Topic.
* Sensors Schema - The temperature and humidity events schema.


## TODO List
- [ ] Warehouse Service;
- [ ] Monitoring Service;
- [x] Sensors Schema;
- [ ] Kafka Streams;
- [ ] IT Tests;
- [ ] Unit Tests;
- [ ] Observability;
- [ ] Fallback;
- [ ] Logs;
