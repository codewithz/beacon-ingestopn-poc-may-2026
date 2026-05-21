//package com.beacon.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
///**
// * Declares the Kafka topic so Spring auto-creates it on startup if missing.
// * KafkaTemplate itself is fully auto-configured from application.properties —
// * no manual ProducerFactory wiring needed.
// */
//@Configuration
//public class KafkaConfig {
//
//    @Value("${beacon.property.transactions}")
//    private String topicName;
//
//    @Bean
//    public NewTopic propertyTransactionsTopic() {
//        return TopicBuilder.name(topicName)
//                .partitions(3)   // 3 partitions → 3 parallel Spark consumers in Phase 2
//                .replicas(3)     // single broker for POC; set to 3 in prod
//                .build();
//    }
//}