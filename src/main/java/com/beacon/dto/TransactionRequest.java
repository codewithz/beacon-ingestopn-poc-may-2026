package com.beacon.dto;

import com.beacon.model.PropertyTransaction.PropertyType;
import com.beacon.model.PropertyTransaction.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * What the REST API accepts from the caller.
 * Never expose the domain model (PropertyTransaction) directly to the HTTP layer.
 */
@Data
public class TransactionRequest {

    private String transactionId;   // optional — service generates one if absent
    private String parcelId;

    private TransactionType transactionType;
    private PropertyType propertyType;

    private String area;
    private String city;

    private String buyerName;
    private String sellerName;

    private double transactionAmount;
    private String currency;        // optional — defaults to PHP

    private String titleDeedUri;    // optional — auto-built from parcelId if absent

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime transactionDate;
}