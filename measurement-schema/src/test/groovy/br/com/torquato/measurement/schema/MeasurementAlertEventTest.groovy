package br.com.torquato.measurement.schema

import spock.lang.Specification

import java.time.LocalDateTime

class MeasurementAlertEventTest extends Specification {

    def validMeasurementEvent = new MeasurementEvent(
            "1",
            "w01",
            "s01",
            30,
            MeasurementType.TEMPERATURE,
            LocalDateTime.now()
    )

    def "Should fail when ID is null or empty"() {
        when:
        new MeasurementAlertEvent(
                id,
                validMeasurementEvent,
                LocalDateTime.now(),
                40
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "ID cannot be null or empty."

        where:
        id << [null, "", " "]

    }


    def "Should fail when Source Event is null"() {
        when:
        new MeasurementAlertEvent(
                "1",
                null,
                LocalDateTime.now(),
                40
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Source Event cannot be null."
    }

    def "Should fail when Moment is null"() {
        when:
        new MeasurementAlertEvent(
                "1",
                validMeasurementEvent,
                null,
                40
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Moment cannot be null."
    }

    def "Should fail when threshold is null"() {
        when:
        new MeasurementAlertEvent(
                "1",
                validMeasurementEvent,
                LocalDateTime.now(),
                null
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Threshold cannot be null."
    }

    def "Should create a HumidityAlertEvent with success"() {
        when:
        def event = new MeasurementAlertEvent(
                "1",
                validMeasurementEvent,
                LocalDateTime.now(),
                40
        )

        then:
        event != null
    }

}
