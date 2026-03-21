package com.example.bank.domain.model;

public final class AccountNotFoundException extends DomainException {

    public AccountNotFoundException(AccountNumber accountNumber) {
        super("口座が見つかりません。口座番号: " + accountNumber.value());
    }
}
