package com.example.bank.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 取引（トランザクション）を表すドメインモデル。
 *
 * <p>入金・出金・返金といった取引の記録を保持するイミュータブルなオブジェクト。</p>
 */
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

    /**
     * 永続化層から取引を再構築する。
     *
     * @param id            取引ID
     * @param accountNumber 口座番号
     * @param type          取引種別
     * @param amount        取引金額
     * @param balanceAfter  取引後の残高
     * @param createdAt     取引日時
     * @return 再構築された取引
     */
    public static Transaction reconstruct(String id, AccountNumber accountNumber,
                                            TransactionType type, Money amount,
                                            Money balanceAfter, LocalDateTime createdAt) {
        return new Transaction(id, accountNumber, type, amount, balanceAfter, createdAt);
    }

    private static Transaction create(AccountNumber accountNumber, TransactionType type,
                                      Money amount, Money balanceAfter) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountNumber,
                type,
                amount,
                balanceAfter,
                LocalDateTime.now()
        );
    }

    /**
     * 入金取引を作成する。
     *
     * @param accountNumber 口座番号
     * @param amount        入金額
     * @param balanceAfter  入金後の残高
     * @return 入金取引
     */
    public static Transaction deposit(AccountNumber accountNumber, Money amount, Money balanceAfter) {
        return create(accountNumber, TransactionType.DEPOSIT, amount, balanceAfter);
    }

    /**
     * 出金取引を作成する。
     *
     * @param accountNumber 口座番号
     * @param amount        出金額
     * @param balanceAfter  出金後の残高
     * @return 出金取引
     */
    public static Transaction withdrawal(AccountNumber accountNumber, Money amount, Money balanceAfter) {
        return create(accountNumber, TransactionType.WITHDRAWAL, amount, balanceAfter);
    }

    /**
     * 返金取引を作成する。
     *
     * @param accountNumber 口座番号
     * @param amount        返金額
     * @param balanceAfter  返金後の残高
     * @return 返金取引
     */
    public static Transaction refund(AccountNumber accountNumber, Money amount, Money balanceAfter) {
        return create(accountNumber, TransactionType.REFUND, amount, balanceAfter);
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
