package br.com.torquato.measurement.monitoring.adapter.jdbc;

import br.com.torquato.measurement.monitoring.domain.MeasurementThreshold;
import br.com.torquato.measurement.monitoring.domain.MeasurementThresholdRepository;
import br.com.torquato.measurements.schema.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MeasurementThresholdRepositoryJdbc implements MeasurementThresholdRepository {

    private final JdbcTemplate jdbcTemplate;

    @Cacheable("thresholds")
    @Override
    public Optional<MeasurementThreshold> getByType(final Schema.MeasurementType type) {
        log.info("Getting measurement threshold for type {}.", type);
        try {
            final String query = "select * from monitoring.thresholds where type = ?";
            final MeasurementThreshold threshold = this.jdbcTemplate.queryForObject(
                    query,
                    new BeanPropertyRowMapper<>(MeasurementThreshold.class),
                    type.name()
            );
            return threshold == null ? Optional.empty() : Optional.of(threshold);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
