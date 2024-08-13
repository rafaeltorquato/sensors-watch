package br.com.torquato.measurement.monitoring.config;

import br.com.torquato.measurements.schema.Schema;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic humidityMeasurementTopic() {
        return TopicBuilder.name("humidity-measurements-alert-data").partitions(3)
                .replicas(1).build();
    }

    @Bean
    NewTopic temperatureMeasurementTopic() {
        return TopicBuilder.name("temperature-measurements-alert-data").partitions(3)
                .replicas(1).build();
    }

    @Bean
    DefaultKafkaProducerFactoryCustomizer defaultKafkaProducerFactoryCustomizer() {
        return producerFactory -> {
            final KafkaProtobufSerializer<Message> kafkaProtobufSerializer = new KafkaProtobufSerializer<>();
            final Serializer delegatingByTypeSerializer = new DelegatingByTypeSerializer(Map.of(
                    byte[].class, new ByteArraySerializer(),
                    GeneratedMessageV3.class, kafkaProtobufSerializer,
                    Schema.MeasurementEvent.class, kafkaProtobufSerializer,
                    Schema.MeasurementAlertEvent.class, kafkaProtobufSerializer)
            );
            producerFactory.setValueSerializer(delegatingByTypeSerializer);
        };
    }

}
