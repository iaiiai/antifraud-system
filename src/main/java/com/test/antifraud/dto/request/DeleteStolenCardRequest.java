package com.test.antifraud.dto.request;

import com.test.antifraud.validation.annotation.ValidLuhn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddStolenCardRequest(
        @NotBlank
        @ValidLuhn
        String number
) {
}
