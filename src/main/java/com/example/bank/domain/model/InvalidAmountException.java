package com.example.bank.domain.model;

public final class InvalidAmountException extends DomainException {

    public InvalidAmountException(long amount) {
        super("不正な金額です: " + amount);
    }
}
