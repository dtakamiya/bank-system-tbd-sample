package com.example.bank.application.port;

import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;

@FunctionalInterface
public interface WithdrawalPolicy {

    WithdrawalResult withdraw(Account account, Money amount);
}
