package com.example.bank.domain.model;

/**
 * 不正な金額が指定された場合にスローされる例外。
 */
public final class InvalidAmountException extends DomainException {

    /**
     * 不正金額例外を生成する。
     *
     * @param amount 不正な金額
     */
    public InvalidAmountException(long amount) {
        super("不正な金額です: " + amount);
    }
}
