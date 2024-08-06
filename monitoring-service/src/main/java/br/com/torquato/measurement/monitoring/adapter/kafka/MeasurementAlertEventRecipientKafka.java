package br.com.torquato.measurement.monitoring.adapter.kafka;

import br.com.torquato.measurement.monitoring.domain.UnprocessedEvent;
import br.com.torquato.measurement.monitoring.port.MeasurementAlertEventRecipient;
import br.com.torquato.measurements.schema.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeasurementAlertEventRecipientKafka implements MeasurementAlertEventRecipient {

    private final ApplicationEventPublisher eventPublisher;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(final Schema.MeasurementAlertEvent alertEvent) {
        final Schema.MeasurementEvent sourceEvent = alertEvent.getSourceEvent();
        final String topic = switch (sourceEvent.getType()) {
            case TEMPERATURE -> "temperature-measurements-alert-data";
            case HUMIDITY -> "humidity-measurements-alert-data";
        };
        // Ensure that events from same warehouse and sensor will be processed like a queue
        final String messageKey = sourceEvent.getWarehouseId() + "-" + sourceEvent.getSensorId();
        var future = this.kafkaTemplate.send(topic, messageKey, alertEvent);
        future.whenCompleteAsync((stringObjectSendResult, throwable) -> {
            if (throwable != null) {
                log.error("Cannot send measurement alert event.", throwable);
                this.eventPublisher.publishEvent(new UnprocessedEvent(sourceEvent.getId()));
            }
        });
    }
}
