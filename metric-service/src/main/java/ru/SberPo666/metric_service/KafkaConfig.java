package ru.SberPo666.metric_service;

import com.fasterxml.jackson.core.type.TypeReference; // Для Jackson TypeReference
import com.fasterxml.jackson.databind.ObjectMapper; // Для создания JsonDeserializer с ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.properties.spring.json.trusted.packages:*}") // Значение по умолчанию '*'
    private String trustedPackages;

    @Bean
    public ConsumerFactory<String, TrackMetricEvent> singleEventConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        JsonDeserializer<TrackMetricEvent> jsonDeserializer = new JsonDeserializer<>(TrackMetricEvent.class, objectMapper, false);
        if (!"*".equals(trustedPackages)) {
            jsonDeserializer.addTrustedPackages(trustedPackages.split(","));
        }


        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TrackMetricEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, TrackMetricEvent> singleEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TrackMetricEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(singleEventConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, List<TrackMetricEvent>> batchEventsConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        JsonDeserializer<List<TrackMetricEvent>> listJsonDeserializer =
                new JsonDeserializer<>(new TypeReference<List<TrackMetricEvent>>() {}, objectMapper, false);

        if (!"*".equals(trustedPackages)) {
            listJsonDeserializer.addTrustedPackages(trustedPackages.split(","));
        }

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(listJsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<TrackMetricEvent>> batchKafkaListenerContainerFactory(
            ConsumerFactory<String, List<TrackMetricEvent>> batchEventsConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, List<TrackMetricEvent>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(batchEventsConsumerFactory);
        factory.setBatchListener(true);
        return factory;
    }
}
