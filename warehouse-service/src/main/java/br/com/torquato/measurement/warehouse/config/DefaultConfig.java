package br.com.torquato.measurement.warehouse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class DefaultConfig {

    @Bean
    public Supplier<LocalDateTime> currentLocalDateTime() {
        log.warn("Using a generated timestamp. Is recommended that client send the timestamp.");
        return LocalDateTime::now;
    }

    @Bean
    public Supplier<UUID> randomUuid() {
        log.warn("Using a generated UUID id. Is recommended that client send the id.");
        return UUID::randomUUID;
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
