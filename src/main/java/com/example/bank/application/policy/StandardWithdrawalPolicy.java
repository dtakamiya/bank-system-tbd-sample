package com.example.bank.application.policy;

import com.example.bank.application.port.WithdrawalPolicy;
import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;

public class StandardWithdrawalPolicy implements WithdrawalPolicy {

    @Override
    public WithdrawalResult withdraw(Account account, Money amount) {
        Account updated = account.withdraw(amount);
        return new WithdrawalResult(updated, amount, Money.ZERO);
    }
}
