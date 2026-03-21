package com.example.bank.domain.model;

public sealed abstract class DomainException extends RuntimeException
        permits InsufficientBalanceException, AccountNotFoundException, InvalidAmountException {

    protected DomainException(String message) {
        super(message);
    }
}
