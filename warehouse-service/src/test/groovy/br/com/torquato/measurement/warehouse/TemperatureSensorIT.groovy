package br.com.torquato.measurement.warehouse


import br.com.torquato.measurement.warehouse.support.ITSupport
import br.com.torquato.measurement.warehouse.support.UdpClient
import br.com.torquato.measurements.schema.Schema
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils

class TemperatureSensorIT extends ITSupport {

    UdpClient temperatureClient
    @Autowired
    ConsumerFactory<String, Schema.MeasurementEvent> consumerFactory
    Consumer<String, Schema.MeasurementEvent> consumer

    def setup() {
        temperatureClient = new UdpClient("localhost", 3344)
        consumer = consumerFactory.createConsumer()
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

        then: 'Kafka topic temperature-measurements-data have both messages'
        records.count() == 2
        records.iterator()
                *.value()
                .find { it -> it.sensorId == 't1' && it.value == 40 } != null
        records.iterator()
                *.value()
                .find { it -> it.sensorId == 't2' && it.value == 45 } != null
    }
}
