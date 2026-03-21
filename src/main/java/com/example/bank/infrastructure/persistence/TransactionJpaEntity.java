package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.Money;
import com.example.bank.domain.model.Transaction;
import com.example.bank.domain.model.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(nullable = false, length = 20)
    private String type;

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
        entity.accountNumber = transaction.getAccountNumber().getValue();
        entity.type = transaction.getType().name();
        entity.amount = transaction.getAmount().getAmount();
        entity.balanceAfter = transaction.getBalanceAfter().getAmount();
        entity.createdAt = transaction.getCreatedAt();
        return entity;
    }

    public Transaction toDomain() {
        return Transaction.reconstruct(
                id,
                new AccountNumber(accountNumber),
                TransactionType.valueOf(type),
                Money.of(amount.longValue()),
                Money.of(balanceAfter.longValue()),
                createdAt
        );
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
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
