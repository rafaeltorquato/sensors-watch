package br.com.torquato.measurement.monitoring.adapter.kafka;

import br.com.torquato.measurement.monitoring.domain.DuplicatedEventException;
import br.com.torquato.measurement.monitoring.service.ThresholdMonitoringService;
import br.com.torquato.measurements.schema.Schema;
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

    private final ThresholdMonitoringService monitoringService;

    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = {"temperature-measurements-data", "humidity-measurements-data"})
    public void handleMeasurementEvent(final ConsumerRecord<String, Schema.MeasurementEvent> record) {
        try {
            monitoringService.handle(record.value());
        } catch (DuplicatedEventException e) {
            log.info("{}", e.getMessage());
        }
    }

    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = {"temperature-measurements-alert-data", "humidity-measurements-alert-data"})
    public void handleAlertEvent(final ConsumerRecord<String, Schema.MeasurementAlertEvent> record) {
        log.warn("ALERT: {}", record.value());
    }

    @RetryableTopic(
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "malformed-measurements-data")
    public void handleMalformedEvent(final ConsumerRecord<String, Schema.MalformedMeasurementEvent> record) {
        log.warn("MALFORMED: {}", record.value());
    }

}
