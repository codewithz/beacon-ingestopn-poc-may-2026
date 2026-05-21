package com.beacon.controller;

import com.beacon.dto.TransactionRequest;
import com.beacon.dto.TransactionResponse;
import com.beacon.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HTTP layer only.
 * Receives requests, hands off to service, returns response.
 * No business logic. No Kafka. No null checks. No defaults.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> submit(
            @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(transactionService.process(request));
    }

//    @PostMapping("/batch")
//    public ResponseEntity<TransactionResponse> submitBatch(
//            @RequestBody List<TransactionRequest> requests) {
//
//        return ResponseEntity.ok(transactionService.processBatch(requests));
//    }
//
//    @GetMapping("/sample")
//    public ResponseEntity<TransactionRequest> sample() {
//        return ResponseEntity.ok(transactionService.buildSample());
//    }
}