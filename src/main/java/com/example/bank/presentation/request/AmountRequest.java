package com.example.bank.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AmountRequest(
        @NotNull(message = "金額は必須です")
        @Positive(message = "金額は正の値である必要があります")
        Long amount
) {
}
