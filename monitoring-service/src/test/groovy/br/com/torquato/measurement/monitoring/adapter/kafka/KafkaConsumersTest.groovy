package br.com.torquato.measurement.monitoring.adapter.kafka

import br.com.torquato.measurement.monitoring.domain.DuplicatedEventException
import br.com.torquato.measurement.monitoring.service.ThresholdMonitoringService
import br.com.torquato.measurements.schema.Schema
import org.apache.kafka.clients.consumer.ConsumerRecord
import spock.lang.Specification

class KafkaConsumersTest extends Specification {

    def "Should call measurementService.handle with success"() {
        given:
        def mockedMeasurementService = Mock(ThresholdMonitoringService)
        def consumers = new KafkaConsumers(mockedMeasurementService)
        ConsumerRecord<String, Schema.MeasurementEvent> record = Mock()

        when:
        consumers.handleMeasurementEvent(record)

        then:
        1 * mockedMeasurementService.handle(_)
        1 * record.value()
    }

    def "Should call measurementService.handle with error"() {
        given:
        def mockedMeasurementService = Stub(ThresholdMonitoringService)
        def consumers = new KafkaConsumers(mockedMeasurementService)
        ConsumerRecord<String, Schema.MeasurementEvent> record = Mock()

        mockedMeasurementService.handle(_) >> {
            throw new DuplicatedEventException("Mocked Exception!")
        }

        when:
        consumers.handleMeasurementEvent(record)

        then:
        1 * record.value()
    }

    def "Should call measurementService.handleAlertEvent with success"() {
        given:
        def consumers = new KafkaConsumers(Mock(ThresholdMonitoringService))
        ConsumerRecord<String, Schema.MeasurementAlertEvent> record = Mock()

        when:
        consumers.handleAlertEvent(record)

        then:
        true
    }

    def "Should call measurementService.handleMalformedEvent with success"() {
        given:
        def consumers = new KafkaConsumers(Mock(ThresholdMonitoringService))
        ConsumerRecord<String, Schema.MalformedMeasurementEvent> record = Mock()

        when:
        consumers.handleMalformedEvent(record)

        then:
        true
    }

}
