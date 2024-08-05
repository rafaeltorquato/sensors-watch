package br.com.torquato.measurement.monitoring.port;

import br.com.torquato.measurement.schema.MeasurementAlertEvent;

public interface MeasurementAlertEventRecipient {

    void send(final MeasurementAlertEvent alertEvent);

}
