package br.com.torquato.sensors.warehouse.adapter.mapper;

import br.com.torquato.sensors.schema.MalformedMeasurementEvent;
import br.com.torquato.sensors.schema.MeasurementType;
import br.com.torquato.sensors.warehouse.utils.LocalDateTimeUtils;
import br.com.torquato.sensors.warehouse.config.Configurations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class MalformedMeasurementEventMapper {

    private final Configurations configurations;
    private final Supplier<LocalDateTime> localDateTimeSupplier;
    private final Supplier<UUID> ramdomUuidSupplier;

    public  MalformedMeasurementEvent from(final Message<byte[]> message, final MeasurementType type) {
        final MessageHeaders headers = message.getHeaders();
        UUID uuid = headers.getId();
        if(uuid == null) {
            uuid = this.ramdomUuidSupplier.get();
        }
        final String messageId = uuid.toString();
        final LocalDateTime moment = Optional.ofNullable(headers.getTimestamp())
                .map(LocalDateTimeUtils::toLocalDateTime)
                .orElseGet(this.localDateTimeSupplier);

        return new MalformedMeasurementEvent(
                messageId,
                this.configurations.getWarehouseId(),
                type,
                moment,
                new String(message.getPayload(), StandardCharsets.UTF_8)
        );
    }

}
