package com.example.bank.infrastructure.config;

import com.example.bank.application.policy.FeeChargedWithdrawalPolicy;
import com.example.bank.application.policy.StandardWithdrawalPolicy;
import com.example.bank.application.port.FeatureFlagService;
import com.example.bank.application.port.WithdrawalPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;

@Configuration
public class WithdrawalPolicyConfig {

    @Bean
    @Primary
    public WithdrawalPolicy withdrawalPolicy(
            FeatureFlagService featureFlagService,
            @Value("${bank.withdrawal.fee-rate:0.01}") BigDecimal feeRate) {
        if (featureFlagService.isEnabled("withdrawal-fee")) {
            return new FeeChargedWithdrawalPolicy(feeRate);
        }
        return new StandardWithdrawalPolicy();
    }

    @Bean("standardWithdrawalPolicy")
    public WithdrawalPolicy standardWithdrawalPolicy() {
        return new StandardWithdrawalPolicy();
    }
}
