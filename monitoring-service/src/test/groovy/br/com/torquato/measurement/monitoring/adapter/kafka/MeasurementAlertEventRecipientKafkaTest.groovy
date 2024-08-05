package br.com.torquato.measurement.monitoring.adapter.kafka


import br.com.torquato.measurement.schema.MeasurementAlertEvent
import br.com.torquato.measurement.schema.MeasurementEvent
import br.com.torquato.measurement.schema.MeasurementType
import org.springframework.context.ApplicationEventPublisher
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import spock.lang.Specification

import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import java.util.function.BiConsumer

class MeasurementAlertEventRecipientKafkaTest extends Specification {

    KafkaTemplate<String, Object> mockedKafkaTemplate
    ApplicationEventPublisher mockedEventPublisher
    MeasurementAlertEventRecipientKafka recipient


    def setup() {
        mockedKafkaTemplate = Mock()
        mockedEventPublisher = Mock()
        recipient = new MeasurementAlertEventRecipientKafka(mockedEventPublisher, mockedKafkaTemplate)
    }

    def "Should send MeasurementEvent"() {
        given:
        def measurementEvent = new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                type as MeasurementType,
                LocalDateTime.now()
        )
        def alertEvent = new MeasurementAlertEvent(
                "1",
                measurementEvent,
                LocalDateTime.now(),
                30
        )

        def future = Stub(CompletableFuture<SendResult<String, Object>>)
        AtomicReference<BiConsumer<?,?>> biConsumerRef = new AtomicReference<>()
        future.whenCompleteAsync(_) >> { List args -> biConsumerRef.set(args[0] as BiConsumer<?,?>)}

        mockedKafkaTemplate.send(topic, _, alertEvent) >> future

        when:
        recipient.send(alertEvent)
        biConsumerRef.get().accept(Stub(SendResult), null)

        then:
        0 * mockedEventPublisher.publishEvent(_)

        where:
        type                        | topic
        MeasurementType.HUMIDITY    | "humidity-measurements-alert-data"
        MeasurementType.TEMPERATURE | "temperature-measurements-alert-data"
    }

    def "Should send UnprocessedEvent when cannot sent to Kafka"() {
        given:
        def measurementEvent = new MeasurementEvent(
                "1",
                "w01",
                "s01",
                30,
                type as MeasurementType,
                LocalDateTime.now()
        )
        def alertEvent = new MeasurementAlertEvent(
                "1",
                measurementEvent,
                LocalDateTime.now(),
                30
        )
        def future = Stub(CompletableFuture<SendResult<String, Object>>)
        AtomicReference<BiConsumer<?,?>> biConsumerRef = new AtomicReference<>()
        future.whenCompleteAsync(_) >> { List args -> biConsumerRef.set(args[0] as BiConsumer<?,?>)}

        mockedKafkaTemplate.send(topic, _, alertEvent) >> future

        when:
        recipient.send(alertEvent)
        biConsumerRef.get().accept(null, new RuntimeException())

        then:
        1 * mockedEventPublisher.publishEvent(_)

        where:
        type                        | topic
        MeasurementType.HUMIDITY    | "humidity-measurements-alert-data"
        MeasurementType.TEMPERATURE | "temperature-measurements-alert-data"
    }

}
