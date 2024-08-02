package br.com.torquato.sensors.schema


import spock.lang.Specification

import java.time.LocalDateTime

class TemperatureEventTest extends Specification {


    def "Should fail when ID is null or empty"() {
        when:
        new TemperatureEvent(
                id,
                "w01",
                "s01",
                (short) 30,
                DegreeUnit.C,
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
        new TemperatureEvent(
                "1",
                warehouseId as String,
                "s01",
                (short) 30,
                DegreeUnit.C,
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
        new TemperatureEvent(
                "1",
                "w01",
                sensorId,
                (short) 30,
                DegreeUnit.C,
                LocalDateTime.now()
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Sensor ID cannot be null or empty."

        where:
        sensorId << [null, "", " "]
    }

    def "Should fail when DegreeUnit is null"() {
        when:
        new TemperatureEvent(
                "1",
                "w01",
                "s01",
                (short) 30,
                null,
                LocalDateTime.now()
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "DegreeUnit cannot be null."
    }


    def "Should fail when Moment is null"() {
        when:
        new TemperatureEvent(
                "1",
                "w01",
                "s01",
                (short) 30,
                DegreeUnit.C,
                null
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Moment cannot be null."
    }

    def "Should create a TemperatureEvent with success"() {
        when:
        def event = new TemperatureEvent(
                "1",
                "w01",
                "s01",
                (short) 30,
                DegreeUnit.C,
                LocalDateTime.now()
        )

        then:
        event != null
    }

}
