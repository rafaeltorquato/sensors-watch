package br.com.torquato.measurement.warehouse.port;

import br.com.torquato.measurements.schema.Schema;

public interface MeasurementEventRecipient {

    void send(final Schema.MeasurementEvent event);

    void send(final Schema.MalformedMeasurementEvent event);

}
