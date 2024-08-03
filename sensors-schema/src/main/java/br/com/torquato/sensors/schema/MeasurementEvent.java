package br.com.torquato.sensors.schema;

import java.time.LocalDateTime;


/**
 * Represents a measurement event.
 * @param id The id of the event.
 * @param warehouseId The warehouse id which it events was fired.
 * @param sensorId The id of the sensor that gathered the change.
 * @param value The value measured.
 * @param type The type of measurement.
 * @param moment The moment that change happen.
 */
public record MeasurementEvent(String id,
                               String warehouseId,
                               String sensorId,
                               Integer value,
                               MeasurementType type,
                               LocalDateTime moment) {

    public MeasurementEvent {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
        if(warehouseId == null || warehouseId.isBlank()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        if(sensorId == null || sensorId.isBlank()) {
            throw new IllegalArgumentException("Sensor ID cannot be null or empty.");
        }
        if(value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        if(type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        if(moment == null) {
            throw new IllegalArgumentException("Moment cannot be null.");
        }

    }
}
