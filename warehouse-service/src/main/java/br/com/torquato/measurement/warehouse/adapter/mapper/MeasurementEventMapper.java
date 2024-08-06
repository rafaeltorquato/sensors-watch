package br.com.torquato.measurement.warehouse.adapter.mapper;

import br.com.torquato.measurement.warehouse.config.Configurations;
import br.com.torquato.measurements.schema.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeasurementEventMapper {

    private final Configurations configurations;
    private final Supplier<Long> currentTimestampSupplier;
    private final Supplier<UUID> ramdomUuidSupplier;

    public Schema.MeasurementEvent from(final Message<byte[]> message, final Schema.MeasurementType type) {
        final MessageHeaders headers = message.getHeaders();
        UUID uuid = headers.getId();
        if (uuid == null) {
            uuid = this.ramdomUuidSupplier.get();
        }
        final String messageId = uuid.toString();
        final Long moment = Optional.ofNullable(headers.getTimestamp())
                .orElseGet(this.currentTimestampSupplier);

        final Map<String, String> parameters = getParametersMap(message);
        final String sensorId = parameters.get("sensor_id");
        final Integer value = Optional.ofNullable(parameters.get("value"))
                .map(Integer::parseInt)
                .orElse(null);

        try {
            return Schema.MeasurementEvent.newBuilder()
                    .setId(messageId)
                    .setWarehouseId(this.configurations.getWarehouseId())
                    .setSensorId(sensorId)
                    .setValue(value)
                    .setType(type)
                    .setMoment(moment)
                    .build();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
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
