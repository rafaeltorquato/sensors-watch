package br.com.torquato.measurement.warehouse.port;

import br.com.torquato.measurement.schema.MalformedMeasurementEvent;
import br.com.torquato.measurement.schema.MeasurementEvent;

public interface MeasurementEventRecipient {

    void send(final MeasurementEvent event);

    void send(final MalformedMeasurementEvent event);

}
