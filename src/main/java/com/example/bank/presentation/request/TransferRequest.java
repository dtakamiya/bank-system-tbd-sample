package com.example.bank.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 送金リクエストDTO。
 *
 * @param targetAccountNumber 送金先口座番号
 * @param amount              送金額
 */
public record TransferRequest(
        @NotBlank(message = "送金先口座番号は必須です")
        String targetAccountNumber,
        @NotNull(message = "金額は必須です")
        @Positive(message = "金額は正の値である必要があります")
        Long amount
) {
}
