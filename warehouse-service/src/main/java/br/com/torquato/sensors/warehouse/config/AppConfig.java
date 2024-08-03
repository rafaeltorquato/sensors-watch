package br.com.torquato.sensors.warehouse.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class AppConfig {

    private String warehouseId;
    private int temperatureServerPort;
    private int humidityServerPort;

}
