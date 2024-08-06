package br.com.torquato.measurement.warehouse.adapter.kafka


import br.com.torquato.measurements.schema.Schema
import com.google.protobuf.ByteString
import org.springframework.kafka.core.KafkaTemplate
import spock.lang.Specification

class MeasurementEventRecipientKafkaTest extends Specification {

    KafkaTemplate<String, Object> mockedKafkaTemplate
    MeasurementEventRecipientKafka recipient

    def setup() {
        mockedKafkaTemplate = Mock()
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


        when:
        recipient.send(event)

        then:
        1 * mockedKafkaTemplate.send(_, _, event)

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
                .setType(Schema.MeasurementType.HUMIDITY)
                .setMoment(System.currentTimeMillis())
                .setPayload(ByteString.copyFromUtf8("sensor_id;value"))
                .build();

        when:
        recipient.send(malformedEvent)

        then:
        1 * mockedKafkaTemplate.send("malformed-measurements-data", _, malformedEvent)
    }
}
