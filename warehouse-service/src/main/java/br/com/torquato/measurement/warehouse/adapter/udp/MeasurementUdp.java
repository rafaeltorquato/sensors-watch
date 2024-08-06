package br.com.torquato.measurement.warehouse.adapter.udp;

import br.com.torquato.measurement.warehouse.adapter.mapper.MalformedMeasurementEventMapper;
import br.com.torquato.measurement.warehouse.adapter.mapper.MeasurementEventMapper;
import br.com.torquato.measurement.warehouse.port.MeasurementEventRecipient;
import br.com.torquato.measurements.schema.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@Slf4j
@MessageEndpoint
@RequiredArgsConstructor
public class MeasurementUdp {

    private final MeasurementEventMapper eventMapper;
    private final MalformedMeasurementEventMapper malformedEventMapper;
    private final MeasurementEventRecipient messageRecipient;

    @ServiceActivator(inputChannel = "humidityInboundChannel")
    public void handleHumidityMessage(final Message<byte[]> message) {
        handle(message, Schema.MeasurementType.HUMIDITY);
    }

    @ServiceActivator(inputChannel = "temperatureInboundChannel")
    public void handleTemperatureMessage(final Message<byte[]> message) {
        handle(message, Schema.MeasurementType.TEMPERATURE);
    }

    private void handle(Message<byte[]> message, Schema.MeasurementType type) {
        final Schema.MeasurementEvent event;
        try {
            event = this.eventMapper.from(message, type);
        } catch (IllegalArgumentException e) {
            log.warn("Malformed %s measurement event.".formatted(type));
            sendMalformedEvent(message, type);
            return;
        }
        log.info("{}", event);
        this.messageRecipient.send(event);
    }

    private void sendMalformedEvent(Message<byte[]> message, Schema.MeasurementType type) {
        final Schema.MalformedMeasurementEvent malformedEvent = this.malformedEventMapper.from(
                message,
                type
        );
        this.messageRecipient.send(malformedEvent);
    }

}
