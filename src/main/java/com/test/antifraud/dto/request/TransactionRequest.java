package com.test.antifraud.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
        @NotNull(message = "Amount can not be null")
        @Min(value = 1, message = "Amount should be more than 0")
        int amount
) { }
