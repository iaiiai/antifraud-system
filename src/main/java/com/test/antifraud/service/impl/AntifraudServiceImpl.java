package com.test.antifraud.service.impl;

import com.test.antifraud.dto.request.*;
import com.test.antifraud.dto.response.AddIpResponse;
import com.test.antifraud.dto.response.AddStolenCardResponse;
import com.test.antifraud.dto.response.OperationStatusResponse;
import com.test.antifraud.dto.response.TransactionResponse;
import com.test.antifraud.exception.CardExistsException;
import com.test.antifraud.exception.CardNotFoundException;
import com.test.antifraud.exception.IPExistsException;
import com.test.antifraud.exception.IPNotFoundException;
import com.test.antifraud.model.entity.BlackListedIp;
import com.test.antifraud.model.entity.StolenCard;
import com.test.antifraud.repository.BlacklistedIpRepository;
import com.test.antifraud.repository.StolenCardRepository;
import com.test.antifraud.service.AntifraudService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AntifraudServiceImpl implements AntifraudService {
    private final BlacklistedIpRepository blacklistedIpRepository;
    private final StolenCardRepository stolenCardRepository;

    @Autowired
    public AntifraudServiceImpl(BlacklistedIpRepository blacklistedIpRepository, StolenCardRepository stolenCardRepository) {
        this.blacklistedIpRepository = blacklistedIpRepository;
        this.stolenCardRepository = stolenCardRepository;
    }

    public TransactionResponse validateTransaction(TransactionRequest transactionRequest) {
        int amount = transactionRequest.amount();
        String ip = transactionRequest.ip();
        String cardNumber = transactionRequest.number();
        boolean blacklistedIp = blacklistedIpRepository
                .existsByAddress(ip);
        boolean stolenCard = stolenCardRepository
                .existsByNumber(cardNumber);
        Map<String, Boolean> rules = new HashMap<>();
        rules.put("blacklistedIp", blacklistedIp);
        rules.put("stolenCard", stolenCard);
        List<String> violations = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : rules.entrySet()) {
            String ruleName = entry.getKey();
            boolean violated = entry.getValue();
            if (violated) {
                violations.add(ruleName);
            }
        }
        Collections.sort(violations);
        if (violations.isEmpty() == false) return new TransactionResponse("PROHIBITED", violations);
        if (amount <= 200) return new TransactionResponse("ALLOWED");
        if (amount <= 1500) return new TransactionResponse("MANUAL_PROCESSING");
        else return new TransactionResponse("PROHIBITED");
    }

    @Transactional
    public AddIpResponse addIp(AddBlacklistedIpRequest request) {
        String ipAddress = request.ip();
        boolean ipExists = blacklistedIpRepository
                .findByAddress(ipAddress)
                .isPresent();
        if (ipExists) throw new IPExistsException();
        BlackListedIp blackListedIp = blacklistedIpRepository.save(new BlackListedIp(ipAddress));
        return new AddIpResponse(blackListedIp.getId(), blackListedIp.getAddress());
    }

    @Transactional
    public OperationStatusResponse deleteIp(DeleteBlacklistedIpRequest request) {
        BlackListedIp blackListedIp = blacklistedIpRepository
                .findByAddress(request.ip())
                .orElseThrow(IPNotFoundException::new);
        blacklistedIpRepository.delete(blackListedIp);
        return new OperationStatusResponse("IP " + blackListedIp.getAddress() + " successfully removed!");
    }

    public List<BlackListedIp> getIpList() {
        return blacklistedIpRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional
    public AddStolenCardResponse addCard(AddStolenCardRequest request) {
        String cardNumber = request.number();
        boolean cardExists = stolenCardRepository
                .findByNumber(cardNumber)
                .isPresent();
        if (cardExists) throw new CardExistsException();
        StolenCard stolenCard = stolenCardRepository.save(new StolenCard(cardNumber));
        return new AddStolenCardResponse(stolenCard.getId(), stolenCard.getNumber());
    }

    @Transactional
    public OperationStatusResponse deleteCard(DeleteStolenCardRequest request) {
        String cardNumber = request.number();
        StolenCard stolenCard = stolenCardRepository
                .findByNumber(cardNumber)
                .orElseThrow(CardNotFoundException::new);
        stolenCardRepository.delete(stolenCard);
        return new OperationStatusResponse("Card " + stolenCard.getNumber() + " successfully removed");
    }

    public List<StolenCard> getCardList() { return stolenCardRepository.findAll(Sort.by(Sort.Direction.ASC, "id")); }
}
