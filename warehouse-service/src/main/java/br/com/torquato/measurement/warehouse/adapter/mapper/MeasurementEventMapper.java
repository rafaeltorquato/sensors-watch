package br.com.torquato.measurement.warehouse.adapter.mapper;

import br.com.torquato.measurement.schema.MeasurementEvent;
import br.com.torquato.measurement.schema.MeasurementType;
import br.com.torquato.measurement.warehouse.config.Configurations;
import br.com.torquato.measurement.warehouse.utils.LocalDateTimeUtils;
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
public class MeasurementEventMapper {

    private final Configurations configurations;
    private final Supplier<LocalDateTime> localDateTimeSupplier;
    private final Supplier<UUID> ramdomUuidSupplier;

    public MeasurementEvent from(final Message<byte[]> message, final MeasurementType type) {
        final MessageHeaders headers = message.getHeaders();
        UUID uuid = headers.getId();
        if (uuid == null) {
            uuid = this.ramdomUuidSupplier.get();
        }
        final String messageId = uuid.toString();
        final LocalDateTime moment = Optional.ofNullable(headers.getTimestamp())
                .map(LocalDateTimeUtils::toLocalDateTime)
                .orElseGet(this.localDateTimeSupplier);

        final Map<String, String> parameters = getParametersMap(message);
        final String sensorId = parameters.get("sensor_id");
        final Integer value = Optional.ofNullable(parameters.get("value"))
                .map(Integer::parseInt)
                .orElse(null);

        return new MeasurementEvent(
                messageId,
                this.configurations.getWarehouseId(),
                sensorId,
                value,
                type,
                moment
        );
    }

    private Map<String, String> getParametersMap(final Message<byte[]> message) {
        final String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        final String[] parts = payload.split(";");
        if (parts.length != 2) {
            return Collections.emptyMap();
        }
        final Map<String, String> parameters = new HashMap<>(2);
        for (String part : parts) {
            final String[] subParts = part.split("=");
            if (subParts.length != 2) {
                continue;
            }
            parameters.put(subParts[0].trim(), subParts[1].trim());
        }
        return parameters;
    }

}
