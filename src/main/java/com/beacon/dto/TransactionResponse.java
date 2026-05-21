package com.beacon.dto;

import lombok.Builder;
import lombok.Data;

/**
 * What the REST API returns to the caller.
 * Keeps Kafka internals (partition, offset) out of the API contract.
 */
@Data
@Builder
public class TransactionResponse {

    private String status;          // ACCEPTED | FAILED
    private String transactionId;
    private String parcelId;
    private String topic;
    private int count;              // meaningful for batch responses
    private String error;           // populated only on failure
}