package com.example.bank.application.policy;

import com.example.bank.application.port.TransferFeePolicy;
import com.example.bank.domain.model.Money;

/**
 * 手数料なしの送金ポリシー。
 *
 * <p>送金額に関わらず手数料をゼロで返す。</p>
 */
public class NoFeeTransferPolicy implements TransferFeePolicy {

    @Override
    public Money calculateFee(Money amount) {
        return Money.ZERO;
    }
}
