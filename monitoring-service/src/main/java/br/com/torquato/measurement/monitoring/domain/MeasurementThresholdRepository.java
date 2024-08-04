package br.com.torquato.measurement.monitoring.domain;

import br.com.torquato.measurement.schema.MeasurementType;

import java.util.Optional;

public interface MeasurementThresholdRepository {

    Optional<MeasurementThreshold> getByType(final MeasurementType type);

}
