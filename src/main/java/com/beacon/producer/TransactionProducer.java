package com.beacon.producer;

import com.beacon.model.PropertyTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka communication only — nothing else.
 *
 * Key = parcelId so all events for the same property always land
 * in the same partition. This guarantees ordering for Spark in Phase 2
 * (e.g. TITLE_REGISTRATION must be processed before TRANSFER).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${beacon.kafka.topic}")
    private String topic;

    public CompletableFuture<SendResult<String, Object>> send(PropertyTransaction transaction) {

        String key = transaction.getParcelId();

        log.info("Publishing to Kafka | topic={} key={} txnId={} type={}",
                topic, key,
                transaction.getTransactionId(),
                transaction.getTransactionType());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, transaction);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka send failed | txnId={} error={}",
                        transaction.getTransactionId(), ex.getMessage(), ex);
            } else {
                log.info("Kafka send success | txnId={} partition={} offset={}",
                        transaction.getTransactionId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });

        return future;
    }
}