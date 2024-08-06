package br.com.torquato.measurement.monitoring.adapter.jdbc;

import br.com.torquato.measurement.monitoring.domain.ProcessedEvent;
import br.com.torquato.measurement.monitoring.domain.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryJdbc implements ProcessedEventRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(ProcessedEvent processedEvent) {
        final int update = this.jdbcTemplate.update(
                "insert into monitoring.processed_events(eventid) values (?)",
                processedEvent.eventId()
        );
        if (update != 1) {
            throw new DuplicateKeyException("No rows inserted!");
        }
    }

    @Override
    public void delete(ProcessedEvent processedEvent) {
        final int update = this.jdbcTemplate.update(
                "delete from monitoring.processed_events where eventid = ?",
                processedEvent.eventId()
        );
        if (update != 1) {
            log.error("There is no rows to deleted! {}.", processedEvent);
        }
    }
}
