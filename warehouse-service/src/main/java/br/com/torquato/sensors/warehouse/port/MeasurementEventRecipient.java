package br.com.torquato.sensors.warehouse.port;

import br.com.torquato.sensors.schema.MalformedMeasurementEvent;
import br.com.torquato.sensors.schema.MeasurementEvent;

public interface MeasurementEventRecipient {

    void send(final MeasurementEvent event);

    void send(final MalformedMeasurementEvent event);

}
