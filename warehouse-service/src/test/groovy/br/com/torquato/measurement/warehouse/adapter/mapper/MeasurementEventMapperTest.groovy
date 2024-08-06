package br.com.torquato.measurement.warehouse.adapter.mapper

import br.com.torquato.measurement.warehouse.config.Configurations
import br.com.torquato.measurements.schema.Schema
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.util.function.Supplier

class MeasurementEventMapperTest extends Specification {

    MeasurementEventMapper mapper
    Message<byte[]> mockedMessage
    MessageHeaders mockedHeaders
    Supplier<Long> mockedTimestampSupplier
    Supplier<UUID> mockedUuidSupplier

    def setup() {
        mockedMessage = Stub()
        mockedHeaders = Stub()
        mockedMessage.headers >> mockedHeaders
        mockedTimestampSupplier = Stub()
        mockedUuidSupplier = Stub()

        def mockedConfigurations = Stub(Configurations)
        mockedConfigurations.warehouseId >> 'default'
        mapper = new MeasurementEventMapper(
                mockedConfigurations,
                mockedTimestampSupplier,
                mockedUuidSupplier
        )
    }

    def "Should fail when message payload is malformed"() {
        given:
        def messageId = UUID.randomUUID()
        def timestamp = System.currentTimeMillis()

        mockedMessage.payload >> payload.getBytes(StandardCharsets.UTF_8)
        mockedHeaders.getId() >> messageId
        mockedHeaders.getTimestamp() >> timestamp

        when:
        mapper.from(mockedMessage, Schema.MeasurementType.TEMPERATURE)

        then:

        thrown(IllegalArgumentException)

        where:
        payload << ['sensor_id;value=30', 'sensor_id=t1;value=', 'sensor_id=t1value=30']
    }

    def "Should map MeasurementEvent with success"() {
        given:
        def messageId = UUID.randomUUID()
        def timestamp = System.currentTimeMillis()

        mockedMessage.payload >> "sensor_id=$sensorId;value=$value".getBytes(StandardCharsets.UTF_8)
        mockedHeaders.getId() >> messageId
        mockedHeaders.getTimestamp() >> timestamp

        when:
        def evt = mapper.from(mockedMessage, type)

        then:
        evt != null
        evt.getId() == messageId.toString()
        evt.getValue() == value
        evt.getSensorId() == sensorId
        evt.getWarehouseId() == 'default'
        evt.getType() == type
        evt.getMoment() == timestamp

        where:
        type                               | sensorId | value
        Schema.MeasurementType.TEMPERATURE | 't1'     | 30
        Schema.MeasurementType.HUMIDITY    | 'h1'     | 60
    }


    def "Should map MeasurementEvent with success using suppliers"() {
        given:
        def messageId = UUID.randomUUID()
        def moment = System.currentTimeMillis()

        mockedHeaders.getId() >> null
        mockedHeaders.getTimestamp() >> null
        mockedMessage.payload >> "sensor_id=$sensorId;value=$value".getBytes(StandardCharsets.UTF_8)
        mockedTimestampSupplier.get() >> moment
        mockedUuidSupplier.get() >> messageId

        when:
        def evt = mapper.from(mockedMessage, type)

        then:
        evt != null
        evt.getId() == messageId.toString()
        evt.getValue() == value
        evt.getSensorId() == sensorId
        evt.getWarehouseId() == 'default'
        evt.getType() == type
        evt.getMoment() == moment

        where:
        type                               | sensorId | value
        Schema.MeasurementType.TEMPERATURE | 't1'     | 30
        Schema.MeasurementType.HUMIDITY    | 'h1'     | 60
    }


}
