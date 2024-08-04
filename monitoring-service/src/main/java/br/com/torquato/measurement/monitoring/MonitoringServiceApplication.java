package br.com.torquato.measurement.monitoring;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@EnableCaching
@EnableKafka
@SpringBootApplication
public class MonitoringServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MonitoringServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
