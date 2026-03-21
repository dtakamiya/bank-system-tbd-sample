package com.example.bank.infrastructure.config;

import com.example.bank.application.policy.FeeChargedWithdrawalPolicy;
import com.example.bank.application.policy.StandardWithdrawalPolicy;
import com.example.bank.application.port.WithdrawalPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link WithdrawalPolicyConfig} のインテグレーションテスト。
 *
 * <p>フィーチャーフラグに応じた出金ポリシーBeanの切り替えを検証する。</p>
 *
 * @see WithdrawalPolicyConfig
 */
class WithdrawalPolicyConfigTest {

    @Nested
    @SpringBootTest
    @TestPropertySource(properties = {
            "bank.features.withdrawal-fee=false"
    })
    @DisplayName("withdrawal-fee=false の場合")
    class WhenWithdrawalFeeDisabled {

        @Autowired
        private WithdrawalPolicy withdrawalPolicy;

        @Test
        @DisplayName("@Primary Bean が StandardWithdrawalPolicy であること")
        void shouldRegisterStandardPolicyAsPrimary() {
            assertThat(withdrawalPolicy).isInstanceOf(StandardWithdrawalPolicy.class);
        }
    }

    @Nested
    @SpringBootTest
    @TestPropertySource(properties = {
            "bank.features.withdrawal-fee=true"
    })
    @DisplayName("withdrawal-fee=true の場合")
    class WhenWithdrawalFeeEnabled {

        @Autowired
        private WithdrawalPolicy withdrawalPolicy;

        @Autowired
        @Qualifier("standardWithdrawalPolicy")
        private WithdrawalPolicy standardPolicy;

        @Test
        @DisplayName("@Primary Bean が FeeChargedWithdrawalPolicy であること")
        void shouldRegisterFeeChargedPolicyAsPrimary() {
            assertThat(withdrawalPolicy).isInstanceOf(FeeChargedWithdrawalPolicy.class);
        }

        @Test
        @DisplayName("@Qualifier付きBeanが常にStandardWithdrawalPolicyであること")
        void shouldAlwaysRegisterStandardPolicyWithQualifier() {
            assertThat(standardPolicy).isInstanceOf(StandardWithdrawalPolicy.class);
        }
    }
}
