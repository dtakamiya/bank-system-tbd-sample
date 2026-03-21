package com.example.bank.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 銀行口座を表すドメインモデル。
 *
 * <p>イミュータブルなオブジェクトであり、入金・出金・解約などの操作は
 * 新しい {@code Account} インスタンスを返す。</p>
 */
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

    /**
     * 永続化層から口座を再構築する。
     *
     * @param id            口座ID
     * @param accountNumber 口座番号
     * @param ownerName     口座名義人
     * @param balance       残高
     * @param status        口座状態
     * @param createdAt     作成日時
     * @return 再構築された口座
     */
    public static Account reconstruct(String id, AccountNumber accountNumber,
                                      String ownerName, Money balance,
                                      AccountStatus status, LocalDateTime createdAt) {
        return new Account(id, accountNumber, ownerName, balance, status, createdAt);
    }

    /**
     * 新しい口座を作成する。
     *
     * <p>口座番号は自動生成され、残高0・ACTIVE状態で初期化される。</p>
     *
     * @param ownerName 口座名義人
     * @return 新しい口座
     */
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

    /**
     * 指定された金額を入金する。
     *
     * @param amount 入金額（正の値であること）
     * @return 入金後の新しい口座
     * @throws AccountAlreadyClosedException 口座が解約済みの場合
     * @throws IllegalArgumentException      入金額が正でない場合
     */
    public Account deposit(Money amount) {
        ensureActive();
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("入金額は正の値である必要があります: " + amount);
        }
        return new Account(id, accountNumber, ownerName, balance.add(amount), status, createdAt);
    }

    /**
     * 指定された金額を出金する。
     *
     * @param amount 出金額
     * @return 出金後の新しい口座
     * @throws AccountAlreadyClosedException 口座が解約済みの場合
     * @throws InsufficientBalanceException  残高不足の場合
     */
    public Account withdraw(Money amount) {
        ensureActive();
        if (!canWithdraw(amount)) {
            throw new InsufficientBalanceException(balance, amount);
        }
        return new Account(id, accountNumber, ownerName, balance.subtract(amount), status, createdAt);
    }

    /**
     * 口座を解約する。
     *
     * <p>残高は0にリセットされ、状態は {@link AccountStatus#CLOSED} になる。</p>
     *
     * @return 解約後の新しい口座
     * @throws AccountAlreadyClosedException 口座が既に解約済みの場合
     */
    public Account close() {
        ensureActive();
        return new Account(id, accountNumber, ownerName, Money.ZERO, AccountStatus.CLOSED, createdAt);
    }

    /**
     * 指定された金額を出金可能かどうかを判定する。
     *
     * @param amount 出金希望額
     * @return 残高が出金額以上であれば {@code true}
     */
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

    /**
     * 口座が解約済みかどうかを判定する。
     *
     * @return 解約済みであれば {@code true}
     */
    public boolean isClosed() {
        return status == AccountStatus.CLOSED;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
