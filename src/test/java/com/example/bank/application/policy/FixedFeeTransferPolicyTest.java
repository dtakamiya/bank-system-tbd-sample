package com.example.bank.application.policy;

import com.example.bank.domain.model.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FixedFeeTransferPolicy: 固定手数料送金ポリシー")
class FixedFeeTransferPolicyTest {

    private final FixedFeeTransferPolicy policy = new FixedFeeTransferPolicy(new BigDecimal("0.01"));

    @Test
    @DisplayName("金額10000・手数料率1%で手数料が100であること")
    void shouldCalculateFeeForTenThousand() {
        assertThat(policy.calculateFee(Money.of(10000))).isEqualTo(Money.of(100));
    }

    @Test
    @DisplayName("金額50000・手数料率1%で手数料が500であること")
    void shouldCalculateFeeForFiftyThousand() {
        assertThat(policy.calculateFee(Money.of(50000))).isEqualTo(Money.of(500));
    }

    @Test
    @DisplayName("端数がHALF_UPで丸められること")
    void shouldRoundHalfUp() {
        // 333 * 0.01 = 3.33
        FixedFeeTransferPolicy policy3 = new FixedFeeTransferPolicy(new BigDecimal("0.01"));
        assertThat(policy3.calculateFee(Money.of(333))).isEqualTo(Money.of(new BigDecimal("3.33")));
    }
}
