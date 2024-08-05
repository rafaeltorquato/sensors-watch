package br.com.torquato.measurement.monitoring.adapter.kafka;

import br.com.torquato.measurement.monitoring.domain.DuplicatedEventException;
import br.com.torquato.measurement.monitoring.service.MeasurementService;
import br.com.torquato.measurement.schema.MalformedMeasurementEvent;
import br.com.torquato.measurement.schema.MeasurementAlertEvent;
import br.com.torquato.measurement.schema.MeasurementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumers {

    private final MeasurementService measurementService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = {"temperature-measurements-data", "humidity-measurements-data"})
    public void handleMeasurementEvent(final ConsumerRecord<String, MeasurementEvent> record) {
        try {
            measurementService.handle(record.value());
        } catch (DuplicatedEventException e) {
            log.info("{}", e.getMessage());
        }
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = {"temperature-measurements-alert-data", "humidity-measurements-alert-data"})
    public void handleAlertEvent(final ConsumerRecord<String, MeasurementAlertEvent> record) {
        log.warn("ALERT: {}", record.value());
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "malformed-measurements-data")
    public void handleMalformedEvent(final ConsumerRecord<String, MalformedMeasurementEvent> record) {
        log.warn("MALFORMED: {}", record.value());
    }

}
