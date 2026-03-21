package com.example.bank.domain.model;

/**
 * 指定された口座番号に該当する口座が存在しない場合にスローされる例外。
 */
public final class AccountNotFoundException extends DomainException {

    /**
     * 口座未検出例外を生成する。
     *
     * @param accountNumber 見つからなかった口座番号
     */
    public AccountNotFoundException(AccountNumber accountNumber) {
        super("口座が見つかりません。口座番号: " + accountNumber.value());
    }
}
