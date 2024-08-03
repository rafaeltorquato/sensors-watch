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

class MeasurementEventMapperTest extends Specification {

    MeasurementEventMapper mapper
    Message<byte[]> mockedMessage
    MessageHeaders mockedHeaders
    Supplier<LocalDateTime> mockedLocalDateTimeSupplier
    Supplier<UUID> mockedUuidSupplier

    def setup() {
        mockedMessage = Stub()
        mockedHeaders = Stub()
        mockedMessage.headers >> mockedHeaders
        mockedLocalDateTimeSupplier = Stub()
        mockedUuidSupplier = Stub()

        def mockedAppConfig = Stub(AppConfig)
        mockedAppConfig.warehouseId >> 'default'
        mapper = new MeasurementEventMapper(
                mockedAppConfig,
                mockedLocalDateTimeSupplier,
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
        mapper.from(mockedMessage, MeasurementType.TEMPERATURE)

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
        evt.id() == messageId.toString()
        evt.value() == value
        evt.sensorId() == sensorId
        evt.warehouseId() == 'default'
        evt.type() == type
        evt.moment() == LocalDateTimeUtils.toLocalDateTime(timestamp)

        where:
        type                        | sensorId | value
        MeasurementType.TEMPERATURE | 't1'     | 30
        MeasurementType.HUMIDITY    | 'h1'     | 60
    }


    def "Should map MeasurementEvent with success using suppliers"() {
        given:
        def messageId = UUID.randomUUID()
        def moment = LocalDateTime.now()

        mockedHeaders.getId() >> null
        mockedHeaders.getTimestamp() >> null
        mockedMessage.payload >> "sensor_id=$sensorId;value=$value".getBytes(StandardCharsets.UTF_8)
        mockedLocalDateTimeSupplier.get() >> moment
        mockedUuidSupplier.get() >> messageId

        when:
        def evt = mapper.from(mockedMessage, type)

        then:
        evt != null
        evt.id() == messageId.toString()
        evt.value() == value
        evt.sensorId() == sensorId
        evt.warehouseId() == 'default'
        evt.type() == type
        evt.moment() == moment

        where:
        type                        | sensorId | value
        MeasurementType.TEMPERATURE | 't1'     | 30
        MeasurementType.HUMIDITY    | 'h1'     | 60
    }


}
