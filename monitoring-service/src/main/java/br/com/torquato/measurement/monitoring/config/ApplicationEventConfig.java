package br.com.torquato.measurement.monitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class ApplicationEventConfig {

    //Asynchronous application events
    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        final SimpleApplicationEventMulticaster eventMulticaster =
                new SimpleApplicationEventMulticaster();

        final SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setVirtualThreads(true);
        eventMulticaster.setTaskExecutor(taskExecutor);
        return eventMulticaster;
    }

}
