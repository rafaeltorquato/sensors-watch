package br.com.torquato.measurement.warehouse.adapter.kafka;

import br.com.torquato.measurement.schema.MalformedMeasurementEvent;
import br.com.torquato.measurement.schema.MeasurementEvent;
import br.com.torquato.measurement.warehouse.port.MeasurementEventRecipient;
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
    public void send(final MeasurementEvent event) {
        final String topic = switch (event.type()) {
            case TEMPERATURE -> "temperature-measurements-data";
            case HUMIDITY -> "humidity-measurements-data";
        };
        // Ensure that events from same warehouse and sensor will be processed like a queue
        final String messageKey = event.warehouseId() + "-" + event.sensorId();
        this.kafkaTemplate.send(topic, messageKey, event);
        log.info("{} sent to topic {}.", event, topic);
    }

    @Override
    public void send(final MalformedMeasurementEvent event) {
        // Ensure that events from same warehouse will be processed like a queue
        final String topic = "malformed-measurements-data";
        this.kafkaTemplate.send(topic, event.warehouseId(), event);
        log.info("{} sent to topic {}.", event, topic);
    }
}
