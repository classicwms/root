package com.tekclover.wms.api.inbound.transaction.config.kafka;

import com.tekclover.wms.api.inbound.transaction.kafka.event.*;
import com.tekclover.wms.api.inbound.transaction.service.PutAwayLineService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    PutAwayLineService putAwayLineService;

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
    public ConsumerFactory<String, PutAwayLineProcessEvent> putAwayConsumerFactory() {
        return createConsumerFactory(PutAwayLineProcessEvent.class, "putaway-group-v5");
    }

    @Bean
    public ConsumerFactory<String, PutAwayLineCreatedEvent> putAwaySaveConsumerFactory() {
        return createConsumerFactory(PutAwayLineCreatedEvent.class, "putawayline-save-group-v2");
    }

    @Bean
    public ConsumerFactory<String, PutAwayHeaderUpdateEvent> putAwayUpdateConsumerFactory() {
        return createConsumerFactory(PutAwayHeaderUpdateEvent.class, "putawayheader-update-group-v1");
    }

    @Bean
    public ConsumerFactory<String, InventoryEvent> inventoryConsumerFactory() {
        return createConsumerFactory(InventoryEvent.class, "inventory-save-group-v1");
    }

    @Bean
    public ConsumerFactory<String, StorageBinUpdateEvent> storageBinUpdateConsumerFactory() {
        return createConsumerFactory(StorageBinUpdateEvent.class, "storageBin-update-group-v1");
    }

    @Bean
    public ConsumerFactory<String, InboundLineUpdateEvent> inboundLineUpdateConsumerFactory() {
        return createConsumerFactory(InboundLineUpdateEvent.class, "inboundline-update-group-v1");
    }

    @Bean
    public ConsumerFactory<String, InboundConfirmValidationEvent> updateInboundConfirmValidationConsumerFactory() {
        return createConsumerFactory(InboundConfirmValidationEvent.class, "inboundconfirmvalidation-group-v1");
    }

    @Bean
    public ConsumerFactory<String, IBHeaderReceivedLinesEvent> updateIBReceivedLinesCountConsumerFactory() {
        return createConsumerFactory(IBHeaderReceivedLinesEvent.class, "update-ibreceivedlines-count-group-v1");
    }

    @Bean("putAwayListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PutAwayLineProcessEvent> putAwayListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PutAwayLineProcessEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(putAwayConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("putAwaySaveListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PutAwayLineCreatedEvent> putAwaySaveListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PutAwayLineCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(putAwaySaveConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("putAwayUpdateListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PutAwayHeaderUpdateEvent> putAwayUpdateListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PutAwayHeaderUpdateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(putAwayUpdateConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("inventoryCreatedListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> inventoryListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inventoryConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("storageBinUpdateListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, StorageBinUpdateEvent> storageBinUpdateListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StorageBinUpdateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(storageBinUpdateConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("updateInboundLineListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, InboundLineUpdateEvent> updateInboundLineListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InboundLineUpdateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inboundLineUpdateConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("updateInboundConfirmValidationListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, InboundConfirmValidationEvent> updateInboundConfirmValidationListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InboundConfirmValidationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(updateInboundConfirmValidationConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }

    @Bean("updateIBReceivedLinesCountListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, IBHeaderReceivedLinesEvent> updateIBReceivedLinesCountListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, IBHeaderReceivedLinesEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(updateIBReceivedLinesCountConsumerFactory());
        factory.setConcurrency(10);
        return factory;
    }
}