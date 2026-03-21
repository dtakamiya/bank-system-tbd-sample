package com.example.bank.application.policy;

import com.example.bank.application.port.WithdrawalPolicy;
import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FeeChargedWithdrawalPolicy implements WithdrawalPolicy {

    private final BigDecimal feeRate;

    public FeeChargedWithdrawalPolicy(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    @Override
    public WithdrawalResult withdraw(Account account, Money amount) {
        BigDecimal feeAmount = amount.getAmount()
                .multiply(feeRate)
                .setScale(2, RoundingMode.HALF_UP);
        Money fee = Money.of(feeAmount);
        Money totalAmount = amount.add(fee);

        Account updated = account.withdraw(totalAmount);
        return new WithdrawalResult(updated, totalAmount, fee);
    }
}
