package br.com.torquato.measurement.monitoring.adapter.jdbc

import br.com.torquato.measurement.monitoring.domain.ProcessedEvent
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

class ProcessedEventRepositoryJdbcTest extends Specification {

    JdbcTemplate mockedJdbcTemplate
    ProcessedEventRepositoryJdbc repositoryJdbc

    def setup() {
        mockedJdbcTemplate = Stub()
        repositoryJdbc = new ProcessedEventRepositoryJdbc(mockedJdbcTemplate)
    }

    def "Should call save with success"() {
        given:
        def processedEvent = new ProcessedEvent("1")
        mockedJdbcTemplate.update(_ as String, processedEvent.eventId()) >> 1

        when:
        repositoryJdbc.save(processedEvent)

        then:
        true
    }

    def "Should call save with error"() {
        given:
        def processedEvent = new ProcessedEvent("1")
        mockedJdbcTemplate.update(_ as String, processedEvent.eventId()) >> 0

        when:
        repositoryJdbc.save(processedEvent)

        then:
        thrown(DuplicateKeyException)
    }

}
