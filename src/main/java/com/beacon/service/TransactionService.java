package com.beacon.service;

import com.beacon.dto.TransactionRequest;
import com.beacon.dto.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionService {

    public TransactionResponse process(TransactionRequest request) {

        log.info("=== Transaction Received ===");
        log.info("Transaction ID  : {}", request.getTransactionId());
        log.info("Parcel ID       : {}", request.getParcelId());
        log.info("Type            : {}", request.getTransactionType());
        log.info("Property Type   : {}", request.getPropertyType());
        log.info("Buyer           : {}", request.getBuyerName());
        log.info("Seller          : {}", request.getSellerName());
        log.info("Amount          : {} {}", request.getCurrency(), request.getTransactionAmount());
        log.info("Area            : {}, {}", request.getArea(), request.getCity());
        log.info("Title Deed URI  : {}", request.getTitleDeedUri());
        log.info("Transaction Date: {}", request.getTransactionDate());
        log.info("===========================");

        return TransactionResponse.builder()
                .status("ACCEPTED")
                .transactionId(request.getTransactionId())
                .parcelId(request.getParcelId())
                .count(1)
                .build();
    }
}