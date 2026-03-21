package com.example.bank.domain.model;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(AccountNumber accountNumber) {
        super("口座が見つかりません。口座番号: " + accountNumber.getValue());
    }
}
