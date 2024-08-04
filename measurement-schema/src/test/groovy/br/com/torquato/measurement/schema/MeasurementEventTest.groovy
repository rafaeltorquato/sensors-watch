package br.com.torquato.measurement.schema


import spock.lang.Specification

import java.time.LocalDateTime

class MeasurementEventTest extends Specification {


    def "Should fail when ID is null or empty"() {
        when:
        new MeasurementEvent(
                id,
                "w01",
                "s01",
                30,
                MeasurementType.TEMPERATURE,
                LocalDateTime.now()
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "ID cannot be null or empty."

        where:
        id << [null, "", " "]
    }


    def "Should fail when Warehouse ID is null or empty"() {
        when:
        new MeasurementEvent(
                "1",
                warehouseId as String,
                "s01",
                30,
                MeasurementType.TEMPERATURE,
                LocalDateTime.now()
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Warehouse ID cannot be null or empty."

        where:
        warehouseId << [null, "", " "]
    }

    def "Should fail when Sensor ID is null or empty"() {
        when:
        new MeasurementEvent(
                "1",
                "w01",
                sensorId,
                30,
                MeasurementType.TEMPERATURE,
                LocalDateTime.now()
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Sensor ID cannot be null or empty."

        where:
        sensorId << [null, "", " "]
    }

    def "Should fail when Type is null"() {
        when:
        new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                null,
                LocalDateTime.now()
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Type cannot be null."
    }


    def "Should fail when Moment is null"() {
        when:
        new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                MeasurementType.TEMPERATURE,
                null
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Moment cannot be null."
    }

    def "Should fail when Value is null"() {
        when:
        new MeasurementEvent(
                "1",
                "w01",
                "s01",
                null,
                MeasurementType.TEMPERATURE,
                LocalDateTime.now()
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Value cannot be null."
    }

    def "Should create a MeasurementEvent with success"() {
        when:
        def event = new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                MeasurementType.HUMIDITY,
                LocalDateTime.now()
        )

        then:
        event != null
    }

}
