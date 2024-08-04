package br.com.torquato.measurement.monitoring.adapter.kafka


import br.com.torquato.measurement.schema.MeasurementAlertEvent
import br.com.torquato.measurement.schema.MeasurementEvent
import br.com.torquato.measurement.schema.MeasurementType
import org.springframework.kafka.core.KafkaTemplate
import spock.lang.Specification

import java.time.LocalDateTime

class MeasurementAlertEventRecipientKafkaTest extends Specification {

    KafkaTemplate<String, Object> mockedKafkaTemplate
    MeasurementAlertEventRecipientKafka recipient


    def setup() {
        mockedKafkaTemplate = Mock()
        recipient = new MeasurementAlertEventRecipientKafka(mockedKafkaTemplate)
    }

    def "Should send MeasurementEvent"() {
        given:
        def measurementEvent = new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                type as MeasurementType,
                LocalDateTime.now()
        )
        def alertEvent = new MeasurementAlertEvent(
                "1",
                measurementEvent,
                LocalDateTime.now(),
                30
        )

        when:
        recipient.send(alertEvent)

        then:
        1 * mockedKafkaTemplate.send(topic, _, alertEvent)

        where:
        type                        | topic
        MeasurementType.HUMIDITY    | "humidity-measurements-alert-data"
        MeasurementType.TEMPERATURE | "temperature-measurements-alert-data"
    }

}
