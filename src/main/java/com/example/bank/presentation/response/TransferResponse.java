package com.example.bank.presentation.response;

import com.example.bank.domain.model.TransferResult;

import java.math.BigDecimal;

/**
 * 送金レスポンスDTO。
 *
 * @param sourceAccountNumber 送金元口座番号
 * @param targetAccountNumber 送金先口座番号
 * @param amount              送金額
 * @param fee                 手数料
 * @param sourceBalanceAfter  送金元の送金後残高
 * @param targetBalanceAfter  送金先の送金後残高
 */
public record TransferResponse(
        String sourceAccountNumber,
        String targetAccountNumber,
        BigDecimal amount,
        BigDecimal fee,
        BigDecimal sourceBalanceAfter,
        BigDecimal targetBalanceAfter
) {
    public static TransferResponse from(TransferResult result) {
        return new TransferResponse(
                result.sourceAccount().getAccountNumber().value(),
                result.targetAccount().getAccountNumber().value(),
                result.amount().getAmount(),
                result.fee().getAmount(),
                result.sourceAccount().getBalance().getAmount(),
                result.targetAccount().getBalance().getAmount()
        );
    }
}
