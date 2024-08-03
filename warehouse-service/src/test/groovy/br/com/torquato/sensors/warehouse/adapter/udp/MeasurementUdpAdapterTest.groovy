package br.com.torquato.sensors.warehouse.adapter.udp

import br.com.torquato.sensors.schema.MalformedMeasurementEvent
import br.com.torquato.sensors.schema.MeasurementEvent
import br.com.torquato.sensors.schema.MeasurementType
import br.com.torquato.sensors.warehouse.adapter.mapper.MalformedMeasurementEventMapper
import br.com.torquato.sensors.warehouse.adapter.mapper.MeasurementEventMapper
import br.com.torquato.sensors.warehouse.port.MeasurementEventRecipient
import org.springframework.messaging.Message
import spock.lang.Specification

import java.time.LocalDateTime

class MeasurementUdpAdapterTest extends Specification {

    MeasurementEventMapper eventMapper
    MalformedMeasurementEventMapper malformedEventMapper
    MeasurementEventRecipient messageRecipient
    MeasurementUdpAdapter adapter

    def setup() {
        eventMapper = Stub()
        malformedEventMapper = Stub()
        messageRecipient = Mock(MeasurementEventRecipient)
        adapter = new MeasurementUdpAdapter(eventMapper, malformedEventMapper, messageRecipient)
    }

    def "Should handle a malformed event properly"() {
        given:
        def message = Stub(Message<byte[]>)
        def malformedEvent = new MalformedMeasurementEvent(
                "1",
                "w01",
                MeasurementType.HUMIDITY,
                LocalDateTime.now(),
                "sensor_id;value"
        )

        eventMapper.from(message, MeasurementType.HUMIDITY) >> {
            throw new IllegalArgumentException("Mocked error")
        }
        malformedEventMapper.from(message, MeasurementType.HUMIDITY) >> malformedEvent

        when:
        adapter.handleHumidityMessage(message)

        then:
        1 * messageRecipient.send(malformedEvent)
    }

    def "Should process an UDP humidity message"() {
        given:
        def message = Stub(Message<byte[]>)
        def event = new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                MeasurementType.HUMIDITY,
                LocalDateTime.now()
        )

        eventMapper.from(message, MeasurementType.HUMIDITY) >> event

        when:
        adapter.handleHumidityMessage(message)

        then:
        1 * messageRecipient.send(event)
    }


    def "Should process an UDP temperature message"() {
        given:
        def message = Stub(Message<byte[]>)
        def event = new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                MeasurementType.TEMPERATURE,
                LocalDateTime.now()
        )

        eventMapper.from(message, MeasurementType.TEMPERATURE) >> event

        when:
        adapter.handleTemperatureMessage(message)

        then:
        1 * messageRecipient.send(event)
    }

}
