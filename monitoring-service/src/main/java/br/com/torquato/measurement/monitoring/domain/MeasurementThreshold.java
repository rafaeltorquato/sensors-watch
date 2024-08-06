package br.com.torquato.measurement.monitoring.domain;

import br.com.torquato.measurements.schema.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementThreshold {
    private Short id;
    private Schema.MeasurementType type;
    private Integer value;
}
