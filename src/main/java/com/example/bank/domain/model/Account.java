package com.example.bank.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Account {

    private final String id;
    private final AccountNumber accountNumber;
    private final String ownerName;
    private final Money balance;
    private final LocalDateTime createdAt;

    private Account(String id, AccountNumber accountNumber, String ownerName,
                    Money balance, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public static Account reconstruct(String id, AccountNumber accountNumber,
                                      String ownerName, Money balance, LocalDateTime createdAt) {
        return new Account(id, accountNumber, ownerName, balance, createdAt);
    }

    public static Account create(String ownerName) {
        return new Account(
                UUID.randomUUID().toString(),
                AccountNumber.generate(),
                ownerName,
                Money.ZERO,
                LocalDateTime.now()
        );
    }

    public Account deposit(Money amount) {
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("入金額は正の値である必要があります: " + amount);
        }
        return new Account(id, accountNumber, ownerName, balance.add(amount), createdAt);
    }

    public Account withdraw(Money amount) {
        if (!canWithdraw(amount)) {
            throw new InsufficientBalanceException(balance, amount);
        }
        return new Account(id, accountNumber, ownerName, balance.subtract(amount), createdAt);
    }

    public boolean canWithdraw(Money amount) {
        return balance.isGreaterThanOrEqual(amount);
    }

    public String getId() {
        return id;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Money getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
