package com.example.bank.domain.model;

/**
 * 同一口座への送金を行おうとした場合にスローされる例外。
 */
public final class SameAccountTransferException extends DomainException {

    /**
     * 同一口座送金例外を生成する。
     *
     * @param accountNumber 送金元・送金先の口座番号
     */
    public SameAccountTransferException(String accountNumber) {
        super("同一口座への送金はできません: " + accountNumber);
    }
}
