package com.example.bank.presentation.response;

import com.example.bank.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
