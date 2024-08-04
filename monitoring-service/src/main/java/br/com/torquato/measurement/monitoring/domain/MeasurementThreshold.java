package br.com.torquato.measurement.monitoring.domain;

import br.com.torquato.measurement.schema.MeasurementType;
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
    private MeasurementType type;
    private Integer value;
}
