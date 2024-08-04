package br.com.torquato.measurement.monitoring.service

import br.com.torquato.measurement.monitoring.domain.DuplicatedEventException
import br.com.torquato.measurement.monitoring.domain.MeasurementThreshold
import br.com.torquato.measurement.monitoring.domain.MeasurementThresholdRepository
import br.com.torquato.measurement.monitoring.domain.ProcessedEventRepository
import br.com.torquato.measurement.monitoring.port.MeasurementAlertEventRecipient
import br.com.torquato.measurement.schema.MeasurementEvent
import br.com.torquato.measurement.schema.MeasurementType
import spock.lang.Specification

import java.time.LocalDateTime

class MeasurementServiceTest extends Specification {

    MeasurementThresholdRepository mockedThresholdRepository
    ProcessedEventRepository mockedProcessedEventRepository
    MeasurementAlertEventRecipient mockedAlertEventRecipient
    MeasurementService service

    def setup() {
        mockedThresholdRepository = Stub()
        mockedProcessedEventRepository = Mock()
        mockedAlertEventRecipient = Stub()

        service = new MeasurementService(
                mockedThresholdRepository,
                mockedProcessedEventRepository,
                mockedAlertEventRecipient
        )
    }

    def "Should notify an Alert when threshold is overcome"() {
        given:
        def event = new MeasurementEvent(
                "1",
                "w1",
                "s1",
                value,
                type,
                LocalDateTime.now()
        )

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
        type                        | threshold | value
        MeasurementType.HUMIDITY    | 50        | 51
        MeasurementType.TEMPERATURE | 35        | 36
    }

    def "Shouldn't notify an Alert when threshold isn't overcome"() {
        given:
        def event = new MeasurementEvent(
                "1",
                "w1",
                "s1",
                value,
                type,
                LocalDateTime.now()
        )

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
        type                        | threshold | value
        MeasurementType.HUMIDITY    | 50        | 50
        MeasurementType.HUMIDITY    | 50        | 49
        MeasurementType.TEMPERATURE | 35        | 35
        MeasurementType.TEMPERATURE | 35        | 34
    }

    def "Should notify an Alert"() {
        given:
        def event = new MeasurementEvent(
                "1",
                "w1",
                "s1",
                value,
                type,
                LocalDateTime.now()
        )

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
        type                        | threshold | value
        MeasurementType.HUMIDITY    | 50        | 51
        MeasurementType.TEMPERATURE | 35        | 36
    }

    def "Shouldn't notify when event is duplicated"() {
        given:
        def event = new MeasurementEvent(
                "1",
                "w1",
                "s1",
                value,
                type,
                LocalDateTime.now()
        )

        mockedThresholdRepository.getByType(type) >> {
            Optional.of(new MeasurementThreshold(
                    (short) 1,
                    type,
                    threshold
            ))
        }
        mockedProcessedEventRepository.save(_) >> {
            throw new DuplicatedEventException("Mocked Exception")
        }

        when:
        service.handle(event)

        then:
        0 * mockedAlertEventRecipient.send(_)

        where:
        type                        | threshold | value
        MeasurementType.HUMIDITY    | 50        | 51
        MeasurementType.TEMPERATURE | 35        | 36
    }

}
