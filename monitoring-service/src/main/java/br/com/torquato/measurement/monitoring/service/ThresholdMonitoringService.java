package br.com.torquato.measurement.monitoring.service;

import br.com.torquato.measurement.monitoring.domain.*;
import br.com.torquato.measurement.monitoring.port.MeasurementAlertEventRecipient;
import br.com.torquato.measurements.schema.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThresholdMonitoringService {

    private final MeasurementThresholdRepository thresholdRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final MeasurementAlertEventRecipient alertEventRecipient;

    public void handle(final Schema.MeasurementEvent event) {
        final MeasurementThreshold threshold = this.thresholdRepository.getByType(event.getType())
                .orElseThrow();
        if (event.getValue() <= threshold.getValue()) {
            ensureIdempotence(event);
            return;
        }

        try {
            final Schema.MeasurementAlertEvent alertEvent = Schema.MeasurementAlertEvent.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setSourceEvent(event)
                    .setMoment(System.currentTimeMillis())
                    .setThreshold(threshold.getValue())
                    .build();
            ensureIdempotence(event);
            this.alertEventRecipient.send(alertEvent);
        } catch (DuplicatedEventException e) {
            throw e;
        } catch (RuntimeException e) {
            deleteProcessed(new UnprocessedEvent(event.getId()));
            throw e;
        }
    }

    private void ensureIdempotence(Schema.MeasurementEvent event) {
        final ProcessedEvent processedEvent = new ProcessedEvent(event.getId());
        try {
            this.processedEventRepository.save(processedEvent);
        } catch (DuplicateKeyException e) {
            final String msg = "Event with id %s is duplicated.".formatted(processedEvent.eventId());
            throw new DuplicatedEventException(msg);
        }
    }

    @EventListener
    public void handleUnprocessed(final UnprocessedEvent event) {
        log.info("Unprocessed event: {}", event);
        deleteProcessed(event);
    }

    private void deleteProcessed(UnprocessedEvent event) {
        final ProcessedEvent processedEvent = new ProcessedEvent(event.eventId());
        this.processedEventRepository.delete(processedEvent);
        log.warn("{} deleted!", processedEvent);
    }
}
