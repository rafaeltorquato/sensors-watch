package br.com.torquato.measurement.warehouse.adapter.mapper


import br.com.torquato.measurement.warehouse.config.Configurations
import br.com.torquato.measurements.schema.Schema
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.util.function.Supplier

class MalformedMeasurementEventMapperTest extends Specification {

    MalformedMeasurementEventMapper mapper
    Message<byte[]> mockedMessage
    MessageHeaders mockedHeaders
    Supplier<Long> mockedTimestampSupplier
    Supplier<UUID> mockedUuidSupplier


    def setup() {
        mockedMessage = Stub()
        mockedHeaders = Stub()
        mockedMessage.headers >> mockedHeaders
        mockedTimestampSupplier = Stub();
        mockedUuidSupplier = Stub();

        def mockedConfigurations = Stub(Configurations)
        mockedConfigurations.warehouseId >> 'default'
        mapper = new MalformedMeasurementEventMapper(
                mockedConfigurations,
                mockedTimestampSupplier,
                mockedUuidSupplier
        )
    }

    def "Should map MalformedMeasurementEvent with success"() {
        given:
        def messageId = UUID.randomUUID()
        def timestamp = System.currentTimeMillis()

        mockedMessage.payload >> "sensor_id;value".getBytes(StandardCharsets.UTF_8)
        mockedHeaders.getId() >> messageId
        mockedHeaders.getTimestamp() >> timestamp

        when:
        def evt = mapper.from(mockedMessage, type as Schema.MeasurementType)

        then:
        evt != null
        evt.getId() == messageId.toString()
        evt.getWarehouseId() == 'default'
        evt.getType() == type
        evt.getMoment() == timestamp

        where:
        type << Schema.MeasurementType.values()
    }

    def "Should map MalformedMeasurementEvent with success using suppliers"() {
        given:
        def messageId = UUID.randomUUID()
        def timestamp = System.currentTimeMillis()

        mockedHeaders.getId() >> null
        mockedHeaders.getTimestamp() >> null
        mockedMessage.payload >> "sensor_id;value".getBytes(StandardCharsets.UTF_8)
        mockedUuidSupplier.get() >> messageId
        mockedTimestampSupplier.get() >> timestamp

        when:
        def evt = mapper.from(mockedMessage, type as Schema.MeasurementType)

        then:
        evt != null
        evt.getId() == messageId.toString()
        evt.getWarehouseId() == 'default'
        evt.getType() == type
        evt.getMoment() == timestamp

        where:
        type << Schema.MeasurementType.values()
    }

}
