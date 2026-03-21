package com.example.bank.domain.model;

public final class AccountAlreadyClosedException extends DomainException {

    public AccountAlreadyClosedException(String accountNumber) {
        super("口座は既に解約されています: " + accountNumber);
    }
}
