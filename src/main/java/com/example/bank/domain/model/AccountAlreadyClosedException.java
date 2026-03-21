package com.example.bank.domain.model;

/**
 * 既に解約済みの口座に対して操作を行おうとした場合にスローされる例外。
 */
public final class AccountAlreadyClosedException extends DomainException {

    /**
     * 口座解約済み例外を生成する。
     *
     * @param accountNumber 解約済みの口座番号
     */
    public AccountAlreadyClosedException(String accountNumber) {
        super("口座は既に解約されています: " + accountNumber);
    }
}
