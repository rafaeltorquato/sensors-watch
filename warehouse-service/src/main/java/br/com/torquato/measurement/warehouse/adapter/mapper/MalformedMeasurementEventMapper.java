package br.com.torquato.measurement.warehouse.adapter.mapper;

import br.com.torquato.measurement.warehouse.config.Configurations;
import br.com.torquato.measurements.schema.Schema;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class MalformedMeasurementEventMapper {

    private final Configurations configurations;
    private final Supplier<Long> currentTimestampSupplier;
    private final Supplier<UUID> ramdomUuidSupplier;

    public Schema.MalformedMeasurementEvent from(final Message<byte[]> message, final Schema.MeasurementType type) {
        final MessageHeaders headers = message.getHeaders();
        UUID uuid = headers.getId();
        if (uuid == null) {
            uuid = this.ramdomUuidSupplier.get();
        }
        final String messageId = uuid.toString();
        final Long moment = Optional.ofNullable(headers.getTimestamp())
                .orElseGet(this.currentTimestampSupplier);
        return Schema.MalformedMeasurementEvent.newBuilder()
                .setId(messageId)
                .setWarehouseId(this.configurations.getWarehouseId())
                .setType(type)
                .setMoment(moment)
                .setPayload(ByteString.copyFrom(message.getPayload()))
                .build();
    }

}
