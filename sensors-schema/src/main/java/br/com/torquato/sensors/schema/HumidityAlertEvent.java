package br.com.torquato.sensors.schema;

import java.time.LocalDateTime;

/**
 * Represents an alert that humidity overcome the configured threshold.
 * @param id The id of the event.
 * @param sourceEvent The event that overcome the configured threshold.
 * @param moment The moment of notification.
 * @param threshold The configured threshold.
 */
public record HumidityAlertEvent(String id,
                                 HumidityEvent sourceEvent,
                                 LocalDateTime moment,
                                 float threshold) {
    public HumidityAlertEvent {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }
        if (sourceEvent == null) {
            throw new IllegalArgumentException("Source Event cannot be null.");
        }
        if (moment == null) {
            throw new IllegalArgumentException("Moment cannot be null.");
        }
    }
}
