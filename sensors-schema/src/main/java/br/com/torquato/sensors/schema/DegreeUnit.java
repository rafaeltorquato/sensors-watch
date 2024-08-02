package br.com.torquato.sensors.schema;

/**
 * Represents the degree unit in Celsius or Fahrenheit.
 */
public enum DegreeUnit {

    C("Celsius"),
    F("Fahrenheit");

    public final String description;

    DegreeUnit(String description) {
        this.description = description;
    }
}
