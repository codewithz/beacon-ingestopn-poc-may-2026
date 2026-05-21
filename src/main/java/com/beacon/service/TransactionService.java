package com.beacon.service;

import com.beacon.dto.TransactionRequest;
import com.beacon.dto.TransactionResponse;
import com.beacon.model.PropertyTransaction;
import com.beacon.producer.TransactionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Business logic layer — the only place that knows about:
 *   - Mapping request DTO → domain model
 *   - Applying defaults and enrichment rules
 *   - Coordinating with the producer
 *   - Building the response DTO
 *
 * No HTTP types. No Kafka types. Pure business logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionProducer producer;

    @Value("${beacon.kafka.topic}")
    private String topic;

    // ── Single ────────────────────────────────────────────────────────────────

    public TransactionResponse process(TransactionRequest request) {
        log.info("Processing transaction | parcelId={} type={}",
                request.getParcelId(), request.getTransactionType());

        PropertyTransaction transaction = mapAndEnrich(request);

        try {
            producer.send(transaction).get();   // block until Kafka acks

            return TransactionResponse.builder()
                    .status("ACCEPTED")
                    .transactionId(transaction.getTransactionId())
                    .parcelId(transaction.getParcelId())
                    .topic(topic)
                    .count(1)
                    .build();

        } catch (Exception ex) {
            log.error("Failed to process transaction | parcelId={} error={}",
                    request.getParcelId(), ex.getMessage(), ex);

            return TransactionResponse.builder()
                    .status("FAILED")
                    .parcelId(request.getParcelId())
                    .error(ex.getMessage())
                    .build();
        }
    }

    // ── Batch ─────────────────────────────────────────────────────────────────

//    public TransactionResponse processBatch(List<TransactionRequest> requests) {
//        log.info("Processing batch | count={}", requests.size());
//
//        List<PropertyTransaction> transactions = requests.stream()
//                .map(this::mapAndEnrich)
//                .toList();
//
//        List<CompletableFuture<?>> futures = transactions.stream()
//                .map(producer::send)
//                .toList();
//
//        try {
//            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
//
//            return TransactionResponse.builder()
//                    .status("ACCEPTED")
//                    .topic(topic)
//                    .count(transactions.size())
//                    .build();
//
//        } catch (Exception ex) {
//            log.error("Batch processing failed | error={}", ex.getMessage(), ex);
//
//            return TransactionResponse.builder()
//                    .status("FAILED")
//                    .error(ex.getMessage())
//                    .build();
//        }
//    }
//
//    // ── Sample ────────────────────────────────────────────────────────────────
//
//    public TransactionRequest buildSample() {
//        TransactionRequest sample = new TransactionRequest();
//        sample.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
//        sample.setParcelId("PCL-MM-2024-00142");
//        sample.setTransactionType(PropertyTransaction.TransactionType.PURCHASE);
//        sample.setPropertyType(PropertyTransaction.PropertyType.CONDO);
//        sample.setBuyerName("Juan dela Cruz");
//        sample.setSellerName("Maria Santos");
//        sample.setTransactionAmount(4_500_000.00);
//        sample.setCurrency("PHP");
//        sample.setArea("Makati");
//        sample.setCity("Metro Manila");
//        sample.setTransactionDate(LocalDateTime.now());
//        return sample;
//    }
//
    // ── Private ───────────────────────────────────────────────────────────────

    private PropertyTransaction mapAndEnrich(TransactionRequest req) {
        PropertyTransaction t = new PropertyTransaction();

        t.setParcelId(req.getParcelId());
        t.setTransactionType(req.getTransactionType());
        t.setPropertyType(req.getPropertyType());
        t.setArea(req.getArea());
        t.setCity(req.getCity());
        t.setBuyerName(req.getBuyerName());
        t.setSellerName(req.getSellerName());
        t.setTransactionAmount(req.getTransactionAmount());
        t.setCurrency(req.getCurrency() != null ? req.getCurrency() : "PHP");
        t.setTitleDeedUri(req.getTitleDeedUri());

        // Defaults for optional fields
        t.setTransactionId(
                req.getTransactionId() != null
                        ? req.getTransactionId()
                        : "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );
        t.setTransactionDate(
                req.getTransactionDate() != null
                        ? req.getTransactionDate()
                        : LocalDateTime.now()
        );
        t.setStatus("PENDING");
        t.setValidationStatus(PropertyTransaction.ValidationStatus.PENDING);

        // Auto-build S3 URI if caller didn't provide one
        if (t.getTitleDeedUri() == null && t.getParcelId() != null) {
            t.setTitleDeedUri("s3://beacon-docs/titles/" + t.getParcelId() + "/deed.pdf");
        }

        return t;
    }
}