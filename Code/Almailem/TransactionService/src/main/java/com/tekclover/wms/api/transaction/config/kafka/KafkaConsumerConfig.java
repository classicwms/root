package com.tekclover.wms.api.transaction.config.kafka;

import com.tekclover.wms.api.transaction.model.kafka.PickupLineCreateEvent;
import com.tekclover.wms.api.transaction.model.kafka.PickupLineEvent;
import com.tekclover.wms.api.transaction.model.kafka.UpdatePickupHeaderEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {


    private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> clazz, String groupId) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz, false);
        deserializer.addTrustedPackages("*");
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 52428800); // 50 MB
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 52428800); // 50 MB
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConsumerFactory<String, PickupLineEvent> pickupLineConsumerFactory() {
        return createConsumerFactory(PickupLineEvent.class, "pickupline-topic-v1");
    }
    @Bean
    public ConsumerFactory<String, PickupLineCreateEvent> pickupLineCreateConsumerFactory() {
        return createConsumerFactory(PickupLineCreateEvent.class, "pickupline-save-topic-v1");
    }
    @Bean
    public ConsumerFactory<String, UpdatePickupHeaderEvent> updatePickupHeaderEventConsumerFactory() {
        return createConsumerFactory(UpdatePickupHeaderEvent.class, "pickupheader-update-topic-v1");
    }


    @Bean("pickupLineListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PickupLineEvent> pickupLineListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PickupLineEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(pickupLineConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }
    @Bean("pickupLineSaveListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PickupLineCreateEvent> pickupLineCreateListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PickupLineCreateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(pickupLineCreateConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }
    @Bean("updatePickupHeaderListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UpdatePickupHeaderEvent> updatePickupHeaderEventListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UpdatePickupHeaderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(updatePickupHeaderEventConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }
}