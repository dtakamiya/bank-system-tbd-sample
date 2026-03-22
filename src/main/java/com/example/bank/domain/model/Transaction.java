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
    private final Money fee;
    private final Money balanceAfter;
    private final AccountNumber referenceAccountNumber;
    private final LocalDateTime createdAt;

    private Transaction(String id, AccountNumber accountNumber, TransactionType type,
                        Money amount, Money fee, Money balanceAfter,
                        AccountNumber referenceAccountNumber, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.fee = fee;
        this.balanceAfter = balanceAfter;
        this.referenceAccountNumber = referenceAccountNumber;
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
        return new Transaction(id, accountNumber, type, amount, null, balanceAfter, null, createdAt);
    }

    /**
     * 永続化層から送金取引を再構築する（fee, referenceAccountNumber付き）。
     *
     * @param id                     取引ID
     * @param accountNumber          口座番号
     * @param type                   取引種別
     * @param amount                 取引金額
     * @param fee                    手数料（nullの場合あり）
     * @param balanceAfter           取引後の残高
     * @param referenceAccountNumber 相手先口座番号（nullの場合あり）
     * @param createdAt              取引日時
     * @return 再構築された取引
     */
    public static Transaction reconstruct(String id, AccountNumber accountNumber,
                                            TransactionType type, Money amount, Money fee,
                                            Money balanceAfter, AccountNumber referenceAccountNumber,
                                            LocalDateTime createdAt) {
        return new Transaction(id, accountNumber, type, amount, fee, balanceAfter, referenceAccountNumber, createdAt);
    }

    private static Transaction create(AccountNumber accountNumber, TransactionType type,
                                      Money amount, Money balanceAfter) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountNumber,
                type,
                amount,
                null,
                balanceAfter,
                null,
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

    /**
     * 送金出金取引を作成する。
     *
     * @param accountNumber          送金元口座番号
     * @param amount                 送金額
     * @param fee                    手数料
     * @param balanceAfter           取引後の残高
     * @param referenceAccountNumber 送金先口座番号
     * @return 送金出金取引
     */
    public static Transaction transferOut(AccountNumber accountNumber, Money amount, Money fee,
                                          Money balanceAfter, AccountNumber referenceAccountNumber) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountNumber,
                TransactionType.TRANSFER_OUT,
                amount,
                fee,
                balanceAfter,
                referenceAccountNumber,
                LocalDateTime.now()
        );
    }

    /**
     * 送金入金取引を作成する。
     *
     * @param accountNumber          送金先口座番号
     * @param amount                 送金額
     * @param balanceAfter           取引後の残高
     * @param referenceAccountNumber 送金元口座番号
     * @return 送金入金取引
     */
    public static Transaction transferIn(AccountNumber accountNumber, Money amount,
                                         Money balanceAfter, AccountNumber referenceAccountNumber) {
        return new Transaction(
                UUID.randomUUID().toString(),
                accountNumber,
                TransactionType.TRANSFER_IN,
                amount,
                null,
                balanceAfter,
                referenceAccountNumber,
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

    public Money getFee() {
        return fee;
    }

    public Money getBalanceAfter() {
        return balanceAfter;
    }

    public AccountNumber getReferenceAccountNumber() {
        return referenceAccountNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
