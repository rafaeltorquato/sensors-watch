package br.com.torquato.measurement.schema;

import java.time.LocalDateTime;

public record MalformedMeasurementEvent(String id,
                                        String warehouseId,
                                        MeasurementType type,
                                        LocalDateTime moment,
                                        String payload) {

    public MalformedMeasurementEvent {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
        if(warehouseId == null || warehouseId.isBlank()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        if(type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        if(moment == null) {
            throw new IllegalArgumentException("Moment cannot be null.");
        }
    }
}
