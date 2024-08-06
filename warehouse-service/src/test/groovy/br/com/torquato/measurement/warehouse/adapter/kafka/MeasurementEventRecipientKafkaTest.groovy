package br.com.torquato.measurement.warehouse.adapter.kafka

import br.com.torquato.measurements.schema.Schema
import com.google.protobuf.ByteString
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class MeasurementEventRecipientKafkaTest extends Specification {

    KafkaTemplate<String, Object> mockedKafkaTemplate
    MeasurementEventRecipientKafka recipient

    def setup() {
        mockedKafkaTemplate = Stub()
        recipient = new MeasurementEventRecipientKafka(mockedKafkaTemplate)
    }

    def "Should send MeasurementEvent"() {
        given:
        def event = Schema.MeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("w01")
                .setSensorId("s01")
                .setValue(30)
                .setType(type)
                .setMoment(System.currentTimeMillis())
                .build()

        def future = Mock(CompletableFuture<SendResult<String, Object>>)
        mockedKafkaTemplate.send(topic, _ as String, event) >> future

        when:
        recipient.send(event)

        then:
        1 * future.whenCompleteAsync(_ as BiConsumer)

        where:
        type                               | topic
        Schema.MeasurementType.HUMIDITY    | "humidity-measurements-data"
        Schema.MeasurementType.TEMPERATURE | "temperature-measurements-data"
    }


    def "Should send MalformedMeasurementEvent"() {
        given:
        def malformedEvent = Schema.MalformedMeasurementEvent.newBuilder()
                .setId("1")
                .setWarehouseId("1")
                .setType(type as Schema.MeasurementType)
                .setMoment(System.currentTimeMillis())
                .setPayload(ByteString.copyFromUtf8("sensor_id;value"))
                .build()

        def future = Mock(CompletableFuture<SendResult<String, Object>>)
        mockedKafkaTemplate.send("malformed-measurements-data", _ as String, malformedEvent) >> future

        when:
        recipient.send(malformedEvent)

        then: 'Kafka send it'
        1 * future.whenCompleteAsync(_ as BiConsumer)

        where:
        type << Schema.MeasurementType.values()
    }

}
