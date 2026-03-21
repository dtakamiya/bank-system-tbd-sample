package com.example.bank.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 入金・出金時の金額を指定するリクエストDTO。
 *
 * @param amount 金額（必須、正の値）
 */
public record AmountRequest(
        @NotNull(message = "金額は必須です")
        @Positive(message = "金額は正の値である必要があります")
        Long amount
) {
}
