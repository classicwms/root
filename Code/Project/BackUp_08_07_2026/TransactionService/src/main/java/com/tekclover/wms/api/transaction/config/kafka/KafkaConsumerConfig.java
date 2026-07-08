package com.tekclover.wms.api.transaction.config.kafka;

import com.tekclover.wms.api.transaction.model.kafka.*;
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
    public ConsumerFactory<String, OutboundHeaderUpdateEvent> outboundHeaderUpdateEventConsumerFactory() {
        return createConsumerFactory(OutboundHeaderUpdateEvent.class, "outbound-header-update-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, DeliveryConfirmEvent> deliveryConfirmEventConsumerFactory() {
        return createConsumerFactory(DeliveryConfirmEvent.class, "delivery-confirm-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, OutboundLineStatusUpdate> outboundLineStatusEventConsumerFactory() {
        return createConsumerFactory(OutboundLineStatusUpdate.class, "outbound-line-update-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, PreOutboundLineStatusUpdateEvent> preOutboundLineStatusEventConsumerFactory() {
        return createConsumerFactory(PreOutboundLineStatusUpdateEvent.class, "preoutbound-line-update-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, PreOutboundHeaderStatusEvent> preOutboundHeaderStatusEventConsumerFactory() {
        return createConsumerFactory(PreOutboundHeaderStatusEvent.class, "preoutbound-header-update-topic-v2");
    }

    @Bean("outboundHeaderListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboundHeaderUpdateEvent> pickupLineListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OutboundHeaderUpdateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(outboundHeaderUpdateEventConsumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean("deliveryConfirmListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryConfirmEvent> deliveryConfirmProcessListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryConfirmEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryConfirmEventConsumerFactory());
        factory.setConcurrency(3);
        return factory;
    }

    @Bean("outboundLineUpdateListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboundLineStatusUpdate> outboundLineStatusUpdateListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OutboundLineStatusUpdate> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(outboundLineStatusEventConsumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean("preOutboundLineUpdateListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PreOutboundLineStatusUpdateEvent> preOutboundLineStatusUpdateListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PreOutboundLineStatusUpdateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(preOutboundLineStatusEventConsumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean("preOutboundHeaderUpdateListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PreOutboundHeaderStatusEvent> preOutboundHeaderStatusUpdateListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PreOutboundHeaderStatusEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(preOutboundHeaderStatusEventConsumerFactory());
        factory.setConcurrency(5);
        return factory;
    }
}