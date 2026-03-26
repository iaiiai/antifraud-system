package com.test.antifraud.controller;

import com.test.antifraud.dto.request.AddIPRequest;
import com.test.antifraud.dto.request.TransactionRequest;
import com.test.antifraud.dto.response.AddIPResponse;
import com.test.antifraud.dto.response.TransactionResponse;
import com.test.antifraud.service.TransactionService;
import com.test.antifraud.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionRestController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    @ResponseBody
    public TransactionResponse handleTransaction(@Valid @RequestBody TransactionRequest request) {
        return transactionService.validateTransaction(request);
    }

    @PostMapping("/suspicious-id")
    @ResponseBody
    public AddIPResponse addIP(@Valid @RequestBody AddIPRequest request) {
        return transactionService.addIP();
    }
}
