package com.tekclover.wms.api.transaction.config.kafka;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

//    @Autowired
//    PropertiesConfig propertiesConfig;
//
//    private String getBootstrapServers() {
//        return propertiesConfig.getBootstrapServers();
//    }
//
//    @Bean
//    public ProducerFactory<String, Object> producerFactory() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
//        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 52428800);
//        return new DefaultKafkaProducerFactory<>(config);
//    }
//
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }


        private static final int MAX_MESSAGE_BYTES = 52428800; // 50 MB

        @Autowired
        PropertiesConfig propertiesConfig;

        public KafkaProducerConfig(PropertiesConfig propertiesConfig) {
            this.propertiesConfig = propertiesConfig;
        }

        /** Settings shared by both producers. */
        private Map<String, Object> baseProducerProps() {
            Map<String, Object> config = new HashMap<>();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, propertiesConfig.getBootstrapServers());
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, MAX_MESSAGE_BYTES);

            // Reliability: wait for all replicas, no duplicates on producer retry
            config.put(ProducerConfig.ACKS_CONFIG, "all");
            config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

            // Large JSON (GR documents) compresses very well: less network and disk
            config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

            // Small wait to group messages into fewer requests
            config.put(ProducerConfig.LINGER_MS_CONFIG, 5);
            return config;
        }

        // ---------- Main producer for JSON events ----------

        @Bean
        public ProducerFactory<String, Object> producerFactory() {
            Map<String, Object> config = baseProducerProps();
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
            return new DefaultKafkaProducerFactory<>(config);
        }

        @Bean
        public KafkaTemplate<String, Object> kafkaTemplate() {
            return new KafkaTemplate<>(producerFactory());
        }

        // ---------- Raw bytes producer, used only for the dead-letter topic ----------
        // Messages that failed JSON deserialization are forwarded to the DLT as
        // original bytes.
        @Bean
        public ProducerFactory<String, byte[]> bytesProducerFactory() {
            Map<String, Object> config = new HashMap<>();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, propertiesConfig.getBootstrapServers());
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
            config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 52428800);
            return new DefaultKafkaProducerFactory<>(config);
        }

        @Bean
        public KafkaTemplate<String, byte[]> bytesKafkaTemplate() {
            return new KafkaTemplate<>(bytesProducerFactory());
        }

}