package br.com.torquato.sensors.schema

import spock.lang.Specification

import java.time.LocalDateTime

class HumidityAlertEventTest extends Specification {

    def validHumidityEvent = new HumidityEvent(
            "1",
            "w01",
            "s01",
            0.3,
            LocalDateTime.now()
    )

    def "Should fail when ID is null or empty"() {
        when:
        new HumidityAlertEvent(
                id,
                validHumidityEvent,
                LocalDateTime.now(),
                0.3
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "ID cannot be null or empty."

        where:
        id << [null, "", " "]

    }


    def "Should fail when Source Event is null"() {
        when:
        new HumidityAlertEvent(
                "1",
                null,
                LocalDateTime.now(),
                0.3
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Source Event cannot be null."
    }

    def "Should fail when Moment is null"() {
        when:
        new HumidityAlertEvent(
                "1",
                validHumidityEvent,
                null,
                0.3
        )

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Moment cannot be null."
    }

    def "Should create a HumidityAlertEvent with success"() {
        when:
        def event = new HumidityAlertEvent(
                "1",
                validHumidityEvent,
                LocalDateTime.now(),
                0.3
        )

        then:
        event != null
    }

}
