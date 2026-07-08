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


    @Bean
    public ConsumerFactory<String, OutboundLineReverseEvent> outboundLineReversalEventConfumerFactory() {
        return createConsumerFactory(OutboundLineReverseEvent.class, "outbound-line-reversal-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, OutboundLineReverseEvent> qualityLineReversalEventConfumerFactory() {
        return createConsumerFactory(OutboundLineReverseEvent.class, "quality-line-reversal-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, QualityHeaderReverseEvent> qualityHeaderReversalEventConfumerFactory() {
        return createConsumerFactory(QualityHeaderReverseEvent.class, "quality-header-reversal-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, OutboundLineReverseEvent> outboundLineInterimReversalEventConfumerFactory() {
        return createConsumerFactory(OutboundLineReverseEvent.class, "outboundline-interim-reversal-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, OutboundLineReverseEvent> pikcupHeaderReversalEventConfumerFactory() {
        return createConsumerFactory(OutboundLineReverseEvent.class, "pickup-header-reversal-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, OutboundLineReverseEvent> orderManagementLineReversalEventConfumerFactory() {
        return createConsumerFactory(OutboundLineReverseEvent.class, "order-management-line-reversal-topic-v1");
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

    @Bean("outboundLineReversalListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> outboundLineReverseEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(outboundLineReversalEventConfumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean("qualityLineReversalListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> qualityLineReverseEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(qualityLineReversalEventConfumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean("qualityHeaderReversalListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, QualityHeaderReverseEvent> qualityHeaderReverseEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, QualityHeaderReverseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(qualityHeaderReversalEventConfumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean("outboundLineInterimReversalListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> outboundLineInterimReverseEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(outboundLineInterimReversalEventConfumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean("pickupHeaderReversalListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> pikcupHeaderReverseEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(pikcupHeaderReversalEventConfumerFactory());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean("orderManagementLineReversalListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> orderManagementLineReverseEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OutboundLineReverseEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderManagementLineReversalEventConfumerFactory());
        factory.setConcurrency(5);
        return factory;
    }
}