package br.com.torquato.measurement.monitoring.domain;

public class DuplicatedEventException extends RuntimeException {
    public DuplicatedEventException(String message) {
        super(message);
    }
}
