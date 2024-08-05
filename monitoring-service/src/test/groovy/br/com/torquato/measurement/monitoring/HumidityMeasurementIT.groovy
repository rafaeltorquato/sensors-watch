package br.com.torquato.measurement.monitoring

import br.com.torquato.measurement.monitoring.support.ITSupport
import br.com.torquato.measurement.schema.MeasurementAlertEvent
import br.com.torquato.measurement.schema.MeasurementEvent
import br.com.torquato.measurement.schema.MeasurementType
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils

import java.time.LocalDateTime

class HumidityMeasurementIT extends ITSupport {

    @Autowired
    ProducerFactory<String, MeasurementEvent> producerFactory
    @Autowired
    ConsumerFactory<String, MeasurementAlertEvent> consumerFactory

    Producer<String, MeasurementEvent> producer
    Consumer<String, MeasurementAlertEvent> consumer

    def setup() {
        producer = producerFactory.createProducer()
        consumer = consumerFactory.createConsumer()
    }

    def cleanup() {
        producer?.close()
        consumer?.close()
    }

    def "Should consume a humidity alert event"() {
        given: 'A measurement event'
        def measurementEvent = new MeasurementEvent(
                UUID.randomUUID().toString(),
                "w01",
                "h01",
                51,
                MeasurementType.HUMIDITY,
                LocalDateTime.now()
        )
        consumer.subscribe(["humidity-measurements-alert-data"])

        when: 'A humidity measurement is sent'
        producer.send(new ProducerRecord<>("humidity-measurements-data", measurementEvent))
        producer.flush()
        def records = KafkaTestUtils.getRecords(consumer)
        def alertEvent = records.iterator()
                *.value()
                .find { it.sourceEvent().id() == measurementEvent.id()}

        then: 'An alert exists'
        alertEvent.sourceEvent() == measurementEvent
    }

}
