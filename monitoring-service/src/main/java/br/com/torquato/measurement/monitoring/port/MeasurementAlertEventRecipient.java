package br.com.torquato.measurement.monitoring.port;

import br.com.torquato.measurements.schema.Schema;

public interface MeasurementAlertEventRecipient {

    void send(final Schema.MeasurementAlertEvent alertEvent);

}
