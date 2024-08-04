package br.com.torquato.measurement.monitoring.adapter.jdbc

import br.com.torquato.measurement.monitoring.domain.MeasurementThreshold
import br.com.torquato.measurement.schema.MeasurementType
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

class MeasurementThresholdRepositoryJdbcTest extends Specification {

    JdbcTemplate mockedJdbcTemplate
    MeasurementThresholdRepositoryJdbc repositoryJdbc

    def setup() {
        mockedJdbcTemplate = Stub()
        repositoryJdbc = new MeasurementThresholdRepositoryJdbc(mockedJdbcTemplate)
    }

    def "Should return a Threshold"() {
        given:
        def threshold = new MeasurementThreshold(Short.valueOf("1"), type as MeasurementType, 30)
        mockedJdbcTemplate.queryForObject(_ as String, _ as BeanPropertyRowMapper, _) >> threshold

        when:
        def optional = repositoryJdbc.getByType(type as MeasurementType)

        then:
        optional.get() == threshold

        where:
        type << MeasurementType.values()
    }

    def "Should return an empty Optional when there is no threshold"() {
        given:
        mockedJdbcTemplate.queryForObject(_ as String, _ as BeanPropertyRowMapper, _) >> null

        when:
        def optional = repositoryJdbc.getByType(type as MeasurementType)

        then:
        optional.isEmpty()

        where:
        type << MeasurementType.values()
    }

    def "Should return an empty Optional when EmptyResultDataAccessException occurs"() {
        given:
        mockedJdbcTemplate.queryForObject(_ as String, _ as BeanPropertyRowMapper, _) >> {
            throw new EmptyResultDataAccessException("Mocked Exception", 1)
        }

        when:
        def optional = repositoryJdbc.getByType(type as MeasurementType)

        then:
        optional.isEmpty()

        where:
        type << MeasurementType.values()
    }

}
