package com.example.bank.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Account {

    private final String id;
    private final AccountNumber accountNumber;
    private final String ownerName;
    private final Money balance;
    private final AccountStatus status;
    private final LocalDateTime createdAt;

    private Account(String id, AccountNumber accountNumber, String ownerName,
                    Money balance, AccountStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Account reconstruct(String id, AccountNumber accountNumber,
                                      String ownerName, Money balance,
                                      AccountStatus status, LocalDateTime createdAt) {
        return new Account(id, accountNumber, ownerName, balance, status, createdAt);
    }

    public static Account create(String ownerName) {
        return new Account(
                UUID.randomUUID().toString(),
                AccountNumber.generate(),
                ownerName,
                Money.ZERO,
                AccountStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    public Account deposit(Money amount) {
        ensureActive();
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("入金額は正の値である必要があります: " + amount);
        }
        return new Account(id, accountNumber, ownerName, balance.add(amount), status, createdAt);
    }

    public Account withdraw(Money amount) {
        ensureActive();
        if (!canWithdraw(amount)) {
            throw new InsufficientBalanceException(balance, amount);
        }
        return new Account(id, accountNumber, ownerName, balance.subtract(amount), status, createdAt);
    }

    public Account close() {
        ensureActive();
        return new Account(id, accountNumber, ownerName, Money.ZERO, AccountStatus.CLOSED, createdAt);
    }

    public boolean canWithdraw(Money amount) {
        return balance.isGreaterThanOrEqual(amount);
    }

    private void ensureActive() {
        if (isClosed()) {
            throw new AccountAlreadyClosedException(accountNumber.value());
        }
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

    public AccountStatus getStatus() {
        return status;
    }

    public boolean isClosed() {
        return status == AccountStatus.CLOSED;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
