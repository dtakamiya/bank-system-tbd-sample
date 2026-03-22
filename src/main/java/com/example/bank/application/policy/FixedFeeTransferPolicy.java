package com.example.bank.application.policy;

import com.example.bank.application.port.TransferFeePolicy;
import com.example.bank.domain.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 固定手数料率の送金ポリシー。
 *
 * <p>送金額に対して設定された手数料率を適用し、手数料を計算する。</p>
 */
public class FixedFeeTransferPolicy implements TransferFeePolicy {

    private final BigDecimal feeRate;

    public FixedFeeTransferPolicy(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    @Override
    public Money calculateFee(Money amount) {
        BigDecimal feeAmount = amount.getAmount()
                .multiply(feeRate)
                .setScale(2, RoundingMode.HALF_UP);
        return Money.of(feeAmount);
    }
}
