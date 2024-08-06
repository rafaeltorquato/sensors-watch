package br.com.torquato.measurement.warehouse.adapter.udp


import br.com.torquato.measurement.warehouse.adapter.mapper.MalformedMeasurementEventMapper
import br.com.torquato.measurement.warehouse.adapter.mapper.MeasurementEventMapper
import br.com.torquato.measurement.warehouse.port.MeasurementEventRecipient
import br.com.torquato.measurements.schema.Schema
import com.google.protobuf.ByteString
import org.springframework.messaging.Message
import spock.lang.Specification

class MeasurementUdpTest extends Specification {

    MeasurementEventMapper eventMapper
    MalformedMeasurementEventMapper malformedEventMapper
    MeasurementEventRecipient messageRecipient
    MeasurementUdp adapter

    def setup() {
        eventMapper = Stub()
        malformedEventMapper = Stub()
        messageRecipient = Mock(MeasurementEventRecipient)
        adapter = new MeasurementUdp(eventMapper, malformedEventMapper, messageRecipient)
    }

    def "Should handle a malformed event properly"() {
        given:
        def message = Stub(Message<byte[]>)
        def malformedEvent = Schema.MalformedMeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("w01")
                .setType(Schema.MeasurementType.HUMIDITY)
                .setMoment(System.currentTimeMillis())
                .setPayload(ByteString.copyFromUtf8("sensor_id;value"))
                .build()

        eventMapper.from(message, Schema.MeasurementType.HUMIDITY) >> {
            throw new IllegalArgumentException("Mocked error")
        }
        malformedEventMapper.from(message, Schema.MeasurementType.HUMIDITY) >> malformedEvent

        when:
        adapter.handleHumidityMessage(message)

        then:
        1 * messageRecipient.send(malformedEvent)
    }

    def "Should process an UDP humidity message"() {
        given:
        def message = Stub(Message<byte[]>)
        def event = Schema.MeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("w01")
                .setSensorId("s01")
                .setValue(30)
                .setType(Schema.MeasurementType.HUMIDITY)
                .setMoment(System.currentTimeMillis())
                .build()

        eventMapper.from(message, Schema.MeasurementType.HUMIDITY) >> event

        when:
        adapter.handleHumidityMessage(message)

        then:
        1 * messageRecipient.send(event)
    }


    def "Should process an UDP temperature message"() {
        given:
        def message = Stub(Message<byte[]>)
        def event = Schema.MeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("w01")
                .setSensorId("s01")
                .setValue(30)
                .setType(Schema.MeasurementType.TEMPERATURE)
                .setMoment(System.currentTimeMillis())
                .build()

        eventMapper.from(message, Schema.MeasurementType.TEMPERATURE) >> event

        when:
        adapter.handleTemperatureMessage(message)

        then:
        1 * messageRecipient.send(event)
    }

}
