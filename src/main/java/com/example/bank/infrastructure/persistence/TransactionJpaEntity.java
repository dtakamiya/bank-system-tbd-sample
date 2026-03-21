package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionJpaEntity {

    @Id
    private String id;

    @Column(name = "account_number", nullable = false, length = 10)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected TransactionJpaEntity() {
    }

    public static TransactionJpaEntity fromDomain(Transaction transaction) {
        TransactionJpaEntity entity = new TransactionJpaEntity();
        entity.id = transaction.getId();
        entity.accountNumber = transaction.getAccountNumber().value();
        entity.type = transaction.getType();
        entity.amount = transaction.getAmount().getAmount();
        entity.balanceAfter = transaction.getBalanceAfter().getAmount();
        entity.createdAt = transaction.getCreatedAt();
        return entity;
    }

    public Transaction toDomain() {
        return Transaction.reconstruct(
                id,
                new AccountNumber(accountNumber),
                type,
                Money.of(amount),
                Money.of(balanceAfter),
                createdAt
        );
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
