package br.com.torquato.sensors.schema;

import java.time.LocalDateTime;

/**
 * Represents a humidity change.
 * @param id The id of the event.
 * @param warehouseId The warehouse id which it events was fired.
 * @param sensorId The id of the sensor that gathered the change.
 * @param humidity The humidity in percentage.
 * @param moment The moment that change happen.
 */
public record HumidityEvent(String id,
                            String warehouseId,
                            String sensorId,
                            float humidity,
                            LocalDateTime moment) {

    public HumidityEvent {
        if(id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
        if(warehouseId == null || warehouseId.isBlank()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty.");
        }
        if(sensorId == null || sensorId.isBlank()) {
            throw new IllegalArgumentException("Sensor ID cannot be null or empty.");
        }
        if(moment == null) {
            throw new IllegalArgumentException("Moment cannot be null.");
        }
    }
}
