package com.example.bank.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Transaction {

    private final String id;
    private final AccountNumber accountNumber;
    private final TransactionType type;
    private final Money amount;
    private final Money balanceAfter;
    private final LocalDateTime createdAt;

    private Transaction(String id, AccountNumber accountNumber, TransactionType type,
                        Money amount, Money balanceAfter, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public static Transaction deposit(AccountNumber accountNumber, Money amount, Money balanceAfter) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountNumber,
                TransactionType.DEPOSIT,
                amount,
                balanceAfter,
                LocalDateTime.now()
        );
    }

    public static Transaction withdrawal(AccountNumber accountNumber, Money amount, Money balanceAfter) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountNumber,
                TransactionType.WITHDRAWAL,
                amount,
                balanceAfter,
                LocalDateTime.now()
        );
    }

    public String getId() {
        return id;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public Money getAmount() {
        return amount;
    }

    public Money getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
