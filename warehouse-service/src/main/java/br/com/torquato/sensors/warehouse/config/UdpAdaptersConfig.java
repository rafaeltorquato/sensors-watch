package br.com.torquato.sensors.warehouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class UdpAdaptersConfig {

    @Bean
    public MessageChannel inboundChannel() {
        return new DirectChannel();
    }

    @Bean(name = "udpReceivingTemperatureAdapter")
    public UnicastReceivingChannelAdapter udpReceivingTemperatureAdapter(
            @Value("${app.temperature.server-port}") final int port
    ) {
        UnicastReceivingChannelAdapter adapter = new UnicastReceivingChannelAdapter(port);
        adapter.setTaskExecutor(executor());
        adapter.setOutputChannel(inboundChannel());
        adapter.setOutputChannelName("temperatureInboundChannel");
        return adapter;
    }

    @Bean(name = "udpReceivingHumidityAdapter")
    public UnicastReceivingChannelAdapter udpReceivingHumidityAdapter(
            @Value("${app.humidity.server-port}") final int port
    ) {
        UnicastReceivingChannelAdapter adapter = new UnicastReceivingChannelAdapter(port);
        adapter.setTaskExecutor(executor());
        adapter.setOutputChannel(inboundChannel());
        adapter.setOutputChannelName("humidityInboundChannel");
        return adapter;
    }

    @Bean
    public TaskExecutor executor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        return executor;
    }

}
