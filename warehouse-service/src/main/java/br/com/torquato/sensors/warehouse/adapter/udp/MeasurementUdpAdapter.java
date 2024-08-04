package br.com.torquato.sensors.warehouse.adapter.udp;

import br.com.torquato.sensors.schema.MalformedMeasurementEvent;
import br.com.torquato.sensors.schema.MeasurementEvent;
import br.com.torquato.sensors.schema.MeasurementType;
import br.com.torquato.sensors.warehouse.adapter.mapper.MalformedMeasurementEventMapper;
import br.com.torquato.sensors.warehouse.adapter.mapper.MeasurementEventMapper;
import br.com.torquato.sensors.warehouse.port.MeasurementEventRecipient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@Slf4j
@MessageEndpoint
@RequiredArgsConstructor
public class MeasurementUdpAdapter {

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
        } catch (Exception e) {
            log.error("Malformed %s measurement event.".formatted(type) , e);
            final MalformedMeasurementEvent malformedEvent = this.malformedEventMapper.from(
                    message,
                    type
            );
            this.messageRecipient.send(malformedEvent);
            return;
        }
        log.info("{}", event);
        this.messageRecipient.send(event);
    }

}
