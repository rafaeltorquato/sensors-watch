package br.com.torquato.sensors.schema


import spock.lang.Specification

import java.time.LocalDateTime

class HumidityEventTest extends Specification {


    def "Should fail when ID is null or empty"() {
        when:
        new HumidityEvent(
                id,
                "w01",
                "s01",
                0.3,
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
        new HumidityEvent(
                "1",
                warehouseId as String,
                "s01",
                0.3,
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
        new HumidityEvent(
                "1",
                "w01",
                sensorId,
                0.3,
                LocalDateTime.now()
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Sensor ID cannot be null or empty."

        where:
        sensorId << [null, "", " "]
    }

    def "Should fail when Moment is null"() {
        when:
        new HumidityEvent(
                "1",
                "w01",
                "s01",
                0.3,
                null
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Moment cannot be null."
    }

    def "Should create a HumidityEvent with success"() {
        when:
        def event = new HumidityEvent(
                "1",
                "w01",
                "s01",
                0.3,
                LocalDateTime.now()
        )

        then:
        event != null
    }

}
