package br.com.torquato.measurement.warehouse

import br.com.torquato.measurement.schema.MeasurementEvent
import br.com.torquato.measurement.warehouse.support.ITSupport
import br.com.torquato.measurement.warehouse.support.UdpClient
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils

class TemperatureSensorIT extends ITSupport {

    UdpClient temperatureClient
    @Autowired
    ConsumerFactory<String, MeasurementEvent> consumerFactory
    Consumer<String, MeasurementEvent> consumer

    def setup() {
        temperatureClient = new UdpClient("localhost", 3344)
        this.consumer = consumerFactory.createConsumer()
    }

    def cleanup() {
        temperatureClient?.close()
        consumer?.close()
    }

    def "Should receive UDP temperature messages and process it"() {
        given: 'Two UPD messages'
        def payload1 = "sensor_id=t1;value=40"
        def payload2 = "sensor_id=t2;value=45"

        when: 'Messages are sent'
        temperatureClient.sendEcho(payload1)
        temperatureClient.sendEcho(payload2)
        this.consumer.subscribe(["temperature-measurements-data"])
        def records = KafkaTestUtils.getRecords(this.consumer)

        then: 'Kafka topic temperature-measurements-data has both messages'
        records.iterator()
                *.value()
                *.sensorId()
                .containsAll(["t1", "t2"])
        records.iterator()
                *.value()
                *.value()
                .containsAll([40, 45])
    }
}
