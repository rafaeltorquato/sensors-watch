package br.com.torquato.measurement.monitoring.domain;

public interface ProcessedEventRepository {

    void save(final ProcessedEvent processedEvent);

    void delete(ProcessedEvent processedEvent);

}
