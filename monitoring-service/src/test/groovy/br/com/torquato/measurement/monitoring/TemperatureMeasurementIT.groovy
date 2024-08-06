package br.com.torquato.measurement.monitoring

import br.com.torquato.measurement.monitoring.support.ITSupport
import br.com.torquato.measurements.schema.Schema
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils

class TemperatureMeasurementIT extends ITSupport {

    @Autowired
    ProducerFactory<String, Schema.MeasurementEvent> producerFactory
    @Autowired
    ConsumerFactory<String, Schema.MeasurementAlertEvent> consumerFactory

    Producer<String, Schema.MeasurementEvent> producer
    Consumer<String, Schema.MeasurementAlertEvent> consumer

    def setup() {
        producer = producerFactory.createProducer()
        consumer = consumerFactory.createConsumer()
    }

    def cleanup() {
        producer?.close()
        consumer?.close()
    }

    def "Should consume a temperature alert event"() {
        given: 'A measurement event'
        def measurementEvent = Schema.MeasurementEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setWarehouseId("w01")
                .setSensorId("t01")
                .setValue(36)
                .setType(Schema.MeasurementType.TEMPERATURE)
                .setMoment(System.currentTimeMillis())
                .build()
        consumer.subscribe(["temperature-measurements-alert-data"])

        when: 'A temperature measurement is sent'
        producer.send(new ProducerRecord<>("temperature-measurements-data", measurementEvent))
        producer.flush()
        def records = KafkaTestUtils.getRecords(consumer)
        def alertEvent = records.iterator()
                *.value()
                .find { it.getSourceEvent().getId() == measurementEvent.getId() }

        then: 'An alert exists'
        alertEvent.getSourceEvent() == measurementEvent
    }

}
