package com.test.antifraud.service;

import com.test.antifraud.dto.request.*;
import com.test.antifraud.dto.response.AddIpResponse;
import com.test.antifraud.dto.response.AddStolenCardResponse;
import com.test.antifraud.dto.response.OperationStatusResponse;
import com.test.antifraud.dto.response.TransactionResponse;
import com.test.antifraud.model.entity.BlackListedIp;
import com.test.antifraud.model.entity.StolenCard;

import java.util.List;

public interface AntifraudService {
    TransactionResponse handleTransaction(TransactionRequest request);

    List<BlackListedIp> getIpList();
    AddIpResponse addIp(AddBlacklistedIpRequest request);
    OperationStatusResponse deleteIp(DeleteBlacklistedIpRequest request);

    List<StolenCard> getCardList();
    AddStolenCardResponse addCard(AddStolenCardRequest request);
    OperationStatusResponse deleteCard(DeleteStolenCardRequest request);
}