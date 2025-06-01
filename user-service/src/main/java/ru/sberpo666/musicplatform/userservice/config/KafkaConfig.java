package ru.sberpo666.musicplatform.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfig {

    public static final String USER_CREATED_TOPIC = "ru.sberpo666.user.created";
    public static final String USER_UPDATED_TOPIC = "ru.sberpo666.user.updated";
    public static final String USER_DELETED_TOPIC = "ru.sberpo666.user.deleted";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic userCreatedTopic() {
        return new NewTopic(USER_CREATED_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic userUpdatedTopic() {
        return new NewTopic(USER_UPDATED_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic userDeletedTopic() {
        return new NewTopic(USER_DELETED_TOPIC, 1, (short) 1);
    }

    @Bean
    public ProducerFactory<UUID, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<UUID, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}