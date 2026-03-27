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
import com.test.antifraud.mapper.TransactionMapper;
import com.test.antifraud.model.entity.BlackListedIp;
import com.test.antifraud.model.entity.StolenCard;
import com.test.antifraud.model.entity.Transaction;
import com.test.antifraud.model.enums.TransactionResult;
import com.test.antifraud.repository.BlacklistedIpRepository;
import com.test.antifraud.repository.StolenCardRepository;
import com.test.antifraud.repository.TransactionRepository;
import com.test.antifraud.service.AntifraudService;
import com.test.antifraud.service.validation.TransactionValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AntifraudServiceImpl implements AntifraudService {
    private final BlacklistedIpRepository blacklistedIpRepository;
    private final StolenCardRepository stolenCardRepository;
    private final TransactionRepository transactionRepository;

    private final TransactionValidator transactionValidator;

    @Autowired
    public AntifraudServiceImpl(
        BlacklistedIpRepository blacklistedIpRepository,
        StolenCardRepository stolenCardRepository,
        TransactionRepository transactionRepository,
        TransactionValidator transactionValidator
    ) {
        this.blacklistedIpRepository = blacklistedIpRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.transactionRepository = transactionRepository;
        this.transactionValidator = transactionValidator;
    }

    @Transactional
    public TransactionResponse handleTransaction(TransactionRequest request) {
        Transaction transaction = TransactionMapper.toEntity(request);
        Long amount = transaction.getAmount();
        String ALLOWED = TransactionResult.ALLOWED.name();
        String MANUAL_PROCESSING = TransactionResult.MANUAL_PROCESSING.name();
        String PROHIBITED = TransactionResult.PROHIBITED.name();
        List<String> violations = transactionValidator.validate(request);
        if (amount <= 200) transaction.setResult(ALLOWED);
        else if (amount <= 1500) transaction.setResult(MANUAL_PROCESSING);
        else transaction.setResult(PROHIBITED);
        if (!violations.isEmpty()) {
            transaction.setResult(PROHIBITED);
        }
        Transaction saved = transactionRepository.save(transaction);
        return TransactionMapper.toResponse(saved, violations);
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
