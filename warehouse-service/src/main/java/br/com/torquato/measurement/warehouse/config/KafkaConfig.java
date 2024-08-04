package br.com.torquato.measurement.warehouse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerializer;

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

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JavaTimeModule module = new JavaTimeModule();
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Bean
    public DefaultKafkaProducerFactoryCustomizer kafkaProducerFactoryCustomizer(ObjectMapper objectMapper) {
        return producerFactory -> producerFactory.setValueSerializer(new JsonSerializer<>(objectMapper));
    }
}
