package com.example.bank.application.policy;

import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NoFeeTransferPolicy: 手数料なし送金ポリシー")
class NoFeeTransferPolicyTest {

    private final NoFeeTransferPolicy policy = new NoFeeTransferPolicy();

    @Test
    @DisplayName("任意の金額に対して手数料がゼロであること")
    void shouldReturnZeroFee() {
        assertThat(policy.calculateFee(Money.of(10000))).isEqualTo(Money.ZERO);
        assertThat(policy.calculateFee(Money.of(50000))).isEqualTo(Money.ZERO);
        assertThat(policy.calculateFee(Money.of(1))).isEqualTo(Money.ZERO);
    }
}
