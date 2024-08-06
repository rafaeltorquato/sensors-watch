package br.com.torquato.measurement.warehouse.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic humidityMeasurementTopic() {
        return TopicBuilder.name("humidity-measurements-data").partitions(3)
                .replicas(1).build();
    }

    @Bean
    NewTopic temperatureMeasurementTopic() {
        return TopicBuilder.name("temperature-measurements-data").partitions(3)
                .replicas(1).build();
    }

    @Bean
    NewTopic malformedMeasurementsTopic() {
        return TopicBuilder.name("malformed-measurements-data").partitions(3)
                .replicas(1).build();
    }

}
