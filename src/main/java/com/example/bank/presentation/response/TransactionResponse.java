package com.example.bank.presentation.response;

import com.example.bank.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 取引情報のレスポンスDTO。
 *
 * @param id           取引ID
 * @param type         取引種別（DEPOSIT, WITHDRAWAL など）
 * @param amount       取引金額
 * @param balanceAfter 取引後の残高
 * @param createdAt    取引日時
 */
public record TransactionResponse(
        String id,
        String type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().name(),
                transaction.getAmount().getAmount(),
                transaction.getBalanceAfter().getAmount(),
                transaction.getCreatedAt()
        );
    }
}
