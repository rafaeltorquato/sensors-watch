package br.com.torquato.measurement.warehouse.adapter.kafka

import br.com.torquato.measurement.schema.MalformedMeasurementEvent
import br.com.torquato.measurement.schema.MeasurementEvent
import br.com.torquato.measurement.schema.MeasurementType
import org.springframework.kafka.core.KafkaTemplate
import spock.lang.Specification

import java.time.LocalDateTime


class MeasurementEventRecipientKafkaAdapterTest extends Specification {

    KafkaTemplate<String, Object> kafkaTemplate = Mock()
    MeasurementEventRecipientKafka adapter

    def setup() {
        adapter = new MeasurementEventRecipientKafka(kafkaTemplate)
    }

    def "Should send MeasurementEvent"() {
        given:
        def event = new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                type as MeasurementType,
                LocalDateTime.now()
        )

        when:
        adapter.send(event)

        then:
        1 * kafkaTemplate.send(_, _, event)

        where:
        type                        | topic
        MeasurementType.HUMIDITY    | "humidity-measurements-data"
        MeasurementType.TEMPERATURE | "temperature-measurements-data"
    }


    def "Should send MalformedMeasurementEvent"() {
        given:
        def malformedEvent = new MalformedMeasurementEvent(
                "1",
                "w01",
                MeasurementType.HUMIDITY,
                LocalDateTime.now(),
                "sensor_id;value"
        )

        when:
        adapter.send(malformedEvent)

        then:
        1 * kafkaTemplate.send("malformed-measurements-data", _, malformedEvent)
    }
}
