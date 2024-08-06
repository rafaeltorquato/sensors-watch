package br.com.torquato.measurement.warehouse


import br.com.torquato.measurement.warehouse.support.ITSupport
import br.com.torquato.measurement.warehouse.support.UdpClient
import br.com.torquato.measurements.schema.Schema
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils

import java.nio.charset.StandardCharsets

class MalformedMessagesIT extends ITSupport {

    UdpClient temperatureClient
    UdpClient humidityClient
    @Autowired
    ConsumerFactory<String, Schema.MalformedMeasurementEvent> consumerFactory
    Consumer<String, Schema.MalformedMeasurementEvent> consumer

    def setup() {
        humidityClient = new UdpClient("localhost", 3345)
        temperatureClient = new UdpClient("localhost", 3344)
        consumer = consumerFactory.createConsumer()
    }

    def cleanup() {
        humidityClient?.close()
        temperatureClient?.close()
        consumer?.close()
    }


    def "Should receive UDP malformed messages and process it"() {
        given: 'Two UPD messages'
        def payload1 = "sensor_id=;value="
        def payload2 = "sensor_id=t2value=51"

        when: 'Messages are sent'
        humidityClient.sendEcho(payload1)
        temperatureClient.sendEcho(payload2)

        this.consumer.subscribe(["malformed-measurements-data"])
        def records = KafkaTestUtils.getRecords(this.consumer)

        then: 'Kafka topic malformed-measurements-data has both messages'
        records.count() == 2
        records.iterator()
                *.value()
                *.getType().containsAll(Schema.MeasurementType.values())
        records.iterator()
                *.value()
                *.getPayload()
                *.toString(StandardCharsets.UTF_8)
                .containsAll([payload1, payload2])


    }
}
