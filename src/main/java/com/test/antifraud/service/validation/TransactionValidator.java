package com.test.antifraud.service.validation;

import com.test.antifraud.dto.request.TransactionRequest;
import com.test.antifraud.model.entity.Transaction;
import com.test.antifraud.repository.BlacklistedIpRepository;
import com.test.antifraud.repository.StolenCardRepository;
import com.test.antifraud.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class TransactionValidator {
    private final TransactionRepository transactionRepository;
    private final BlacklistedIpRepository blacklistedIpRepository;
    private final StolenCardRepository stolenCardRepository;

    @Autowired
    public TransactionValidator(
            TransactionRepository transactionRepository,
            BlacklistedIpRepository blacklistedIpRepository,
            StolenCardRepository stolenCardRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.blacklistedIpRepository = blacklistedIpRepository;
        this.stolenCardRepository = stolenCardRepository;
    }

    public List<String> validate(TransactionRequest request) {
        Map<String, Boolean> rules = new HashMap<>();
        List<String> violations = new ArrayList<>();
        String ip = request.ip();
        String region = request.region().name();
        LocalDateTime dateTime = request.date();
        String cardNumber = request.number();
        boolean blacklistedIp = blacklistedIpRepository
                .existsByAddress(ip);
        boolean stolenCard = stolenCardRepository
                .existsByNumber(cardNumber);
        boolean regionCorrelation = transactionRepository
                .countDistinctByNumberAndRegion(cardNumber, region) > 2;
        boolean ipCorrelation = transactionRepository
                .countDistinctByNumberAndIp(cardNumber, ip) > 2;
        rules.put("blacklistedIp", blacklistedIp);
        rules.put("stolenCard", stolenCard);
        rules.put("regionCorrelation", regionCorrelation);
        rules.put("ipCorrelation", ipCorrelation);
        for (Map.Entry<String, Boolean> entry : rules.entrySet()) {
            String ruleName = entry.getKey();
            boolean violated = entry.getValue();
            if (violated) {
                violations.add(ruleName);
            }
        }
        Collections.sort(violations);
        return violations;
    }
}
