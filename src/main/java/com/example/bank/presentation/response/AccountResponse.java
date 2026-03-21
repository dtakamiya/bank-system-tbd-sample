package com.example.bank.presentation.response;

import com.example.bank.domain.model.Account;

import java.math.BigDecimal;

public record AccountResponse(
        String accountNumber,
        String ownerName,
        BigDecimal balance,
        String status
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountNumber().value(),
                account.getOwnerName(),
                account.getBalance().getAmount(),
                account.getStatus().name()
        );
    }
}
