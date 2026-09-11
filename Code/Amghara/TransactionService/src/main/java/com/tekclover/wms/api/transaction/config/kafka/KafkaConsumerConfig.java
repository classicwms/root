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
    public ConsumerFactory<String, PickupLineEvent> pickupLineConsumerFactory() {
        return createConsumerFactory(PickupLineEvent.class, "pickupline-group-v1");
    }
    @Bean
    public ConsumerFactory<String, PickupLineCreateEvent> pickupLineCreateConsumerFactory() {
        return createConsumerFactory(PickupLineCreateEvent.class, "pickupline-save-topic-v1");
    }
    @Bean
    public ConsumerFactory<String, UpdatePickupHeaderEvent> updatePickupHeaderEventConsumerFactory() {
        return createConsumerFactory(UpdatePickupHeaderEvent.class, "pickupheader-update-topic-v1");
    }
    @Bean
    public ConsumerFactory<String, QualityLineSaveEvent> qualityLineCreateEventConsumerFactory() {
        return createConsumerFactory(QualityLineSaveEvent.class, "qualityline-save-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, QualityHeaderUpdateEvent> qualityHeaderUpdateEventConsumerFactory() {
        return createConsumerFactory(QualityHeaderUpdateEvent.class, "qualityheader-update-topic-v1");
    }
    @Bean
    public ConsumerFactory<String, DeliveryConfirmEvent> deliveryConfirmEventConsumerFactory() {
        return createConsumerFactory(DeliveryConfirmEvent.class, "delivery-confirm-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, QualityLineCreateEvent> qualityLineProcessConsumerFactory() {
        return createConsumerFactory(QualityLineCreateEvent.class, "qualityline-create-topic-v1");
    }

    @Bean
    public ConsumerFactory<String, PickupHeaderEvent> pikcupHeaderProcessConsumerFactory() {
        return createConsumerFactory(PickupHeaderEvent.class, "save-pickupheader-topic-v1");
    }


    @Bean
    public ConsumerFactory<String, AssignPickerEvent> assignPickerEventConsumerFactory() {
        return createConsumerFactory(AssignPickerEvent.class, "assign-picker-topic-v1");
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

    @Bean("qualitylineListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, QualityLineSaveEvent> qualityLineListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, QualityLineSaveEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(qualityLineCreateEventConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }
    @Bean("qualityHeaderUpdateListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, QualityHeaderUpdateEvent> qualityHeaderListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, QualityHeaderUpdateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(qualityHeaderUpdateEventConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("deliveryConfirmInterimListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryConfirmEvent> deliveryConfirmEventConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryConfirmEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryConfirmEventConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    // New PreObHeader Update
    @Bean
    public ConsumerFactory<String, UpdatePreOutboundHeaderStatus> updatePreOutboundHeaderStatusConsumerFactory() {
        return createConsumerFactory(UpdatePreOutboundHeaderStatus.class, "preobheader-status-update-group-v1");
    }

    @Bean("updatePreObHeaderStatusListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UpdatePreOutboundHeaderStatus> updatePreObHeaderStatusListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UpdatePreOutboundHeaderStatus> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(updatePreOutboundHeaderStatusConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    // New OutboundHeader Update
    @Bean
    public ConsumerFactory<String, UpdateOutboundHeaderStatus> updateOutboundHeaderStatusConsumerFactory() {
        return createConsumerFactory(UpdateOutboundHeaderStatus.class, "obheader-status-update-group-v1");
    }


    @Bean("updateObHeaderStatusListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UpdateOutboundHeaderStatus> updateObHeaderStatusListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UpdateOutboundHeaderStatus> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(updateOutboundHeaderStatusConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }


    @Bean("qualityLineProcessListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, QualityLineCreateEvent> qualityLineProcessListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, QualityLineCreateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(qualityLineProcessConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("assignPickerListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AssignPickerEvent> assignProcessListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AssignPickerEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(assignPickerEventConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("pickupHeaderSaveListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PickupHeaderEvent> savePickupHeaderListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PickupHeaderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(pikcupHeaderProcessConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }
}