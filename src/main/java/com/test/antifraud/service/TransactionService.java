package com.test.antifraud.service;

import com.test.antifraud.dto.request.TransactionRequest;
import com.test.antifraud.dto.response.TransactionResponse;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    public TransactionResponse validateTransaction(TransactionRequest transactionRequest) {
        int transactionRequestAmount = transactionRequest.amount();
        if (transactionRequestAmount <= 200) return new TransactionResponse("ALLOWED");
        if (transactionRequestAmount <= 1500) return new TransactionResponse("MANUAL_PROCESSING");
        else return new TransactionResponse("PROHIBITED");
    }
}
