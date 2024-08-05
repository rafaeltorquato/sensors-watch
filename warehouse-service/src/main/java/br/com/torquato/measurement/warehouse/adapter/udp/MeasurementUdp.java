package br.com.torquato.measurement.warehouse.adapter.udp;

import br.com.torquato.measurement.schema.MalformedMeasurementEvent;
import br.com.torquato.measurement.schema.MeasurementEvent;
import br.com.torquato.measurement.schema.MeasurementType;
import br.com.torquato.measurement.warehouse.adapter.mapper.MalformedMeasurementEventMapper;
import br.com.torquato.measurement.warehouse.adapter.mapper.MeasurementEventMapper;
import br.com.torquato.measurement.warehouse.port.MeasurementEventRecipient;
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
        handle(message, MeasurementType.HUMIDITY);
    }

    @ServiceActivator(inputChannel = "temperatureInboundChannel")
    public void handleTemperatureMessage(final Message<byte[]> message) {
        handle(message, MeasurementType.TEMPERATURE);
    }

    private void handle(Message<byte[]> message, MeasurementType type) {
        final MeasurementEvent event;
        try {
            event = this.eventMapper.from(message, type);
        } catch (IllegalArgumentException e) {
            log.error("Malformed %s measurement event.".formatted(type), e);
            sendMalformedEvent(message, type);
            return;
        }
        log.info("{}", event);
        this.messageRecipient.send(event);
    }

    private void sendMalformedEvent(Message<byte[]> message, MeasurementType type) {
        final MalformedMeasurementEvent malformedEvent = this.malformedEventMapper.from(
                message,
                type
        );
        this.messageRecipient.send(malformedEvent);
    }

}
