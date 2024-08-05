package br.com.torquato.measurement.warehouse.support

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("local")
@SpringBootTest
class ITSupport extends Specification {
}
