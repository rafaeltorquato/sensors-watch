package br.com.torquato.measurement.warehouse

import br.com.torquato.measurement.schema.MeasurementEvent
import br.com.torquato.measurement.warehouse.support.ITSupport
import br.com.torquato.measurement.warehouse.support.UdpClient
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils

class HumiditySensorIT extends ITSupport {

    UdpClient humidityClient
    @Autowired
    ConsumerFactory<String, MeasurementEvent> consumerFactory
    Consumer<String, MeasurementEvent> consumer

    def setup() {
        humidityClient = new UdpClient("localhost", 3345)
        this.consumer = consumerFactory.createConsumer()
    }

    def cleanup() {
        printf "closing..."
        humidityClient?.close()
        consumer?.close()
    }


    def "Should receive UDP humidity messages and process it"() {
        given: 'Two UPD messages'
        def hPayload1 = "sensor_id=h1;value=50"
        def hPayload2 = "sensor_id=h2;value=51"

        when: 'Messages are sent'
        humidityClient.sendEcho(hPayload1)
        humidityClient.sendEcho(hPayload2)

        this.consumer.subscribe(["humidity-measurements-data"])
        def records = KafkaTestUtils.getRecords(this.consumer)

        then: 'Kafka topic humidity-measurements-data has both messages'
        records.iterator()
                *.value()
                *.sensorId()
                .containsAll(["h1", "h2"])
        records.iterator()
                *.value()
                *.value()
                .containsAll([50, 51])

    }

}