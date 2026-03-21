package com.example.bank.application.policy;

import com.example.bank.application.port.WithdrawalPolicy;
import com.example.bank.application.port.WithdrawalResult;
import com.example.bank.domain.model.Account;
import com.example.bank.domain.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 手数料付きの出金ポリシー。
 *
 * <p>出金額に対して設定された手数料率を適用し、出金額と手数料の合計を口座から引き出す。</p>
 */
public class FeeChargedWithdrawalPolicy implements WithdrawalPolicy {

    private final BigDecimal feeRate;

    public FeeChargedWithdrawalPolicy(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    /** {@inheritDoc} */
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
