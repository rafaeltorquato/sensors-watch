package br.com.torquato.measurement.monitoring.service;

import br.com.torquato.measurement.monitoring.domain.*;
import br.com.torquato.measurement.monitoring.port.MeasurementAlertEventRecipient;
import br.com.torquato.measurement.schema.MeasurementAlertEvent;
import br.com.torquato.measurement.schema.MeasurementEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final MeasurementThresholdRepository thresholdRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final MeasurementAlertEventRecipient alertEventRecipient;

    @Transactional(timeout = 10)
    public void handle(final MeasurementEvent event) {
        final MeasurementThreshold threshold = this.thresholdRepository.getByType(event.type())
                .orElseThrow();
        try {
            ensureIdempotence(event);
        } catch (DuplicatedEventException e) {
            log.info("{}", e.getMessage());
            return;
        }
        if (event.value() > threshold.getValue()) {
            final MeasurementAlertEvent alertEvent = new MeasurementAlertEvent(
                    UUID.randomUUID().toString(),
                    event,
                    LocalDateTime.now(),
                    threshold.getValue()
            );
            this.alertEventRecipient.send(alertEvent);
        }
    }

    private void ensureIdempotence(MeasurementEvent event) {
        this.processedEventRepository.save(new ProcessedEvent(event.id()));
    }
}
