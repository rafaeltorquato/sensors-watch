package br.com.torquato.measurement.monitoring.service

import br.com.torquato.measurement.monitoring.domain.*
import br.com.torquato.measurement.monitoring.port.MeasurementAlertEventRecipient
import br.com.torquato.measurements.schema.Schema
import org.springframework.dao.DuplicateKeyException
import spock.lang.Specification

class MeasurementServiceTest extends Specification {

    MeasurementThresholdRepository mockedThresholdRepository
    ProcessedEventRepository mockedProcessedEventRepository
    MeasurementAlertEventRecipient mockedAlertEventRecipient
    MeasurementService service

    def setup() {
        mockedThresholdRepository = Stub()
        mockedProcessedEventRepository = Mock()
        mockedAlertEventRecipient = Mock()

        service = new MeasurementService(
                mockedThresholdRepository,
                mockedProcessedEventRepository,
                mockedAlertEventRecipient
        )
    }

    def "Should notify an Alert when threshold is overcome"() {
        given:
        def event = Schema.MeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("w01")
                .setSensorId("s01")
                .setValue(value)
                .setType(type)
                .setMoment(System.currentTimeMillis())
                .build()

        mockedThresholdRepository.getByType(type) >> {
            Optional.of(new MeasurementThreshold(
                    (short) 1,
                    type,
                    threshold
            ))
        }

        when:
        service.handle(event)

        then:
        1 * mockedAlertEventRecipient.send(_)

        where:
        type                               | threshold | value
        Schema.MeasurementType.HUMIDITY    | 50        | 51
        Schema.MeasurementType.TEMPERATURE | 35        | 36
    }

    def "Shouldn't notify an Alert when threshold isn't overcome"() {
        given:
        def event = Schema.MeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("w01")
                .setSensorId("s01")
                .setValue(value)
                .setType(type)
                .setMoment(System.currentTimeMillis())
                .build()

        mockedThresholdRepository.getByType(type) >> {
            Optional.of(new MeasurementThreshold(
                    (short) 1,
                    type,
                    threshold
            ))
        }

        when:
        service.handle(event)

        then:
        0 * mockedAlertEventRecipient.send(_)

        where:
        type                               | threshold | value
        Schema.MeasurementType.HUMIDITY    | 50        | 50
        Schema.MeasurementType.HUMIDITY    | 50        | 49
        Schema.MeasurementType.TEMPERATURE | 35        | 35
        Schema.MeasurementType.TEMPERATURE | 35        | 34
    }

    def "Should notify an Alert"() {
        given:
        def event = Schema.MeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("w01")
                .setSensorId("s01")
                .setValue(value)
                .setType(type)
                .setMoment(System.currentTimeMillis())
                .build()

        mockedThresholdRepository.getByType(type) >> {
            Optional.of(new MeasurementThreshold(
                    (short) 1,
                    type,
                    threshold
            ))
        }

        when:
        service.handle(event)

        then:
        1 * mockedAlertEventRecipient.send(_)

        where:
        type                               | threshold | value
        Schema.MeasurementType.HUMIDITY    | 50        | 51
        Schema.MeasurementType.TEMPERATURE | 35        | 36
    }

    def "Shouldn't notify when event is duplicated"() {
        given:
        def event = Schema.MeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("w01")
                .setSensorId("s01")
                .setValue(value)
                .setType(type)
                .setMoment(System.currentTimeMillis())
                .build()

        mockedThresholdRepository.getByType(type) >> {
            Optional.of(new MeasurementThreshold(
                    (short) 1,
                    type,
                    threshold
            ))
        }
        mockedProcessedEventRepository.save(_ as ProcessedEvent) >> {
            throw new DuplicateKeyException("Mocked Exception")
        }

        when:
        service.handle(event)

        then:
        thrown(DuplicatedEventException)

        where:
        type                               | threshold | value
        Schema.MeasurementType.HUMIDITY    | 50        | 51
        Schema.MeasurementType.TEMPERATURE | 35        | 36
    }

    def "Should delete processed event"() {
        given:
        def eventId = "1"
        def event = Schema.MeasurementEvent.newBuilder()
                .setId(eventId)
                .setWarehouseId("w01")
                .setSensorId("s01")
                .setValue(51)
                .setType(Schema.MeasurementType.HUMIDITY)
                .setMoment(System.currentTimeMillis())
                .build()
        mockedThresholdRepository.getByType(Schema.MeasurementType.HUMIDITY) >> {
            Optional.of(new MeasurementThreshold(
                    (short) 1,
                    Schema.MeasurementType.HUMIDITY,
                    50
            ))
        }
        mockedAlertEventRecipient.send(_ as Schema.MeasurementAlertEvent) >> {
            throw new RuntimeException("Mocked Exception")
        }

        when:
        service.handle(event)

        then:
        thrown(RuntimeException)
        1 * mockedProcessedEventRepository.delete(_)
    }

    def "Should handle unprocessed event"() {
        given:
        def eventId = "1"
        mockedThresholdRepository.getByType(Schema.MeasurementType.HUMIDITY) >> {
            Optional.of(new MeasurementThreshold(
                    (short) 1,
                    Schema.MeasurementType.HUMIDITY,
                    40
            ))
        }
        mockedAlertEventRecipient.send(_ as Schema.MeasurementAlertEvent) >> {
            throw new RuntimeException("Mocked Exception")
        }

        when:
        service.handleUnprocessed(new UnprocessedEvent(eventId))

        then:
        1 * mockedProcessedEventRepository.delete(_)

    }

}
