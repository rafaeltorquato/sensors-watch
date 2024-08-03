package br.com.torquato.sensors.warehouse.adapter.mapper

import br.com.torquato.sensors.schema.MeasurementType
import br.com.torquato.sensors.warehouse.config.AppConfig
import br.com.torquato.sensors.warehouse.utils.LocalDateTimeUtils
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.function.Supplier

class MalformedMeasurementEventMapperTest extends Specification {

    MalformedMeasurementEventMapper mapper
    Message<byte[]> mockedMessage
    MessageHeaders mockedHeaders
    Supplier<LocalDateTime> mockedLocalDateTimeSupplier
    Supplier<UUID> mockedUuidSupplier


    def setup() {
        mockedMessage = Stub()
        mockedHeaders = Stub()
        mockedMessage.headers >> mockedHeaders
        mockedLocalDateTimeSupplier = Stub();
        mockedUuidSupplier = Stub();

        def mockedAppConfig = Stub(AppConfig)
        mockedAppConfig.warehouseId >> 'default'
        mapper = new MalformedMeasurementEventMapper(
                mockedAppConfig,
                mockedLocalDateTimeSupplier,
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
        def evt = mapper.from(mockedMessage, type as MeasurementType)

        then:
        evt != null
        evt.id() == messageId.toString()
        evt.warehouseId() == 'default'
        evt.type() == type
        evt.moment() == LocalDateTimeUtils.toLocalDateTime(timestamp)

        where:
        type << MeasurementType.values()
    }

    def "Should map MalformedMeasurementEvent with success using suppliers"() {
        given:
        def messageId = UUID.randomUUID()
        def moment = LocalDateTime.now()

        mockedHeaders.getId() >> null
        mockedHeaders.getTimestamp() >> null
        mockedMessage.payload >> "sensor_id;value".getBytes(StandardCharsets.UTF_8)
        mockedUuidSupplier.get() >> messageId
        mockedLocalDateTimeSupplier.get() >> moment

        when:
        def evt = mapper.from(mockedMessage, type as MeasurementType)

        then:
        evt != null
        evt.id() == messageId.toString()
        evt.warehouseId() == 'default'
        evt.type() == type
        evt.moment() == moment

        where:
        type << MeasurementType.values()
    }

}
