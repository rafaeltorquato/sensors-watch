package br.com.torquato.measurement.monitoring.domain;

import br.com.torquato.measurement.schema.MeasurementType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurementThreshold {
    private Short id;
    private MeasurementType type;
    private Integer value;
}
