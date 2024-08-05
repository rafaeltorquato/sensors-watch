package br.com.torquato.measurement.monitoring.support

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@DirtiesContext
@ActiveProfiles("local")
@SpringBootTest
class ITSupport extends Specification {
}
