package br.com.torquato.measurement.monitoring.adapter.jdbc;

import br.com.torquato.measurement.monitoring.domain.DuplicatedEventException;
import br.com.torquato.measurement.monitoring.domain.ProcessedEvent;
import br.com.torquato.measurement.monitoring.domain.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryJdbc implements ProcessedEventRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(ProcessedEvent processedEvent) {
        final int update = this.jdbcTemplate.update(
                "insert into monitoring.processed_events(eventId) values (?)",
                processedEvent.eventId()
        );
        if (update != 1) {
            final String msg = "Event with id %s is duplicated.".formatted(processedEvent.eventId());
            throw new DuplicatedEventException(msg);
        }
    }
}
