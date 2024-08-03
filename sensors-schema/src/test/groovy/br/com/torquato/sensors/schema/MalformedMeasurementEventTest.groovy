package br.com.torquato.sensors.schema

import spock.lang.Specification

import java.time.LocalDateTime

class MalformedMeasurementEventTest extends Specification {
    def "Should fail when ID is null or empty"() {
        when:
        new MalformedMeasurementEvent(
                id,
                "w1",
                MeasurementType.TEMPERATURE,
                LocalDateTime.now(),
                null
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "ID cannot be null or empty."

        where:
        id << [null, "", " "]
    }

    def "Should fail when Warehouse ID is null or empty"() {
        when:
        new MalformedMeasurementEvent(
                "1",
                warehouseId,
                MeasurementType.TEMPERATURE,
                LocalDateTime.now(),
                null
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Warehouse ID cannot be null or empty."

        where:
        warehouseId << [null, "", " "]
    }

    def "Should fail when Moment is null or empty"() {
        when:
        new MalformedMeasurementEvent(
                "1",
                "w1",
                MeasurementType.TEMPERATURE,
                null,
                null
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Moment cannot be null."
    }

    def "Should fail when Type is null or empty"() {
        when:
        new MalformedMeasurementEvent(
                "1",
                "w1",
                null,
                LocalDateTime.now(),
                null
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Type cannot be null."
    }

    def "Should create a MalformedMeasurementEvent with success"() {
        when:
        def event = new MalformedMeasurementEvent(
                "1",
                "w1",
                MeasurementType.TEMPERATURE,
                LocalDateTime.now(),
                null
        )

        then:
        event != null
    }
}
