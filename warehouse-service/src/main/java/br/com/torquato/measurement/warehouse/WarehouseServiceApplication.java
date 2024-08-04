package br.com.torquato.measurement.warehouse;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableIntegration
@EnableConfigurationProperties
@SpringBootApplication
public class WarehouseServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(WarehouseServiceApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
