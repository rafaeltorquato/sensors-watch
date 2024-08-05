package br.com.torquato.measurement.monitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.VirtualThreadTaskExecutor;

@Configuration
public class ApplicationEventConfig {

    //Asynchronous application events
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        final SimpleApplicationEventMulticaster eventMulticaster =
                new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new VirtualThreadTaskExecutor("apvt"));
        return eventMulticaster;
    }

}
