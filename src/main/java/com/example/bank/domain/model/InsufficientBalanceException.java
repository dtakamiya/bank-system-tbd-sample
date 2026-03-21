package com.example.bank.domain.model;

/**
 * 残高不足により出金できない場合にスローされる例外。
 */
public final class InsufficientBalanceException extends DomainException {

    /**
     * 残高不足例外を生成する。
     *
     * @param currentBalance 現在の残高
     * @param withdrawAmount 要求された出金額
     */
    public InsufficientBalanceException(Money currentBalance, Money withdrawAmount) {
        super("残高不足です。現在残高: " + currentBalance.getAmount()
                + ", 出金額: " + withdrawAmount.getAmount());
    }
}
