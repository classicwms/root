package com.tekclover.wms.api.transaction.config.kafka;

import com.tekclover.wms.api.transaction.model.kafka.InhouseTransferHeaderEvent;
import com.tekclover.wms.api.transaction.model.kafka.InhouseTransferInventoryEvent;
import com.tekclover.wms.api.transaction.model.kafka.TransferInventoryEvent;
import com.tekclover.wms.api.transaction.model.kafka.TransferOrderEvent;
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
    public ConsumerFactory<String, InhouseTransferHeaderEvent> inhouseTransferHeaderEventConsumerFactory() {
        return createConsumerFactory(InhouseTransferHeaderEvent.class, "inhouse-transfer-save-group-v2");
    }

    @Bean
    public ConsumerFactory<String, InhouseTransferInventoryEvent> inhouseTransferInventoryEventConsumerFactory() {
        return createConsumerFactory(InhouseTransferInventoryEvent.class, "inventory-transfer-group-v2");
    }

    @Bean
    public ConsumerFactory<String, TransferInventoryEvent> transferInventoryEventListenerFactory() {
        return createConsumerFactory(TransferInventoryEvent.class, "transfer-inventory-hht-group-v1");
    }

    @Bean
    public ConsumerFactory<String, TransferOrderEvent> inhouseTransferHeaderInHhtEventConsumerFactory() {
        return createConsumerFactory(TransferOrderEvent.class, "transfer-order-hht-group-v1");
    }

    @Bean("inhouseTransferListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, InhouseTransferHeaderEvent> inhouseTransferListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InhouseTransferHeaderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inhouseTransferHeaderEventConsumerFactory());
//        factory.setConcurrency(2);
        return factory;
    }

    @Bean("transferInventoryListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, InhouseTransferInventoryEvent> putAwaySaveListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InhouseTransferInventoryEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inhouseTransferInventoryEventConsumerFactory());
//        factory.setConcurrency(2);
        return factory;
    }
    @Bean("transferInventoryHhtListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, TransferInventoryEvent> transferListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransferInventoryEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transferInventoryEventListenerFactory());
//        factory.setConcurrency(2);
        return factory;
    }


    @Bean("inhouseTransferInHhtListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, TransferOrderEvent> inhouseTransferInHhtListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransferOrderEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(inhouseTransferHeaderInHhtEventConsumerFactory());
//        factory.setConcurrency(2);
        return factory;
    }

}