//package com.tekclover.wms.api.outbound.transaction.kafka;
//
//import com.tekclover.wms.api.outbound.transaction.kafka.event.*;
//import com.tekclover.wms.api.outbound.transaction.service.PickupLineService;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableKafka
//public class KafkaConsumerConfig {
//
//    @Autowired
//    PickupLineService putAwayLineService;
//
//
//
//    private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> clazz, String groupId) {
//        JsonDeserializer<T> deserializer = new JsonDeserializer<>(clazz, false);
//        deserializer.addTrustedPackages("*");
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 52428800); // 50 MB
//        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 52428800); // 50 MB
//        return new DefaultKafkaConsumerFactory<>(
//                props,
//                new StringDeserializer(),
//                deserializer
//        );
//    }
//
//    @Bean
//    public ConsumerFactory<String, PickupLineCreatedEvent> pickupLineCreateConsumerFactory() {
//        return createConsumerFactory(PickupLineCreatedEvent.class, "pickupline-group-v1");
//    }
//
//    @Bean
//    public ConsumerFactory<String, PickupLineSaveEvent> pickupLineSaveConsumerFactory() {
//        return createConsumerFactory(PickupLineSaveEvent.class, "pickupline-save-group-v1");
//    }
//
//
//    @Bean
//    public ConsumerFactory<String, UpdateOutboundLineStatusEvent> outboundLineStatusConsumerFactory() {
//        return createConsumerFactory(UpdateOutboundLineStatusEvent.class, "outboundline-status-update-group-v1");
//    }
//    @Bean
//    public ConsumerFactory<String, UpdateOutboundLineEvent> outboundLineConsumerFactory() {
//        return createConsumerFactory(UpdateOutboundLineEvent.class, "outboundline-update-group-v1");
//    }
//
//    @Bean
//    public ConsumerFactory<String, UpdateExpDateEvent> updateExpDateEventConsumerFactory() {
//        return createConsumerFactory(UpdateExpDateEvent.class, "pickupLine-expdate-group-v1");
//    }
//
//    @Bean
//    public ConsumerFactory<String, UpdateStorageBinStatusEvent> updateStorageBinStatusConsumerFactory() {
//        return createConsumerFactory(UpdateStorageBinStatusEvent.class, "storagebinstatus-update-group-v1");
//    }
//
//    @Bean
//    public ConsumerFactory<String, InventorySaveEvent> inventorySaveConsumerFactory() {
//        return createConsumerFactory(InventorySaveEvent.class, "inventory-saves-group-v1");
//    }
//
//    @Bean
//    public ConsumerFactory<String, UpdatePreOutboundHeaderStatus> updatePreOutboundHeaderStatusConsumerFactory() {
//        return createConsumerFactory(UpdatePreOutboundHeaderStatus.class, "preobheader-status-update-group-v1");
//    }
//    @Bean
//    public ConsumerFactory<String, UpdateOutboundHeaderStatus> updateOutboundHeaderStatusConsumerFactory() {
//        return createConsumerFactory(UpdateOutboundHeaderStatus.class, "obheader-status-update-group-v1");
//    }
//
//    @Bean("pickupLineCreateListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, PickupLineCreatedEvent> pickupLineCreateListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, PickupLineCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(pickupLineCreateConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//
//    @Bean("pickupLineSaveListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, PickupLineSaveEvent> pickupLineSaveListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, PickupLineSaveEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(pickupLineSaveConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//
//    @Bean("outboundLineStatusListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, UpdateOutboundLineStatusEvent> outboundLineStatusListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, UpdateOutboundLineStatusEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(outboundLineStatusConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//
//    @Bean("outboundLineListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, UpdateOutboundLineEvent> outboundLineListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, UpdateOutboundLineEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(outboundLineConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//
//    @Bean("expDateListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, UpdateExpDateEvent> expDateListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, UpdateExpDateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(updateExpDateEventConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//
//    @Bean("storageBinStatusListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, UpdateStorageBinStatusEvent> storageBinStatusListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, UpdateStorageBinStatusEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(updateStorageBinStatusConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//
//    @Bean("inventorySaveListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, InventorySaveEvent> inventorySaveListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, InventorySaveEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(inventorySaveConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//
//    @Bean("updatePreObHeaderStatusListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, UpdatePreOutboundHeaderStatus> updatePreObHeaderStatusListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, UpdatePreOutboundHeaderStatus> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(updatePreOutboundHeaderStatusConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//
//    @Bean("updateObHeaderStatusListenerFactory")
//    public ConcurrentKafkaListenerContainerFactory<String, UpdateOutboundHeaderStatus> updateObHeaderStatusListenerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, UpdateOutboundHeaderStatus> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(updateOutboundHeaderStatusConsumerFactory());
//        factory.setConcurrency(10);
//        return factory;
//    }
//}