package com.example.bank.application.policy;

import com.example.bank.application.port.WithdrawalPolicy;
import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;

/**
 * 標準の出金ポリシー。
 *
 * <p>手数料なしで、要求された金額をそのまま口座から引き出す。</p>
 */
public class StandardWithdrawalPolicy implements WithdrawalPolicy {

    /** {@inheritDoc} */
    @Override
    public WithdrawalResult withdraw(Account account, Money amount) {
        Account updated = account.withdraw(amount);
        return new WithdrawalResult(updated, amount, Money.ZERO);
    }
}
