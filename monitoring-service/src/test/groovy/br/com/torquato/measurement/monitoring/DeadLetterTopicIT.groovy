package br.com.torquato.measurement.monitoring

import br.com.torquato.measurement.monitoring.support.ITSupport
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils

class DeadLetterTopicIT extends ITSupport {

    @Autowired
    ProducerFactory<String, Object> producerFactory
    @Autowired
    ConsumerFactory<String, Object> consumerFactory
    @Value('${spring.kafka.bootstrap-servers}')
    String bootstrapServers

    Producer<String, Object> producer
    Consumer<String, Object> consumer

    def setup() {
        producer = producerFactory.createProducer()
        consumer = createDltConsumer()
    }

    def cleanup() {
        producer?.close()
        consumer?.close()
    }

    def "Should consume a event from DLT when an bad format event is written to a measurement topic"() {
        given: 'A measurement event'
        def jsonPayload = 'poisoned-pill'

        when: 'A humidity measurement is sent'
        producer.send(new ProducerRecord<>("humidity-measurements-data", jsonPayload)).get()
        consumer.subscribe(["humidity-measurements-data-dlt"])

        def records = KafkaTestUtils.getRecords(consumer)


        then: 'An event on DLT exists'
        def result = records.iterator()
                *.value()
                .findResult()
        result == jsonPayload
    }

    // Needs to create another consumer because the default consumer from Spring
    // is configured to consume from protobuf
    Consumer<String, Object> createDltConsumer() {
        def properties = new Properties()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer.class
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer.class

        properties[ConsumerConfig.GROUP_ID_CONFIG] = "dlt-group"
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        properties[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

        new KafkaConsumer<>(properties)
    }

}
