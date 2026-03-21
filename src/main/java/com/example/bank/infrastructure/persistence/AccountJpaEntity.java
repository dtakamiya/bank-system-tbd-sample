package com.example.bank.infrastructure.persistence;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.AccountNumber;
import com.example.bank.domain.model.AccountStatus;
import com.example.bank.domain.model.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 口座情報のJPAエンティティ。
 *
 * <p>データベースの {@code accounts} テーブルとマッピングし、
 * ドメインモデル {@link Account} との相互変換を担う。</p>
 */
@Entity
@Table(name = "accounts")
public class AccountJpaEntity {

    @Id
    private String id;

    @Column(name = "account_number", nullable = false, unique = true, length = 10)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected AccountJpaEntity() {
    }

    @PrePersist
    @PreUpdate
    void onPersist() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * ドメインモデルからJPAエンティティを生成する。
     *
     * @param account 変換元の口座ドメインモデル
     * @return 対応するJPAエンティティ
     */
    public static AccountJpaEntity fromDomain(Account account) {
        AccountJpaEntity entity = new AccountJpaEntity();
        entity.id = account.getId();
        entity.accountNumber = account.getAccountNumber().value();
        entity.ownerName = account.getOwnerName();
        entity.balance = account.getBalance().getAmount();
        entity.status = account.getStatus();
        entity.createdAt = account.getCreatedAt();
        return entity;
    }

    /**
     * JPAエンティティからドメインモデルを復元する。
     *
     * @return 復元された口座ドメインモデル
     */
    public Account toDomain() {
        return Account.reconstruct(
                id,
                new AccountNumber(accountNumber),
                ownerName,
                Money.of(balance),
                status,
                createdAt
        );
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
