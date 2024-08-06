package br.com.torquato.measurement.warehouse.adapter.kafka;

import br.com.torquato.measurement.warehouse.port.MeasurementEventRecipient;
import br.com.torquato.measurements.schema.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeasurementEventRecipientKafka implements MeasurementEventRecipient {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(final Schema.MeasurementEvent event) {
        final String topic = switch (event.getType()) {
            case TEMPERATURE -> "temperature-measurements-data";
            case HUMIDITY -> "humidity-measurements-data";
        };
        // Ensure that events from same warehouse and sensor will be processed like a queue
        final String messageKey = event.getWarehouseId() + "-" + event.getSensorId();
        this.kafkaTemplate.send(topic, messageKey, event)
                .whenCompleteAsync((stringObjectSendResult, throwable) -> {
                    if (throwable != null) {
                        //TODO A fallback here
                        log.error("Error sending %s to topic %s".formatted(event, topic), throwable);
                    }
                });
        log.info("{} sent to topic {}.", event, topic);
    }

    @Override
    public void send(final Schema.MalformedMeasurementEvent event) {
        // Ensure that events from same warehouse will be processed like a queue
        final String messageKey = event.getWarehouseId();
        final String topic = "malformed-measurements-data";
        this.kafkaTemplate.send(topic, messageKey, event)
                .whenCompleteAsync((stringObjectSendResult, throwable) -> {
                    if (throwable != null) {
                        //TODO A fallback here
                        log.error("Error sending %s to topic %s".formatted(event, topic), throwable);
                    }
                });
        log.info("{} sent to topic {}.", event, topic);
    }
}
