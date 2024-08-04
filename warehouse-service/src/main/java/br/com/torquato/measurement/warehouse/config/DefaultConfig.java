package br.com.torquato.measurement.warehouse.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Configuration
public class DefaultConfig {

    @Bean
    public Supplier<LocalDateTime> currentLocalDateTime() {
        return () -> {
            log.warn("Using a generated timestamp. Is recommended that client send the timestamp.");
            return LocalDateTime.now();
        };
    }

    @Bean
    public Supplier<UUID> randomUuid() {
        return () -> {
            log.warn("Using a generated UUID id. Is recommended that client send the id.");
            return UUID.randomUUID();
        };
    }

}
