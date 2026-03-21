package com.example.bank.domain.model;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(Money currentBalance, Money withdrawAmount) {
        super("残高不足です。現在残高: " + currentBalance.getAmount()
                + ", 出金額: " + withdrawAmount.getAmount());
    }
}
