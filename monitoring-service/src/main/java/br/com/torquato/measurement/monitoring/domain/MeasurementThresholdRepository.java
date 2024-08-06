package br.com.torquato.measurement.monitoring.domain;

import br.com.torquato.measurements.schema.Schema;

import java.util.Optional;

public interface MeasurementThresholdRepository {

    Optional<MeasurementThreshold> getByType(final Schema.MeasurementType type);

}
