package br.com.torquato.sensors.schema

import spock.lang.Specification

import java.time.LocalDateTime

class TemperatureAlertEventTest extends Specification {

    def validTemperatureEvent = new TemperatureEvent(
            "1",
            "w01",
            "s01",
            (short) 30,
            DegreeUnit.C,
            LocalDateTime.now()
    )

    def "Should fail when ID is null or empty"() {
        when:
        new TemperatureAlertEvent(
                id,
                validTemperatureEvent,
                LocalDateTime.now(),
                (short) 40
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "ID cannot be null or empty."

        where:
        id << [null, "", " "]

    }


    def "Should fail when Source Event is null"() {
        when:
        new TemperatureAlertEvent(
                "1",
                null,
                LocalDateTime.now(),
                (short) 40
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Source Event cannot be null."
    }

    def "Should fail when Moment is null"() {
        when:
        new TemperatureAlertEvent(
                "1",
                validTemperatureEvent,
                null,
                (short) 40
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Moment cannot be null."
    }

    def "Should create a HumidityAlertEvent with success"() {
        when:
        def event = new TemperatureAlertEvent(
                "1",
                validTemperatureEvent,
                LocalDateTime.now(),
                (short) 40
        )

        then:
        event != null
    }

}
